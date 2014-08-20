package chylex.hee.mechanics.compendium;
import chylex.hee.gui.GuiEnderCompendium;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;

public final class CompendiumKeyHandler{
	private static CompendiumKeyHandler instance;
	
	public static void register(){
		if (instance == null)FMLCommonHandler.instance().bus().register(instance = new CompendiumKeyHandler());
	}
	
	private KeyBinding keyOpenCompendium;
	
	private CompendiumKeyHandler(){
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
