package chylex.hee.block;
import java.util.Random;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.tileentity.TileEntityEndPortalCustom;
import chylex.hee.tileentity.TileEntityEndPortalFrame;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockEndPortalFrame extends BlockContainer{
	@SideOnly(Side.CLIENT)
	private IIcon iconTopPlain, iconTopAcceptor;
	
	public BlockEndPortalFrame(){
		super(Material.rock);
		setBlockBounds(0F,0F,0F,1F,0.8125F,1F);
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ){
		// TODO temp
		Pos pos1 = Pos.at(x,y,z);
		
		for(Facing4 facing:Facing4.list){
			if (pos1.offset(facing).checkBlock(world,Blocks.end_portal,Meta.endPortalDisabled)){
				pos1 = pos1.offset(facing).offset(facing.rotateRight());
				Pos pos2 = Pos.at(pos1).offset(facing,2).offset(facing.rotateLeft(),2);
				
				Pos.forEachBlock(pos1,pos2,pos -> {
					if (pos.checkBlock(world,Blocks.end_portal,Meta.endPortalDisabled)){
						pos.setMetadata(world,Meta.endPortalActive);
						((TileEntityEndPortalCustom)pos.getTileEntity(world)).startAnimation();
					}
				});
				
				break;
			}
		}
		
		return true;
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
