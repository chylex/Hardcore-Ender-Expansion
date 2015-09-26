package chylex.hee.world.loot;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import chylex.hee.world.loot.LootTable.LootItem;
import chylex.hee.world.loot.interfaces.IItemPostProcessor;

public abstract class LootTable<ITEM extends LootItem>{
	protected final List<IItemPostProcessor> postProcessors = new ArrayList<>();
	
	abstract ITEM createLootItem(Item item);
	
	public final ITEM addLoot(Block block){
		Item item = Item.getItemFromBlock(block);
		if (item == null)throw new IllegalArgumentException("Invalid block, no item found: "+block);
		return createLootItem(item);
	}
	
	public final ITEM addLoot(Item item){
		return createLootItem(item);
	}
	
	public final void addPostProcessor(IItemPostProcessor processor){
		postProcessors.add(processor);
	}
	
	public abstract class LootItem{
		protected final Item item;
		
		LootItem(Item item){
			if (item == null)throw new IllegalArgumentException("Item cannot be null!");
			this.item = item;
		}
		
		public abstract ItemStack generate(Object obj, Random rand);
		
		@Override
		public boolean equals(Object obj){
			return obj instanceof LootTable.LootItem && ((LootTable.LootItem)obj).item == item;
		}
		
		@Override
		public int hashCode(){
			return Item.getIdFromItem(item);
		}
	}
}
