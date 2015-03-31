package chylex.hee.item;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import chylex.hee.mechanics.wand.WandCore;
import chylex.hee.mechanics.wand.WandType;

public class ItemSacredWandCores extends Item{
	// 0-19 - WandType
	// 20+  - WandCore
	
	public static WandType getTypeFromDamage(int damage){
		return WandType.values[damage >= 0 && damage < WandType.values.length ? damage : 0];
	}
	
	public static WandCore getCoreFromDamage(int damage){
		damage -= 20;
		return WandCore.values[damage >= 0 && damage < WandCore.values.length ? damage : 0];
	}
	
	@Override
	public IIcon getIconFromDamage(int damage){
		return super.getIconFromDamage(damage);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack is){
		if (is.getItemDamage() < 20)return "item.sacredWand.type."+getTypeFromDamage(is.getItemDamage()).toString().toLowerCase();
		else return "item.sacredWand.core."+getCoreFromDamage(is.getItemDamage()).toString().toLowerCase();
	}
	
	@Override
	public void registerIcons(IIconRegister iconRegister){
		
	}
}
