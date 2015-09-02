/*package chylex.hee.world.structure.island;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.init.BlockList;
import chylex.hee.system.collections.WeightedList;
import chylex.hee.system.commands.HeeDebugCommand.HeeTest;
import chylex.hee.system.logging.Stopwatch;
import chylex.hee.system.savedata.WorldDataHandler;
import chylex.hee.system.savedata.types.WorldGenSavefile;
import chylex.hee.system.savedata.types.WorldGenSavefile.WorldGenElement;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.world.structure.ComponentLargeStructureWorld;
import chylex.hee.world.structure.island.biome.IslandBiomeBase;
import chylex.hee.world.structure.island.biome.data.BiomeContentVariation;
import chylex.hee.world.structure.island.biome.data.IslandBiomeData;
import chylex.hee.world.structure.island.gen.CaveGenerator;
import chylex.hee.world.structure.island.gen.OreGenerator;
import chylex.hee.world.structure.island.gen.TerrainGenerator;

public class ComponentIsland extends ComponentLargeStructureWorld{
	public static final int size = 208, halfSize = size>>1;
	
	private IslandBiomeBase biome;
	private IslandBiomeData biomeData;
	
	/**
	 * Required for reflection.
	 *//*
	public ComponentIsland(){}
	
	public ComponentIsland(Random rand, int x, int z){
		super(rand,x,20,z,208,140,208);		
		coordBaseMode = 0;
		boundingBox = new StructureBoundingBox(x,20,z,x+sizeX-1,140+sizeY-1,z+sizeZ-1);
		WorldDataHandler.<WorldGenSavefile>get(WorldGenSavefile.class).addElementAt(getStartX()>>4,getStartZ()>>4,WorldGenElement.BIOME_ISLAND);
	}
	
	@Override
	protected int setupStructure(long seed){
		int centerX = sizeX>>1, centerZ = sizeZ>>1;
		
		Random consistentRand = new Random(((getStartX()/9)*238504L+(getStartZ()/9)*10058432215L)^seed);
		int islandBottomY = 6+consistentRand.nextInt(25);
		biome = IslandBiomeBase.pickRandomBiome(consistentRand);
		biomeData = biome.generateData(consistentRand);
		
		// PREGEN
		
		biome.prepareDecoration(biomeData);
		
		Block[] blocks;
		int chunkX, chunkZ, blockStartX, blockStartZ, xInChunk, zInChunk, xx, zz;
		float yMp = 0.66F*biome.getIslandMassHeightMultiplier(); // limit height a bit
		
		Stopwatch.time("ComponentIsland - total");
		Stopwatch.time("ComponentIsland - terrain");
		
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
		
		Stopwatch.finish("ComponentIsland - terrain");
		Stopwatch.time("ComponentIsland - caves");
		
		CaveGenerator caveGen = new CaveGenerator(centerX,28,centerZ,halfSize-8,24,halfSize-8);
		caveGen.setup(consistentRand,biome);
		caveGen.generate(structure);
		
		Stopwatch.finish("ComponentIsland - caves");
		Stopwatch.time("ComponentIsland - ores");
		
		OreGenerator oreGen = new OreGenerator(8,0,8,size-8,55,size-8);
		oreGen.setup(consistentRand,biome);
		oreGen.generate(structure,consistentRand);
		
		Stopwatch.finish("ComponentIsland - ores");
		Stopwatch.time("ComponentIsland - biome content "+biomeData.content.id);
		
		biome.decorateGen(structure,consistentRand,centerX,centerZ);
		
		Stopwatch.finish("ComponentIsland - biome content "+biomeData.content.id);
		structure.setBlock(104,8,104,BlockList.biome_core,biomeData.content.id,true);
		Stopwatch.finish("ComponentIsland - total");
		
		return islandBottomY;
	}
	
	public static final HeeTest $debugTest = new HeeTest(){
		@Override
		public void run(String...args){
			int variation = args.length == 0 ? -1 : DragonUtil.tryParse(args[0],-1);
			
			for(IslandBiomeBase biome:new ArrayList<IslandBiomeBase>(IslandBiomeBase.biomeList)){
				if (biome.isValidMetadata(variation)){
					List<IslandBiomeBase> prevBiomes = new ArrayList<>(IslandBiomeBase.biomeList);
					IslandBiomeBase.biomeList.clear();
					IslandBiomeBase.biomeList.add(biome);
					
					try{
						Field fieldVariations = IslandBiomeBase.class.getDeclaredField("contentVariations");
						fieldVariations.setAccessible(true);
						
						Object prevVariations = fieldVariations.get(biome);
						BiomeContentVariation variationInst = null;
						
						for(BiomeContentVariation var:(WeightedList<BiomeContentVariation>)prevVariations){
							if (var.id == variation){
								variationInst = var;
								break;
							}
						}
						
						if (variationInst != null){
							fieldVariations.set(biome,new WeightedList<BiomeContentVariation>(variationInst));
							generate();
							fieldVariations.set(biome,prevVariations);
							variation = -2;
						}
					}catch(NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e){
						e.printStackTrace();
					}
					
					IslandBiomeBase.biomeList.clear();
					IslandBiomeBase.biomeList.addAll(prevBiomes);
				}
			}
			
			if (variation == -1)generate();
		}
		
		private void generate(){
			ComponentIsland island = new ComponentIsland(world.rand,(int)player.posX-104,(int)player.posZ-104);
			island.addComponentParts(world,world.rand,new StructureBoundingBox(-9999999,0,-9999999,9999999,128,9999999));
			HardcoreEnderExpansion.notifications.report("Generated island: "+island.biomeData.getDeviationsAsString(),true);
		}
	};
}
*/