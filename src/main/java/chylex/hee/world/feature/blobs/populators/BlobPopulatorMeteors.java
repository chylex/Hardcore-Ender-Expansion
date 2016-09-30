package chylex.hee.world.feature.blobs.populators;
import java.util.List;
import java.util.Random;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.Vec;
import chylex.hee.system.abstractions.facing.Facing6;
import chylex.hee.system.collections.CollectionUtil;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.feature.blobs.StructureWorldBlob;
import chylex.hee.world.util.IRangeGenerator;
import chylex.hee.world.util.IRangeGenerator.RangeGenerator;
import chylex.hee.world.util.RandomAmount;

public class BlobPopulatorMeteors extends BlobPopulator{
	private IRangeGenerator amount;
	private IRandGenerator radius = (rand) -> 0D;
	
	public BlobPopulatorMeteors(int weight){
		super(weight);
	}
	
	public BlobPopulatorMeteors setAmount(int minAmount, int maxAmount){
		this.amount = new RangeGenerator(minAmount, maxAmount, RandomAmount.linear);
		return this;
	}
	
	public BlobPopulatorMeteors setAmount(IRangeGenerator amountGenerator){
		this.amount = amountGenerator;
		return this;
	}
	
	public BlobPopulatorMeteors setRadius(double minRadius, double maxRadius){
		this.radius = rand -> minRadius+rand.nextDouble()*(maxRadius-minRadius);
		return this;
	}
	
	public BlobPopulatorMeteors setRadius(IRandGenerator radiusGenerator){
		this.radius = radiusGenerator;
		return this;
	}

	@Override
	public void populate(StructureWorldBlob world, Random rand){
		int meteorsLeft = (amount == null ? 0 : amount.next(rand));
		if (meteorsLeft <= 0)return;
		
		List<Pos> endStone = world.getEndStoneBlocks();
		int meteorAttempts = endStone.size()/4;
		
		while(--meteorAttempts >= 0 && meteorsLeft > 0){
			double rad = radius.generate(rand);
			Vec dir = Vec.xyzRandom(rand);
			
			Pos block = CollectionUtil.random(endStone, rand).orElseGet(() -> Pos.at(0, world.getCenterY(), 0));
			Vec pos = Vec.xyz(block.getX()+0.5D, block.getY()+0.5D, block.getZ()+0.5D).offset(dir, rad+2D+4D*rand.nextDouble());
			
			if (isBlobSeparated(world, pos.x, pos.y, pos.z, rad) && isBlobInsideWorld(world, pos.x, pos.y, pos.z, rad)){
				generateBlob(world, pos.x, pos.y, pos.z, rad);
				--meteorsLeft;
			}
		}
	}
	
	private boolean isBlobSeparated(StructureWorldBlob world, double x, double y, double z, double rad){
		int ix = MathUtil.floor(x), iy = MathUtil.floor(y), iz = MathUtil.floor(z);
		int irad = MathUtil.ceil(rad)+2, icorner = MathUtil.ceil((rad+2D)*0.707D);
		
		if (!world.isAir(ix, iy, iz))return false;
		
		for(Facing6 facing:Facing6.list){
			if (!world.isAir(ix+facing.getX()*irad, iy+facing.getY()*irad, iz+facing.getZ()*irad))return false;
		}
		
		for(int cornerX = -1; cornerX <= 1; cornerX += 2){
			for(int cornerY = -1; cornerY <= 1; cornerY += 2){
				for(int cornerZ = -1; cornerZ <= 1; cornerZ += 2){
					if (!world.isAir(ix+cornerX*icorner, iy+cornerY*icorner, iz+cornerZ*icorner))return false;
				}
			}
		}
		
		return true;
	}
}
