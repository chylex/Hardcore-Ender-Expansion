package chylex.hee.block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import chylex.hee.tileentity.TileEntityExtractionTable;

public class BlockExtractionTable extends BlockAbstractTable{
	@Override
	public TileEntity createNewTileEntity(World world, int meta){
		return new TileEntityExtractionTable();
	}
	
	@Override
	protected int getGuiID(){
		return 3;
	}
}
