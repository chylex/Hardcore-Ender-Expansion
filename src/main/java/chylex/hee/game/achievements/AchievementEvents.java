package chylex.hee.game.achievements;
import java.util.UUID;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import chylex.hee.init.ItemList;
import chylex.hee.mechanics.essence.EssenceType;
import chylex.hee.system.util.GameRegistryUtil;
import com.google.common.collect.ArrayListMultimap;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemPickupEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemSmeltedEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public final class AchievementEvents{
	private static final AchievementEvents instance = new AchievementEvents();
	
	public static void register(){
		GameRegistryUtil.registerEventHandler(instance);
	}
	
	public static void addDelayedAchievement(UUID id, Achievement achievement){
		instance.delayedAchievements.put(id,achievement);
	}
	
	private ArrayListMultimap<UUID,Achievement> delayedAchievements = ArrayListMultimap.create();

	private AchievementEvents(){}
	
	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent e){
		// TODO if (e.player.dimension == 1 && WorldDataHandler.<DragonSavefile>get(DragonSavefile.class).isDragonDead())e.player.addStat(AchievementManager.GO_INTO_THE_END,1);
		
		/* TODO if (!delayedAchievements.isEmpty() && delayedAchievements.containsKey(e.player.getUniqueID())){
			for(Achievement achievement:delayedAchievements.removeAll(e.player.getUniqueID()))e.player.addStat(achievement,1);
			SaveData.<QuickSaveFile>global(QuickSaveFile.class).setModified();
		}*/
	}
	
	/* TODO @SubscribeEvent
	public void onPlayerChangedDimension(PlayerChangedDimensionEvent e){
		if (e.toDim == 1){
			if (WorldDataHandler.<DragonSavefile>get(DragonSavefile.class).isDragonDead())e.player.addStat(AchievementManager.GO_INTO_THE_END,1);
			CausatumUtils.increase(e.player,CausatumMeters.OVERWORLD_PORTAL,100);
		}
	}*/
	
	@SubscribeEvent
	public void onItemPickup(ItemPickupEvent e){
		ItemStack is = e.pickedUp.getEntityItem();
		Item item = is.getItem();
		
		if (item == ItemList.end_powder)e.player.addStat(AchievementManager.END_POWDER,1);
		else if (item == ItemList.stardust)e.player.addStat(AchievementManager.STARDUST,1);
		else if (item == ItemList.endium_ingot)e.player.addStat(AchievementManager.ENDIUM_INGOT,1);
		else if (item == ItemList.essence && is.getItemDamage() == EssenceType.FIERY.getItemDamage())e.player.addStat(AchievementManager.FIERY_ESSSENCE,1);
	}
	
	@SubscribeEvent
	public void onItemSmelted(ItemSmeltedEvent e){
		if (e.smelting.getItem() == ItemList.endium_ingot)e.player.addStat(AchievementManager.ENDIUM_INGOT,1);
	}
	
	/* TODO @SubscribeEvent
	public void onLivingDeath(LivingDeathEvent e){
		if (!e.entity.worldObj.isRemote && e.entity.dimension == 1 && e.entity.worldObj.getTotalWorldTime()-EntityBossDragon.lastUpdate < 20 && e.entity instanceof EntityPlayer){
			for(Object o:e.entity.worldObj.loadedEntityList){
				if (o instanceof EntityBossDragon){
					((EntityBossDragon)o).achievements.onPlayerDied((EntityPlayer)e.entity);
					break;
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent e){
		World world = e.entityPlayer.worldObj;
		PosMutable tmpPos = new PosMutable(e.x,e.y,e.z);
		
		if (e.action != Action.RIGHT_CLICK_BLOCK || world.isRemote || e.entityPlayer.dimension != 1 || tmpPos.getBlock(world) != Blocks.bed)return;
		
		EntityBossDragon dragon = getDragon(world);
		if (dragon == null || dragon.getHealth() <= 0F)return;
		
		e.useBlock = Result.DENY;
		
		double dX = e.x+0.5D, dY = e.y+0.5D, dZ = e.z+0.5D;
		tmpPos.setAir(world);
		
		int dir = tmpPos.getMetadata(world)&3;
		
		if (tmpPos.move(BlockBed.field_149981_a[dir][0],0,BlockBed.field_149981_a[dir][1]).getBlock(world) == Blocks.bed){
			tmpPos.setAir(world);
			dX = (dX+tmpPos.x+0.5D)/2D;
			dY = (dY+tmpPos.y+0.5D)/2D;
			dZ = (dZ+tmpPos.z+0.5D)/2D;
		}

		world.newExplosion(null,dX,dY,dZ,5F,true,true);
		if (dragon.getHealth() <= 0F)e.entityPlayer.addStat(AchievementManager.CHALLENGE_BEDEXPLODE,1);
	}
	
	private EntityBossDragon getDragon(World world){
		for(Object o:world.loadedEntityList){
			if (o instanceof EntityBossDragon)return (EntityBossDragon)o;
		}
		
		return null;
	}*/
}
