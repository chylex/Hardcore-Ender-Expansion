package chylex.hee.mechanics.compendium.content;
import java.util.Arrays;

public enum LoreTexts{
	UNKNOWN("oops", 0),
	STRONGHOLD("stronghold", 3);
	
	final String name;
	final int count;
	
	private LoreTexts(String name, int count){
		this.name = name;
		this.count = count;
	}
	
	public int getTextCount(){
		return count;
	}
	
	public String getTitle(){
		return name;
	}
	
	public String getUnlocalizedName(int index){
		return "ec.note."+name+"."+index;
	}
	
	public static LoreTexts fromTitle(String title){
		return Arrays.stream(LoreTexts.values()).filter(category -> category.getTitle().equals(title)).findAny().orElse(UNKNOWN);
	}
}
