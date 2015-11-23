package chylex.hee.world.feature.blobs.populators;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import org.apache.commons.lang3.tuple.Pair;
import chylex.hee.system.abstractions.Vec;
import chylex.hee.system.abstractions.facing.Facing6;
import chylex.hee.system.collections.CollectionUtil;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.feature.blobs.StructureWorldBlob;
import chylex.hee.world.util.BoundingBox;
import chylex.hee.world.util.RandomAmount;
import chylex.hee.world.util.RangeGenerator;

public class BlobPopulatorCaves extends BlobPopulator{
	private static final RangeGenerator zero = new RangeGenerator(0,0,RandomAmount.exact);
	
	private final List<Pair<Vec,Double>> tmpCavePositions = new ArrayList<>();
	private BoundingBox tmpArea;
	
	private IRandGenerator radiusMain = rand -> 0D, radiusMinorMp = rand -> 1D;
	private RangeGenerator amountMain = zero, amountMinor = zero;
	
	/**
	 * Each cycle moves the position iteration by one block.
	 */
	private RangeGenerator cyclesMain = zero, cyclesMinor = zero;
	
	public BlobPopulatorCaves(int weight){
		super(weight);
	}
	
	public BlobPopulatorCaves setMainAmount(RangeGenerator amountGenerator){
		this.amountMain = amountGenerator;
		return this;
	}
	
	public BlobPopulatorCaves setMainAmount(int minAmount, int maxAmount){
		this.amountMain = new RangeGenerator(minAmount,maxAmount,RandomAmount.linear);
		return this;
	}
	
	public BlobPopulatorCaves setMinorAmount(RangeGenerator amountGenerator){
		this.amountMinor = amountGenerator;
		return this;
	}
	
	public BlobPopulatorCaves setMinorAmount(int minAmount, int maxAmount){
		this.amountMinor = new RangeGenerator(minAmount,maxAmount,RandomAmount.linear);
		return this;
	}
	
	public BlobPopulatorCaves setMainRadius(double radius){
		this.radiusMain = rand -> radius;
		return this;
	}
	
	public BlobPopulatorCaves setMainRadius(double minRadius, double maxRadius){
		this.radiusMain = rand -> minRadius+rand.nextDouble()*(maxRadius-minRadius);
		return this;
	}
	
	public BlobPopulatorCaves setMainRadius(IRandGenerator radiusGenerator){
		this.radiusMain = radiusGenerator;
		return this;
	}
	
	public BlobPopulatorCaves setMinorRadiusMp(double radiusMp){
		this.radiusMinorMp = rand -> radiusMp;
		return this;
	}
	
	public BlobPopulatorCaves setMinorRadiusMp(double minRadiusMp, double maxRadiusMp){
		this.radiusMinorMp = rand -> minRadiusMp+rand.nextDouble()*(maxRadiusMp-minRadiusMp);
		return this;
	}
	
	public BlobPopulatorCaves setMinorRadiusMp(IRandGenerator radiusMpGenerator){
		this.radiusMinorMp = radiusMpGenerator;
		return this;
	}
	
	public BlobPopulatorCaves setMainCycles(RangeGenerator cycleGenerator){
		this.cyclesMain = cycleGenerator;
		return this;
	}
	
	public BlobPopulatorCaves setMainCycles(int minCycles, int maxCycles){
		this.cyclesMain = new RangeGenerator(minCycles,maxCycles,RandomAmount.linear);
		return this;
	}
	
	public BlobPopulatorCaves setMinorCycles(RangeGenerator cycleGenerator){
		this.cyclesMinor = cycleGenerator;
		return this;
	}
	
	public BlobPopulatorCaves setMinorCycles(int minCycles, int maxCycles){
		this.cyclesMinor = new RangeGenerator(minCycles,maxCycles,RandomAmount.linear);
		return this;
	}

	@Override
	public void populate(StructureWorldBlob world, Random rand){
		tmpArea = world.getArea();
		
		int mainLeft = amountMain.next(rand), minorLeft = amountMinor.next(rand);
		int mainAttempts = mainLeft*3, minorAttempts = minorLeft*3;
		
		while(--mainAttempts >= 0 && mainLeft > 0){
			if (generateCave(world,rand,findNewPosition(world,rand),radiusMain.generate(rand),cyclesMain.next(rand))){
				--mainLeft;
			}
		}
		
		while(--minorAttempts >= 0 && minorLeft > 0 && !tmpCavePositions.isEmpty()){
			Pair<Vec,Double> cave = CollectionUtil.randomOrNull(tmpCavePositions,rand);
			
			if (generateCave(world,rand,cave.getKey(),cave.getValue().doubleValue()*radiusMinorMp.generate(rand),cyclesMinor.next(rand))){
				--minorLeft;
			}
		}
		
		tmpCavePositions.clear();
		tmpArea = null;
	}
	
	private Vec findNewPosition(StructureWorldBlob world, Random rand){
		Vec lastPos = null;
		
		for(int attempt = 0; attempt < 10; attempt++){
			lastPos = Vec.xyz(
				tmpArea.x1+rand.nextInt(1+tmpArea.x2-tmpArea.x1)+rand.nextDouble(),
				tmpArea.y1+rand.nextInt(1+tmpArea.y2-tmpArea.y1)+rand.nextDouble(),
				tmpArea.z1+rand.nextInt(1+tmpArea.z2-tmpArea.z1)+rand.nextDouble()
			);
			
			if (isNearEndStone(world,lastPos,4D))return lastPos;
		}
		
		return lastPos;
	}
	
	private boolean generateCave(StructureWorldBlob world, Random rand, Vec startPos, double radius, int cycles){
		Vec dir = Vec.xyzRandom(rand), pos = startPos.copy();
		boolean isNearEndStone, wasNearEndStone = false;
		
		while(--cycles >= 0){
			if (isNearEndStone(world,pos,radius)){
				wasNearEndStone = true;
				tmpCavePositions.add(Pair.of(pos.copy(),radius));
			}
			
			generateBlob(world,pos.x,pos.y,pos.z,radius,Blocks.air);
			pos.moveBy(dir);
			
			if (rand.nextInt(3) == 0){
				dir = dir.offset(Vec.xyzRandom(rand),rand.nextDouble()*4D).normalized();
			}
		}
		
		return wasNearEndStone;
	}
	
	private boolean isNearEndStone(StructureWorldBlob world, Vec pos, double radius){
		if (world.getBlock(MathUtil.floor(pos.x),MathUtil.floor(pos.y),MathUtil.floor(pos.z)) == Blocks.end_stone)return true;
		
		for(Facing6 facing:Facing6.list){
			if (world.getBlock(MathUtil.floor(pos.x+radius*facing.getX()),
				               MathUtil.floor(pos.y+radius*facing.getY()),
				               MathUtil.floor(pos.z+radius*facing.getZ())) == Blocks.end_stone){
				return true;
			}
		}
		
		return false;
	}
}
