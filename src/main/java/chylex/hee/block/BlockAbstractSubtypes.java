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

public abstract class BlockAbstractSubtypes extends Block implements IBlockSubtypes{
	@SideOnly(Side.CLIENT)
	protected IIcon[] iconArray;
	
	public BlockAbstractSubtypes(Material material){
		super(material);
	}
	
	@Override
	public final int damageDropped(int meta){
		return meta;
	}
	
	@Override
	public final String getUnlocalizedName(ItemStack is){
		return getUnlocalizedName(is.getItemDamage());
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public final void getSubBlocks(Item item, CreativeTabs tab, List list){
		for(int meta = 0; meta < iconArray.length; meta++)list.add(new ItemStack(item,1,meta));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		return iconArray[MathUtil.clamp(meta,0,iconArray.length-1)];
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister){
		iconArray = new IIcon[countSubtypes()];
		
		for(int meta = 0; meta < iconArray.length; meta++){
			iconArray[meta] = iconRegister.registerIcon(getTextureName(meta));
		}
	}
	
	// Abstract and overridable methods
	
	public abstract int countSubtypes();
	
	protected String getUnlocalizedName(int meta){
		return getUnlocalizedName()+"."+meta;
	}

	@SideOnly(Side.CLIENT)
	protected String getTextureName(int meta){
		return textureName+"_"+meta;
	}
}
