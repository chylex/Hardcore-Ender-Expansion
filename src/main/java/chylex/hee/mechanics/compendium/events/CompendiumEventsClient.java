package chylex.hee.mechanics.compendium.events;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.game.save.types.player.CompendiumFile;
import chylex.hee.gui.GuiEnderCompendium;
import chylex.hee.proxy.ModCommonProxy;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CompendiumEventsClient{
	private static final Minecraft mc = Minecraft.getMinecraft();
	private static CompendiumEventsClient instance;
	
	public static void register(){
		if (instance == null)FMLCommonHandler.instance().bus().register(instance = new CompendiumEventsClient());
	}
	
	public static void loadClientData(CompendiumFile file){
		instance.data = file;
		
		if (mc.currentScreen instanceof GuiEnderCompendium){
			((GuiEnderCompendium)mc.currentScreen).updateCompendiumData(file);
		}
	}
	
	public static CompendiumFile getClientData(){
		return instance.data;
	}
	
	private final KeyBinding keyOpenCompendium;
	private CompendiumFile data;
	
	private CompendiumEventsClient(){
		keyOpenCompendium = new KeyBinding(ModCommonProxy.hardcoreEnderbacon ? "key.openCompendium.bacon" : "key.openCompendium",25,"Hardcore Ender Expansion");
		ClientRegistry.registerKeyBinding(keyOpenCompendium);
		mc.gameSettings.loadOptions();
	}
	
	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent e){
		for(KeyBinding kb:mc.gameSettings.keyBindings){
			if (kb != instance.keyOpenCompendium && kb.getKeyCode() == instance.keyOpenCompendium.getKeyCode()){
				HardcoreEnderExpansion.notifications.report(I18n.format("key.openCompendium.conflict").replace("$",I18n.format(kb.getKeyDescription())));
				break;
			}
		}
	}
}
