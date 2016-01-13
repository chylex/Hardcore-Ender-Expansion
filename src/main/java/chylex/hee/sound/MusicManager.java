package chylex.hee.sound;
import java.util.HashSet;
import java.util.Set;
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
	
	private static final Set<String> ignoredTickers = new HashSet<>(1); // TODO add vazkii.ambience.NilMusicTicker
	
	public static boolean addIgnoredTicker(String fullClassName){
		return ignoredTickers.add(fullClassName);
	}
	
	public static boolean isMusicAvailable(ResourceLocation resource){
		SoundEventAccessorComposite sound = Minecraft.getMinecraft().getSoundHandler().getSound(resource);
		return sound != null && sound.func_148720_g() != SoundHandler.missing_sound; // OBFUSCATED getSoundEntry
	}
	
	public static void register(){
		GameRegistryUtil.registerEventHandler(new MusicManager());
	}
	
	private boolean hasLoaded;
	
	private MusicManager(){}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onSoundLoad(SoundLoadEvent e){
		if (hasLoaded || (!enableCustomMusic && !removeVanillaDelay))return;
		
		Minecraft mc = Minecraft.getMinecraft();
		
		if (mc.mcMusicTicker != null){
			Class<? extends MusicTicker> tickerClass = mc.mcMusicTicker.getClass();
			
			if (ignoredTickers.contains(tickerClass.getName())){
				Log.warn("Another mod has already replaced the music system: $0",tickerClass.getName());
			}
			else if (tickerClass == MusicTicker.class){
				mc.mcMusicTicker = new CustomMusicTicker(mc,null);
				Log.info("Successfully replaced music system.");
			}
			else{
				mc.mcMusicTicker = new CustomMusicTicker(mc,mc.mcMusicTicker);
				Log.info("Successfully wrapped a music system replaced by another mod: $0",tickerClass.getName());
			}
			
			hasLoaded = true;
		}
	}
}
