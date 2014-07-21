package chylex.hee.world.structure.island.biome.decorator;
import net.minecraft.init.Blocks;
import chylex.hee.block.BlockCrossedDecoration;
import chylex.hee.block.BlockList;
import chylex.hee.world.structure.island.biome.IslandBiomeBase;
import chylex.hee.world.structure.island.feature.StructureMountainIgneousOre;

public class BiomeDecoratorBurningMountains extends IslandBiomeDecorator{
	private static IslandBiomeBase getBiome(){
		return IslandBiomeBase.burningMountains;
	}
	
	/*
	 * SCORCHING
	 */
	
	public void genScorching(){
		generateStructure(new StructureMountainIgneousOre().setAttemptAmount(120),getBiome());
		
		for(int a = 0; a < 80; a++){
			int xx = getRandomXZ(rand,1) ,zz = getRandomXZ(rand,1), yy = 26+rand.nextInt(40);
			
			if (world.getBlock(xx,yy,zz) == Blocks.end_stone && (world.isAir(xx+1,yy,zz) || world.isAir(xx-1,yy,zz) || world.isAir(xx,yy,zz+1) || world.isAir(xx,yy,zz-1))){
				world.setBlock(xx,yy,zz,Blocks.flowing_lava,0,true);
			}
		}
		
		for(int cx = 0; cx < world.getChunkAmountX(); cx++){
			for(int cz = 0; cz < world.getChunkAmountZ(); cz++){
				if (rand.nextInt(4) <= 1){
					for(int a = 0; a < 1+rand.nextInt(6)+rand.nextInt(5); a++){
						int xx = cx*16+rand.nextInt(16),zz = cz*16+rand.nextInt(16), yy = world.getHighestY(xx,zz);
						if (world.getBlock(xx,yy,zz) == BlockList.end_terrain){
							world.setBlock(xx,yy,zz,BlockList.crossed_decoration,BlockCrossedDecoration.dataLilyFire);
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
		generateStructure(new StructureMountainIgneousOre().setAttemptAmount(160),getBiome());
	}
}
