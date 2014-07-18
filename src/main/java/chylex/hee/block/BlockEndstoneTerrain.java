package chylex.hee.block;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import chylex.hee.item.block.ItemBlockWithSubtypes.IBlockSubtypes;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockEndstoneTerrain extends Block implements IBlockSubtypes{
	private static final Random rand = new Random();
	
	private static final String[] types = new String[]{
		"infested", "burned", "enchanted"
	};
	
	public static final byte metaInfested = 0, metaBurned = 1, metaEnchanted = 2;
	
	@SideOnly(Side.CLIENT)
    private IIcon[] iconTop,iconSide;
	
	public BlockEndstoneTerrain(){
		super(Material.rock);
	}
	
	@Override
	public int damageDropped(int meta){
		return meta;
	}
	
	@Override
	public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z){
		switch(world.getBlockMetadata(x,y,z)){
			case metaInfested: return rand.nextInt(10) <= 2;
			case metaBurned: return false;
			case metaEnchanted:
			default: return true;
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack is){
		switch(is.getItemDamage()){
			case BlockEndstoneTerrain.metaInfested: return "tile.endStoneTerrain.infested";
			case BlockEndstoneTerrain.metaBurned: return "tile.endStoneTerrain.burned";
			case BlockEndstoneTerrain.metaEnchanted: return "tile.endStoneTerrain.enchanted";
			default: return "";
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		if (meta >= types.length)meta = 0;
		return side == 0 ? blockIcon : (side == 1 ? iconTop[meta] : iconSide[meta]);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list){
		for(int a = 0; a < types.length; a++)list.add(new ItemStack(item,1,a));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister){
		blockIcon = iconRegister.registerIcon("end_stone");
		iconTop = new IIcon[types.length];
		iconSide = new IIcon[types.length];
		
		for(int a = 0; a < types.length; a++){
			iconTop[a] = iconRegister.registerIcon("hardcoreenderexpansion:endstone_ter_"+types[a]+"_top");
			iconSide[a] = iconRegister.registerIcon("hardcoreenderexpansion:endstone_ter_"+types[a]+"_side");
		}
	}
}
