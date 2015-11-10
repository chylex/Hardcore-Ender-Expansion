package chylex.hee.world.feature.noise;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.util.BoundingBox;

public class GenerateIslandNoise{
	private final Block block;
	private final NoiseGeneratorOctaves noiseGen1, noiseGen2, noiseGen3;
	private double[] noiseData1, noiseData2, noiseData3;
	private double[] densities;
	
	public GenerateIslandNoise(Block block, Random rand){
		this.block = block;
		this.noiseGen1 = new NoiseGeneratorOctaves(rand,16);
		this.noiseGen2 = new NoiseGeneratorOctaves(rand,16);
		this.noiseGen3 = new NoiseGeneratorOctaves(rand,8);
	}
	
	private double[] initializeNoiseField(double[] densities, int x, int y, int z, int sizeX, int sizeY, int sizeZ){
		if (densities == null)densities = new double[sizeX*sizeY*sizeZ];
		
		double noiseScaleXZ = 1368.824D, noiseScaleY = 684.412D;
		
		noiseData1 = noiseGen3.generateNoiseOctaves(noiseData1,x,y,z,sizeX,sizeY,sizeZ,noiseScaleXZ/80D,noiseScaleY/160D,noiseScaleXZ/80D);
		noiseData2 = noiseGen1.generateNoiseOctaves(noiseData2,x,y,z,sizeX,sizeY,sizeZ,noiseScaleXZ,noiseScaleY,noiseScaleXZ);
		noiseData3 = noiseGen2.generateNoiseOctaves(noiseData3,x,y,z,sizeX,sizeY,sizeZ,noiseScaleXZ,noiseScaleY,noiseScaleXZ);
		
		int indexFull = 0;
		int topPart = sizeY/2-2;

		for(int xx = 0; xx < sizeX; xx++){
			for(int zz = 0; zz < sizeZ; zz++){
				float distanceX = xx+x;
				float distanceZ = zz+z;
				
				float f2 = 100F-MathHelper.sqrt_float(distanceX*distanceX+distanceZ*distanceZ)*8F;
				f2 = MathUtil.clamp(f2,-100F,80F);

				for(int yy = 0; yy < sizeY; ++yy){
					double density = 0D;
					
					double lowestDensity = noiseData2[indexFull]/512D;
					double highestDensity = noiseData3[indexFull]/512D;
					double densityInterpolation = MathUtil.clamp((noiseData1[indexFull]/10D+1D)/2D,0D,1D);
					density = lowestDensity+(highestDensity-lowestDensity)*densityInterpolation;
					
					density -= 8D;
					density += f2;
					
					if (yy > topPart){
						double d10 = MathUtil.clamp((yy-topPart)/64F,0D,1D);
						density = density*(1D-d10)-3000D*d10;
					}
					
					byte bottomPart = 8;

					if (yy < bottomPart){
						double smoothBottom = (bottomPart-yy)/(bottomPart-1F);
						density = density*(1D-smoothBottom)-30D*smoothBottom;
					}

					densities[indexFull] = density;
					++indexFull;
				}
			}
		}

		return densities;
	}
	
	private void generateChunk(StructureWorld world, int x, int z){
		int noiseSizeXZ = 3;
		byte height = 33;
		densities = initializeNoiseField(densities,x*2,0,z*2,noiseSizeXZ,height,noiseSizeXZ);
		
		for(int xx = 0; xx < 2; xx++){
			for(int zz = 0; zz < 2; zz++){
				for(int yy = 0; yy < 32; yy++){
					double verticalSmoothing = 0.25D;
					double dBL = densities[((xx+0)*noiseSizeXZ+zz+0)*height+yy];
					double dBR = densities[((xx+0)*noiseSizeXZ+zz+1)*height+yy];
					double dTL = densities[((xx+1)*noiseSizeXZ+zz+0)*height+yy];
					double dTR = densities[((xx+1)*noiseSizeXZ+zz+1)*height+yy];
					double dTopBL = (densities[((xx+0)*noiseSizeXZ+zz+0)*height+yy+1]-dBL)*verticalSmoothing;
					double dTopBR = (densities[((xx+0)*noiseSizeXZ+zz+1)*height+yy+1]-dBR)*verticalSmoothing;
					double dTopTL = (densities[((xx+1)*noiseSizeXZ+zz+0)*height+yy+1]-dTL)*verticalSmoothing;
					double dTopTR = (densities[((xx+1)*noiseSizeXZ+zz+1)*height+yy+1]-dTR)*verticalSmoothing;
					
					for(int yChunk = 0; yChunk < 4; yChunk++){
						double horizontalSmoothing = 0.125D;
						double d10 = dBL;
						double d11 = dBR;
						double d12 = (dTL-dBL)*horizontalSmoothing;
						double d13 = (dTR-dBR)*horizontalSmoothing;
						
						for(int yBlock = 0; yBlock < 8; yBlock++){
							int index = yBlock+xx*8<<11|zz*8<<7|yy*4+yChunk;
							double d15 = d10;
							double d16 = (d11-d10)*0.125D;
							
							for(int yBlock3 = 0; yBlock3 < 8; yBlock3++){
								if (d15 > 0D)world.setBlock(x*16+((index>>11)&15),index&127,z*16+((index>>7)&15),block);
								index += 128;
								d15 += d16;
							}
							
							d10 += d12;
							d11 += d13;
						}
						
						dBL += dTopBL;
						dBR += dTopBR;
						dTL += dTopTL;
						dTR += dTopTR;
					}
				}
			}
		}
	}
	
	public void generate(StructureWorld world){
		BoundingBox box = world.getArea();
		int cx1 = box.x1/16, cx2 = box.x2/16;
		int cz1 = box.z1/16, cz2 = box.z2/16;
		
		for(int chunkX = cx1; chunkX < cx2; chunkX++){
			for(int chunkZ = cz1; chunkZ < cz2; chunkZ++){
				generateChunk(world,chunkX,chunkZ);
			}
		}
	}
}
