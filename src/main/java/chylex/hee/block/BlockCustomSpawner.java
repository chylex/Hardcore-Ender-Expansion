package chylex.hee.block;
import net.minecraft.block.BlockMobSpawner;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import chylex.hee.tileentity.TileEntityCustomSpawner;

public class BlockCustomSpawner extends BlockMobSpawner{
	public BlockCustomSpawner(){
		disableStats();
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state){
		return new TileEntityCustomSpawner().setLogicId(metadata);
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state){
		if (!world.isRemote){
			TileEntityCustomSpawner spawner = (TileEntityCustomSpawner)world.getTileEntity(pos);
			if (spawner != null)spawner.getSpawnerLogic().onBlockBreak();
		}
		
		super.breakBlock(world,pos,state);
	}
}
