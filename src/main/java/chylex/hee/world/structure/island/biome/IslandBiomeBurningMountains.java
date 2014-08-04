package chylex.hee.world.structure.island.biome;
import java.util.Random;
import chylex.hee.block.BlockEndstoneTerrain;
import chylex.hee.entity.mob.EntityMobFireGolem;
import chylex.hee.entity.mob.EntityMobScorchingLens;
import chylex.hee.mechanics.knowledge.data.KnowledgeRegistration;
import chylex.hee.world.structure.island.biome.data.BiomeContentVariation;
import chylex.hee.world.structure.island.biome.decorator.BiomeDecoratorBurningMountains;
import chylex.hee.world.structure.island.biome.decorator.IslandBiomeDecorator;
import chylex.hee.world.structure.util.pregen.LargeStructureWorld;
import chylex.hee.world.util.SpawnEntry;

public class IslandBiomeBurningMountains extends IslandBiomeBase{
	public static final BiomeContentVariation SCORCHING = new BiomeContentVariation(1,8);
	public static final BiomeContentVariation MINE = new BiomeContentVariation(5,5);
	
	private final BiomeDecoratorBurningMountains decorator = new BiomeDecoratorBurningMountains();
	
	protected IslandBiomeBurningMountains(int biomeID, KnowledgeRegistration knowledgeRegistration){
		super(biomeID,knowledgeRegistration);
		
		//contentVariations.add(SCORCHING);
		contentVariations.add(MINE);
		
		getSpawnEntries(SCORCHING).addAll(new SpawnEntry[]{
			new SpawnEntry(EntityMobFireGolem.class,14,10),
			new SpawnEntry(EntityMobScorchingLens.class,10,6)
		});
	}

	@Override
	protected void decorate(LargeStructureWorld world, Random rand, int centerX, int centerZ){
		if (data.content == SCORCHING)decorator.genScorching();
		else if (data.content == MINE)decorator.genMine();
	}
	
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

	@Override
	protected IslandBiomeDecorator getDecorator(){
		return decorator;
	}
	
	@Override
	public int getTopBlockMeta(){
		return BlockEndstoneTerrain.metaBurned;
	}
}
