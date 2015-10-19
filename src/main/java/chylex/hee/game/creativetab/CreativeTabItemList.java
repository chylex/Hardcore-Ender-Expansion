package chylex.hee.game.creativetab;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import chylex.hee.init.ModInitHandler;
import com.google.common.collect.ImmutableList;
import cpw.mods.fml.common.registry.RegistryDelegate;

public class CreativeTabItemList{
	private final ModCreativeTab tab;
	private final List<RegistryDelegate<?>> delegates = new ArrayList<>();
	private ImmutableList<ItemStack> resolvedItems = ImmutableList.of();
	
	public CreativeTabItemList(ModCreativeTab tab){
		this.tab = tab;
		ModInitHandler.afterPreInit(this::resolveItems);
	}
	
	public void add(Object...objects){
		for(Object obj:objects){
			if (obj instanceof Block)delegates.add(((Block)obj).delegate);
			else if (obj instanceof Item)delegates.add(((Item)obj).delegate);
			else throw new IllegalArgumentException("Object is neither a Block nor Item!");
		}
	}
	
	public void resolveItems(){
		List<ItemStack> items = new ArrayList<>(delegates.size());
		
		for(RegistryDelegate<?> delegate:delegates){
			if (delegate.type() == Block.class){
				Block block = (Block)delegate.get();
				block.setCreativeTab(tab);
				block.getSubBlocks(Item.getItemFromBlock(block),tab,items);
			}
			else if (delegate.type() == Item.class){
				Item item = (Item)delegate.get();
				item.setCreativeTab(tab);
				item.getSubItems(item,tab,items);
			}
			else throw new IllegalArgumentException("Delegate contains an invalid object: "+delegate.type());
		}
		
		resolvedItems = ImmutableList.copyOf(items);
	}
	
	public List<ItemStack> getAllItems(){
		return resolvedItems;
	}
}
