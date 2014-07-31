package chylex.hee.world.structure.island.biome.decorator;
import net.minecraft.init.Blocks;
import chylex.hee.block.BlockCrossedDecoration;
import chylex.hee.block.BlockList;
import chylex.hee.world.structure.island.biome.IslandBiomeBase;
import chylex.hee.world.structure.island.biome.feature.mountains.StructureIgneousRockOre;
import chylex.hee.world.structure.island.biome.feature.mountains.StructureLavaPool;
import chylex.hee.world.structure.island.biome.feature.mountains.StructureResourcePit;

public class BiomeDecoratorBurningMountains extends IslandBiomeDecorator{
	private static IslandBiomeBase getBiome(){
		return IslandBiomeBase.burningMountains;
	}
	
	private final StructureIgneousRockOre genIgneousRockOre = new StructureIgneousRockOre();
	private final StructureLavaPool genLavaPool = new StructureLavaPool();
	private final StructureResourcePit genResourcePit = new StructureResourcePit();
	
	/*
	 * SCORCHING
	 */
	
	public void genScorching(){
		// IGNEOUS ROCK ORE
		generateStructure(genIgneousRockOre.setAttemptAmount(120),getBiome());
		
		// SINGLE LAVA BLOCKS
		for(int a = 0; a < 6500; a++){
			int xx = getRandomXZ(rand,32), zz = getRandomXZ(rand,32), yy = 10+rand.nextInt(65);
			
			if (world.getBlock(xx,yy,zz) == Blocks.end_stone && (world.isAir(xx+1,yy,zz) || world.isAir(xx-1,yy,zz) || world.isAir(xx,yy,zz+1) || world.isAir(xx,yy,zz-1))){
				world.setBlock(xx,yy,zz,Blocks.flowing_lava,0,true);
			}
		}
		
		// LAVA POOLS
		for(int attempt = 0, placed = 0, placedMax = 3+rand.nextInt(10); attempt < 450 && placed < placedMax; attempt++){
			if (generateStructure(genLavaPool,getBiome()))++placed;
		}
		
		// LILYFIRES
		for(int cx = 0; cx < world.getChunkAmountX(); cx++){
			for(int cz = 0; cz < world.getChunkAmountZ(); cz++){
				if (rand.nextInt(4) <= 1){
					for(int a = 0; a < 1+rand.nextInt(6)+rand.nextInt(5); a++){
						int xx = cx*16+rand.nextInt(16), zz = cz*16+rand.nextInt(16), yy = world.getHighestY(xx,zz);
						if (world.getBlock(xx,yy,zz) == BlockList.end_terrain){
							world.setBlock(xx,yy+1,zz,BlockList.crossed_decoration,BlockCrossedDecoration.dataLilyFire);
						}
					}
				}
			}
		}
	}
	
	/*
	 * MINE
	 */
	
	public void genMine(){
		// IGNEOUS ROCK ORE
		generateStructure(genIgneousRockOre.setAttemptAmount(160),getBiome());
	}
}
