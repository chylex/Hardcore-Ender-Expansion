package chylex.hee.block;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.IIcon;
import chylex.hee.init.BlockList;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockRavagedBrickSmooth extends Block{
	public BlockRavagedBrickSmooth(){
		super(Material.rock);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		return side == 0 || side == 1 ? BlockList.ravaged_brick.getIcon(0, 0) : blockIcon;
	}
}
