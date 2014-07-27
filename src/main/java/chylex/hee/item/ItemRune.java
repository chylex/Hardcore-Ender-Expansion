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
	private IIcon[] iconArray;
	
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
	public IIcon getIconFromDamage(int damage){
		return damage < 0 || damage >= iconArray.length ? iconArray[0] : iconArray[damage];
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconRegister){
		RuneType[] types = RuneType.values();
		iconArray = new IIcon[RuneType.values().length];
		
		for(int a = 0; a < iconArray.length; a++){
			iconArray[a] = iconRegister.registerIcon(iconString+"_"+types[a].iconSuffix);
		}
	}
}
