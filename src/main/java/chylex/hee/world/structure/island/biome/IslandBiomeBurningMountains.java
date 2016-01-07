package chylex.hee.world.structure.island.biome;
import net.minecraft.stats.Achievement;
import chylex.hee.block.BlockEndstoneTerrain;
import chylex.hee.game.achievements.AchievementManager;
import chylex.hee.world.structure.island.biome.data.BiomeContentVariation;
import chylex.hee.world.structure.island.biome.data.BiomeRandomDeviation;

public class IslandBiomeBurningMountains extends IslandBiomeBase{
	public static final BiomeContentVariation SCORCHING = new BiomeContentVariation(1,8);
	public static final BiomeContentVariation MINE = new BiomeContentVariation(5,6);
	
	public static final BiomeRandomDeviation EXCESSIVE_CINDER = new BiomeRandomDeviation("ExcessiveCinder", SCORCHING);
	public static final BiomeRandomDeviation SINGLE_LAVA_ONLY = new BiomeRandomDeviation("SingleLava", SCORCHING);
	public static final BiomeRandomDeviation LIMITED_ORES = new BiomeRandomDeviation("LimitedOres", MINE);
	public static final BiomeRandomDeviation DEEP_RESOURCE_PITS = new BiomeRandomDeviation("DeepResPits", MINE);
	
	//private final BiomeDecoratorBurningMountains decorator = new BiomeDecoratorBurningMountains();
	
	protected IslandBiomeBurningMountains(int biomeID){
		super(biomeID);
		
		contentVariations.add(SCORCHING);
		contentVariations.add(MINE);
		
		randomDeviations.add(EXCESSIVE_CINDER);
		randomDeviations.add(SINGLE_LAVA_ONLY);
		randomDeviations.add(LIMITED_ORES);
		randomDeviations.add(DEEP_RESOURCE_PITS);
		
		/*getSpawnEntries(SCORCHING).add(new SpawnEntry[]{
			new SpawnEntry(EntityMobFireGolem.class,14,10),
			new SpawnEntry(EntityMobScorchingLens.class,10,6)
		});
		
		getSpawnEntries(MINE).add(new SpawnEntry[]{
			new SpawnEntry(EntityMobHauntedMiner.class,20,10)
		});*/
	}

	/*@Override
	protected void decorate(LargeStructureWorld world, Random rand, int centerX, int centerZ){
		if (data.content == SCORCHING)decorator.genScorching();
		else if (data.content == MINE)decorator.genMine();
	}*/
	
	@Override
	public float getIslandSurfaceHeightMultiplier(){
		return data.content == SCORCHING ? 6.5F : 7.5F;
	}
	
	@Override
	public float getIslandMassHeightMultiplier(){
		return data.content == SCORCHING ? 0.9F : 1F;
	}
	
	@Override
	public float getCaveAmountMultiplier(){
		return data.content == MINE ? 2F : 1.6F;
	}
	
	@Override
	public float getCaveBranchingChance(){
		return data.content == MINE ? 0.05F : 0.035F;
	}
	
	@Override
	public float getOreAmountMultiplier(){
		return data.content == MINE ? 1.4F : 1.15F;
	}

	/*@Override
	protected IslandBiomeDecorator getDecorator(){
		return decorator;
	}*/
	
	@Override
	protected Achievement getAchievement(){
		return AchievementManager.BIOME_BURNING_MOUNTAINS;
	}
	
	@Override
	public int getTopBlockMeta(){
		return BlockEndstoneTerrain.metaBurned;
	}
}
