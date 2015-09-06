package chylex.hee.block;
import java.util.Random;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.tileentity.TileEntityEndPortalFrame;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockEndPortalFrame extends BlockContainer{
	@SideOnly(Side.CLIENT)
	private IIcon iconTopPlain, iconTopAcceptor;
	
	public BlockEndPortalFrame(){
		super(Material.rock);
		setBlockBounds(0F,0F,0F,1F,0.8125F,1F);
	}
	
	@Override
	public boolean hasTileEntity(int meta){
		return meta == Meta.endPortalFrameAcceptor;
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int meta){
		return new TileEntityEndPortalFrame();
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
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		return side == 0 ? Blocks.end_stone.getIcon(0,0) :
			   side == 1 ? (meta == Meta.endPortalFrameAcceptor ? iconTopAcceptor : iconTopPlain) : blockIcon;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister){
		blockIcon = iconRegister.registerIcon("hardcoreenderexpansion:end_portal_frame_side");
		iconTopPlain = iconRegister.registerIcon("hardcoreenderexpansion:end_portal_frame_top_plain");
		iconTopAcceptor = iconRegister.registerIcon("hardcoreenderexpansion:end_portal_frame_top_acceptor");
	}
}
