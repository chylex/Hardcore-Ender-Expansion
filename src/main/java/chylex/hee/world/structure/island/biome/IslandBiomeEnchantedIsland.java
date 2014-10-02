package chylex.hee.world.structure.island.biome;
import java.util.Random;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.world.World;
import chylex.hee.block.BlockEndstoneTerrain;
import chylex.hee.entity.mob.EntityMobBabyEnderman;
import chylex.hee.world.structure.island.biome.data.BiomeContentVariation;
import chylex.hee.world.structure.island.biome.data.BiomeRandomDeviation;
import chylex.hee.world.structure.island.biome.decorator.BiomeDecoratorEnchantedIsland;
import chylex.hee.world.structure.island.biome.decorator.IslandBiomeDecorator;
import chylex.hee.world.structure.util.pregen.LargeStructureWorld;
import chylex.hee.world.util.SpawnEntry;

public class IslandBiomeEnchantedIsland extends IslandBiomeBase{
	public static final BiomeContentVariation HOMELAND = new BiomeContentVariation(2,6);
	public static final BiomeContentVariation LABORATORY = new BiomeContentVariation(6,4);
	
	public static final BiomeRandomDeviation TALL_PILES = new BiomeRandomDeviation(HOMELAND);
	
	private final BiomeDecoratorEnchantedIsland decorator = new BiomeDecoratorEnchantedIsland();
	
	protected IslandBiomeEnchantedIsland(int biomeID){
		super(biomeID);
		
		contentVariations.add(HOMELAND);
		//contentVariations.add(LABORATORY);
		
		randomDeviations.add(TALL_PILES);
		
		getSpawnEntries(HOMELAND).addAll(new SpawnEntry[]{
			new SpawnEntry(EntityEnderman.class,22,38),
			new SpawnEntry(EntityMobBabyEnderman.class,14,20)
		});
		
		/*getSpawnEntries(LABORATORY).addAll(new SpawnEntry[]{
			new SpawnEntry(EntityEnderman.class,10,10),
			new SpawnEntry(EntityMobEnderGuardian.class,15,7)
		});*/
	}

	@Override
	protected void decorate(LargeStructureWorld world, Random rand, int centerX, int centerZ){
		if (data.content == HOMELAND)decorator.genHomeland();
		else if (data.content == LABORATORY)decorator.genLaboratory();
	}
	
	@Override
	public void updateCore(World world, int x, int y, int z, int meta){
		super.updateCore(world,x,y,z,meta);
		
		if (meta == HOMELAND.id){
			// TODO spawn overworld explorers occasionally
		}
	}
	
	@Override
	public float getIslandMassHeightMultiplier(){
		return 0.8F;
	}
	
	@Override
	public float getIslandFillFactor(){
		return 0.92F;
	}
	
	@Override
	public float getCaveAmountMultiplier(){
		return 0.45F;
	}
	
	@Override
	public float getCaveBranchingChance(){
		return 0.005F;
	}
	
	@Override
	public float getOreAmountMultiplier(){
		return 1.25F;
	}

	@Override
	protected IslandBiomeDecorator getDecorator(){
		return decorator;
	}
	
	@Override
	public int getTopBlockMeta(){
		return BlockEndstoneTerrain.metaEnchanted;
	}
}
