package chylex.hee.world.structure.island.biome.decorator;
import gnu.trove.map.hash.TObjectIntHashMap;
import java.util.List;
import chylex.hee.block.BlockList;
import chylex.hee.entity.mob.EntityMobHomelandEnderman;
import chylex.hee.mechanics.misc.HomelandEndermen.HomelandRole;
import chylex.hee.mechanics.misc.HomelandEndermen.OvertakeGroupRole;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.structure.island.ComponentIsland;
import chylex.hee.world.structure.island.biome.IslandBiomeBase;
import chylex.hee.world.structure.island.biome.IslandBiomeEnchantedIsland;
import chylex.hee.world.structure.island.biome.feature.island.StructureGooLake;
import chylex.hee.world.structure.island.biome.feature.island.StructureHiddenCellar;
import chylex.hee.world.structure.island.biome.feature.island.StructureObsidianRoad;
import chylex.hee.world.structure.island.biome.feature.island.StructureHiddenCellar.EnchantedIslandVariation;

public class BiomeDecoratorEnchantedIsland extends IslandBiomeDecorator{
	@Override
	protected final IslandBiomeBase getBiome(){
		return IslandBiomeBase.enchantedIsland;
	}
	
	private final StructureGooLake genGooLake = new StructureGooLake();
	private final StructureObsidianRoad genRoads = new StructureObsidianRoad();
	private final StructureHiddenCellar genCellar = new StructureHiddenCellar();
	
	/*
	 * HOMELAND
	 */
	
	public void genHomeland(){
		// HIDDEN CELLAR
		genCellar.setVariation(EnchantedIslandVariation.HOMELAND);
		
		for(int attempt = 0, placed = 0; attempt < 20 && placed < 4; attempt++){
			if (genCellar.generateInWorld(world,rand,getBiome()))++placed;
		}
		
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
		
		// HOMELAND ENDERMEN
		TObjectIntHashMap<HomelandRole> map = new TObjectIntHashMap<>();
		
		for(int spawnAttempt = 0, spawnedTotal = 52+rand.nextInt(28)+rand.nextInt(16); spawnAttempt < spawnedTotal; spawnAttempt++){
			EntityMobHomelandEnderman enderman = new EntityMobHomelandEnderman(null);
			
			HomelandRole role = HomelandRole.getRandomRole(rand);
			enderman.setHomelandRole(role);
			map.adjustOrPutValue(role,1,1);
			
			for(int posAttempt = 0, xx, yy, zz; posAttempt < 30; posAttempt++){
				xx = rand.nextInt(ComponentIsland.size-40)+20;
				zz = rand.nextInt(ComponentIsland.size-40)+20;
				yy = world.getHighestY(xx,zz);
				
				if (world.getBlock(xx,yy,zz) == topBlock){
					enderman.setPosition(xx,yy+1,zz);
					world.addEntity(enderman);
					break;
				}
			}
		}
		
		List<EntityMobHomelandEnderman> endermanList = world.getAllEntities(EntityMobHomelandEnderman.class);
		int size = endermanList.size();
		
		if (size > 0){
			for(int leaders = 1+rand.nextInt(3+rand.nextInt(3)); leaders > 0 && size > 0; leaders--){
				endermanList.remove(rand.nextInt(size--)).setHomelandRole(HomelandRole.ISLAND_LEADERS);
			}
		}
		
		for(int groupLeaders = rand.nextInt(3+rand.nextInt(3)*rand.nextInt(2)); groupLeaders > 0 && size > 0; groupLeaders--){
			long groupId = endermanList.remove(rand.nextInt(size--)).setNewGroupLeader();
			if (groupId == -1)continue;
			
			for(int state = rand.nextBoolean() ? rand.nextInt(1+rand.nextInt(4+rand.nextInt(8))) : 0; state > 0 && size > 0; state--){
				endermanList.remove(rand.nextInt(size--)).setGroupMember(groupId,OvertakeGroupRole.getRandomMember(rand));
			}
		}
	}
	
	/*
	 * LABORATORY
	 */
	
	public void genLaboratory(){
		// HIDDEN CELLAR
		genCellar.setVariation(EnchantedIslandVariation.LABORATORY);
		
		for(int attempt = 0, placed = 0; attempt < 20 && placed < 4; attempt++){
			if (genCellar.generateInWorld(world,rand,getBiome()))++placed;
		}
	}
}
