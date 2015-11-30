package chylex.hee.game.creativetab;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import chylex.hee.init.ModInitHandler;
import com.google.common.collect.ImmutableList;
import cpw.mods.fml.common.registry.RegistryDelegate;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CreativeTabItemList{
	private final ModCreativeTab tab;
	private final List<RegistryDelegate<?>> delegates = new ArrayList<>();
	private ImmutableList<ItemStack> resolvedItems = ImmutableList.of();
	
	public CreativeTabItemList(ModCreativeTab tab){
		this.tab = tab;
	}
	
	public void add(Object...objects){
		for(Object obj:objects){
			if (obj instanceof Block)delegates.add(((Block)obj).delegate);
			else if (obj instanceof Item)delegates.add(((Item)obj).delegate);
			else throw new IllegalArgumentException("Object is neither a Block nor Item!");
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void setupClient(){
		ModInitHandler.afterPreInit(this::resolveItems);
	}
	
	@SideOnly(Side.CLIENT)
	private void resolveItems(){
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

	@SideOnly(Side.CLIENT)
	public List<ItemStack> getAllItems(){
		return resolvedItems;
	}
}
