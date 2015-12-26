package chylex.hee.mechanics.compendium.events;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerOpenContainerEvent;
import chylex.hee.game.save.SaveData;
import chylex.hee.game.save.types.player.CompendiumFile;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.util.KnowledgeUtils;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C19CompendiumData;
import chylex.hee.system.collections.ExpiringSet;
import chylex.hee.system.util.GameRegistryUtil;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemPickupEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;

public final class CompendiumEvents{
	public static void register(){
		GameRegistryUtil.registerEventHandler(new CompendiumEvents());
	}
	
	public static CompendiumFile getPlayerData(EntityPlayer player){
		return SaveData.player(player,CompendiumFile.class);
	}
	
	public static boolean tryDiscover(EntityPlayer player, Object obj){
		KnowledgeObject knowledgeObj = KnowledgeObject.fromObject(obj);
		return knowledgeObj != null && getPlayerData(player).tryDiscoverObject(player,knowledgeObj,false);
	}
	
	public static boolean tryDiscover(EntityPlayer player, ItemStack is){
		KnowledgeObject knowledgeObj = KnowledgeObject.fromObject(is);
		return knowledgeObj != null && getPlayerData(player).tryDiscoverObject(player,knowledgeObj,false);
	}

	private final ExpiringSet<UUID> playerLivingTicks = new ExpiringSet<>();
	private final ExpiringSet<UUID> playerContainerTicks = new ExpiringSet<>();
	
	private CompendiumEvents(){
		playerLivingTicks.setExpiryTime(500L);
		playerContainerTicks.setExpiryTime(5000L);
	}
	
	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent e){
		PacketPipeline.sendToPlayer(e.player,new C19CompendiumData(e.player));
	}
	
	@SubscribeEvent
	public void onPlayerLogout(PlayerLoggedOutEvent e){
		playerContainerTicks.remove(e.player.getGameProfile().getId());
	}
	
	@SubscribeEvent
	public void onItemPickup(ItemPickupEvent e){
		if (e.player != null && !e.player.worldObj.isRemote)tryDiscover(e.player,e.pickedUp.getEntityItem());
	}
	
	@SubscribeEvent
	public void onItemCrafted(ItemCraftedEvent e){
		if (e.player != null && !e.player.worldObj.isRemote)tryDiscover(e.player,e.crafting);
	}
	
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent e){
		if (e.phase != Phase.START || e.player.worldObj.isRemote || !playerLivingTicks.update(e.player.getGameProfile().getId()))return;
		
		KnowledgeObject<?> obj = KnowledgeUtils.getObservedObject(e.player);
		if (obj != null)getPlayerData(e.player).tryDiscoverObject(e.player,obj,false);
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onContainerOpen(PlayerOpenContainerEvent e){ // triggers every tick
		final EntityPlayer player = e.entityPlayer;
		final World world = player.worldObj;
		
		if (e.canInteractWith && !world.isRemote && player.openContainer != player.inventoryContainer && playerContainerTicks.update(player.getGameProfile().getId())){
			for(Slot slot:(List<Slot>)player.openContainer.inventorySlots){
				if (slot.getHasStack())tryDiscover(player,slot.getStack());
			}
		}
	}
}
