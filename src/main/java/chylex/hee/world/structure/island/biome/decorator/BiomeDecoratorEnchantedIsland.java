package chylex.hee.world.structure.island.biome.decorator;
import chylex.hee.block.BlockList;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.structure.island.biome.IslandBiomeBase;
import chylex.hee.world.structure.island.biome.IslandBiomeEnchantedIsland;
import chylex.hee.world.structure.island.biome.feature.island.StructureGooLake;
import chylex.hee.world.structure.island.biome.feature.island.StructureObsidianRoad;

public class BiomeDecoratorEnchantedIsland extends IslandBiomeDecorator{
	@Override
	protected final IslandBiomeBase getBiome(){
		return IslandBiomeBase.enchantedIsland;
	}
	
	private final StructureGooLake genGooLake = new StructureGooLake();
	private final StructureObsidianRoad genRoads = new StructureObsidianRoad();
	
	/*
	 * HOMELAND
	 */
	
	public void genHomeland(){
		// GOO LAKES
		for(int attempt = 0, placed = 0, amount = rand.nextInt(3)+5; attempt < 170 && placed < amount; attempt++){
			if (generateStructure(genGooLake))++placed;
		}
		
		// OBSIDIAN PILES
		for(int cx = 0; cx < world.getChunkAmountX(); cx++){
			for(int cz = 0; cz < world.getChunkAmountZ(); cz++){	
				if (rand.nextInt(5) <= 2){
					int height = rand.nextInt(14)+(data.hasDeviation(IslandBiomeEnchantedIsland.TALL_PILES) ? 6+rand.nextInt(8) : 4);
					int radius = rand.nextInt(2)+1;
					
					int ox = cx*16+rand.nextInt(16),
						oz = cz*16+rand.nextInt(16),
						oy = world.getHighestY(ox,oz);
					
					if (oy > 0){
						boolean generateObsidian = true;
					
						for(int xx = ox-radius; xx <= ox+radius && generateObsidian; ++xx){
							for(int zz = oz-radius; zz <= oz+radius && generateObsidian; ++zz){
								if (MathUtil.square(xx-ox)+MathUtil.square(zz-oz) <= radius*radius+1){
									if (Math.abs(world.getHighestY(xx,zz)-oy) > 2)generateObsidian = false;
								}
							}
						}
						
						if (generateObsidian){
							for(int xx = ox-radius; xx <= ox+radius; ++xx){
								for(int zz = oz-radius; zz <= oz+radius; ++zz){
									for(int yy = world.getHighestY(xx,zz)+1; yy < oy+height && yy < 128; ++yy){
										if (MathUtil.square(xx-ox)+MathUtil.square(zz-oz) <= radius*radius+0.5D+rand.nextGaussian()*0.7D){
											world.setBlock(xx,yy,zz,BlockList.obsidian_falling,0,true);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		// OBSIDIAN ROADS
		for(int attempt = 0, placed = 0, placedMax = 8+rand.nextInt(5); attempt < 36 && placed < placedMax; attempt++){
			if (generateStructure(genRoads))++placed;
		}
	}
	
	/*
	 * LABORATORY
	 */
	
	public void genLaboratory(){
		
	}
}
