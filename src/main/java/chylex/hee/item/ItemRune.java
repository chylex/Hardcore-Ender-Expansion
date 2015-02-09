package chylex.hee.item;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import chylex.hee.mechanics.charms.RuneType;

public class ItemRune extends Item implements IMultiModel{
	public ItemRune(){
		setHasSubtypes(true);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List list){
		for(RuneType runeType:RuneType.values)list.add(new ItemStack(item,1,runeType.damage));
		
	}
	
	@Override
	public String getUnlocalizedName(ItemStack is){
		int damage = is.getItemDamage();
		return "item.rune."+(damage >= 0 && damage < RuneType.values.length ? RuneType.values[damage].name().toLowerCase() : damage == OreDictionary.WILDCARD_VALUE ? "any" : "invalid");
	}
	
	@Override
	public String[] getModels(){
		return new String[]{
			"^rune_power",
			"^rune_agility",
			"^rune_vigor",
			"^rune_defense",
			"^rune_magic",
			"^rune_void"
		};
	}
}
