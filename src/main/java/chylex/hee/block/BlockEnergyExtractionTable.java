package chylex.hee.block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import chylex.hee.tileentity.TileEntityEnergyExtractionTable;

public class BlockEnergyExtractionTable extends BlockAbstractTable{
	@Override
	public TileEntity createNewTileEntity(World world, int meta){
		return new TileEntityEnergyExtractionTable();
	}
	
	@Override
	protected int getGuiID(){
		return 3;
	}
}
