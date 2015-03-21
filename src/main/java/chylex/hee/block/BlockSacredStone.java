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
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockSacredStone extends Block implements IBlockSubtypes{
	public static final byte metaAmount = 10;
	
	@SideOnly(Side.CLIENT)
	private IIcon[] iconArray;
	
	public BlockSacredStone(){
		super(Material.rock);
	}
	
	@Override
	public int damageDropped(int meta){
		return meta;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack is){
		return getUnlocalizedName();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		return iconArray[MathUtil.clamp(meta,0,iconArray.length-1)];
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list){
		for(int a = 0; a < iconArray.length; a++)list.add(new ItemStack(item,1,a));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister){
		String s = getTextureName()+"_";
		iconArray = new IIcon[metaAmount];
		for(int a = 0; a < metaAmount; a++)iconArray[a] = iconRegister.registerIcon(s+(a+1));
	}
}
