package chylex.hee.system.util;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface IItemSelector{
	boolean isValid(ItemStack is);
	
	public static interface IRepresentativeItemSelector extends IItemSelector{
		ItemStack getRepresentativeItem();
		
		public static class SimpleItemSelector implements IRepresentativeItemSelector{
			private Item item;
			
			public SimpleItemSelector(Block block){
				this.item = Item.getItemFromBlock(block);
			}
			
			public SimpleItemSelector(Item item){
				this.item = item;
			}
			
			@Override
			public boolean isValid(ItemStack is){
				return is.getItem() == item;
			}
	
			@Override
			public ItemStack getRepresentativeItem(){
				return new ItemStack(item);
			}
		}
	}
}
