package chylex.hee.world.feature.blobs.populators;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.MathUtil;
import chylex.hee.system.util.Vec3M;
import chylex.hee.world.feature.blobs.BlobPopulator;
import chylex.hee.world.feature.util.DecoratorFeatureGenerator;
import chylex.hee.world.util.IRandomAmount;

public class BlobPopulatorSpikes extends BlobPopulator{
	private Block block;
	private IRandomAmount amountGen = IRandomAmount.exact;
	private byte minAmount, maxAmount, maxOffsetFromCenter;
	
	public BlobPopulatorSpikes(int weight){
		super(weight);
	}
	
	public BlobPopulatorSpikes block(Block block){
		this.block = block;
		return this;
	}
	
	public BlobPopulatorSpikes amount(IRandomAmount amountGen, int minAmount, int maxAmount){
		this.amountGen = amountGen;
		this.minAmount = (byte)minAmount;
		this.maxAmount = (byte)maxAmount;
		return this;
	}
	
	public BlobPopulatorSpikes maxOffset(int maxOffsetFromCenter){
		this.maxOffsetFromCenter = (byte)maxOffsetFromCenter;
		return this;
	}

	@Override
	public void generate(DecoratorFeatureGenerator gen, Random rand){
		for(int a = 0, amount = amountGen.generate(rand,minAmount,maxAmount), x, y, z; a < amount; a++){
			for(int attempt = 0; attempt < 10; attempt++){
				Vec3M vec = DragonUtil.getRandomVector(rand);
				x = maxOffsetFromCenter == 0 ? 0 : rand.nextInt(maxOffsetFromCenter*2)-maxOffsetFromCenter;
				y = maxOffsetFromCenter == 0 ? 0 : rand.nextInt(maxOffsetFromCenter*2)-maxOffsetFromCenter;
				z = maxOffsetFromCenter == 0 ? 0 : rand.nextInt(maxOffsetFromCenter*2)-maxOffsetFromCenter;
				
				if (genSpike(gen,x,y,z,vec))break;
			}
		}
	}
	
	private boolean genSpike(DecoratorFeatureGenerator gen, double x, double y, double z, Vec3M dirVec){
		boolean placedSomething = false;
		
		x -= dirVec.x*16;
		y -= dirVec.y*16;
		z -= dirVec.z*16;
		
		for(int unit = 0, ix, iy, iz; unit < 32; unit++){
			if (gen.getBlock(ix = MathUtil.floor(x),iy = MathUtil.floor(y),iz = MathUtil.floor(z)) == Blocks.end_stone){
				gen.setBlock(ix,iy,iz,block);
				placedSomething = true;
			}
			
			x += dirVec.x;
			y += dirVec.y;
			z += dirVec.z;
		}
		
		return placedSomething;
	}
}
