package chylex.hee.system.sound;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Map;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.audio.SoundPoolEntry;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import paulscode.sound.SoundSystem;
import chylex.hee.system.util.DragonUtil;
import com.google.common.collect.Multimap;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CustomSoundManager extends SoundManager{
	private static final Marker logMarker = MarkerManager.getMarker("SOUNDS");
    private static final Logger logger = LogManager.getLogger();
    
    private final SoundHandler soundHandler;
    private final GameSettings gameSettings;
    private int playTime = 0;
    
    private Field fieldSndSystem;
    private Map playingSounds;
    private Map playingSoundPoolEntries;
    private Multimap categorySounds;
    private Map playingSoundsStopTime;
    
	public CustomSoundManager(SoundHandler soundHandler, GameSettings gameSettings){
		super(soundHandler,gameSettings);
		this.soundHandler = soundHandler;
		this.gameSettings = gameSettings;
		
		Field[] fields = SoundManager.class.getDeclaredFields();
		try{
			for(int a = 4,mapCounter = 0; a < fields.length; a++){
				Class<?> type = fields[a].getType();
				fields[a].setAccessible(true);
				
				if (a == 4)fieldSndSystem = fields[a];
				else if (Map.class.isAssignableFrom(type)){
					++mapCounter;
					
					if (mapCounter == 1)playingSounds = (Map)fields[a].get(this);
					else if (mapCounter == 3)playingSoundPoolEntries = (Map)fields[a].get(this);
					else if (mapCounter > 4)playingSoundsStopTime = (Map)fields[a].get(this); // last map
					else continue;
				}
				else if (Multimap.class.isAssignableFrom(type))categorySounds = (Multimap)fields[a].get(this);
				else continue;
				
				DragonUtil.info("CustomSoundManager - added "+type+"/"+fields[a].getName());
			}
		}catch(Exception e){
			e.printStackTrace();
			DragonUtil.severe("Failed to set up sound manager!");
		}
	}
	
	@Override
	public void updateAllSounds(){
		++playTime;
		super.updateAllSounds();
	}
	
	@Override
	public boolean isSoundPlaying(ISound sound){
		ResourceLocation soundRes = sound.getPositionedSoundLocation();
		if (!soundRes.getResourceDomain().startsWith("hee~"))return super.isSoundPlaying(sound);
		
		SoundSystem sndSystem = null;
		try{
			sndSystem = (SoundSystem)fieldSndSystem.get(this);
		}catch(Exception e){
			e.printStackTrace();
		}

		return sndSystem.playing("hee-"+soundRes.getResourcePath());
	}

	@Override
	public void playSound(ISound sound){
		ResourceLocation soundRes = sound.getPositionedSoundLocation();
		if (!soundRes.getResourceDomain().startsWith("hee~")){
			super.playSound(sound);
			return;
		}
		
		SoundSystem sndSystem = null;
		try{
			sndSystem = (SoundSystem)fieldSndSystem.get(this);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		if (sndSystem == null)return;

		float soundVol = sound.getVolume();
		float distOrRoll = 16F;
		if (soundVol > 1F)distOrRoll *= soundVol;

		SoundPoolEntry poolEntry = new SoundPoolEntry(soundRes,1F,1F,true);

		float volume = getNormalizedVolume(sound,poolEntry,SoundCategory.MUSIC);
		float pitch = getNormalizedPitch(sound,poolEntry);
		if (volume == 0F)return;

		boolean shouldRepeat = sound.canRepeat() && sound.getRepeatDelay() == 0;
		String identifier = "hee-"+soundRes.getResourcePath();DragonUtil.info("starting id "+identifier);

		URL url = getURLForSoundResource(soundRes);
		if (url == null)return;

		sndSystem.newStreamingSource(true,identifier,url,soundRes.toString(),shouldRepeat,sound.getXPosF(),sound.getYPosF(),sound.getZPosF(),sound.getAttenuationType().getTypeInt(),distOrRoll);
		sndSystem.setPitch(identifier,pitch);
		sndSystem.setVolume(identifier,volume);
		sndSystem.play(identifier);
		
		playingSoundsStopTime.put(identifier,Integer.valueOf(playTime+20));
		playingSounds.put(identifier,sound);
		playingSoundPoolEntries.put(sound,poolEntry);
		categorySounds.put(SoundCategory.MUSIC,identifier);
	}
	
	@Override
	public void stopSound(ISound sound){
		ResourceLocation soundRes = sound.getPositionedSoundLocation();
		if (!soundRes.getResourceDomain().startsWith("hee~")){
			super.stopSound(sound);
			return;
		}
		
		SoundSystem sndSystem = null;
		try{
			sndSystem = (SoundSystem)fieldSndSystem.get(this);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		sndSystem.stop("hee-"+soundRes.getResourcePath());
	}
	
	private float getNormalizedPitch(ISound sound, SoundPoolEntry entry){
		return (float)MathHelper.clamp_double(sound.getPitch()*entry.getPitch(),0.5D,2D);
	}

	private float getNormalizedVolume(ISound sound, SoundPoolEntry entry, SoundCategory category){
		return (float)MathHelper.clamp_double(sound.getVolume()*entry.getVolume()*getSoundCategoryVolume(category),0D,1D);
	}
	
	private float getSoundCategoryVolume(SoundCategory category){
		return category != null && category != SoundCategory.MASTER?gameSettings.getSoundLevel(category):1F;
	}
	
	private static URL getURLForSoundResource(final ResourceLocation resourceLocation){
		String domain = resourceLocation.getResourceDomain();
		String path = resourceLocation.getResourcePath();
		path = path.substring(0,path.length()-4);
		
		if (domain.equals("hee~music"))return MusicManager.getRandomTrackFromPool(path);
		else if (domain.equals("hee~record"))return MusicManager.getTrackFromRecord(path);
		else return null;
	}
}
