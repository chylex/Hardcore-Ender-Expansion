package chylex.hee.block;
import java.util.Random;
import net.minecraft.block.BlockOre;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import chylex.hee.item.ItemList;

public class BlockStardustOre extends BlockOre{
	private static final byte iconAmount = 16;
	private static final byte[][] iconIndexes = new byte[6][16];
	
	static{
		Random rand = new Random(69);
		
		for(int side = 0; side < 6; side++){
			for(int meta = 0; meta < 16; meta++){
				iconIndexes[side][meta] = (byte)rand.nextInt(iconAmount);
			}
		}
	}
	
	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state){
		if (world.getBlockMetadata(x,y,z) == 0){
			world.setBlockMetadataWithNotify(x,y,z,world.rand.nextInt(15)+1,3);
		}
	}
	
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune){
		return ItemList.stardust;
	}

	@Override
	public int quantityDroppedWithBonus(int fortune, Random rand){
		return rand.nextInt(3)+rand.nextInt(3);
	}
	
	@Override
	public int getExpDrop(IBlockAccess world, BlockPos pos, int fortune){
		return MathHelper.getRandomIntegerInRange(BlockList.blockRandom,1,6);
	}
}
