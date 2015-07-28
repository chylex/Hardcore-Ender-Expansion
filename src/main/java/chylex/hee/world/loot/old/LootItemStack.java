package chylex.hee.world.loot.old;
import java.util.Random;
import chylex.hee.system.collections.weight.IWeightProvider;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Item stack randomizer.
 */
@Deprecated
public class LootItemStack implements IWeightProvider{
	private Item item;
	private short minDamage = 0, maxDamage = 0;
	private byte minAmount = 1, maxAmount = 1;
	private short weight = 1;
	
	@Deprecated
	public LootItemStack(Block block){
		this.item = Item.getItemFromBlock(block);
		if (item == null)throw new IllegalArgumentException("Invalid LootItemStack block, no item found: "+block);
	}

	@Deprecated
	public LootItemStack(Item item){
		this.item = item;
	}

	@Deprecated
	public Item getItem(){
		return item;
	}

	@Deprecated
	public LootItemStack setDamage(int min, int max){
		this.minDamage = (short)Math.max(0,min);
		this.maxDamage = (short)Math.max(0,max);
		return this;
	}

	@Deprecated
	public LootItemStack setDamage(int damage){
		this.minDamage = this.maxDamage = (short)Math.max(0,damage);
		return this;
	}

	@Deprecated
	public LootItemStack setAmount(int min, int max){
		this.minAmount = (byte)Math.max(1,min);
		this.maxAmount = (byte)Math.max(1,max);
		return this;
	}

	@Deprecated
	public LootItemStack setAmount(int amount){
		this.minAmount = this.maxAmount = (byte)Math.max(1,amount);
		return this;
	}

	@Deprecated
	public LootItemStack setWeight(int weight){
		this.weight = (short)Math.max(1,weight);
		return this;
	}
	
	@Override
	public int getWeight(){
		return weight;
	}

	@Deprecated
	public ItemStack getIS(Random rand){
		return new ItemStack(item,rand.nextInt(maxAmount-minAmount+1)+minAmount,rand.nextInt(maxDamage-minDamage+1)+minDamage);
	}
}
