package chylex.hee.system.achievements;
import net.minecraft.block.BlockBed;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.item.ItemList;
import chylex.hee.system.savedata.WorldDataHandler;
import chylex.hee.system.savedata.types.DragonSavefile;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemPickupEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public final class AchievementEvents{
	public static void register(){
		AchievementEvents events = new AchievementEvents();
		MinecraftForge.EVENT_BUS.register(events);
		FMLCommonHandler.instance().bus().register(events);
	}

	private AchievementEvents(){}
	
	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent e){
		if (e.player.dimension == 1 && WorldDataHandler.<DragonSavefile>get(DragonSavefile.class).isDragonDead())e.player.addStat(AchievementManager.TIME_FOR_NEW_ADVENTURES,1);
	}
	
	@SubscribeEvent
	public void onPlayerChangedDimension(PlayerChangedDimensionEvent e){
		if (e.toDim == 1 && WorldDataHandler.<DragonSavefile>get(DragonSavefile.class).isDragonDead())e.player.addStat(AchievementManager.TIME_FOR_NEW_ADVENTURES,1);
	}
	
	@SubscribeEvent
	public void onItemPickup(ItemPickupEvent e){
		if (e.player.worldObj.isRemote)return;
		
		if (e.pickedUp.getEntityItem().getItem() == ItemList.stardust)e.player.addStat(AchievementManager.MAGIC_OF_DECOMPOSITION,1);
	}
	
	@SubscribeEvent
	public void onLivingDeath(LivingDeathEvent e){
		if (e.entity.worldObj.isRemote)return;
		
		if (e.entity.dimension == 1){
			if (System.currentTimeMillis()-EntityBossDragon.lastUpdate < 20){
				EntityBossDragon dragon;
				
				if (e.entity instanceof EntityPlayer){
					if ((dragon = getDragon(e.entity.worldObj)) != null)dragon.achievements.onPlayerDied(((EntityPlayer)e.entityLiving).getCommandSenderName());
				}
				
				if (e.entity instanceof EntityEnderman && e.source.getEntity() instanceof EntityPlayer){
					if ((dragon = getDragon(e.entity.worldObj)) != null)dragon.achievements.onPlayerKilledEnderman(((EntityPlayer)e.source.getEntity()).getCommandSenderName());
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent e){
		World world = e.entityPlayer.worldObj;
		if (e.action != Action.RIGHT_CLICK_BLOCK || world.isRemote || e.entityPlayer.dimension != 1 || world.getBlock(e.x,e.y,e.z) != Blocks.bed)return;
		
		EntityBossDragon dragon = getDragon(world);
		if (dragon == null || dragon.getHealth() <= 0F)return;
		
		e.useBlock = Result.DENY;
		
		double dX = e.x+0.5D, dY = e.y+0.5D, dZ = e.z+0.5D;
		world.setBlockToAir(e.x,e.y,e.z);
		
		int dir = world.getBlockMetadata(e.x,e.y,e.z)&3;
		int x2 = e.x+BlockBed.field_149981_a[dir][0];
		int z2 = e.z+BlockBed.field_149981_a[dir][1];

		if (world.getBlock(x2,e.y,z2) == Blocks.bed){
			world.setBlockToAir(x2,e.y,z2);
			dX = (dX+x2+0.5D)/2D;
			dY = (dY+e.y+0.5D)/2D;
			dZ = (dZ+z2+0.5D)/2D;
		}

		world.newExplosion(null,dX,dY,dZ,5F,true,true);
		if (dragon.getHealth() <= 0F)e.entityPlayer.addStat(AchievementManager.CHALLENGE_BEDEXPLODE,1);
	}
	
	private EntityBossDragon getDragon(World world){
		for(Object o:world.loadedEntityList){
			if (o instanceof EntityBossDragon)return (EntityBossDragon)o;
		}
		return null;
	}
}
