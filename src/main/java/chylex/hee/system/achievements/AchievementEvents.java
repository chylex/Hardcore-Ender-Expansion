package chylex.hee.system.achievements;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemPickupEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemSmeltedEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import chylex.hee.block.BlockList;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.essence.EssenceType;
import chylex.hee.system.savedata.WorldDataHandler;
import chylex.hee.system.savedata.types.DragonSavefile;
import chylex.hee.system.savedata.types.QuickSavefile;
import chylex.hee.system.savedata.types.QuickSavefile.IQuickSavefile;
import chylex.hee.system.util.BlockPosM;
import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;

public final class AchievementEvents implements IQuickSavefile{
	private static AchievementEvents instance;
	
	public static void register(){
		instance = new AchievementEvents();
		MinecraftForge.EVENT_BUS.register(instance);
		FMLCommonHandler.instance().bus().register(instance);
		QuickSavefile.addHandler(instance);
	}
	
	public static void addDelayedAchievement(UUID id, Achievement achievement){
		instance.delayedAchievements.put(id,achievement);
		WorldDataHandler.<QuickSavefile>get(QuickSavefile.class).setModified();
	}
	
	private ArrayListMultimap<UUID,Achievement> delayedAchievements = ArrayListMultimap.create();

	private AchievementEvents(){}

	@Override
	public void onSave(NBTTagCompound nbt){
		for(UUID id:delayedAchievements.keySet()){
			List<String> achievements = new ArrayList<>();
			for(Achievement achievement:delayedAchievements.get(id))achievements.add(achievement.statId);
			nbt.setString(id.toString(),Joiner.on('|').join(achievements));
		}
	}

	@Override
	public void onLoad(NBTTagCompound nbt){
		delayedAchievements.clear();
		
		for(String key:(Set<String>)nbt.getKeySet()){
			for(String achievementId:nbt.getString(key).split("\\|")){
				for(Achievement achievement:(List<Achievement>)AchievementList.achievementList){
					if (achievement.statId.equals(achievementId)){
						delayedAchievements.put(UUID.fromString(key),achievement);
						break;
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent e){
		if (e.player.dimension == 1 && WorldDataHandler.<DragonSavefile>get(DragonSavefile.class).isDragonDead())e.player.addStat(AchievementManager.GO_INTO_THE_END,1);
		
		QuickSavefile file = WorldDataHandler.<QuickSavefile>get(QuickSavefile.class);
		
		if (!delayedAchievements.isEmpty() && delayedAchievements.containsKey(e.player.getUniqueID())){
			for(Achievement achievement:delayedAchievements.removeAll(e.player.getUniqueID()))e.player.addStat(achievement,1);
			file.setModified();
		}
	}
	
	@SubscribeEvent
	public void onPlayerChangedDimension(PlayerChangedDimensionEvent e){
		if (e.toDim == 1 && WorldDataHandler.<DragonSavefile>get(DragonSavefile.class).isDragonDead())e.player.addStat(AchievementManager.GO_INTO_THE_END,1);
	}
	
	@SubscribeEvent
	public void onItemPickup(ItemPickupEvent e){
		ItemStack is = e.pickedUp.getEntityItem();
		Item item = is.getItem();
		
		if (item == ItemList.end_powder)e.player.addStat(AchievementManager.END_POWDER,1);
		else if (item == ItemList.stardust)e.player.addStat(AchievementManager.STARDUST,1);
		else if (item == ItemList.essence && is.getItemDamage() == EssenceType.FIERY.getItemDamage())e.player.addStat(AchievementManager.FIERY_ESSSENCE,1);
	}
	
	@SubscribeEvent
	public void onItemCrafted(ItemCraftedEvent e){
		if (e.crafting.getItem() == Item.getItemFromBlock(BlockList.void_chest))e.player.addStat(AchievementManager.VOID_CHEST,1);
	}
	
	@SubscribeEvent
	public void onItemSmelted(ItemSmeltedEvent e){
		if (e.smelting.getItem() == ItemList.endium_ingot)e.player.addStat(AchievementManager.ENDIUM_INGOT,1);
	}
	
	@SubscribeEvent
	public void onLivingDeath(LivingDeathEvent e){
		if (e.entity.worldObj.isRemote)return;
		
		if (e.entity.dimension == 1 && e.entity.worldObj.getTotalWorldTime()-EntityBossDragon.lastUpdate < 20){
			EntityBossDragon dragon;
			
			if (e.entity instanceof EntityPlayer){
				if ((dragon = getDragon(e.entity.worldObj)) != null)dragon.achievements.onPlayerDied((EntityPlayer)e.entityLiving);
			}
			
			if (e.entity instanceof EntityEnderman && e.source.getEntity() instanceof EntityPlayer){
				if ((dragon = getDragon(e.entity.worldObj)) != null)dragon.achievements.onPlayerKilledEnderman((EntityPlayer)e.source.getEntity());
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent e){
		World world = e.entityPlayer.worldObj;
		if (e.action != Action.RIGHT_CLICK_BLOCK || world.isRemote || e.entityPlayer.dimension != 1)return;
		
		BlockPosM pos = new BlockPosM(e.pos);
		IBlockState state =  world.getBlockState(pos);
		EnumFacing facing = (EnumFacing)state.getValue(BlockDirectional.FACING);
		
		if (state.getBlock() != Blocks.bed)return;
		if (state.getValue(BlockBed.PART) != BlockBed.EnumPartType.HEAD){
			if ((state = world.getBlockState(pos.moveBy(facing,1))).getBlock() != Blocks.bed)return;
		}
		
		EntityBossDragon dragon = getDragon(world);
		if (dragon == null || dragon.getHealth() <= 0F)return;
		
		e.useBlock = Result.DENY;
		
		pos.setToAir(world);
		pos.moveBy(facing.getOpposite(),1).setToAir(world);
		pos.moveBy(facing,1);

		world.newExplosion(null,pos.x+0.5D,pos.y+0.5D,pos.z+0.5D,5F,true,true);
		if (dragon.getHealth() <= 0F)e.entityPlayer.addStat(AchievementManager.CHALLENGE_BEDEXPLODE,1);
	}
	
	private EntityBossDragon getDragon(World world){
		for(Object o:world.loadedEntityList){
			if (o instanceof EntityBossDragon)return (EntityBossDragon)o;
		}
		
		return null;
	}
}
