package chylex.hee.world.structure.island.biome;
import java.util.List;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import chylex.hee.block.BlockBiomeIslandCore.Biome;
import chylex.hee.block.BlockEndstoneTerrain;
import chylex.hee.block.BlockList;
import chylex.hee.entity.mob.EntityMobInfestedBat;
import chylex.hee.system.savedata.WorldDataHandler;
import chylex.hee.system.savedata.types.InfestationSavefile;
import chylex.hee.system.util.BlockPosM;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.structure.island.biome.data.AbstractBiomeInteraction.BiomeInteraction;
import chylex.hee.world.structure.island.biome.data.BiomeContentVariation;
import chylex.hee.world.structure.island.biome.data.BiomeRandomDeviation;
import chylex.hee.world.structure.island.biome.decorator.BiomeDecoratorInfestedForest;
import chylex.hee.world.structure.island.biome.decorator.IslandBiomeDecorator;
import chylex.hee.world.structure.island.biome.interaction.BiomeInteractionsInfestedForest.InteractionCollapsingTrees;
import chylex.hee.world.structure.util.pregen.LargeStructureWorld;
import chylex.hee.world.util.SpawnEntry;

public class IslandBiomeInfestedForest extends IslandBiomeBase{
	public static final BiomeContentVariation DEEP = new BiomeContentVariation(Biome.INFESTED_FOREST_DEEP.ordinal(), 8);
	public static final BiomeContentVariation RAVAGED = new BiomeContentVariation(Biome.INFESTED_FOREST_RAVAGED.ordinal(), 6);
	public static final BiomeContentVariation RUINS = new BiomeContentVariation(Biome.INFESTED_FOREST_RUINS.ordinal(), 3);
	
	public static final BiomeRandomDeviation TALL_TREES = new BiomeRandomDeviation(DEEP, RAVAGED);
	public static final BiomeRandomDeviation MORE_THORNY_BUSHES = new BiomeRandomDeviation(DEEP);
	
	private final BiomeDecoratorInfestedForest decorator = new BiomeDecoratorInfestedForest();
	
	protected IslandBiomeInfestedForest(int biomeID){
		super(biomeID);

		contentVariations.add(DEEP);
		contentVariations.add(RAVAGED);
		
		randomDeviations.add(TALL_TREES);
		randomDeviations.add(MORE_THORNY_BUSHES);
		
		getSpawnEntries(DEEP).addAll(new SpawnEntry[]{
			new SpawnEntry(EntitySilverfish.class,35,35),
			new SpawnEntry(EntityMobInfestedBat.class,8,10)
		});
		
		getInteractions(DEEP).addAll(new BiomeInteraction[]{
			new BiomeInteraction("IF_Deep_CollapsingTrees",InteractionCollapsingTrees.class,40,2)
		});
	}
	
	@Override
	public void decorate(LargeStructureWorld world, Random rand, int centerX, int centerZ){
		if (data.content == DEEP)decorator.genDeep();
		else if (data.content == RAVAGED)decorator.genRavaged();
		else if (data.content == RUINS)decorator.genRuins();
	}
	
	@Override
	public void updateCore(World world, BlockPosM pos, int meta){
		super.updateCore(world,pos,meta);
		
		for(EntityPlayer player:(List<EntityPlayer>)world.playerEntities){
			if (world.rand.nextInt(5) <= 2)continue;
			
			int xx = MathUtil.floor(player.posX), yy = MathUtil.floor(player.posY), zz = MathUtil.floor(player.posZ);
			boolean found = false;
			BlockPosM testPos = new BlockPosM();
			
			for(int testY = yy-2; testY <= yy+1 && !found; testY++){
				for(int testX = xx-1; testX <= xx+1 && !found; testX++){
					for(int testZ = zz-1; testZ <= zz+1; testZ++){
						IBlockState state = testPos.moveTo(testX,testY,testZ).getBlockState(world);
						
						if (state.getBlock() == BlockList.end_terrain && (BlockEndstoneTerrain.Variant)state.getValue(BlockEndstoneTerrain.VARIANT) == getTopBlockVariant()){
							WorldDataHandler.<InfestationSavefile>get(InfestationSavefile.class).increaseInfestationPower(player);
							found = true;
							break;
						}
					}
				}
			}
		}
	}
	
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

	@Override
	protected IslandBiomeDecorator getDecorator(){
		return decorator;
	}
	
	@Override
	protected Achievement getAchievement(){
		return AchievementManager.BIOME_INFESTED_FOREST;
	}
	
	@Override
	public BlockEndstoneTerrain.Variant getTopBlockVariant(){
		return BlockEndstoneTerrain.Variant.INFESTED;
	}
}