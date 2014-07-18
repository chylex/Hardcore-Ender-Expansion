package chylex.hee.mechanics.orb;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import chylex.hee.system.weight.IWeightProvider;

public class WeightedItem implements IWeightProvider{
	private Item item;
	private short[] possibleDamageValues;
	private short weight;
	
	public WeightedItem(Block block, int damage, int weight){
		this(Item.getItemFromBlock(block),damage,weight);
	}
	
	public WeightedItem(Item item, int damage, int weight){
		this.item = item;
		this.possibleDamageValues = new short[]{ (short)damage };
		this.weight = (short)weight;
	}
	
	public Item getItem(){
		return item;
	}
	
	public void combineDamageValues(WeightedItem otherItem){
		Set<Short> newDamageValues = new HashSet<>();

		for(short currentDmg:possibleDamageValues)newDamageValues.add(currentDmg);
		for(short otherDmg:otherItem.possibleDamageValues)newDamageValues.add(otherDmg);
		
		possibleDamageValues = new short[newDamageValues.size()];
		
		int a = -1;
		for(Iterator<Short> iter = newDamageValues.iterator(); iter.hasNext();)possibleDamageValues[++a] = iter.next();
	}
	
	public short[] getDamageValues(){
		return possibleDamageValues;
	}
	
	public void setWeight(short weight){
		this.weight = weight;
	}
	
	@Override
	public short getWeight(){
		return weight;
	}
	
	@Override
	public boolean equals(Object o){
		if (o instanceof WeightedItem)return ((WeightedItem)o).item == item;
		return false;
	}
	
	@Override
	public int hashCode(){
		return item.hashCode();
	}
	
	@Override
	public String toString(){
		return new StringBuilder().append("WeightedItemID: ITEM ").append(item.getUnlocalizedName()).append(", DMG ").append(Arrays.toString(possibleDamageValues)).append(", WEIGHT ").append(weight).toString();
	}
}
