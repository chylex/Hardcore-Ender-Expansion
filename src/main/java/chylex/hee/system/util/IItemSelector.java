package chylex.hee.system.util;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@FunctionalInterface
public interface IItemSelector{
	boolean isValid(ItemStack is);
	
	public static interface IRepresentativeItemSelector extends IItemSelector{
		ItemStack getRepresentativeItem();
		
		public static class SimpleItemSelector implements IRepresentativeItemSelector{
			private final Item item;
			
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
		
		public static class ItemStackSelector implements IRepresentativeItemSelector{
			private final ItemStack is;
			
			public ItemStackSelector(ItemStack is){
				this.is = is;
			}
			
			@Override
			public boolean isValid(ItemStack is){
				return ItemStack.areItemStacksEqual(this.is,is);
			}
			
			@Override
			public ItemStack getRepresentativeItem(){
				return is;
			}
		}
	}
}
