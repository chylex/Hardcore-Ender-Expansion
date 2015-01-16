package chylex.hee.world.structure.island.biome;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import chylex.hee.block.BlockEndstoneTerrain;
import chylex.hee.entity.fx.FXType;
import chylex.hee.entity.mob.EntityMobBabyEnderman;
import chylex.hee.entity.mob.EntityMobEnderGuardian;
import chylex.hee.entity.mob.EntityMobEndermage;
import chylex.hee.entity.mob.EntityMobEnderman;
import chylex.hee.entity.mob.EntityMobHomelandEnderman;
import chylex.hee.mechanics.misc.HomelandEndermen.HomelandRole;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C21EffectEntity;
import chylex.hee.world.structure.island.ComponentIsland;
import chylex.hee.world.structure.island.biome.data.AbstractBiomeInteraction.BiomeInteraction;
import chylex.hee.world.structure.island.biome.data.BiomeContentVariation;
import chylex.hee.world.structure.island.biome.data.BiomeRandomDeviation;
import chylex.hee.world.structure.island.biome.decorator.BiomeDecoratorEnchantedIsland;
import chylex.hee.world.structure.island.biome.decorator.IslandBiomeDecorator;
import chylex.hee.world.structure.island.biome.interaction.BiomeInteractionEnchantedIsland.InteractionCellarSounds;
import chylex.hee.world.structure.island.biome.interaction.BiomeInteractionEnchantedIsland.InteractionOvertake;
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
		contentVariations.add(LABORATORY);
		
		randomDeviations.add(TALL_PILES);
		
		getSpawnEntries(HOMELAND).addAll(new SpawnEntry[]{
			new SpawnEntry(EntityMobEnderman.class,22,38),
			new SpawnEntry(EntityMobBabyEnderman.class,14,20)
		});
		
		getSpawnEntries(LABORATORY).addAll(new SpawnEntry[]{
			new SpawnEntry(EntityMobEndermage.class,6,15),
			new SpawnEntry(EntityMobEnderman.class,12,10),
			new SpawnEntry(EntityMobEnderGuardian.class,20,7)
		});
		
		getInteractions(HOMELAND).addAll(new BiomeInteraction[]{
			new BiomeInteraction("EI_Homeland_Overtake",InteractionOvertake.class,50,1),
			new BiomeInteraction("EI_Homeland_CellarSounds",InteractionCellarSounds.class,10,20)
		});
		
		getInteractions(LABORATORY).addAll(new BiomeInteraction[]{
			new BiomeInteraction("EI_Laboratory_CellarSounds",InteractionCellarSounds.class,10,20)
		});
	}

	@Override
	protected void decorate(LargeStructureWorld world, Random rand, int centerX, int centerZ){
		if (data.content == HOMELAND)decorator.genHomeland();
		else if (data.content == LABORATORY)decorator.genLaboratory();
	}
	
	@Override
	public void updateCore(World world, int x, int y, int z, int meta){
		super.updateCore(world,x,y,z,meta);
		
		if (meta == HOMELAND.id && world.rand.nextInt(40) == 0 && world.difficultySetting != EnumDifficulty.PEACEFUL){
			AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(x-ComponentIsland.halfSize,10,z-ComponentIsland.halfSize,x+ComponentIsland.halfSize,128,z+ComponentIsland.halfSize);
			
			List<EntityMobHomelandEnderman> all = world.getEntitiesWithinAABB(EntityMobHomelandEnderman.class,aabb);
			if (all.size() > 15+world.rand.nextInt(50))return;
			
			for(EntityMobHomelandEnderman enderman:all){
				if (enderman.getHomelandRole() == HomelandRole.ISLAND_LEADERS && enderman.attackedRecentlyTimer == 0){
					List<EntityMobBabyEnderman> babies = world.getEntitiesWithinAABB(EntityMobBabyEnderman.class,aabb);
					
					if (!babies.isEmpty()){
						EntityMobBabyEnderman chosenOne = babies.get(world.rand.nextInt(babies.size()));
						chosenOne.setDead();
						
						if (chosenOne.isCarrying())world.spawnEntityInWorld(new EntityItem(world,chosenOne.posX,chosenOne.posY,chosenOne.posZ,chosenOne.getCarrying()));
						
						EntityMobHomelandEnderman grown = new EntityMobHomelandEnderman(world);
						grown.copyLocationAndAnglesFrom(chosenOne);
						grown.setHomelandRole(HomelandRole.getRandomRole(world.rand));
						world.spawnEntityInWorld(grown);
						
						PacketPipeline.sendToAllAround(grown,64D,new C21EffectEntity(FXType.Entity.BABY_ENDERMAN_GROW,grown));
					}
					
					break;
				}
			}
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
