package chylex.hee.render.json;

public final class JsonRenderPatterns{
	public static final JsonRenderPatterns ITEM_DEFAULT = new JsonRenderPatterns(
		"{'parent':'builtin/generated','textures':{'layer0':'$0'},'display':{'thirdperson':{'rotation':[-90,0,0],'translation':[0,1,-3],'scale':[0.55,0.55,0.55]},'firstperson':{'rotation':[0,-135,25],'translation':[0,4,2],'scale':[1.7,1.7,1.7]}}}"
	); // textureName
	
	public static final JsonRenderPatterns ITEM_TOOL = new JsonRenderPatterns(
		"{'parent':'builtin/generated','textures':{'layer0':'$0'},'display':{'thirdperson':{'rotation':[0,90,-35],'translation':[0,1.25,-3.5],'scale':[0.85,0.85,0.85]},'firstperson':{'rotation':[0,-135,25],'translation':[0,4,2],'scale':[1.7,1.7,1.7]}}}"
	); // textureName
	
	public static final JsonRenderPatterns ITEMBLOCK_DEFAULT = new JsonRenderPatterns(
		"{'parent':'$0','display':{'thirdperson':{'rotation':[10,-45,170],'translation':[0,1.5,-2.75],'scale':[0.375,0.375,0.375]}}}"
	); // blockModel
	
	public final String contents;
	
	private JsonRenderPatterns(String fileData){
		contents = fileData.replace('\'','"');
	}
	
	public String generate(Object...data){
		String text = contents;
		for(int a = data.length-1; a >= 0; a--)text = text.replace("$"+a,data[a] == null ? "null" : String.valueOf(data[a]));
		return text;
	}
}
