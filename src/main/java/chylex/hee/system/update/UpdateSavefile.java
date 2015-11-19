package chylex.hee.system.update;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.system.logging.Log;

class UpdateSavefile{
	private static File file;
	
	public static void prepare(){
		file = new File(HardcoreEnderExpansion.configPath,"HardcoreEnderExpansion.clientsave.cfg");
	}
	
	public String newestModVersion = "";
	public long lastCheckTime = 0L;
	
	public UpdateSavefile(){
		if (file == null)throw new IllegalStateException("Update savefile is null!");
	}
	
	public void load(){
		try{
			if (!file.exists())file.createNewFile();
			
			List<String> lines = FileUtils.readLines(file,StandardCharsets.UTF_8);
			if (lines.size() >= 1)newestModVersion = lines.get(0);
			if (lines.size() >= 2)lastCheckTime = Long.parseLong(lines.get(1));
		}catch(IOException | NumberFormatException e){
			Log.throwable(e,"Could not read notification save data!");
		}
	}
	
	public void save(){
		try{
			List<String> lines = new ArrayList<>(2);
			lines.add(newestModVersion);
			lines.add(String.valueOf(lastCheckTime));
			FileUtils.writeLines(file,lines);
		}catch(IOException e){
			Log.throwable(e,"Could not save notification save data!");
		}
	}
}
