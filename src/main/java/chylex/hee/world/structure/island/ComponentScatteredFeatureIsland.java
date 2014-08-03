package chylex.hee.world.structure.island;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import chylex.hee.block.BlockList;
import chylex.hee.system.logging.Stopwatch;
import chylex.hee.world.structure.ComponentScatteredFeatureCustom;
import chylex.hee.world.structure.island.biome.IslandBiomeBase;
import chylex.hee.world.structure.island.biome.data.IslandBiomeData;
import chylex.hee.world.structure.island.gen.CaveGenerator;
import chylex.hee.world.structure.island.gen.OreGenerator;
import chylex.hee.world.structure.util.Offsets;
import chylex.hee.world.structure.util.pregen.LargeStructureWorld;

public class ComponentScatteredFeatureIsland extends ComponentScatteredFeatureCustom{
	public static final int size = 208, halfSize = size>>1;
	
	private boolean isSetup = false;
	
	private LargeStructureWorld structure;
	
	private NoiseGeneratorOctaves noiseGen1,noiseGen2,noiseGen3,noiseGen4,noiseGen5;
	private double[] noiseData1,noiseData2,noiseData3,noiseData4,noiseData5;
	private double[] densities; // TODO see if I can get rid of these somehow
	
	private int startX,startZ,islandBottomY;
	
	private IslandBiomeBase biome;
	private IslandBiomeData biomeData;
	
	/**
	 * Required for reflection.
	 */
	public ComponentScatteredFeatureIsland(){}
	
	protected ComponentScatteredFeatureIsland(Random rand, int x, int z){
		super(rand,x,20,z,208,140,208);
		this.startX = x;
		this.startZ = z;
		
		coordBaseMode = 0;
		boundingBox = new StructureBoundingBox(x,20,z,x+sizeX-1,140+sizeY-1,z+sizeZ-1);
	}
	
	private Offsets getOffsets(int x, int y, int z, StructureBoundingBox bb){
		int xx = getXWithOffset(x,z), yy = getYWithOffset(y), zz = getZWithOffset(x,z);
		return new Offsets(xx,yy,zz,bb.isVecInside(xx,yy,zz));
	}

