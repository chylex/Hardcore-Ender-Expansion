package chylex.hee.block;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import chylex.hee.init.BlockList;
import chylex.hee.item.block.ItemBlockWithSubtypes.IBlockSubtypes;
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockSpecialEffects extends Block implements IBlockSubtypes{
	public static final byte metaBiomeIslandIcon = 0;
	public static final byte metaTestOffset = 1;
	
	private static final String[] textures = new String[]{
		"_2", "_3", "_4", "_5", "_9", "_10"
	};
	
	@SideOnly(Side.CLIENT)
	private IIcon[] iconArray;
	
	public BlockSpecialEffects(){
		super(Material.rock);
	}
	
	@Override
	public IIcon getIcon(int side, int meta){
		if (meta == metaBiomeIslandIcon)return BlockList.end_terrain.getIcon(side,side>>1); 
		return iconArray[MathUtil.clamp(meta-metaTestOffset,0,iconArray.length-1)];
	}

	@Override
	public String getUnlocalizedName(ItemStack is){
		return "specialEffects."+is.getItemDamage();
	}
	
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list){
		for(int a = 0; a < textures.length; a++)list.add(new ItemStack(item,1,a+metaTestOffset));
	}
	
	@Override
	public void registerBlockIcons(IIconRegister iconRegister){
		iconArray = new IIcon[textures.length];
		for(int a = 0; a < textures.length; a++)iconArray[a] = iconRegister.registerIcon("hardcoreenderexpansion:"+textures[a]);
	}
}
