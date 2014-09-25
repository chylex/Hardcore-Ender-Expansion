package chylex.hee.system.savedata;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import chylex.hee.system.logging.Log;
import chylex.hee.system.logging.Stopwatch;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public final class WorldDataHandler{
	private static WorldDataHandler instance;
	
	public static void register(){
		if (instance == null)MinecraftForge.EVENT_BUS.register(instance = new WorldDataHandler());
	}
	
	public static <T> T get(Class<? extends WorldSavefile> cls){
		Stopwatch.timeAverage("WorldDataHandler - get",4096);
		
		WorldSavefile savefile = instance.cache.get(cls);
		
		if (savefile == null){
			try{
				instance.cache.put(cls,savefile = cls.newInstance());
				
				File file = new File(instance.worldSaveDir,savefile.filename);
				
				if (file.exists()){
					try{
						savefile.loadFromNBT(CompressedStreamTools.readCompressed(new FileInputStream(file)));
					}catch(IOException ioe){
						Log.throwable(ioe,"Error reading NBT file - $0",cls.getName());
					}
				}
				else savefile.loadFromNBT(new NBTTagCompound());
			}catch(InstantiationException | IllegalAccessException e){
				throw new RuntimeException("Could not construct a new instance of WorldSavefile - "+cls.getName(),e);
			}
		}
		
		Stopwatch.finish("WorldDataHandler - get");
		
		return (T)savefile;
	}
	
	private final Map<Class<? extends WorldSavefile>,WorldSavefile> cache = new IdentityHashMap<>();
	private File worldSaveDir;
	private String worldIdentifier = "";
	
	private WorldDataHandler(){}
	
	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load e){
		if (e.world.isRemote)return;
		
		String id = e.world.getSaveHandler().getWorldDirectoryName()+e.world.getWorldInfo().getWorldName()+e.world.getWorldInfo().getSeed();
		
		if (!worldIdentifier.equals(id)){
			Log.debug("Clearing cache - old $0, new $1",worldIdentifier,id);
			cache.clear();
			worldIdentifier = id;
			
			File root = DimensionManager.getCurrentSaveRootDirectory();
			
			if (root != null){
				File oldSaveDir = new File(root,"hed");
				worldSaveDir = new File(root,"hee");
				
				if (oldSaveDir.exists()){
					if (!worldSaveDir.exists())oldSaveDir.renameTo(worldSaveDir);
					else oldSaveDir.delete();
				}
				
				if (!worldSaveDir.exists())worldSaveDir.mkdirs();
			}
		}
	}
	
	@SubscribeEvent
	public void onWorldSave(WorldEvent.Save e){
		for(WorldSavefile savefile:cache.values()){
			if (savefile.wasModified()){
				NBTTagCompound nbt = new NBTTagCompound();
				savefile.saveToNBT(nbt);
				
				try{
					CompressedStreamTools.writeCompressed(nbt,new FileOutputStream(new File(worldSaveDir,savefile.filename)));
				}catch(Exception ex){
					Log.throwable(ex,"Error writing WorldData file $0",savefile.getClass());
				}
			}
		}
	}
}
