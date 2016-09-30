package chylex.hee.item;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemGhostAmulet extends Item{
	@SideOnly(Side.CLIENT)
	private IIcon iconImpure, iconPure;
	
	public ItemGhostAmulet(){
		setHasSubtypes(true);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack is){
		return is.getItemDamage() == 0 ? super.getUnlocalizedName(is)+".impure" : super.getUnlocalizedName(is);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int damage){
		return damage == 0 ? iconImpure : iconPure;
	}
	
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list){
		list.add(new ItemStack(item, 1, 0));
		list.add(new ItemStack(item, 1, 1));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister){
		iconImpure = iconRegister.registerIcon(iconString+"_impure");
		iconPure = iconRegister.registerIcon(iconString);
	}
}
