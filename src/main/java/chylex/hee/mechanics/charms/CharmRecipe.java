package chylex.hee.mechanics.charms;
import gnu.trove.iterator.TObjectByteIterator;
import gnu.trove.map.hash.TObjectByteHashMap;
import gnu.trove.map.hash.TObjectFloatHashMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		
		List<RuneType> runeList = new ArrayList<>();
		for(RuneType rune:runes)runeList.add(rune);
		
		for(TObjectByteIterator<RuneType> iter = this.runes.iterator(); iter.hasNext();){
			iter.advance();
			
			for(int amt = 0, total = iter.value(); amt < total; amt++){
				if (!runeList.remove(iter.key()))return false;
			}
		}
		
		return runeList.isEmpty();
	}
	
	public Map<RuneType,Byte> getRunes(){
		Map<RuneType,Byte> runes = new HashMap<>();
		for(RuneType type:this.runes.keySet())runes.put(type,this.runes.get(type));
		return runes;
	}
}
