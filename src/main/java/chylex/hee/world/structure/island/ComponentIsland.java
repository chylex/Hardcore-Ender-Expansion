package chylex.hee.world.structure.island;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import chylex.hee.block.BlockList;
import chylex.hee.system.logging.Stopwatch;
import chylex.hee.world.structure.ComponentScatteredFeatureCustom;
import chylex.hee.world.structure.island.biome.IslandBiomeBase;
import chylex.hee.world.structure.island.biome.data.IslandBiomeData;
import chylex.hee.world.structure.island.gen.CaveGenerator;
import chylex.hee.world.structure.island.gen.OreGenerator;
import chylex.hee.world.structure.island.gen.TerrainGenerator;
import chylex.hee.world.structure.util.Offsets;
import chylex.hee.world.structure.util.pregen.LargeStructureWorld;

public class ComponentIsland extends ComponentScatteredFeatureCustom{
	public static final int size = 208, halfSize = size>>1;
	
	private boolean isSetup = false;
	
	private final LargeStructureWorld structure;
	private IslandBiomeBase biome;
	private IslandBiomeData biomeData;
	
	private int startX,startZ,islandBottomY;
	
	/**
	 * Required for reflection.
	 */
	public ComponentIsland(){
		structure = new LargeStructureWorld(null);
	}
	
	protected ComponentIsland(Random rand, int x, int z){
		super(rand,x,20,z,208,140,208);
		this.startX = x;
		this.startZ = z;
		
		coordBaseMode = 0;
		boundingBox = new StructureBoundingBox(x,20,z,x+sizeX-1,140+sizeY-1,z+sizeZ-1);
		structure = new LargeStructureWorld(this);
	}
	
	private Offsets getOffsets(int x, int y, int z, StructureBoundingBox bb){
		int xx = getXWithOffset(x,z), yy = getYWithOffset(y), zz = getZWithOffset(x,z);
		return new Offsets(xx,yy,zz,bb.isVecInside(xx,yy,zz));
	}

	@Override
	public boolean addComponentParts(World world, Random rand, StructureBoundingBox bb){
		int centerX = sizeX>>1, centerZ = sizeZ>>1;
		
		if (!isSetup){
			Random consistentRand = new Random(((startX/9)*238504L+(startZ/9)*10058432215L)^world.getWorldInfo().getSeed());
			islandBottomY = 6+consistentRand.nextInt(25);
			biome = IslandBiomeBase.pickRandomBiome(consistentRand);
			biomeData = biome.generateData(consistentRand);
			
			// PREGEN
			
			biome.prepareDecoration(biomeData);
			
			Block[] blocks;
			int chunkX, chunkZ, blockStartX, blockStartZ, xInChunk, zInChunk, xx, zz;
			float yMp = 0.66F*biome.getIslandMassHeightMultiplier(); // limit height a bit
			
			Stopwatch.time("IslandGen - terrain");
			
			TerrainGenerator terrainGen = new TerrainGenerator(consistentRand,biome);
			
			for(chunkX = 0; chunkX < 13; chunkX++){
				for(chunkZ = 0; chunkZ < 13; chunkZ++){
					blockStartX = chunkX*16;
					blockStartZ = chunkZ*16;
					
					blocks = new Block[32768];
					terrainGen.generateTerrain(chunkX-6,chunkZ-6,blocks);

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
			
			CaveGenerator caveGen = new CaveGenerator(centerX,28,centerZ,halfSize-8,24,halfSize-8);
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

	@Override
	protected void func_143012_a(NBTTagCompound nbt){ // OBFUSCATED writeToNBT
		super.func_143012_a(nbt);
		nbt.setInteger("startX",startX);
		nbt.setInteger("startZ",startZ);
		nbt.setByte("bottomY",(byte)islandBottomY);
		nbt.setTag("structure",structure.saveToNBT());
	}

	@Override
	protected void func_143011_b(NBTTagCompound nbt){ // OBFUSCATED readFromNBT
		super.func_143011_b(nbt);
		startX = nbt.getInteger("startX");
		startZ = nbt.getInteger("startZ");
		islandBottomY = nbt.getByte("bottomY");
		structure.loadFromNBT(nbt.getCompoundTag("structure"));
	}
}
