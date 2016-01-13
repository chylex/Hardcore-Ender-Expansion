package chylex.hee.sound;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundEventAccessorComposite;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import chylex.hee.system.logging.Log;
import chylex.hee.system.util.GameRegistryUtil;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class MusicManager{
	public static boolean enableCustomMusic = true;
	public static boolean removeVanillaDelay = false;
	
	public static void register(){
		GameRegistryUtil.registerEventHandler(new MusicManager());
	}
	
	public static boolean isMusicAvailable(ResourceLocation resource){
		SoundEventAccessorComposite sound = Minecraft.getMinecraft().getSoundHandler().getSound(resource);
		return sound != null && sound.func_148720_g() != SoundHandler.missing_sound; // OBFUSCATED getSoundEntry
	}
	
	private boolean hasLoaded;
	
	private MusicManager(){}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onSoundLoad(SoundLoadEvent e){
		if (hasLoaded || (!enableCustomMusic && !removeVanillaDelay))return;
		
		Minecraft mc = Minecraft.getMinecraft();
		
		if (mc.mcMusicTicker != null){
			if (mc.mcMusicTicker.getClass() == MusicTicker.class){
				mc.mcMusicTicker = CustomMusicTicker.setup(mc);
				hasLoaded = true;
				Log.debug("Successfully replaced MusicTicker.");
			}
			else Log.warn("Another mod has already replaced the music system: $0",mc.mcMusicTicker.getClass().getName());
		}
	}
}
