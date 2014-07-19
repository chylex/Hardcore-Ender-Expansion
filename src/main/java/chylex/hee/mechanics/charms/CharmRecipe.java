package chylex.hee.mechanics.charms;
import java.util.HashMap;
import java.util.Map;

public class CharmRecipe{
	public final byte id;
	private final Map<RuneType,Byte> runes = new HashMap<>();
	private final Map<String,Short> properties = new HashMap<>();
	
	public CharmRecipe(int id){
		this.id = (byte)id;
	}
	
	public CharmRecipe rune(RuneType rune){
		return rune(rune,1);
	}
	
	public CharmRecipe rune(RuneType rune, int amount){
		runes.put(rune,(byte)amount);
		return this;
	}
	
	public CharmRecipe prop(String name, int value){
		properties.put(name,(short)value);
		return this;
	}
}
