package chylex.hee.block;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockStone;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import chylex.hee.entity.block.EntityBlockFallingObsidian;
import chylex.hee.mechanics.knowledge.KnowledgeRegistrations;
import chylex.hee.mechanics.knowledge.util.ObservationUtil;

public class BlockObsidianEnd extends BlockStone{
	public BlockObsidianEnd(){
		super();
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z){
		world.scheduleBlockUpdate(x,y,z,this,tickRate(world));
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor){
		world.scheduleBlockUpdate(x,y,z,this,tickRate(world));
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
	public Item getItemDropped(int meta, Random rand, int fortune){
		return Item.getItemFromBlock(Blocks.obsidian);
	}

	@Override
	public int quantityDropped(Random rand){
		return 1;
	}
	
	@Override
	public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float chance, int fortune){
		super.dropBlockAsItemWithChance(world,x,y,z,meta,chance,fortune);
		for(EntityPlayer observer:ObservationUtil.getAllObservers(world,x+0.5D,y+0.5D,z+0.5D,6D))KnowledgeRegistrations.FALLING_OBSIDIAN.tryUnlockFragment(observer,1F,new short[]{ 2,3 });
	}
}