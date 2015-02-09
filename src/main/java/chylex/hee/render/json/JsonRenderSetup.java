package chylex.hee.render.json;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.system.logging.Log;

public class JsonRenderSetup{
	private static final Map<String,JsonRenderPatterns> items = new HashMap<>();
	
	public static void addItem(String modelName){
		items.put(modelName,JsonRenderPatterns.ITEM_DEFAULT);
	}
	
	public static void postSetup(){
		
	}
	
	public static void generate(){
		Log.info(" === GENERATING JSON FILES === ");
		
		JsonRenderGenerator generator = new JsonRenderGenerator(new File(HardcoreEnderExpansion.sourceFile,"assets/hardcoreenderexpansion"));
		
		for(Entry<String,JsonRenderPatterns> entry:items.entrySet()){
			generator.createModelItem(entry.getKey(),entry.getValue().generate(entry.getKey().replace(":",":items/")));
		}
			
		Log.info(" === FINISHED GENERATING JSON FILES === ");
	}
}
