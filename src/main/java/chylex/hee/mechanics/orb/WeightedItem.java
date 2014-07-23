package chylex.hee.mechanics.orb;
import gnu.trove.set.hash.TShortHashSet;
import java.util.Arrays;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import chylex.hee.system.weight.IWeightProvider;

public class WeightedItem implements IWeightProvider{
	private Item item;
	private short[] possibleDamageValues;
	private int weight;
	
	public WeightedItem(Block block, int damage, int weight){
		this(Item.getItemFromBlock(block),damage,weight);
	}
	
	public WeightedItem(Item item, int damage, int weight){
		this.item = item;
		this.possibleDamageValues = new short[]{ (short)damage };
		this.weight = weight;
	}
	
	public Item getItem(){
		return item;
	}
	
	public void combineDamageValues(WeightedItem otherItem){
		TShortHashSet newDamageValues = new TShortHashSet();
		newDamageValues.addAll(possibleDamageValues);
		newDamageValues.addAll(otherItem.possibleDamageValues);
		possibleDamageValues = newDamageValues.toArray();
	}
	
	public short[] getDamageValues(){
		return possibleDamageValues;
	}
	
	public void setWeight(int weight){
		this.weight = weight;
	}
	
	@Override
	public int getWeight(){
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
