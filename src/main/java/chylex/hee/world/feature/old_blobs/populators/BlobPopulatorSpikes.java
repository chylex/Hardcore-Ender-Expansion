package chylex.hee.world.feature.old_blobs.populators;
import net.minecraft.block.Block;
import chylex.hee.world.feature.old_blobs.BlobPopulator;
import chylex.hee.world.util.RandomAmount;

public class BlobPopulatorSpikes extends BlobPopulator{
	private Block block;
	private RandomAmount amountGen = RandomAmount.exact;
	private byte minAmount, maxAmount, maxOffsetFromCenter;
	
	public BlobPopulatorSpikes(int weight){
		super(weight);
	}
	
	public BlobPopulatorSpikes block(Block block){
		this.block = block;
		return this;
	}
	
	public BlobPopulatorSpikes amount(RandomAmount amountGen, int minAmount, int maxAmount){
		this.amountGen = amountGen;
		this.minAmount = (byte)minAmount;
		this.maxAmount = (byte)maxAmount;
		return this;
	}
	
	public BlobPopulatorSpikes maxOffset(int maxOffsetFromCenter){
		this.maxOffsetFromCenter = (byte)maxOffsetFromCenter;
		return this;
	}

	/*@Override
	public void generate(DecoratorFeatureGenerator gen, Random rand){
		for(int a = 0, amount = amountGen.generate(rand,minAmount,maxAmount), x, y, z; a < amount; a++){
			for(int attempt = 0; attempt < 10; attempt++){
				Vec3 vec = DragonUtil.getRandomVector(rand);
				x = maxOffsetFromCenter == 0 ? 0 : rand.nextInt(maxOffsetFromCenter*2)-maxOffsetFromCenter;
				y = maxOffsetFromCenter == 0 ? 0 : rand.nextInt(maxOffsetFromCenter*2)-maxOffsetFromCenter;
				z = maxOffsetFromCenter == 0 ? 0 : rand.nextInt(maxOffsetFromCenter*2)-maxOffsetFromCenter;
				
				if (genSpike(gen,x,y,z,vec))break;
			}
		}
	}
	
	private boolean genSpike(DecoratorFeatureGenerator gen, double x, double y, double z, Vec3 dirVec){
		boolean placedSomething = false;
		
		x -= dirVec.xCoord*16;
		y -= dirVec.yCoord*16;
		z -= dirVec.zCoord*16;
		
		for(int unit = 0, ix, iy, iz; unit < 32; unit++){
			if (gen.getBlock(ix = MathUtil.floor(x),iy = MathUtil.floor(y),iz = MathUtil.floor(z)) == Blocks.end_stone){
				gen.setBlock(ix,iy,iz,block);
				placedSomething = true;
			}
			
			x += dirVec.xCoord;
			y += dirVec.yCoord;
			z += dirVec.zCoord;
		}
		
		return placedSomething;
	}*/
}
