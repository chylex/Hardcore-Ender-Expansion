package chylex.hee.block;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import chylex.hee.entity.block.EntityBlockFallingObsidian;

public class BlockObsidianEnd extends BlockStone{
	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state){
		world.scheduleUpdate(pos,this,tickRate(world));
	}

	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighbor){
		world.scheduleUpdate(pos,this,tickRate(world));
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand){
		tryToFall(world,pos);
	}

	private void tryToFall(World world, BlockPos pos){
		if (!world.isRemote && BlockFalling.canFallInto(world,pos.down()) && pos.getY() >= 0){
			world.spawnEntityInWorld(new EntityBlockFallingObsidian(world,pos.getX()+0.5F,pos.getY()+0.5F,pos.getZ()+0.5F));
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