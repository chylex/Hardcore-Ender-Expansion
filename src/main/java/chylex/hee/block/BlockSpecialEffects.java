package chylex.hee.block;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import chylex.hee.item.block.ItemBlockWithSubtypes.IBlockSubtypes;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockSpecialEffects extends Block implements IBlockSubtypes{
	private static final String[] textures = new String[]{
		"_1", "_2", "_3", "_4"
	};
	
	@SideOnly(Side.CLIENT)
	private IIcon[] iconArray;
	
	public BlockSpecialEffects(){
		super(Material.rock);
	}
	
	@Override
	public IIcon getIcon(int side, int meta){
		return iconArray[Math.min(Math.max(0,meta),iconArray.length-1)];
	}

	@Override
	public String getUnlocalizedName(ItemStack is){
		return "specialEffects."+is.getItemDamage();
	}
	
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list){
		for(int a = 0; a < textures.length; a++)list.add(new ItemStack(item,1,a));
	}
	
	@Override
	public void registerBlockIcons(IIconRegister iconRegister){
		iconArray = new IIcon[textures.length];
		for(int a = 0; a < textures.length; a++)iconArray[a] = iconRegister.registerIcon("hardcoreenderexpansion:"+textures[a]);
	}
}
