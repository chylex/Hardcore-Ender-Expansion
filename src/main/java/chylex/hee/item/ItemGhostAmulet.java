package chylex.hee.item;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemGhostAmulet extends Item{
	public ItemGhostAmulet(){
		setHasSubtypes(true);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack is){
		return is.getItemDamage() == 0 ? super.getUnlocalizedName(is)+".impure" : super.getUnlocalizedName(is);
	}
	
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list){
		list.add(new ItemStack(item,1,0));
		list.add(new ItemStack(item,1,1));
	}
}
