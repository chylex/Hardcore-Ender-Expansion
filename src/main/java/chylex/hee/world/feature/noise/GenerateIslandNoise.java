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
	private final NoiseGeneratorOctaves noiseGen1, noiseGen2, noiseGen3, noiseGen4;
	private double[] noiseData1, noiseData2, noiseData3, noiseData4;
	private double[] densities;
	
	public GenerateIslandNoise(Block block, Random rand){
		this.block = block;
		this.noiseGen1 = new NoiseGeneratorOctaves(rand,16);
		this.noiseGen2 = new NoiseGeneratorOctaves(rand,16);
		this.noiseGen3 = new NoiseGeneratorOctaves(rand,8);
		this.noiseGen4 = new NoiseGeneratorOctaves(rand,10);
	}
	
	private double[] initializeNoiseField(double[] densities, int x, int y, int z, int sizeX, int sizeY, int sizeZ){
		if (densities == null)densities = new double[sizeX*sizeY*sizeZ];
		
		double noiseScaleXZ = 1368.824D, noiseScaleY = 684.412D;
		
		noiseData4 = noiseGen4.generateNoiseOctaves(noiseData4,x,z,sizeX,sizeZ,1.121D,1.121D,0.5D);
		noiseData1 = noiseGen3.generateNoiseOctaves(noiseData1,x,y,z,sizeX,sizeY,sizeZ,noiseScaleXZ/80D,noiseScaleY/160D,noiseScaleXZ/80D);
		noiseData2 = noiseGen1.generateNoiseOctaves(noiseData2,x,y,z,sizeX,sizeY,sizeZ,noiseScaleXZ,noiseScaleY,noiseScaleXZ);
		noiseData3 = noiseGen2.generateNoiseOctaves(noiseData3,x,y,z,sizeX,sizeY,sizeZ,noiseScaleXZ,noiseScaleY,noiseScaleXZ);
		int indexFull = 0;
		int indexHorizontal = 0;

		for(int xx = 0; xx < sizeX; xx++){
			for(int zz = 0; zz < sizeZ; zz++){
				double d2 = Math.min(1D,(noiseData4[indexHorizontal]+256D)/512D);
				if (d2 < 0D)d2 = 0D;
				d2 += 0.5D;
				
				float distanceX = (xx+x); // TODO divide
				float distanceZ = (zz+z);
				
				float f2 = 100F-MathHelper.sqrt_float(distanceX*distanceX+distanceZ*distanceZ)*8F;
				f2 = MathUtil.clamp(f2,-100F,80F);
				
				++indexHorizontal;
				double d4 = sizeY/2D;

				for(int yy = 0; yy < sizeY; ++yy){
					double density = 0D;
					
					double lowestDensity = noiseData2[indexFull]/512D;
					double highestDensity = noiseData3[indexFull]/512D;
					double densityInterpolation = MathUtil.clamp((noiseData1[indexFull]/10D+1D)/2D,0D,1D);
					density = lowestDensity+(highestDensity-lowestDensity)*densityInterpolation;
					
					density -= 8D;
					density += f2;
					double d10;
					
					if (yy > sizeY/2-2){
						d10 = MathUtil.clamp(((yy-(sizeY/2-2))/64F),0D,1D);
						density = density*(1D-d10)-3000D*d10;
					}
					
					byte bottomPart = 8;

					if (yy < bottomPart){
						d10 = ((bottomPart-yy)/(bottomPart-1F));
						density = density*(1D-d10)-30D*d10;
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
					double d0 = 0.25D;
					double dBL = densities[((xx+0)*noiseSizeXZ+zz+0)*height+yy];
					double dBR = densities[((xx+0)*noiseSizeXZ+zz+1)*height+yy];
					double dTL = densities[((xx+1)*noiseSizeXZ+zz+0)*height+yy];
					double dTR = densities[((xx+1)*noiseSizeXZ+zz+1)*height+yy];
					double dTopBL = (densities[((xx+0)*noiseSizeXZ+zz+0)*height+yy+1]-dBL)*d0;
					double dTopBR = (densities[((xx+0)*noiseSizeXZ+zz+1)*height+yy+1]-dBR)*d0;
					double dTopTL = (densities[((xx+1)*noiseSizeXZ+zz+0)*height+yy+1]-dTL)*d0;
					double dTopTR = (densities[((xx+1)*noiseSizeXZ+zz+1)*height+yy+1]-dTR)*d0;
					
					for(int yChunk = 0; yChunk < 4; yChunk++){
						double d9 = 0.125D;
						double d10 = dBL;
						double d11 = dBR;
						double d12 = (dTL-dBL)*d9;
						double d13 = (dTR-dBR)*d9;
						
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
