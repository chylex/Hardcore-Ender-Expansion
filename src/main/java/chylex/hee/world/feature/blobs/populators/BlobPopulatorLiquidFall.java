package chylex.hee.world.feature.blobs.populators;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.Direction;
import chylex.hee.world.feature.blobs.BlobPopulator;
import chylex.hee.world.feature.util.DecoratorFeatureGenerator;
import chylex.hee.world.util.BlockLocation;
import chylex.hee.world.util.IRandomAmount;

public class BlobPopulatorLiquidFall extends BlobPopulator{
	private Block liquid;
	private IRandomAmount amountGen;
	private byte minAmount, maxAmount, minAttempts, maxAttempts;
	
	public BlobPopulatorLiquidFall(int weight){
		super(weight);
	}
	
	public BlobPopulatorLiquidFall block(Block liquid){
		this.liquid = liquid;
		return this;
	}
	
	public BlobPopulatorLiquidFall amount(IRandomAmount amountGen, int minAmount, int maxAmount){
		this.amountGen = amountGen;
		this.minAmount = (byte)minAmount;
		this.maxAmount = (byte)maxAmount;
		return this;
	}
	
	public BlobPopulatorLiquidFall attempts(int minAttempts, int maxAttempts){
		this.minAttempts = (byte)minAttempts;
		this.maxAttempts = (byte)maxAttempts;
		return this;
	}

	@Override
	public void generate(DecoratorFeatureGenerator gen, Random rand){
		List<BlockLocation> blocks = gen.getUsedLocations();
		if (blocks.isEmpty())return;
		
		int amount = amountGen.generate(rand,minAmount,maxAmount);
		
		for(int attempts = minAttempts+rand.nextInt(maxAttempts-minAttempts+1), xx, yy, zz; attempts > 0 && amount > 0 && !blocks.isEmpty(); attempts--){
			BlockLocation loc = blocks.remove(rand.nextInt(blocks.size()));
			if (gen.getBlock(loc.x,loc.y,loc.z) != Blocks.end_stone || gen.getBlock(loc.x,loc.y-1,loc.z) != Blocks.end_stone)continue;
			
			byte airDir = -1;
			
			for(byte dir = 0; dir < 4; dir++){
				if (gen.getBlock(loc.x+Direction.offsetX[dir],loc.y,loc.z+Direction.offsetZ[dir]) == Blocks.air){
					if (airDir == -1)airDir = dir;
					else{
						airDir = -1;
						break;
					}
				}
			}
			
			if (airDir == -1)continue;
			
			xx = loc.x+Direction.offsetX[airDir];
			yy = loc.y;
			zz = loc.z+Direction.offsetZ[airDir];
			Block block;
			
			while(--yy >= -16){
				if ((block = gen.getBlock(xx,yy,zz)) == Blocks.end_stone){
					if (gen.getBlock(xx,yy-1,zz) == Blocks.end_stone){
						gen.setBlock(loc.x,loc.y,loc.z,liquid);
						gen.setBlock(xx,yy,zz,Blocks.air);
						--amount;
						break;
					}
				}
				else if (block != Blocks.air)break;
			}
		}
	}
}
