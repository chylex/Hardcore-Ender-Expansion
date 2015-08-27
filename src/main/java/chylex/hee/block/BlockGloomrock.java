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

public class BlockGloomrock extends Block implements IBlockSubtypes{
	public enum Meta{
		PLAIN, SMOOTH, BRICK,
		COL_RED, COL_ORANGE, COL_YELLOW, COL_GREEN, COL_CYAN, COL_LIGHT_BLUE,
		COL_BLUE, COL_PURPLE, COL_MAGENTA, COL_PINK, COL_WHITE, COL_GRAY, COL_BLACK;
		public final byte value = (byte)ordinal();
		
		public static final byte count = (byte)values().length;
		public static final byte firstColor = COL_RED.value;
	}
	
	@SideOnly(Side.CLIENT)
	private IIcon[] iconArray;
	
	public BlockGloomrock(){
		super(Material.rock);
	}
	
	@Override
	public int damageDropped(int meta){
		return meta;
	}

	@Override
	public String getUnlocalizedName(ItemStack is){
		switch(is.getItemDamage()){
			case 0: return "tile.gloomrock.plain";
			case 1: return "tile.gloomrock.smooth";
			case 2: return "tile.gloomrock.brick";
			default: return "tile.gloomrock.color."+(is.getItemDamage()-3);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		return iconArray[MathUtil.clamp(meta,0,iconArray.length-1)];
	}
	
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list){
		for(int damage = 0; damage < Meta.count; damage++)list.add(new ItemStack(item,1,damage));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister){
		String base = "hardcoreenderexpansion:gloomrock_";
		iconArray = new IIcon[Meta.count];
		
		iconArray[0] = iconRegister.registerIcon(base+"plain");
		iconArray[1] = iconRegister.registerIcon(base+"smooth");
		iconArray[2] = iconRegister.registerIcon(base+"brick");
		
		for(int color = 3; color < Meta.count; color++){
			iconArray[color] = iconRegister.registerIcon(base+"color_"+(color-2));
		}
	}
}