	@Override
	public boolean addComponentParts(World world, Random rand, StructureBoundingBox bb){
		int centerX = sizeX>>1, centerZ = sizeZ>>1;
		
		if (!isSetup){
			structure = new LargeStructureWorld(this);
			
			Random consistentRand = new Random(((startX/9)*238504L+(startZ/9)*10058432215L)^world.getWorldInfo().getSeed());
			islandBottomY = 6+consistentRand.nextInt(25);
			biome = IslandBiomeBase.pickRandomBiome(consistentRand);
			biomeData = biome.generateData(consistentRand);
			
			noiseGen1 = new NoiseGeneratorOctaves(consistentRand,16);
			noiseGen2 = new NoiseGeneratorOctaves(consistentRand,16);
			noiseGen3 = new NoiseGeneratorOctaves(consistentRand,8);
			noiseGen4 = new NoiseGeneratorOctaves(consistentRand,10);
			noiseGen5 = new NoiseGeneratorOctaves(consistentRand,16);
			
			// PREGEN
			
			biome.prepareDecoration(biomeData);
			
			Block[] blocks;
			int chunkX, chunkZ, blockStartX, blockStartZ, xInChunk, zInChunk, xx, zz;
			float yMp = 0.66F*biome.getIslandMassHeightMultiplier(); // limit height a bit
			
			Stopwatch.time("IslandGen - terrain");
			
			for(chunkX = 0; chunkX < 13; chunkX++){
				for(chunkZ = 0; chunkZ < 13; chunkZ++){
					blockStartX = chunkX*16;
					blockStartZ = chunkZ*16;
					
					blocks = new Block[32768];
					generateTerrain(chunkX-6,chunkZ-6,blocks);

					for(xInChunk = 0; xInChunk < 16; ++xInChunk){
						for(zInChunk = 0; zInChunk < 16; ++zInChunk){
							xx = blockStartX+xInChunk;
							zz = blockStartZ+zInChunk;
							
							for(int yy = 0; yy < 128; ++yy){
								Block block = blocks[xInChunk<<11|zInChunk<<7|yy];
								if (block != Blocks.air)structure.setBlock(xx,(int)(yy*yMp),zz,block);
							}
							
							for(int y = structure.getHighestY(xx,zz); y >= 5; y--){
								if (structure.getBlock(xx,y,zz) == Blocks.end_stone && structure.isAir(xx,y+1,zz)){
									structure.setBlock(xx,y,zz,IslandBiomeBase.getTopBlock(),biome.getTopBlockMeta());
								}
							}
						}
					}
				}
			}
			
			Stopwatch.finish("IslandGen - terrain");
			Stopwatch.time("IslandGen - caves");
			
			CaveGenerator caveGen = new CaveGenerator(centerX,28,centerZ,halfSize-8,18,halfSize-8);
			caveGen.setup(consistentRand,biome);
			caveGen.generate(structure);
			
			Stopwatch.finish("IslandGen - caves");
			Stopwatch.time("IslandGen - ores");
			
			OreGenerator oreGen = new OreGenerator(8,0,8,size-8,55,size-8);
			oreGen.setup(consistentRand,biome);
			oreGen.generate(structure);
			
			Stopwatch.finish("IslandGen - ores");
			Stopwatch.time("IslandGen - biome content "+biomeData.content.id);
			
			biome.decorateGen(structure,consistentRand,centerX,centerZ);
			
			Stopwatch.finish("IslandGen - biome content "+biomeData.content.id);
			
			structure.setBlock(104,8,104,BlockList.biome_core,biomeData.content.id,true);
		
			isSetup = true;
		}

		Stopwatch.timeAverage("IslandGen - chunks",169);
		
		for(int chunkX = 0; chunkX < 13; chunkX++){
			for(int chunkZ = 0; chunkZ < 13; chunkZ++){
				structure.getChunkFromChunkCoords(chunkX,chunkZ).generateInStructure(this,world,bb,8,islandBottomY,8);
			}
		}
		
		Stopwatch.finish("IslandGen - chunks");
		
		return true;
	}
	
	public void generateTerrain(int x, int z, Block[] blocks){
		int noiseSizeXZ = 3;
		byte b1 = 33;
		densities = initializeNoiseField(densities,x*2,0,z*2,noiseSizeXZ,b1,noiseSizeXZ);

		for(int xx = 0; xx < 2; ++xx){
			for(int zz = 0; zz < 2; ++zz){
				for(int yy = 0; yy < 32; ++yy){
					double d0 = 0.25D;
					double d1 = densities[((xx+0)*noiseSizeXZ+zz+0)*b1+yy+0];
					double d2 = densities[((xx+0)*noiseSizeXZ+zz+1)*b1+yy+0];
					double d3 = densities[((xx+1)*noiseSizeXZ+zz+0)*b1+yy+0];
					double d4 = densities[((xx+1)*noiseSizeXZ+zz+1)*b1+yy+0];
					double d5 = (densities[((xx+0)*noiseSizeXZ+zz+0)*b1+yy+1]-d1)*d0;
					double d6 = (densities[((xx+0)*noiseSizeXZ+zz+1)*b1+yy+1]-d2)*d0;
					double d7 = (densities[((xx+1)*noiseSizeXZ+zz+0)*b1+yy+1]-d3)*d0;
					double d8 = (densities[((xx+1)*noiseSizeXZ+zz+1)*b1+yy+1]-d4)*d0;

					for(int yBlock1 = 0; yBlock1 < 4; ++yBlock1){
						double d9 = 0.125D;
						double d10 = d1;
						double d11 = d2;
						double d12 = (d3-d1)*d9;
						double d13 = (d4-d2)*d9;

						for(int yBlock2 = 0; yBlock2 < 8; ++yBlock2){
							int index = yBlock2+xx*8<<11|0+zz*8<<7|yy*4+yBlock1;
							double d15 = d10;
							double d16 = (d11-d10)*0.125D;

							for(int yBlock3 = 0; yBlock3 < 8; ++yBlock3){
								blocks[index] = d15 > 0D ? Blocks.end_stone : Blocks.air;
								index += 128;
								d15 += d16;
							}

							d10 += d12;
							d11 += d13;
						}

						d1 += d5;
						d2 += d6;
						d3 += d7;
						d4 += d8;
					}
				}
			}
		}
	}
	
