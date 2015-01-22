package chylex.hee.block;
import java.util.Random;
import net.minecraft.block.BlockOre;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import chylex.hee.item.ItemList;

public class BlockStardustOre extends BlockOre{
	public static final PropertyInteger TYPE = PropertyInteger.create("type",0,15);
	
	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state){
		if (getMetaFromState(state) == 0)world.setBlockState(pos,state.withProperty(TYPE,world.rand.nextInt(15)+1));
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta){
		return meta >= 0 && meta < 16 ? getDefaultState().withProperty(TYPE,meta) : getDefaultState();
	}
	
	@Override
	public int getMetaFromState(IBlockState state){
		return ((Integer)state.getValue(TYPE)).intValue();
	}
	
	@Override
	protected BlockState createBlockState(){
		return new BlockState(this,new IProperty[]{ TYPE });
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
