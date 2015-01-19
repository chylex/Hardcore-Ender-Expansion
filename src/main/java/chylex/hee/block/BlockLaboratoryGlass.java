package chylex.hee.block;
import net.minecraft.block.BlockBreakable;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockLaboratoryGlass extends BlockBreakable{	
	public BlockLaboratoryGlass(){
		super("",Material.glass,false);
	}

	@Override
	public boolean renderAsNormalBlock(){
		return false;
	}
	
	@Override
	public boolean isOpaqueCube(){
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderBlockPass(){
		return 1;
	}
}
