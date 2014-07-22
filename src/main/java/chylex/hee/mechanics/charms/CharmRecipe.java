package chylex.hee.mechanics.charms;
import gnu.trove.map.hash.TObjectByteHashMap;
import java.util.ArrayList;
import java.util.List;

public class CharmRecipe{
	public final byte id;
	private final TObjectByteHashMap<RuneType> runes = new TObjectByteHashMap<>();
	private final TObjectByteHashMap<String> properties = new TObjectByteHashMap<>();
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
		if (value >= 128)throw new IllegalArgumentException("CharmRecipe prop value cannot be larger than 127!");
		properties.put(name,(byte)value);
		return this;
	}
	
	public short getProp(String name){
		return properties.containsKey(name) ? properties.get(name) : -1;
	}
	
	public boolean checkRunes(RuneType[] runes){
		if (runes.length != runeAmount)return false;
		
		List<RuneType> runeList = new ArrayList<RuneType>();
		for(RuneType rune:runes)runeList.add(rune);
		
		for(RuneType runeType:this.runes.keySet()){
			for(int amt = 0, total = this.runes.get(runeType); amt < total; amt++){
				if (!runeList.remove(runeType))return false;
			}
		}
		
		return runeList.isEmpty();
	}
}
