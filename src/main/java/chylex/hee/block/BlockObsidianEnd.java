package chylex.hee.block;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import chylex.hee.entity.block.EntityBlockFallingObsidian;

public class BlockObsidianEnd extends BlockStone{
	@Override
	public void onBlockAdded(World world, int x, int y, int z){
		world.scheduleUpdate(pos,this,tickRate(world));
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor){
		world.scheduleUpdate(pos,this,tickRate(world));
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand){
		tryToFall(world,x,y,z);
	}

	private void tryToFall(World world, int x, int y, int z){
		if (BlockFalling.func_149831_e(world,x,y-1,z) && y >= 0 && !world.isRemote){
			world.spawnEntityInWorld(new EntityBlockFallingObsidian(world,x+0.5F,y+0.5F,z+0.5F));
		}
	}
	
	@Override
	public int tickRate(World world){
		return 3;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune){
		return Item.getItemFromBlock(Blocks.obsidian);
	}

	@Override
	public int quantityDropped(Random rand){
		return 1;
	}
}