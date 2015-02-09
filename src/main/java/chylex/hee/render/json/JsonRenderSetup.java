package chylex.hee.render.json;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.system.logging.Log;

public class JsonRenderSetup{
	private static final Map<String,RenderItemData> items = new HashMap<>();
	
	public static void addItem(String modelName){
		items.put(modelName,new RenderItemData(JsonRenderPatterns.ITEM_DEFAULT,modelName.replace(":",":items/")));
	}
	
	public static void addItem(String modelName, JsonRenderPatterns pattern, String texture){
		items.put(modelName,new RenderItemData(pattern,texture));
	}
	
	// SETUP
	
	public static void postSetup(){
		String id = "hardcoreenderexpansion:";
		
		addItem(id+"scorching_pickaxe",JsonRenderPatterns.ITEM_TOOL,"hardcoreenderexpansion:items/scorching_pickaxe");
		addItem(id+"exp_bottle_consistent",JsonRenderPatterns.ITEM_DEFAULT,"items/experience_bottle");
	}
	
	public static void generate(){
		Log.info(" === GENERATING JSON FILES === ");
		
		JsonRenderGenerator generator = new JsonRenderGenerator(new File(HardcoreEnderExpansion.sourceFile,"assets/hardcoreenderexpansion"));
		
		for(Entry<String,RenderItemData> entry:items.entrySet()){
			generator.createModelItem(entry.getKey(),entry.getValue().generate());
		}
			
		Log.info(" === FINISHED GENERATING JSON FILES === ");
	}
	
	// CLASSES
	
	private static class RenderItemData{
		final JsonRenderPatterns pattern;
		final String texture;
		
		RenderItemData(JsonRenderPatterns pattern, String texture){
			this.pattern = pattern;
			this.texture = texture;
		}
		
		String generate(){
			return pattern.generate(texture);
		}
	}
}
