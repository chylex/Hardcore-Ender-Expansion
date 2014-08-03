package chylex.hee.system.sound;
import java.lang.reflect.Field;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.common.MinecraftForge;
import chylex.hee.system.logging.Log;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
		if (hasLoaded)return;
		
		new Thread(new Runnable(){ // TODO ugh.... ewwww make it better
			@Override
			public void run(){
				try{
					Thread.sleep(4000L);
				}catch(InterruptedException e){
					e.printStackTrace();
				}
				
				Minecraft mc = Minecraft.getMinecraft();
				
				synchronized(mc){
					Field[] fields = Minecraft.class.getDeclaredFields();
	
					for(int a = 0; a < fields.length; a++){
						if (MusicTicker.class.isAssignableFrom(fields[a].getType())){
							fields[a].setAccessible(true);
	
							try{
								fields[a].set(mc,CustomMusicTicker.getInstance());
								Log.debug("Successfully replaced MusicTicker.");
							}catch(Exception ex){
								Log.throwable(ex,"Could not replace MusicTicker, custom music will not be present.");
								return;
							}
	
							break;
						}
					}
				}
			}
		}).start();
		
		hasLoaded = true;
	}
}
