package chylex.hee.mechanics.orb;
import gnu.trove.list.array.TShortArrayList;
import java.util.Arrays;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import chylex.hee.system.collections.weight.IWeightProvider;
import chylex.hee.system.util.ItemPattern;

public class WeightedItem implements IWeightProvider{
	private Item item;
	private short[] possibleDamageValues;
	private int weight;
	
	public WeightedItem(Block block, int damage, int weight){
		this(Item.getItemFromBlock(block), damage, weight);
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
		possibleDamageValues = ArrayUtils.addAll(possibleDamageValues, otherItem.possibleDamageValues);
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
	
	/**
	 * Runs a blacklist pattern on the item. If it only blacklists certain damage values, those will be
	 * removed. If no damage values are left or the pattern blacklists the item itself, it will return
	 * true to signal it should be removed. It will also return the amount of removed damage values.
	 */
	public Pair<Integer, Boolean> runBlacklistPattern(ItemPattern pattern){
		if (pattern.matchesAnyDamage() && pattern.matches(new ItemStack(item, 1, 0))){
			return Pair.of(possibleDamageValues.length, true);
		}
		
		TShortArrayList dmgToRemove = new TShortArrayList(possibleDamageValues.length);
		
		for(short damage:possibleDamageValues){
			if (pattern.matches(new ItemStack(item, 1, damage)))dmgToRemove.add(damage);
		}
		
		possibleDamageValues = ArrayUtils.removeElements(possibleDamageValues, dmgToRemove.toArray());
		return Pair.of(dmgToRemove.size(), possibleDamageValues.length == 0);
	}
	
	@Override
	public boolean equals(Object o){
		return o instanceof WeightedItem && ((WeightedItem)o).item == item;
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
