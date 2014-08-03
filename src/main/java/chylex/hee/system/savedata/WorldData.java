package chylex.hee.system.savedata;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import chylex.hee.system.logging.Log;

public class WorldData{
	private static Map<String,WorldData> cache = new HashMap<>();
	
	public static WorldData get(World world){
		String identifier = world.getWorldInfo().getWorldName()+world.getSaveHandler().getWorldDirectoryName()+world.getWorldInfo().getSeed();
		
		WorldData wd = cache.get(identifier);
		if (wd == null)cache.put(identifier,wd = new WorldData(world));
		return wd;
	}
	
	private World world;
	private File saveDirectory;
	
	private WorldData(World world){
		this.world = world;
		
		File root = DimensionManager.getCurrentSaveRootDirectory();
		if (root != null){
			saveDirectory = new File(root,"hed"); // TODO rename to hee
			saveDirectory.mkdirs();
		}
	}
	
	public NBTTagCompound readFile(String filename){
		if (saveDirectory == null)return new NBTTagCompound();
		
		File dataFile = new File(saveDirectory,filename);
		if (!dataFile.exists())saveFile(filename,new NBTTagCompound());
		
		if (dataFile.exists()){
			try{
				return CompressedStreamTools.readCompressed(new FileInputStream(dataFile));
			}catch(Exception e){
				Log.throwable(e,"Error reading WorldData file $0",filename);
			}
		}
		return new NBTTagCompound();
	}
	
	public void saveFile(String filename, NBTTagCompound nbt){
		if (saveDirectory == null)return;
		
		try{
			CompressedStreamTools.writeCompressed(nbt,new FileOutputStream(new File(saveDirectory,filename)));
		}catch(Exception e){
			Log.throwable(e,"Error writing WorldData file $0",filename);
		}
	}
	
	public boolean equals(WorldData data){
		return data.world.getWorldInfo().getWorldName().equals(world.getWorldInfo().getWorldName()) &&
			   data.world.getWorldInfo().getSeed() == world.getWorldInfo().getSeed();
	}
	
	@Override
	public boolean equals(Object o){
		if (o instanceof WorldData)return equals((WorldData)o);
		return false;
	}

	@Override
	public int hashCode(){
		return 31*world.getWorldInfo().getWorldName().hashCode()+(int)world.getWorldInfo().getSeed();
	}
}
