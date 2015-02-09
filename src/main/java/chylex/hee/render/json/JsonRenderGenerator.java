package chylex.hee.render.json;
import java.io.File;
import java.io.PrintWriter;

public class JsonRenderGenerator{
	private final File fileBlockStates;
	private final File fileModelsBlocks;
	private final File fileModelsItems;
	
	public JsonRenderGenerator(File root){
		fileBlockStates = new File(root,"blockstates");
		fileBlockStates.mkdir();
		
		File models = new File(root,"models");
		models.mkdir();
		
		fileModelsBlocks = new File(models,"block");
		fileModelsBlocks.mkdir();
		fileModelsItems = new File(models,"item");
		fileModelsItems.mkdir();
	}
	
	public void createBlockState(String stateName, String contents){
		try(PrintWriter file = new PrintWriter(new File(fileBlockStates,stateName.substring(stateName.indexOf(':')+1)+".json"))){
			file.println(contents);
		}catch(Exception e){}
	}
	
	public void createModelBlock(String modelName, String contents){
		try(PrintWriter file = new PrintWriter(new File(fileModelsBlocks,modelName.substring(modelName.indexOf(':')+1)+".json"))){
			file.println(contents);
		}catch(Exception e){}
	}
	
	public void createModelItem(String modelName, String contents){
		try(PrintWriter file = new PrintWriter(new File(fileModelsItems,modelName.substring(modelName.indexOf(':')+1)+".json"))){
			file.println(contents);
		}catch(Exception e){}
	}
}
