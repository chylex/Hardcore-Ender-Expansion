package chylex.hee.mechanics.compendium.events;
import chylex.hee.gui.GuiEnderCompendium;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;

public final class CompendiumEventsClient{
	private static CompendiumEventsClient instance;
	
	public static void register(){
		if (instance == null)FMLCommonHandler.instance().bus().register(instance = new CompendiumEventsClient());
	}
	
	private KeyBinding keyOpenCompendium;
	
	private CompendiumEventsClient(){
		keyOpenCompendium = new KeyBinding("key.openCompendium",25,"Hardcore Ender Expansion");
		ClientRegistry.registerKeyBinding(keyOpenCompendium);
		Minecraft.getMinecraft().gameSettings.loadOptions();
	}
	
	@SubscribeEvent
	public void onKeyInput(KeyInputEvent e){
		Minecraft mc = Minecraft.getMinecraft();
		
		if (mc.inGameHasFocus && keyOpenCompendium.isPressed()){
			mc.displayGuiScreen(new GuiEnderCompendium()); // TODO temp
		}
	}
}
