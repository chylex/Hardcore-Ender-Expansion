package chylex.hee.mechanics.temple;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.WorldEvent;
import org.apache.commons.io.FileUtils;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.item.ItemTempleCaller;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.logging.Log;
import chylex.hee.system.savedata.WorldDataHandler;
import chylex.hee.system.savedata.types.DragonSavefile;
import chylex.hee.system.savedata.types.EnergySavefile;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.world.feature.TempleGenerator;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;

public final class TempleEvents{
	private static TempleEvents instance;
	
	public static void register(){
		instance = new TempleEvents();
		FMLCommonHandler.instance().bus().register(instance);
		MinecraftForge.EVENT_BUS.register(instance);
	}
	
	public static void destroyWorld(){
		if (instance == null)throw new RuntimeException("TempleEvents class has not been registered!");
		
		if (instance.stage == DestroyStage.SILENT){
			instance.setStage(DestroyStage.KICK);
			WorldDataHandler.<DragonSavefile>get(DragonSavefile.class).setDestroyEnd(true);
		}
	}
	
	/**
	 * Returns true if player was in temple, not if the temple was destroyed
	 */
	public static boolean attemptDestroyTemple(EntityPlayer player){
		DragonSavefile save = WorldDataHandler.get(DragonSavefile.class);
		Set<UUID> playersInTemple = save.getPlayersInTemple();
		
		if (playersInTemple.contains(player.getGameProfile().getId())){
			if (playersInTemple.size() == 1 && !save.shouldPreventTempleDestruction()){
				save.resetPlayersInTemple();
				new TempleGenerator(DimensionManager.getWorld(1)).preloadAndClearArea(ItemTempleCaller.templeX,ItemTempleCaller.templeY,ItemTempleCaller.templeZ);
			}
			else save.setPlayerIsInTemple(player,false);
			return true;
		}
		else return false;
	}
	
	private DestroyStage stage = DestroyStage.SILENT;
	private byte tickTimer = 0;
	private byte giveUpCounter = 0;
	
	private TempleEvents(){}
	
	protected enum DestroyStage{
		SILENT(-1), KICK(8), UNLOAD(3), WAIT_FOR_UNLOAD(69), DELETE(100);
		
		protected byte tickTimer;
		
		private DestroyStage(int tickTimer){
			this.tickTimer = (byte)tickTimer;
		}
	}
	
	private void setStage(DestroyStage stage){
		this.stage = stage;
		this.tickTimer = 0;
	}
	
	@SubscribeEvent
	public void onServerTick(ServerTickEvent e){
		if (stage == DestroyStage.SILENT || e.phase != Phase.START || ++tickTimer < stage.tickTimer)return;
		tickTimer = 0;
		
		Log.debug("End dimension deletion stage: $0",stage);
		
		World world = DimensionManager.getWorld(1);
		
		if (stage == DestroyStage.KICK){
			giveUpCounter = 0;
			
			if (world == null)setStage(DestroyStage.DELETE);
			else if (world.playerEntities.isEmpty())setStage(DestroyStage.UNLOAD);
			else{
				List<EntityPlayerMP> players = new ArrayList<>(world.playerEntities);
				
				for(EntityPlayerMP player:players){
					if (player.ridingEntity != null)player.mountEntity(null);
					DragonUtil.teleportToOverworld(player);
				}
			}
		}
		else if (stage == DestroyStage.UNLOAD){
			if (world == null)stage = DestroyStage.DELETE;
			else{
				DimensionManager.unloadWorld(1);
				setStage(DestroyStage.WAIT_FOR_UNLOAD);
			}
		}
		else if (stage == DestroyStage.DELETE){
			File dim1 = new File(DimensionManager.getCurrentSaveRootDirectory(),"DIM1");
			
			if (!dim1.exists()){
				Log.reportedError("DIM1 not found!");
				setStage(DestroyStage.SILENT);
				return;
			}
			
			boolean successful = true;
			
			try{
				FileUtils.cleanDirectory(dim1);
				dim1.delete();
			}catch(Exception ex){
				successful = false;
				Log.throwable(ex,"Error deleting DIM1!");
			}
			
			if (successful){
				DragonSavefile save = WorldDataHandler.get(DragonSavefile.class);
				save.setDestroyEnd(false);
				save.setPreventTempleDestruction(false);
				save.resetPlayersInTemple();
				save.addDragonDeath();
				save.setDragonDead(false);
				save.resetCrystals();
				WorldDataHandler.<EnergySavefile>get(EnergySavefile.class).reset();
				WorldDataHandler.forceSave();
				setStage(DestroyStage.SILENT);
				Log.debug("Successfully deleted!");
			}
			else if (++giveUpCounter > 10){
				Log.reportedError("Gave up deleting DIM1, please "+(HardcoreEnderExpansion.proxy.getClass() == ModCommonProxy.class ? "restart the server" : "reload the world")+" to try again, or delete 'DIM1' manually if the deletion keeps failing.");
				setStage(DestroyStage.SILENT);
				giveUpCounter = 0;
			}
		}
	}
	
	@SubscribeEvent
	public void onGetBlockBreakSpeed(BreakSpeed e){
		if (e.entityPlayer.dimension == 1 && isPlayerInTemple(e.entityPlayer))e.setCanceled(true);
	}
	
	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent e){
		if (e.entity.worldObj.isRemote || e.entity.dimension != 1 || !isPlayerInTemple(e.entityPlayer))return;
		
		ItemStack is = e.entityPlayer.inventory.getCurrentItem();
		if (is == null)return;
		
		if ((e.action == Action.RIGHT_CLICK_AIR || e.action == Action.RIGHT_CLICK_BLOCK) && is.getItem() != Item.getItemFromBlock(Blocks.dragon_egg)){
			e.useBlock = Result.DENY;
			e.useItem = Result.DENY;
		}
	}
	
	@SubscribeEvent
	public void onPlayerLogout(PlayerLoggedOutEvent e){
		if (e.player.dimension == 1 && attemptDestroyTemple(e.player))e.player.setPositionAndUpdate(0,DragonUtil.getTopBlock(e.player.worldObj,Blocks.end_stone,0,0,100),0);
	}
	
	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload e){
		if (e.world.provider.dimensionId == 1 && instance.stage == DestroyStage.WAIT_FOR_UNLOAD)instance.setStage(DestroyStage.DELETE);
	}
	
	public static boolean isPlayerInTemple(EntityPlayer player){
		return player.posY >= ItemTempleCaller.templeY && player.posX >= ItemTempleCaller.templeX && player.posZ >= ItemTempleCaller.templeZ &&
			   player.posY <= ItemTempleCaller.templeY+7 && player.posX <= ItemTempleCaller.templeX+13 && player.posZ <= ItemTempleCaller.templeZ+19;
	}
}
