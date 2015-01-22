package chylex.hee.block.state;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public abstract class BlockAbstractContainerState extends BlockContainer implements IAbstractState{
	public BlockAbstractContainerState(Material material){
		super(material);
	}
	
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
