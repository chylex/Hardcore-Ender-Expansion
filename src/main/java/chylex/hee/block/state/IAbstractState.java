package chylex.hee.block.state;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public interface IAbstractState{
	/**
	 * Sets block property, applicable only if there is exactly one property available.
	 */
	IBlockState setProperty(Comparable value);
	
	/**
	 * Sets block property using default state.
	 */
	IBlockState setProperty(IProperty property, Comparable value);
	
	IBlockState getStateFromMeta(int meta);
	int getMetaFromState(IBlockState state);
	
	int damageDropped(IBlockState state);
	ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos);
}
