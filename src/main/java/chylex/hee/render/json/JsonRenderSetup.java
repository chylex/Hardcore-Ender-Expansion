package chylex.hee.render.json;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.system.logging.Log;

public class JsonRenderSetup{
	private static final Map<String,RenderBlockData> blocks = new HashMap<>();
	private static final Map<String,RenderItemData> items = new HashMap<>();
	
	public static void addBlock(String modelName){
		blocks.put(modelName,new RenderBlockData(JsonRenderPatterns.BLOCK_STATE_NORMAL,JsonRenderPatterns.BLOCK_CUBE,JsonRenderPatterns.ITEMBLOCK_DEFAULT,modelName,modelName.replace(":",":block/"),modelName.replace(":",":blocks/")));
	}
	
	public static void addBlock(String modelName, String texture){
		blocks.put(modelName,new RenderBlockData(JsonRenderPatterns.BLOCK_STATE_NORMAL,JsonRenderPatterns.BLOCK_CUBE,JsonRenderPatterns.ITEMBLOCK_DEFAULT,modelName,modelName.replace(":",":block/"),texture));
	}
	
	public static void addItem(String modelName){
		items.put(modelName,new RenderItemData(JsonRenderPatterns.ITEM_DEFAULT,modelName.replace(":",":items/")));
	}
	
	public static void addItem(String modelName, JsonRenderPatterns pattern, String texture){
		items.put(modelName,new RenderItemData(pattern,texture));
	}
	
	// SETUP
	
	public static void postSetup(){
		String id = "hardcoreenderexpansion:";
		
		addBlock(id+"obsidian_end","blocks/obsidian");
		addItem(id+"scorching_pickaxe",JsonRenderPatterns.ITEM_TOOL,"hardcoreenderexpansion:items/scorching_pickaxe");
		addItem(id+"exp_bottle_consistent",JsonRenderPatterns.ITEM_DEFAULT,"items/experience_bottle");
	}
	
	public static void generate(){
		Log.info(" === GENERATING JSON FILES === ");
		
		JsonRenderGenerator generator = new JsonRenderGenerator(new File(HardcoreEnderExpansion.sourceFile,"assets/hardcoreenderexpansion"));
		
		for(Entry<String,RenderBlockData> entry:blocks.entrySet()){
			generator.createBlockState(entry.getKey(),entry.getValue().generateStates());
			for(String model:entry.getValue().generateModels())generator.createModelBlock(entry.getKey(),model);
			generator.createModelItem(entry.getKey(),entry.getValue().generateItem());
		}
		
		for(Entry<String,RenderItemData> entry:items.entrySet()){
			generator.createModelItem(entry.getKey(),entry.getValue().generate());
		}
			
		Log.info(" === FINISHED GENERATING JSON FILES === ");
	}
	
	// CLASSES
	
	private static class RenderBlockData{
		final JsonRenderPatterns states;
		final Map<String,JsonRenderPatterns> models = new HashMap<>();
		final JsonRenderPatterns itemModel;
		final String blockName;
		final String modelName;
		
		RenderBlockData(JsonRenderPatterns states, JsonRenderPatterns blockModel, JsonRenderPatterns itemModel, String blockName, String modelName, String texture){
			this.states = states;
			this.itemModel = itemModel;
			this.blockName = blockName;
			this.modelName = modelName;
			addModel(texture,blockModel);
		}
		
		void addModel(String name, JsonRenderPatterns pattern){
			models.put(name,pattern);
		}
		
		String generateStates(){
			return states.generate(blockName);
		}
		
		String[] generateModels(){
			List<String> str = new ArrayList<>();
			for(Entry<String,JsonRenderPatterns> entry:models.entrySet())str.add(entry.getValue().generate(entry.getKey()));
			return str.toArray(new String[str.size()]);
		}
		
		String generateItem(){
			return itemModel.generate(modelName);
		}
	}
	
	private static class RenderItemData{
		final JsonRenderPatterns model;
		final String texture;
		
		RenderItemData(JsonRenderPatterns model, String texture){
			this.model = model;
			this.texture = texture;
		}
		
		String generate(){
			return model.generate(texture);
		}
	}
}
