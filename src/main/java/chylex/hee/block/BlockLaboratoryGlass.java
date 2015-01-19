package chylex.hee.block;
import net.minecraft.block.BlockBreakable;
import net.minecraft.block.material.Material;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockLaboratoryGlass extends BlockBreakable{	
	public BlockLaboratoryGlass(){
		super(Material.glass,false);
	}

	@Override
	public boolean isFullCube(){
		return false;
	}
	
	@Override
	public boolean isOpaqueCube(){
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumWorldBlockLayer getBlockLayer(){
		return EnumWorldBlockLayer.TRANSLUCENT;
	}
}