	private double[] initializeNoiseField(double[] densities, int x, int y, int z, int sizeX, int sizeY, int sizeZ){
		if (densities == null)densities = new double[sizeX*sizeY*sizeZ];
		
		double noiseScaleXZ = 1368.824D * 2, noiseScaleY = 684.412D * 2;
		
		noiseData4 = noiseGen4.generateNoiseOctaves(noiseData4,x,z,sizeX,sizeZ,1.121D,1.121D,0.5D);
		noiseData5 = noiseGen5.generateNoiseOctaves(noiseData5,x,z,sizeX,sizeZ,200D,200D,0.5D);
		noiseData1 = noiseGen3.generateNoiseOctaves(noiseData1,x,y,z,sizeX,sizeY,sizeZ,noiseScaleXZ/80D,noiseScaleY/160D,noiseScaleXZ/80D);
		noiseData2 = noiseGen1.generateNoiseOctaves(noiseData2,x,y,z,sizeX,sizeY,sizeZ,noiseScaleXZ,noiseScaleY,noiseScaleXZ);
		noiseData3 = noiseGen2.generateNoiseOctaves(noiseData3,x,y,z,sizeX,sizeY,sizeZ,noiseScaleXZ,noiseScaleY,noiseScaleXZ);
		int index = 0;
		int l1 = 0;

		for(int xx = 0; xx < sizeX; ++xx){
			for(int zz = 0; zz < sizeZ; ++zz){
				double d2 = (noiseData4[l1]+256D)/512D;
				if (d2 > 1D)d2 = 1D;
				
				float islandSizeMp = 0.58F;
				float distanceX = (xx+x)/islandSizeMp;
				float distanceZ = (zz+z)/islandSizeMp;
				float f2 = (100F*biome.getIslandFillFactor())-MathHelper.sqrt_float(distanceX*distanceX+distanceZ*distanceZ)*8F;
				
				if (f2 > 80F)f2 = 80F;
				if (f2<-100F)f2 = -100F;

				if (d2 < 0D)d2 = 0D;

				d2 += 0.5D;
				++l1;
				double d4 = sizeY/2D;

				for(int yy = 0; yy < sizeY; ++yy){
					double d5 = 0D;
					double d6 = (yy-d4)*8D/d2;

					if (d6 < 0D)d6 *= -1D;

					double d7 = noiseData2[index]/512D;
					double d8 = noiseData3[index]/512D;
					double d9 = (noiseData1[index]/10D+1D)/2D;

					if (d9 < 0D)d5 = d7;
					else if (d9 > 1D)d5 = d8;
					else d5 = d7+(d8-d7)*d9;
					
					d5 -= 8D;
					d5 += f2;
					byte b0 = 2;
					double d10;

					if (yy > sizeY/2-b0){
						d10 = ((yy-(sizeY/2-b0))/(64F*biome.getIslandSurfaceHeightMultiplier()));

						if (d10 < 0D)d10 = 0D;
						if (d10 > 1D)d10 = 1D;

						d5 = d5*(1D-d10)+-3000D*d10;
					}

					b0 = 8;

					if (yy < b0){
						d10 = ((b0-yy)/(b0-1F));
						d5 = d5*(1D-d10)+-30D*d10;
					}

					densities[index] = d5;
					++index;
				}
			}
		}

		return densities;
	}

	@Override
	protected void func_143012_a(NBTTagCompound nbt){ // OBFUSCATED writeToNBT
		super.func_143012_a(nbt);
		nbt.setInteger("startX",startX);
		nbt.setInteger("startZ",startZ);
		nbt.setByte("bottomY",(byte)islandBottomY);
	}

	@Override
	protected void func_143011_b(NBTTagCompound nbt){ // OBFUSCATED readFromNBT
		super.func_143011_b(nbt);
		startX = nbt.getInteger("startX");
		startZ = nbt.getInteger("startZ");
		islandBottomY = nbt.getByte("bottomY");
	}
}
