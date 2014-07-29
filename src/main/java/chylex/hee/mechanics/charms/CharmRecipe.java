package chylex.hee.mechanics.charms;
import gnu.trove.map.hash.TObjectByteHashMap;
import gnu.trove.map.hash.TObjectFloatHashMap;
import java.util.ArrayList;
import java.util.List;

public class CharmRecipe{
	public final byte id;
	private final TObjectByteHashMap<RuneType> runes = new TObjectByteHashMap<>();
	private final TObjectFloatHashMap<String> properties = new TObjectFloatHashMap<>();
	private byte runeAmount;
	
	public CharmRecipe(int id){
		this.id = (byte)id;
	}
	
	public CharmRecipe rune(RuneType rune){
		return rune(rune,1);
	}
	
	public CharmRecipe rune(RuneType rune, int amount){
		if (runes.containsKey(rune))runeAmount -= runes.remove(rune);
		runes.put(rune,(byte)amount);
		runeAmount += amount;
		return this;
	}
	
	public CharmRecipe prop(String name, float value){
		properties.put(name,value);
		return this;
	}
	
	public float getProp(String name){
		return properties.containsKey(name) ? properties.get(name) : -1F;
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
