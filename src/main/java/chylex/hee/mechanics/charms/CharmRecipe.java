package chylex.hee.mechanics.charms;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CharmRecipe{
	public final byte id;
	private final Map<RuneType,Byte> runes = new HashMap<>();
	private final Map<String,Short> properties = new HashMap<>();
	private byte runeAmount = 0;
	
	public CharmRecipe(int id){
		this.id = (byte)id;
	}
	
	public CharmRecipe rune(RuneType rune){
		return rune(rune,1);
	}
	
	public CharmRecipe rune(RuneType rune, int amount){
		runes.put(rune,(byte)amount);
		runeAmount = (byte)runes.size();
		return this;
	}
	
	public CharmRecipe prop(String name, int value){
		properties.put(name,(short)value);
		return this;
	}
	
	public short getProp(String name){
		return properties.containsKey(name) ? properties.get(name) : -1;
	}
	
	public boolean checkRunes(RuneType[] runes){
		if (runes.length != runeAmount)return false;
		
		List<RuneType> runeList = new ArrayList<RuneType>();
		for(RuneType rune:runes)runeList.add(rune);
		
		for(Entry<RuneType,Byte> entry:this.runes.entrySet()){
			for(int amt = 0; amt < entry.getValue(); amt++){
				if (!runeList.remove(entry.getKey()))return false;
			}
		}
		
		return runeList.isEmpty();
	}
}
