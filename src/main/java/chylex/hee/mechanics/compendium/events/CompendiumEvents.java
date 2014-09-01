package chylex.hee.mechanics.compendium.events;
import gnu.trove.map.hash.TObjectByteHashMap;
import java.util.UUID;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import chylex.hee.mechanics.compendium.content.objects.ObjectBlock;
import chylex.hee.mechanics.compendium.content.objects.ObjectBlock.BlockMetaWrapper;
import chylex.hee.mechanics.compendium.content.objects.ObjectItem;
import chylex.hee.mechanics.compendium.content.objects.ObjectMob;
import chylex.hee.mechanics.compendium.content.type.KnowledgeObject;
import chylex.hee.mechanics.compendium.player.PlayerCompendiumData;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C19CompendiumData;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemPickupEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;

public final class CompendiumEvents{
	private static final String playerPropertyIdentifier = "HardcoreEnderExpansion~Compendium";
	private static final byte byteZero = 0;
	private static final byte byteOne = 1;
	
	private static CompendiumEvents instance;
	
	public static void register(){
		if (instance == null){
			instance = new CompendiumEvents();
			MinecraftForge.EVENT_BUS.register(instance);
			FMLCommonHandler.instance().bus().register(instance);
		}
	}
	
	public static PlayerCompendiumData getPlayerData(EntityPlayer player){
		return (PlayerCompendiumData)player.getExtendedProperties(playerPropertyIdentifier);
	}
	
	private static void discoverItemStack(EntityPlayer player, ItemStack is){
		if (is.getItem() instanceof ItemBlock){
			KnowledgeObject<ObjectBlock> obj = KnowledgeObject.getObject(new BlockMetaWrapper(((ItemBlock)is.getItem()).field_150939_a,is.getItemDamage()));
			if (obj != null)getPlayerData(player).tryDiscoverBlock(obj,true);
		}
		else{
			KnowledgeObject<ObjectItem> obj = KnowledgeObject.getObject(is.getItem());
			if (obj != null)getPlayerData(player).tryDiscoverItem(obj,true);
		}
	}
	
	private static void discoverMob(EntityPlayer player, EntityLiving mob){
		KnowledgeObject<ObjectMob> obj = KnowledgeObject.getObject(mob);
		if (obj != null)getPlayerData(player).tryDiscoverMob(obj,true);
	}
	
	private TObjectByteHashMap<UUID> playerTickLimiter = new TObjectByteHashMap<>();
	
	private CompendiumEvents(){}
	
	@SubscribeEvent
	public void onEntityConstructing(EntityEvent.EntityConstructing e){
		if (e.entity.worldObj != null && !e.entity.worldObj.isRemote && e.entity instanceof EntityPlayer){
			if (!e.entity.registerExtendedProperties(playerPropertyIdentifier,new PlayerCompendiumData()).equals(playerPropertyIdentifier)){
				throw new IllegalStateException("Could not register player Compendium properties, likely due to the properties already being registered by another mod!");
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent e){
		PacketPipeline.sendToPlayer(e.player,new C19CompendiumData(e.player));
	}
	
	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedOutEvent e){
		playerTickLimiter.remove(e.player.getGameProfile().getId());
	}
	
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent e){
		if (e.phase != Phase.START || e.player.worldObj.isRemote)return;
		
		if (playerTickLimiter.adjustOrPutValue(e.player.getGameProfile().getId(),byteOne,byteOne) >= 10){
			playerTickLimiter.put(e.player.getGameProfile().getId(),byteZero);
		
			// TODO random events
		}
	}
	
	@SubscribeEvent
	public void onItemPickup(ItemPickupEvent e){
		if (!e.player.worldObj.isRemote)discoverItemStack(e.player,e.pickedUp.getEntityItem());
	}
	
	@SubscribeEvent
	public void onItemCrafted(ItemCraftedEvent e){
		if (!e.player.worldObj.isRemote)discoverItemStack(e.player,e.crafting);
	}
}
