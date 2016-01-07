package chylex.hee.world.structure.island.biome;
import net.minecraft.stats.Achievement;
import chylex.hee.block.BlockEndstoneTerrain;
import chylex.hee.game.achievements.AchievementManager;
import chylex.hee.world.structure.island.biome.data.AbstractBiomeInteraction.BiomeInteraction;
import chylex.hee.world.structure.island.biome.data.BiomeContentVariation;
import chylex.hee.world.structure.island.biome.data.BiomeRandomDeviation;
import chylex.hee.world.structure.island.biome.interaction.BiomeInteractionsInfestedForest.InteractionCollapsingTrees;

public class IslandBiomeInfestedForest extends IslandBiomeBase{
	public static final BiomeContentVariation DEEP = new BiomeContentVariation(0,8);
	public static final BiomeContentVariation RAVAGED = new BiomeContentVariation(3,6);
	public static final BiomeContentVariation RUINS = new BiomeContentVariation(4,3);
	
	public static final BiomeRandomDeviation TALL_TREES = new BiomeRandomDeviation("TallTrees", DEEP, RAVAGED);
	public static final BiomeRandomDeviation MORE_THORNY_BUSHES = new BiomeRandomDeviation("ThornyBushes", DEEP);
	
	//private final BiomeDecoratorInfestedForest decorator = new BiomeDecoratorInfestedForest();
	
	protected IslandBiomeInfestedForest(int biomeID){
		super(biomeID);

		contentVariations.add(DEEP);
		contentVariations.add(RAVAGED);
		
		randomDeviations.add(TALL_TREES);
		randomDeviations.add(MORE_THORNY_BUSHES);
		
		/*getSpawnEntries(DEEP).add(new SpawnEntry[]{
			new SpawnEntry(EntitySilverfish.class,35,35),
			new SpawnEntry(EntityMobInfestedBat.class,8,10)
		});*/
		
		getInteractions(DEEP).addAll(new BiomeInteraction[]{
			new BiomeInteraction("IF_Deep_CollapsingTrees",InteractionCollapsingTrees.class,40,2)
		});
	}
	
	/*@Override
	public void decorate(LargeStructureWorld world, Random rand, int centerX, int centerZ){
		if (data.content == DEEP)decorator.genDeep();
		else if (data.content == RAVAGED)decorator.genRavaged();
		else if (data.content == RUINS)decorator.genRuins();
	}*/
	
	@Override
	public float getIslandSurfaceHeightMultiplier(){
		return data.content == RUINS ? 0.9F : 0.7F;
	}
	
	@Override
	public float getIslandMassHeightMultiplier(){
		return data.content == RAVAGED ? 1.15F : 0.95F;
	}
	
	@Override
	public float getIslandFillFactor(){
		return data.content == RAVAGED ? 1.08F : 1.04F;
	}
	
	@Override
	public float getCaveAmountMultiplier(){
		return data.content == RAVAGED ? 0F : data.content == RUINS ? 0.85F : 1.15F;
	}
	
	@Override
	public float getCaveBranchingChance(){
		return data.content == RUINS ? 0.035F : 0.026F;
	}
	
	@Override
	public float getOreAmountMultiplier(){
		return data.content == RAVAGED ? 0.6F : 1F;
	}

	/*@Override
	protected IslandBiomeDecorator getDecorator(){
		return decorator;
	}*/
	
	@Override
	protected Achievement getAchievement(){
		return AchievementManager.BIOME_INFESTED_FOREST;
	}
	
	@Override
	public int getTopBlockMeta(){
		return BlockEndstoneTerrain.metaInfested;
	}
}