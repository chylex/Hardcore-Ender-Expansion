package chylex.hee.world.feature.blobs.populators;
import java.util.Random;
import net.minecraft.util.Vec3;
import chylex.hee.world.feature.blobs.BlobPopulator;
import chylex.hee.world.feature.util.DecoratorFeatureGenerator;
import chylex.hee.world.util.IRandomAmount;

public class BlobPopulatorCave extends BlobPopulator{
	private IRandomAmount amountGen;
	private int minCaveAmount, maxCaveAmount;
	private double minRecursionChance, maxRecursionChance;
	
	public BlobPopulatorCave(int weight){
		super(weight);
	}

	@Override
	public void generate(DecoratorFeatureGenerator gen, Random rand){
		
	}
	
	private void genNewCave(DecoratorFeatureGenerator gen, Random rand, float rad){
		int side = rand.nextInt(6);
		float x = 0F, y = 0F, z = 0F;
		
		switch(side){
			case 0:
			case 1:
				x = rand.nextInt(32)-16;
				y = rand.nextInt(32)-16;
				z = side == 0 ? 16 : -16;
				break;
				
			case 2:
			case 3:
				x = rand.nextInt(32)-16;
				y = side == 0 ? 16 : -16;
				z = rand.nextInt(32)-16;
				break;
				
			case 4:
			case 5:
				x = side == 0 ? 16 : -16;
				y = rand.nextInt(32)-16;
				z = rand.nextInt(32)-16;
				break;
		}
		
		Vec3 vec = Vec3.createVectorHelper(-x,-y,-z).normalize();
	}
}
