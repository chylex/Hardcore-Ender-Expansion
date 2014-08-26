package chylex.hee.mechanics.compendium.events;
import chylex.hee.mechanics.compendium.player.PlayerCompendiumData;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C19CompendiumData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public final class CompendiumEvents{
	private static final String playerPropertyIdentifier = "HardcoreEnderExpansion~Compendium";
	
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
}
