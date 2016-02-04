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
	
	/**
	 * Sets the maximum size of the island on X and Z axis.
	 */
	public int terrainSize = 100;
	
	/**
	 * Sets the noise height multiplier. Default value is 32, which has the potential to fill up 128 blocks in height, but due to smoothing it generally only goes up to ~82.
	 */
	public int noiseHeight = 32;
	
	/**
	 * Sets how steep or smooth peaks are. Default value is 80, lower values increase height variety and chaos, higher values make the terrain smoother.
	 */
	public double peakSmoothness = 80D;
	
	/**
	 * Sets how much the island stretches the sides. Default value is 160, lower values create sharper and longer edges, higher values make them smoother.
	 */
	public double sideSmoothness = 160D;
	
	/**
	 * Modifies the shape of density generators. Values above 1 create extreme peaks and valleys, values below 1 make the island shape more circular.
	 */
	public double densityPeakMultiplier = 1D;
	
	/**
	 * Modifies appearance of peaks. No idea how to describe what it does. Best used with large {@code densityPeakMultiplier}.
	 */
	public double densityVariantLow = 1D;
	
	/**
	 * Modifies appearance of peaks. No idea how to describe what it does. Best used with large {@code densityPeakMultiplier}.
	 */
	public double densityVariantHigh = 1D;
	
	/**
	 * Sets where the surface part of the island begins.
	 */
	public float surfaceStart = 0.45F;
	
	/**
	 * Sets scale for hill height on the island surface. Default value is 64, values between 0 and 1 create completely flat surface. Higher values might exceed the height limit.
	 */
	public float surfaceHillScale = 64F;
	
	/**
	 * Sets smoothing value for surface hills. Default value is 3000, higher values create flatter surface, lower values increase height and variety, but might exceed the height limit.
	 */
	public double surfaceHeightSmoothing = 3000D;
	
	public GenerateIslandNoise(Block block, Random rand){
		this.block = block;
		this.noiseGen1 = new NoiseGeneratorOctaves(rand,16);
		this.noiseGen2 = new NoiseGeneratorOctaves(rand,16);
		this.noiseGen3 = new NoiseGeneratorOctaves(rand,8);
	}
	
	private double[] initializeNoiseField(double[] densities, int x, int y, int z, int sizeX, int sizeY, int sizeZ){
		if (densities == null)densities = new double[sizeX*sizeY*sizeZ];
		
		double noiseScaleXZ = 1368.824D, noiseScaleY = 684.412D;
		
		noiseData1 = noiseGen3.generateNoiseOctaves(noiseData1,x,y,z,sizeX,sizeY,sizeZ,noiseScaleXZ/peakSmoothness,noiseScaleY/sideSmoothness,noiseScaleXZ/peakSmoothness);
		noiseData2 = noiseGen1.generateNoiseOctaves(noiseData2,x,y,z,sizeX,sizeY,sizeZ,noiseScaleXZ*densityPeakMultiplier,noiseScaleY*densityVariantLow,noiseScaleXZ*densityPeakMultiplier);
		noiseData3 = noiseGen2.generateNoiseOctaves(noiseData3,x,y,z,sizeX,sizeY,sizeZ,noiseScaleXZ*densityPeakMultiplier,noiseScaleY*densityVariantHigh,noiseScaleXZ*densityPeakMultiplier);
		
		int indexFull = 0;
		
		int topPart = (int)(sizeY*surfaceStart);
		int bottomPart = 8;

		for(int xx = 0; xx < sizeX; xx++){
			for(int zz = 0; zz < sizeZ; zz++){
				float distanceX = xx+x;
				float distanceZ = zz+z;
				
				float densityOffset = MathUtil.clamp(terrainSize-MathHelper.sqrt_float(distanceX*distanceX+distanceZ*distanceZ)*8F,-100F,80F)-8F;
				
				for(int yy = 0; yy < sizeY; ++yy){
					double lowestDensity = noiseData2[indexFull]/512D;
					double highestDensity = noiseData3[indexFull]/512D;
					double densityInterpolation = MathUtil.clamp((noiseData1[indexFull]/10D+1D)/2D,0D,1D);
					double density = lowestDensity+(highestDensity-lowestDensity)*densityInterpolation+densityOffset;
					
					if (yy > topPart){
						double smoothSurface = MathUtil.clamp((yy-topPart)/surfaceHillScale,0D,1D);
						density = density*(1D-smoothSurface)-surfaceHeightSmoothing*smoothSurface;
					}
					else if (yy < bottomPart){
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
	
	private void generateChunk(StructureWorld world, int x, int z, final int offsetX, final int offsetY, final int offsetZ){
		int noiseSizeXZ = 3;
		byte height = (byte)(noiseHeight+1);
		densities = initializeNoiseField(densities,x*2,0,z*2,noiseSizeXZ,height,noiseSizeXZ);
		
		for(int xx = 0; xx < 2; xx++){
			for(int zz = 0; zz < 2; zz++){
				for(int yy = 0; yy < noiseHeight; yy++){
					double verticalSmoothing = 0.25D;
					double dBL = densities[((xx+0)*noiseSizeXZ+zz+0)*height+yy];
					double dBR = densities[((xx+0)*noiseSizeXZ+zz+1)*height+yy];
					double dTL = densities[((xx+1)*noiseSizeXZ+zz+0)*height+yy];
					double dTR = densities[((xx+1)*noiseSizeXZ+zz+1)*height+yy];
					double dOffsetBL = (densities[((xx+0)*noiseSizeXZ+zz+0)*height+yy+1]-dBL)*verticalSmoothing;
					double dOffsetBR = (densities[((xx+0)*noiseSizeXZ+zz+1)*height+yy+1]-dBR)*verticalSmoothing;
					double dOffsetTL = (densities[((xx+1)*noiseSizeXZ+zz+0)*height+yy+1]-dTL)*verticalSmoothing;
					double dOffsetTR = (densities[((xx+1)*noiseSizeXZ+zz+1)*height+yy+1]-dTR)*verticalSmoothing;
					
					for(int yChunk = 0; yChunk < 4; yChunk++){
						double horizontalSmoothing = 0.125D;
						double dCurrentLeft = dBL;
						double dCurrentRight = dBR;
						double dOffsetLeft = (dTL-dBL)*horizontalSmoothing;
						double dOffsetRight = (dTR-dBR)*horizontalSmoothing;
						
						for(int yBlock = 0; yBlock < 8; yBlock++){
							int index = yBlock+xx*8<<12|zz*8<<8|yy*4+yChunk;
							double density = dCurrentLeft;
							double interpolation = (dCurrentRight-dCurrentLeft)*0.125D;
							
							for(int yBlock3 = 0; yBlock3 < 8; yBlock3++){
								if (density > 0D)world.setBlock(x*16+((index>>12)&15)+offsetX,(index&255)+offsetY,z*16+((index>>8)&15)+offsetZ,block);
								index += 256;
								density += interpolation;
							}
							
							dCurrentLeft += dOffsetLeft;
							dCurrentRight += dOffsetRight;
						}
						
						dBL += dOffsetBL;
						dBR += dOffsetBR;
						dTL += dOffsetTL;
						dTR += dOffsetTR;
					}
				}
			}
		}
	}
	
	public void generate(StructureWorld world){
		generate(world,0,0,0);
	}
	
	public void generate(StructureWorld world, final int offsetX, final int offsetY, final int offsetZ){
		BoundingBox box = world.getArea();
		int cx1 = box.x1/16, cx2 = box.x2/16;
		int cz1 = box.z1/16, cz2 = box.z2/16;
		
		for(int chunkX = cx1; chunkX < cx2; chunkX++){
			for(int chunkZ = cz1; chunkZ < cz2; chunkZ++){
				generateChunk(world,chunkX,chunkZ,offsetX,offsetY,offsetZ);
			}
		}
	}
}
