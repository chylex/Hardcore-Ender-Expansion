package chylex.hee.sound;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.common.MinecraftForge;
import chylex.hee.system.logging.Log;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class MusicManager{
	public static boolean enableCustomMusic = true;
	public static boolean removeVanillaDelay = false;
	
	public static void register(){
		MinecraftForge.EVENT_BUS.register(new MusicManager());
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
