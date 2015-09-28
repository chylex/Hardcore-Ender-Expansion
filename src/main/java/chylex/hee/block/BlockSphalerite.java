package chylex.hee.block;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import chylex.hee.init.BlockList;
import chylex.hee.init.ItemList;
import chylex.hee.item.block.ItemBlockWithSubtypes.IBlockSubtypes;
import chylex.hee.system.collections.CollectionUtil;
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockSphalerite extends Block implements IBlockSubtypes{
	@SideOnly(Side.CLIENT)
	private IIcon[] iconArray;
	
	public BlockSphalerite(){
		super(Material.rock);
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune){
		if (meta == 1)return CollectionUtil.newList(new ItemStack(BlockList.sphalerite,1,0),new ItemStack(ItemList.stardust,1+world.rand.nextInt(3+MathUtil.ceil(fortune*0.49D)),0));
		else return super.getDrops(world,x,y,z,meta,fortune);
	}
	
	@Override
	public final void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int meta){
		super.harvestBlock(world,player,x,y,z,meta);
		// TODO if (meta == 1)CausatumUtils.increase(player,CausatumMeters.END_ORE_MINING,4);
	}

	@Override
	public String getUnlocalizedName(ItemStack is){
		switch(is.getItemDamage()){
			case 0: return "tile.sphalerite";
			case 1: return "tile.sphalerite.stardust";
			default: return "";
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list){
		list.add(new ItemStack(item,1,0));
		list.add(new ItemStack(item,1,1));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		return iconArray[MathUtil.clamp(meta,0,iconArray.length-1)];
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister){
		iconArray = new IIcon[2];
		iconArray[0] = iconRegister.registerIcon("hardcoreenderexpansion:sphalerite");
		iconArray[1] = iconRegister.registerIcon("hardcoreenderexpansion:sphalerite_stardust");
	}
}
