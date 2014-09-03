package chylex.hee.block;
import net.minecraft.block.Block;
import net.minecraft.block.BlockMobSpawner;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import chylex.hee.tileentity.TileEntityCustomSpawner;

public class BlockCustomSpawner extends BlockMobSpawner{
	public BlockCustomSpawner(){
		disableStats();
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata){
		return new TileEntityCustomSpawner().setLogicId(metadata);
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block oldBlock, int oldMeta){
		if (!world.isRemote){
			TileEntityCustomSpawner spawner = (TileEntityCustomSpawner)world.getTileEntity(x,y,z);
			if (spawner != null)spawner.getSpawnerLogic().onBlockBreak();
		}
		
		super.breakBlock(world,x,y,z,oldBlock,oldMeta);
	}
}
