package chylex.hee.block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import chylex.hee.block.vanilla.BlockBasicWall;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockStoneBrickWall extends BlockBasicWall{
	public BlockStoneBrickWall(){
		super(Blocks.stonebrick, 0);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		return blockIcon;
	}
	
	@Override
	public void registerBlockIcons(IIconRegister iconRegister){
		blockIcon = iconRegister.registerIcon("hardcoreenderexpansion:stone_brick_wall");
	}
}
