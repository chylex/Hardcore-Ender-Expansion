package chylex.hee.block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import chylex.hee.tileentity.TileEntityExperienceTable;

public class BlockExperienceTable extends BlockAbstractTable{
	@Override
	public TileEntity createNewTileEntity(World world, int meta){
		return new TileEntityExperienceTable();
	}
	
	@Override
	protected int getGuiID(){
		return 7;
	}
}