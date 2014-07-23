package chylex.hee.world.structure.island.biome.decorator;
import net.minecraft.block.BlockFalling;
import chylex.hee.block.BlockList;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.structure.island.biome.IslandBiomeBase;
import chylex.hee.world.structure.island.feature.StructureIslandLake;
import chylex.hee.world.structure.island.feature.StructureIslandStash;

public class BiomeDecoratorEnchantedIsland extends IslandBiomeDecorator{
	private static IslandBiomeBase getBiome(){
		return IslandBiomeBase.enchantedIsland;
	}
	
	/*
	 * HOMELAND
	 */
	
	public void genHomeland(){ // TODO temp
		StructureIslandLake lake = new StructureIslandLake();
		
		for(int attempt = 0, placed = 0, amount = rand.nextInt(3)+5; attempt < 170 && placed < amount; attempt++){
			if (generateStructure(lake,getBiome()))++placed;
		}
		
		StructureIslandStash stash = new StructureIslandStash();
		
		for(int cx = 0; cx < world.getChunkAmountX(); cx++){
			for(int cz = 0; cz < world.getChunkAmountZ(); cz++){	
				if (rand.nextInt(5) <= 2){
					int height = rand.nextInt(14)+(/*hasRareVariation(RareVariationIsland.TALL_PILLARS)?6+rand.nextInt(8):*/4);
					int radius = rand.nextInt(2)+1;
					
					int ox = cx*16+rand.nextInt(16),
						oz = cz*16+rand.nextInt(16),
						oy = world.getHighestY(ox,oz);
					
					if (oy <= 0)return;
					
					for(int xx = ox-radius; xx <= ox+radius; ++xx){
						for(int zz = oz-radius; zz <= oz+radius; ++zz){
							if (MathUtil.square(xx-ox)+MathUtil.square(zz-oz) <= radius*radius+1){
								if (Math.abs(world.getHighestY(xx,zz)-oy) > 2)return;
							}
						}
					}
					
					BlockFalling.fallInstantly = true;
					
					for(int xx = ox-radius; xx <= ox+radius; ++xx){
						for(int zz = oz-radius; zz <= oz+radius; ++zz){
							for(int yy = world.getHighestY(xx,zz); yy < oy+height && yy < 128; ++yy){
								if (MathUtil.square(xx-ox)+MathUtil.square(zz-oz) <= radius*radius+0.5D+rand.nextGaussian()*0.7D){
									world.setBlock(xx,yy,zz,BlockList.obsidian_end,0,true);
								}
							}
						}
					}
					
					BlockFalling.fallInstantly = false;
				}
				
				if (rand.nextInt(14) <= 3)generateStructure(stash,getBiome());
			}
		}
	}
}
