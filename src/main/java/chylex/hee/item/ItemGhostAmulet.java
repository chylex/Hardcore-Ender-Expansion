package chylex.hee.item;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemGhostAmulet extends Item implements IMultiModel{
	public ItemGhostAmulet(){
		setHasSubtypes(true);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack is){
		return is.getItemDamage() == 0 ? super.getUnlocalizedName(is)+".impure" : super.getUnlocalizedName(is);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List list){
		list.add(new ItemStack(item,1,0));
		list.add(new ItemStack(item,1,1));
	}
	
	@Override
	public String[] getModels(){
		return new String[]{
			"^ghost_amulet",
			"^ghost_amulet_impure"
		};
	}
}
