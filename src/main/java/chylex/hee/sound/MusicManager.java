package chylex.hee.sound;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.common.MinecraftForge;
import chylex.hee.system.logging.Log;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class MusicManager{
	public static boolean enableEndMusic = true;
	public static boolean removeVanillaDelay = false;
	
	public static void register(){
		MinecraftForge.EVENT_BUS.register(new MusicManager());
	}
	
	private boolean hasLoaded;
	
	private MusicManager(){}
	
	@SubscribeEvent
	public void onSoundLoad(SoundLoadEvent e){
		if (hasLoaded || (!enableEndMusic && !removeVanillaDelay))return;
		
		Minecraft mc = Minecraft.getMinecraft();
		
		if (mc.mcMusicTicker != null){
			mc.mcMusicTicker = CustomMusicTicker.setup(mc);
			hasLoaded = true;
			Log.debug("Successfully replaced MusicTicker.");
		}
	}
}
