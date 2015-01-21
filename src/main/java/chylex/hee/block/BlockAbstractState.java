package chylex.hee.block;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public abstract class BlockAbstractState extends Block{
	public BlockAbstractState(Material material){
		super(material);
	}
	
	/**
	 * Sets block property, applicable only if there is exactly one property available.
	 */
	public abstract IBlockState setProperty(Comparable value);
	
	/**
	 * Sets block property using default state.
	 */
	public abstract IBlockState setProperty(IProperty property, Comparable value);
	
	@Override public abstract IBlockState getStateFromMeta(int meta);
	@Override public abstract int getMetaFromState(IBlockState state);
	@Override protected abstract BlockState createBlockState();
	
	@Override
	public int damageDropped(IBlockState state){
		return getMetaFromState(state);
	}
	
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos){
		return new ItemStack(this,1,getMetaFromState(world.getBlockState(pos)));
	}
}
