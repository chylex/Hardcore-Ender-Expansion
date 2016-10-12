package chylex.hee.block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import chylex.hee.block.base.BlockAbstractTable;
import chylex.hee.tileentity.TileEntityDecompositionTable;

public class BlockDecompositionTable extends BlockAbstractTable{
	@Override
	public TileEntity createNewTileEntity(World world, int meta){
		return new TileEntityDecompositionTable();
	}
	
	@Override
	protected int getGuiID(){
		return 2;
	}
}
