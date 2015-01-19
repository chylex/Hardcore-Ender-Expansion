package chylex.hee.sound;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.system.logging.Log;

@SideOnly(Side.CLIENT)
public final class MusicManager{
	public static final MusicManager instance = new MusicManager();
	public static boolean enableMusic = true;
	
	public static void register(){
		MinecraftForge.EVENT_BUS.register(instance);
	}
	
	private boolean hasLoaded;
	
	private MusicManager(){}
	
	@SubscribeEvent
	public void onSoundLoad(SoundLoadEvent e){
		if (hasLoaded || !enableMusic)return;
		
		Minecraft mc = Minecraft.getMinecraft();
		
		if (mc.mcMusicTicker != null){
			mc.mcMusicTicker = CustomMusicTicker.getInstance();
			hasLoaded = true;
			Log.debug("Successfully replaced MusicTicker.");
		}
	}
}
