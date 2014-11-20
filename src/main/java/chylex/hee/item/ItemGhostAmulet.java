package chylex.hee.item;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemGhostAmulet extends Item{
	@SideOnly(Side.CLIENT)
	private IIcon iconImpure, iconPure; // TODO impure icon
	
	@Override
	public String getUnlocalizedName(ItemStack is){
		return is.getItemDamage() == 0 ? super.getUnlocalizedName(is)+".impure" : super.getUnlocalizedName(is);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(ItemStack is, int pass){
		return is.getItemDamage() == 0 ? iconImpure : iconPure;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister){
		iconImpure = iconRegister.registerIcon(iconString+"_impure");
		iconPure = iconRegister.registerIcon(iconString+"_pure");
	}
}
