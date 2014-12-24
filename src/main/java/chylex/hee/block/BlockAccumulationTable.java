package chylex.hee.block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import chylex.hee.tileentity.TileEntityAccumulationTable;

public class BlockAccumulationTable extends BlockAbstractTable{
	@Override
	public TileEntity createNewTileEntity(World world, int meta){
		return new TileEntityAccumulationTable();
	}
	
	@Override
	protected int getGuiID(){
		return 9;
	}
}