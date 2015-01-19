package chylex.hee.block;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import chylex.hee.item.block.ItemBlockWithSubtypes.IBlockSubtypes;

public class BlockSpecialEffects extends Block implements IBlockSubtypes{
	public static final byte metaBiomeIslandIcon = 0;
	public static final byte metaTestOffset = 1;
	
	private static final String[] textures = new String[]{
		"_1", "_2", "_3", "_4", "_5"
	};
	
	public BlockSpecialEffects(){
		super(Material.rock);
	}

	@Override
	public String getUnlocalizedName(ItemStack is){
		return "specialEffects."+is.getItemDamage();
	}
	
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list){
		for(int a = 0; a < textures.length; a++)list.add(new ItemStack(item,1,a+metaTestOffset));
	}
}
