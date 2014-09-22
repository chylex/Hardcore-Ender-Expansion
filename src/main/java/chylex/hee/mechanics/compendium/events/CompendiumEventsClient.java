package chylex.hee.mechanics.compendium.events;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ChatComponentText;
import chylex.hee.gui.ContainerEndPowderEnhancements;
import chylex.hee.gui.GuiEnderCompendium;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.objects.IKnowledgeObjectInstance;
import chylex.hee.mechanics.compendium.player.PlayerCompendiumData;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.server.S03OpenCompendium;
import chylex.hee.system.achievements.AchievementManager;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class CompendiumEventsClient{
	private static CompendiumEventsClient instance;
	
	public static void register(){
		if (instance == null)FMLCommonHandler.instance().bus().register(instance = new CompendiumEventsClient());
	}
	
	public static void loadClientData(PlayerCompendiumData data){
		instance.data = data;
		
		if (Minecraft.getMinecraft().currentScreen instanceof GuiEnderCompendium){
			((GuiEnderCompendium)Minecraft.getMinecraft().currentScreen).updateCompendiumData(data);
		}
		
		if (Minecraft.getMinecraft().thePlayer.openContainer instanceof ContainerEndPowderEnhancements){
			((ContainerEndPowderEnhancements)Minecraft.getMinecraft().thePlayer.openContainer).updateClientItems();
		}
	}
	
	public static PlayerCompendiumData getClientData(){
		return instance.data;
	}
	
	public static void openCompendium(KnowledgeObject<? extends IKnowledgeObjectInstance<?>> obj){
		GuiEnderCompendium compendium = new GuiEnderCompendium(instance.data);
		Minecraft.getMinecraft().displayGuiScreen(compendium);
		if (obj != null)compendium.showObject(obj);
	}
	
	public static int getCompendiumKeyCode(){
		return instance.keyOpenCompendium.getKeyCode();
	}
	
	private final KeyBinding keyOpenCompendium;
	private PlayerCompendiumData data;
	
	private CompendiumEventsClient(){
		keyOpenCompendium = new KeyBinding("key.openCompendium",25,"Hardcore Ender Expansion");
		ClientRegistry.registerKeyBinding(keyOpenCompendium);
		Minecraft.getMinecraft().gameSettings.loadOptions();
	}
	
	@SubscribeEvent
	public void onKeyInput(KeyInputEvent e){
		Minecraft mc = Minecraft.getMinecraft();
		
		if (mc.inGameHasFocus && keyOpenCompendium.isPressed()){
			if (data != null){
				openCompendium(mc.thePlayer.isSneaking() ? CompendiumEvents.getObservation(mc.thePlayer).getObject() : null);
				if (!mc.thePlayer.getStatFileWriter().hasAchievementUnlocked(AchievementManager.THE_MORE_YOU_KNOW))PacketPipeline.sendToServer(new S03OpenCompendium());
			}
			else mc.thePlayer.addChatMessage(new ChatComponentText("Error opening Ender Compendium, server did not provide required data. Relog, wait a few seconds, pray to your favourite deity and try again!"));
		}
	}
}
