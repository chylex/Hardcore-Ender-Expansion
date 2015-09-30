package chylex.hee.world.feature.old_blobs.populators;
import net.minecraft.block.Block;
import chylex.hee.world.feature.old_blobs.BlobPopulator;
import chylex.hee.world.util.RandomAmount;

public class BlobPopulatorOreCluster extends BlobPopulator{
	private Block ore;
	private RandomAmount iterationAmountGen = RandomAmount.exact, blockAmountGen = RandomAmount.exact;
	private byte minAttempts, maxAttempts, minIterationAmount, maxIterationAmount, minBlockAmount, maxBlockAmount;
	
	public BlobPopulatorOreCluster(int weight){
		super(weight);
	}
	
	public BlobPopulatorOreCluster block(Block ore){
		this.ore = ore;
		return this;
	}
	
	public BlobPopulatorOreCluster iterationAmount(RandomAmount iterationAmountGen, int minIterationAmount, int maxIterationAmount){
		this.iterationAmountGen = iterationAmountGen;
		this.minIterationAmount = (byte)minIterationAmount;
		this.maxIterationAmount = (byte)maxIterationAmount;
		return this;
	}
	
	public BlobPopulatorOreCluster blockAmount(RandomAmount blockAmountGen, int minBlockAmount, int maxBlockAmount){
		this.blockAmountGen = blockAmountGen;
		this.minBlockAmount = (byte)minBlockAmount;
		this.maxBlockAmount = (byte)maxBlockAmount;
		return this;
	}
	
	public BlobPopulatorOreCluster attempts(int minAttempts, int maxAttempts){
		this.minAttempts = (byte)minAttempts;
		this.maxAttempts = (byte)maxAttempts;
		return this;
	}

	/*@Override
	public void generate(DecoratorFeatureGenerator gen, Random rand){
		for(int iteration = 0, iterations = iterationAmountGen.generate(rand,minIterationAmount,maxIterationAmount); iteration < iterations; iteration++){
			boolean succeeded = false;
			int blockAmount = blockAmountGen.generate(rand,minBlockAmount,maxBlockAmount);
			
			for(int attempt = 0, attempts = minAttempts+rand.nextInt(maxAttempts-minAttempts+1), x, y, z; attempt < attempts; attempt++){
				x = rand.nextInt(32)-16;
				y = rand.nextInt(32)-16;
				z = rand.nextInt(32)-16;
				
				float randomAngle = rand.nextFloat()*(float)Math.PI;
				double genX1 = x+MathHelper.sin(randomAngle)*blockAmount*0.125F;
				double genX2 = x-MathHelper.sin(randomAngle)*blockAmount*0.125F;
				double genY1 = z+MathHelper.cos(randomAngle)*blockAmount*0.125F;
				double genY2 = z-MathHelper.cos(randomAngle)*blockAmount*0.125F;
				double genZ1 = y+rand.nextInt(4)-2;
				double genZ2 = y+rand.nextInt(4)-2;
	
				for(int a = 0; a <= blockAmount; ++a){
					double centerX = genX1+(genX2-genX1)*a/blockAmount;
					double centerY = genZ1+(genZ2-genZ1)*a/blockAmount;
					double centerZ = genY1+(genY2-genY1)*a/blockAmount;
					double maxDist = rand.nextDouble()*blockAmount*0.0625D;
					double area = (MathHelper.sin(a*(float)Math.PI/blockAmount)+1F)*maxDist+1D;
					
					int minX = MathHelper.floor_double(centerX-area*0.5D);
					int minY = MathHelper.floor_double(centerY-area*0.5D);
					int minZ = MathHelper.floor_double(centerZ-area*0.5D);
					int maxX = MathHelper.floor_double(centerX+area*0.5D);
					int maxY = MathHelper.floor_double(centerY+area*0.5D);
					int maxZ = MathHelper.floor_double(centerZ+area*0.5D);
	
					for(int xx = minX; xx <= maxX; ++xx){
						double d12 = (xx+0.5D-centerX)/(area*0.5D);
	
						if (d12*d12 < 1D){
							for(int yy = minY; yy <= maxY; ++yy){
								double d13 = (yy+0.5D-centerY)/(area*0.5D);
	
								if (d12*d12+d13*d13 < 1D){
									for(int zz = minZ; zz <= maxZ; ++zz){
										double d14 = (zz+0.5D-centerZ)/(area*0.5D);
	
										if (d12*d12+d13*d13+d14*d14 >= 1D)continue;
										
										if (gen.getBlock(xx,yy,zz) == Blocks.end_stone){
											gen.setBlock(xx,yy,zz,ore);
											succeeded = true;
										}
									}
								}
							}
						}
					}
				}
				
				if (succeeded)break;
			}
		}
	}*/
}
