package chylex.hee.block;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.abstractions.Pos;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockVoidPortalFrame extends Block{
	@SideOnly(Side.CLIENT)
	private IIcon iconTopPlain, iconTopStorage;
	
	public BlockVoidPortalFrame(){
		super(Material.rock);
		setBlockBounds(0F,0F,0F,1F,0.8125F,1F);
	}
	
	@Override
	public int getDamageValue(World world, int x, int y, int z){
		return Pos.at(x,y,z).getMetadata(world);
	}
	
	@Override
	public boolean isOpaqueCube(){
		return false;
	}
	
	@Override
	public Item getItemDropped(int meta, Random rand, int fortune){
		return null;
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ){
		if (!world.isRemote && Pos.at(x,y,z).getMetadata(world) == Meta.voidPortalFrameStorage)player.openGui(HardcoreEnderExpansion.instance,8,world,x,y,z);
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		return side == 0 ? Blocks.end_stone.getIcon(0,0) :
			   side == 1 ? (meta == Meta.voidPortalFrameStorage ? iconTopStorage : iconTopPlain) : blockIcon;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public final void getSubBlocks(Item item, CreativeTabs tab, List list){
		list.add(new ItemStack(item,1,Meta.voidPortalFramePlain));
		list.add(new ItemStack(item,1,Meta.voidPortalFrameStorage));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister){
		blockIcon = iconRegister.registerIcon("hardcoreenderexpansion:void_portal_frame_side");
		iconTopPlain = iconRegister.registerIcon("hardcoreenderexpansion:void_portal_frame_top_plain");
		iconTopStorage = iconRegister.registerIcon("hardcoreenderexpansion:void_portal_frame_top_storage");
	}
}
