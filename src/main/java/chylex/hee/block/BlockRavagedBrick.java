package chylex.hee.block;
import java.util.List;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import chylex.hee.item.block.ItemBlockWithSubtypes.IBlockSubtypes;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class BlockRavagedBrick extends Block implements IBlockSubtypes{
	public static byte metaNormal = 0, metaCracked = 1, metaDamaged1 = 2, metaDamaged2 = 3, metaDamaged3 = 4, metaAmount = 5;
	
	@SideOnly(Side.CLIENT)
	private IIcon[] iconArray;
	
	public BlockRavagedBrick(){
		super(Material.rock);
	}

	@Override
	public String getUnlocalizedName(ItemStack is){
		return getUnlocalizedName();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list){
		for(int a = 0; a < metaAmount; a++)list.add(new ItemStack(item,1,a));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		return meta >= metaDamaged1 && meta <= metaDamaged3 && (side == 0 || side == 1) ? iconArray[0] : iconArray[meta < metaAmount ? meta : 0];
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister){
		iconArray = new IIcon[metaAmount];
		iconArray[0] = iconRegister.registerIcon("hardcoreenderexpansion:ravaged_brick");
		iconArray[1] = iconRegister.registerIcon("hardcoreenderexpansion:ravaged_brick_cracked");
		iconArray[2] = iconRegister.registerIcon("hardcoreenderexpansion:ravaged_brick_damaged_1");
		iconArray[3] = iconRegister.registerIcon("hardcoreenderexpansion:ravaged_brick_damaged_2");
		iconArray[4] = iconRegister.registerIcon("hardcoreenderexpansion:ravaged_brick_damaged_3");
	}
}
