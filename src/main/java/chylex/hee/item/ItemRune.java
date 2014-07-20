package chylex.hee.item;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import chylex.hee.mechanics.charms.RuneType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemRune extends Item{
	@SideOnly(Side.CLIENT)
	private IIcon iconBack,iconFore;
	
	public ItemRune(){
		setHasSubtypes(true);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List list){
		for(RuneType runeType:RuneType.values())list.add(new ItemStack(item,1,runeType.damage));
		
	}
	
	@Override
	public String getUnlocalizedName(ItemStack is){
		RuneType[] types = RuneType.values();
		int damage = is.getItemDamage();
		return "item.rune."+(damage >= 0 && damage < types.length ? types[damage].name().toLowerCase() : "invalid");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass(int damage, int pass){
        return pass == 0 ? iconBack : iconFore;
    }

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack is, int pass){
		if (pass == 0)return 16777215;
		else{
			RuneType[] types = RuneType.values();
			int damage = is.getItemDamage();
			return (damage >= 0 && damage < types.length ? types[damage].color : 0);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses(){
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconRegister){
		iconBack = iconRegister.registerIcon(getIconString()+"_back");
		iconFore = iconRegister.registerIcon(getIconString()+"_fore");
	}
}
