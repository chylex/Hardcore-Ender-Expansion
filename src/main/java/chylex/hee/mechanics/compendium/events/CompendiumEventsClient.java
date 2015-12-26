package chylex.hee.mechanics.compendium.events;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.game.achievements.AchievementManager;
import chylex.hee.game.save.types.player.CompendiumFile;
import chylex.hee.gui.GuiEnderCompendium;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.content.objects.IObjectHolder;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.render.OverlayManager;
import chylex.hee.system.util.GameRegistryUtil;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CompendiumEventsClient{
	private static final Minecraft mc = Minecraft.getMinecraft();
	private static CompendiumEventsClient instance;
	
	public static void register(){
		GameRegistryUtil.registerEventHandler(instance = new CompendiumEventsClient());
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
	
	public static boolean canOpenCompendium(){
		if (instance.data == null){
			Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(I18n.format("compendium.dataError")));
			return false;
		}
		else return true;
	}
	
	public static void openCompendium(@Nullable KnowledgeObject<? extends IObjectHolder<?>> obj){
		GuiEnderCompendium compendium = new GuiEnderCompendium(instance.data);
		Minecraft.getMinecraft().displayGuiScreen(compendium);
		
		if (obj != null){
			compendium.showObject(obj); // TODO show most recent fragment
			compendium.moveToCurrentObject(false);
		}
		
		if (Minecraft.getSystemTime()-instance.displayedHintTime <= 9000L){
			OverlayManager.getAchievementOverlay().hide();
		}
	}
	
	public static void displayCompendiumHint(){
		OverlayManager.getAchievementOverlay().display("ec.overlay.hint.title","ec.overlay.hint.desc",9000L);
		instance.displayedHintTime = Minecraft.getSystemTime();
	}
	
	private final KeyBinding keyOpenCompendium;
	private CompendiumFile data;
	private long displayedHintTime;
	
	private CompendiumEventsClient(){
		keyOpenCompendium = new KeyBinding(ModCommonProxy.hardcoreEnderbacon ? "key.openCompendium.bacon" : "key.openCompendium",25,"Hardcore Ender Expansion");
		ClientRegistry.registerKeyBinding(keyOpenCompendium);
		mc.gameSettings.loadOptions();
		
		AchievementManager.ENDER_COMPENDIUM.setStatStringFormatter(str -> {
			if (ModCommonProxy.hardcoreEnderbacon)str = StatCollector.translateToLocal("achievement.enderCompendium.desc.bacon");
			return str.replace("$",GameSettings.getKeyDisplayString(keyOpenCompendium.getKeyCode()));
		});
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
	
	@SubscribeEvent
	public void onClientTick(ClientTickEvent e){
		if (e.phase != Phase.START)return;
		
		if ((keyOpenCompendium.isPressed() || Keyboard.getEventKeyState() && Keyboard.getEventKey() == keyOpenCompendium.getKeyCode()) && (mc.inGameHasFocus || mc.currentScreen instanceof GuiContainer)){
			if (canOpenCompendium()){
				KnowledgeObject<? extends IObjectHolder<?>> obj = null;
				
				if (mc.inGameHasFocus){
					/* TODO if (newlyDiscoveredTime != 0L && System.nanoTime()-newlyDiscoveredTime <= 7000000000L){
						obj = KnowledgeObject.getObjectById(newlyDiscoveredId);
						newlyDiscoveredId = -1;
						newlyDiscoveredTime = 0L;
					}*/
					// TODO else obj = CompendiumEvents.getObservation(mc.thePlayer).getObject();
				}
				else{
					GuiContainer container = (GuiContainer)mc.currentScreen;
					List<Slot> slots = container.inventorySlots.inventorySlots;
					ScaledResolution res = new ScaledResolution(mc,mc.displayWidth,mc.displayHeight);
					
					int mouseX = Mouse.getX()*res.getScaledWidth()/mc.displayWidth,
						mouseY = res.getScaledHeight()-Mouse.getY()*res.getScaledHeight()/mc.displayHeight-1;
					
					mouseX -= (container.width-container.xSize)/2;
					mouseY -= (container.height-container.ySize)/2;
					
					for(Slot slot:slots){
						if (slot.getHasStack() && slot.func_111238_b() &&
							mouseX >= slot.xDisplayPosition-1 && mouseX <= slot.xDisplayPosition+16 &&
							mouseY >= slot.yDisplayPosition-1 && mouseY <= slot.yDisplayPosition+16){
							obj = KnowledgeObject.fromObject(slot.getStack());
							break;
						}
					}
					
					if (obj == null)return;
				}
				
				openCompendium(obj);
				
				/*if (!mc.thePlayer.getStatFileWriter().hasAchievementUnlocked(AchievementManager.ENDER_COMPENDIUM)){
					PacketPipeline.sendToServer(new S03SimpleEvent(EventType.OPEN_COMPENDIUM));
					// TODO achievementTimer = Byte.MIN_VALUE;
				}*/
			} 
		}
	}
}
