package chylex.hee.system.sound;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.io.FilenameUtils;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.system.util.DragonUtil;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class MusicManager{
	private static final short MUSIC_INTERVAL = 1680;
	public static final MusicManager instance = new MusicManager();
	public static boolean enableMusic = true;
	
	public static void register(){
		MinecraftForge.EVENT_BUS.register(instance);
	}
	
	private Random rand = new Random();
	private boolean hasLoaded = false;
	private String lastBgMusic;
	private byte tickTimer;
	private short randomMusicTimer = MUSIC_INTERVAL;
	private List<MusicPool> poolList = new ArrayList<>();
	private MusicPool poolDragonCalm = new MusicPool(poolList,"dragoncalm"),
					  poolDragonAngry = new MusicPool(poolList,"dragonangry"),
					  poolTower = new MusicPool(poolList,"tower"),
					  poolFireFiend = new MusicPool(poolList,"firefiend"),
					  poolEnderDemon = new MusicPool(poolList,"enderdemon"),
					  poolRandom = new MusicPool(poolList,"random"),
					  poolFallback = new MusicPool();
	
	private MusicManager(){}
	
	@SubscribeEvent
	public void onSoundLoad(SoundLoadEvent e){
		if (hasLoaded)return;
		
		/*new Thread(new Runnable(){
			Minecraft mc;
			Field fieldLoaded;
			
			{
				mc = Minecraft.getMinecraft();
				
				for(Field field:SoundManager.class.getDeclaredFields()){
					if (field.getType().isPrimitive()){
						field.setAccessible(true);
						fieldLoaded = field;
						break;
					}
				}
			}
			
			@Override
			public void run(){
				TimeMeasurement.start("SoundManagerOverrideThread");
				
				Field[] fields = Minecraft.class.getDeclaredFields();

				for(int a = 50; a < fields.length; a++){
					if (MusicTicker.class.isAssignableFrom(fields[a].getType())){
						fields[a].setAccessible(true);

						try{
							fields[a].set(mc,new CustomMusicTicker(mc));
							DragonUtil.info("Succesfully replaced MusicTicker.");
						}catch(Exception ex){
							ex.printStackTrace();
							DragonUtil.severe("Error loading music system!");
							return;
						}

						break;
					}
				}
				
				/*fields = SoundHandler.class.getDeclaredFields();

				for(int a = 4; a < fields.length; a++){
					if (SoundManager.class.isAssignableFrom(fields[a].getType())){
						fields[a].setAccessible(true);
						
						try{
							SoundManager oldSndMan = (SoundManager)fields[a].get(mc.getSoundHandler());
							while(true){
								if (!fieldLoaded.getBoolean(oldSndMan))continue;
								
								CustomSoundManager newSndMan = new CustomSoundManager(mc.getSoundHandler(),mc.gameSettings);
								
								for(Field sndManField:SoundManager.class.getDeclaredFields()){
									if ((sndManField.getModifiers()&Modifier.STATIC) == Modifier.STATIC)continue;
									sndManField.setAccessible(true);
									sndManField.set(newSndMan,sndManField.get(oldSndMan));
								}
								
								fields[a].set(mc.getSoundHandler(),newSndMan);
								DragonUtil.info("Succesfully replaced SoundManager.");
								
								break;
							}
						}catch(Exception ex){
							ex.printStackTrace();
							DragonUtil.severe("Error loading music system!");
							return;
						}
						
						break;
					}
				}*/
		/*		
				TimeMeasurement.finish("SoundManagerOverrideThread");
			}
		}).start();*/
		
		DragonUtil.warning("Playing MC 1.7.2, HEE music will not be activated!");
		
		//loadMusicFromZip();
		//loadMusicFromFolder();
		// TODO fix mess
		
		hasLoaded = true;
	}
	
	private void loadMusicFromZip(){
		if (!enableMusic)return;
		
		CodeSource src = MusicManager.class.getProtectionDomain().getCodeSource();
		if (src == null){
			DragonUtil.severe("Error loading music - CodeSource is null!");
			return;
		}

		ZipFile zip = null;
		try{
			String file = src.getLocation().getFile();
			if (!file.contains("!")){
				DragonUtil.warning("Invalid mod file, skipping music...");
				return;
			}
			
			zip = new ZipFile(new File(URLDecoder.decode(file.substring(6).split("!")[0],"UTF-8")));
		}catch(IOException e){
			e.printStackTrace();
			DragonUtil.severe("Error loading music - Error declaring ZipFile");
			return;
		}
		
		short loadedTracks = 0,assetDirLen = (short)("assets/hardcoreenderexpansion/music/".length());
		URL track;

		Enumeration<? extends ZipEntry> entries = zip.entries();
		while(entries.hasMoreElements()){
			String name = entries.nextElement().getName();
			if (!name.startsWith("assets/hardcoreenderexpansion/music/") || name.endsWith("/"))continue;

			if (null == (track = MusicManager.class.getResource("/assets/hardcoreenderexpansion/music/"+(name = name.substring(assetDirLen)))))continue;
			getPool(name).addTrack(FilenameUtils.removeExtension(name),track);
			++loadedTracks;
		}
		
		try{
			zip.close();
		}catch(IOException e){
			e.printStackTrace();
			DragonUtil.severe("Error closing the zip file!");
		}
		
		DragonUtil.info("Loaded %0% music tracks from zip",loadedTracks);
	}
	
	private void loadMusicFromFolder(){
		if (!enableMusic)return;
		
		File musicDir = new File(HardcoreEnderExpansion.configPath,"HardcoreEnderExpansionMusic");
		if (!musicDir.exists())musicDir.mkdir();
		
		short loadedTracks = 0;
		String name;
		
		File[] fileList = musicDir.listFiles();
		if (fileList == null){
			DragonUtil.severe(".minecraft/config/HardcoreEnderExpansionMusic/ is invalid!");
			return;
		}
		
		for(File file:fileList){
			try{
				getPool(name = file.getName()).addTrack(FilenameUtils.removeExtension(name),file.toURI().toURL());
				++loadedTracks;
			}catch(MalformedURLException e){
				e.printStackTrace();
				DragonUtil.severe("Error processing filename: %0%",file.toString());
			}
		}
		
		DragonUtil.info("Loaded %0% music tracks from folder",loadedTracks);
	}
	
	private MusicPool getPool(String filename){
		for(MusicPool pool:poolList){
			if (pool.isValidFile(filename))return pool;
		}
		
		DragonUtil.warning("Incorrectly named music track: %0%. Adding to unused fallback pool.",filename);
		return poolFallback;
	}
	
	public static boolean hasPoolAnyTracks(String poolName){
		for(MusicPool pool:instance.poolList){
			if (pool.getPrefix().equals(poolName))return pool.listTrackEntries().size() > 0;
		}
		
		return false;
	}
	
	public static URL getRandomTrackFromPool(String poolName){
		for(MusicPool pool:instance.poolList){
			if (pool.getPrefix().equals(poolName)){
				List<Entry<String,URL>> entries = new ArrayList<>(pool.listTrackEntries());
				if (entries.size() == 0)return null;
				
				Entry<String,URL> pick = entries.get(instance.rand.nextInt(entries.size()));
				for(int i = 0; i < 10; i++){
					if (!pick.getKey().equals(instance.lastBgMusic))break;
					pick = entries.get(instance.rand.nextInt(entries.size()));
				}
				
				instance.lastBgMusic = pick.getKey();
				return pick.getValue();
			}
		}
		
		return null;
	}
	
	public static boolean checkTrackExists(String trackName){
		return getTrackFromRecord(trackName) != null;
	}
	
	public static URL getTrackFromRecord(String trackName){
		for(MusicPool pool:instance.poolList){
			if (pool.checkTrackExists(trackName))return pool.getURL(trackName);
		}
		
		return null;
	}
}
