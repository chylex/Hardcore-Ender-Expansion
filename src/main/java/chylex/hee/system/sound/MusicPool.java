package chylex.hee.system.sound;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MusicPool{
	private final String filePrefix;
	private final Map<String,URL> poolMap = new HashMap<>();
	
	public MusicPool(){
		this.filePrefix = "";
	}
	
	public MusicPool(List<MusicPool> poolList, String filePrefix){
		poolList.add(this);
		this.filePrefix = filePrefix;
	}
	
	public void addTrack(String identifier, URL url){
		if (url != null)poolMap.put(identifier,url);
	}
	
	public Set<Entry<String,URL>> listTrackEntries(){
		return poolMap.entrySet();
	}
	
	public boolean checkTrackExists(String identifier){
		return poolMap.containsKey(identifier);
	}
	
	public URL getURL(String identifier){
		return poolMap.get(identifier);
	}
	
	public String getPrefix(){
		return filePrefix;
	}
	
	public boolean isValidFile(String filename){
		return filename.startsWith(filePrefix+"_");
	}
}
