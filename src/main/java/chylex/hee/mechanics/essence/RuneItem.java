package chylex.hee.mechanics.essence;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import chylex.hee.system.util.IItemSelector;

public class RuneItem{
	public final IItemSelector selector;
	private ItemStack showedItem;
	public byte indexInArray;
	public final String soundEffect;
	
	public RuneItem(IItemSelector selector, ItemStack showedItem, String soundEffect){
		this.selector = selector;
		this.showedItem = showedItem;
		this.soundEffect = soundEffect;
	}
	
	public RuneItem(final Block block, String soundEffect){
		this(Item.getItemFromBlock(block), soundEffect);
	}
	
	public RuneItem(final Block block, final int damage, String soundEffect){
		this(Item.getItemFromBlock(block), damage, soundEffect);
	}
	
	public RuneItem(final Item item, String soundEffect){
		this.selector = is -> is.getItem() == item;
		this.showedItem = new ItemStack(item, 1, 0);
		this.soundEffect = soundEffect;
	}
	
	public RuneItem(final Item item, final int damage, String soundEffect){
		this.selector = is -> is.getItem() == item && is.getItemDamage() == damage;
		this.showedItem = new ItemStack(item, 1, damage);
		this.soundEffect = soundEffect;
	}
	
	public RuneItem setShowcaseItem(ItemStack showedItem){
		this.showedItem = showedItem;
		return this;
	}
	
	public ItemStack getShowcaseItem(){
		return showedItem;
	}
}
