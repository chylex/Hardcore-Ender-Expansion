package chylex.hee.item.block;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBlockSlab extends ItemBlock{
	private final Block fullBlock;
	
	public ItemBlockSlab(Block block){
		super(block);
		setHasSubtypes(true);
		setUnlocalizedName(block.getUnlocalizedName());
		
		fullBlock = ((IBlockSlab)block).getFullBlock();
	}
	
	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ){
		if (is.stackSize == 0 || !player.canPlayerEdit(pos,side,is))return false;
		
		IBlockState state = world.getBlockState(pos);
		boolean isTopSlab = state.getBlock() == block && (block.getMetaFromState(state)&8) != 0;

		if ((side == EnumFacing.UP && !isTopSlab || side == EnumFacing.DOWN && isTopSlab) && state.getBlock() == block){
			if (world.checkNoEntityCollision(fullBlock.getCollisionBoundingBox(world,pos,world.getBlockState(pos))) && world.setBlockState(pos,fullBlock.getDefaultState(),3)){
				world.playSoundEffect(pos.getX()+0.5D,pos.getY()+0.5D,pos.getZ()+0.5D,fullBlock.stepSound.getPlaceSound(),(fullBlock.stepSound.getVolume()+1F)*0.5F,fullBlock.stepSound.getFrequency()*0.8F);
				--is.stackSize;
			}

			return true;
		}
		else return tryPlace(is,world,pos.offset(side),state) || super.onItemUse(is,player,world,pos,side,hitX,hitY,hitZ);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack is){
		IBlockState state = world.getBlockState(pos);
		boolean isTopSlab = state.getBlock() == block && (block.getMetaFromState(state)&8) != 0;
		
		if ((side == EnumFacing.UP && !isTopSlab || side == EnumFacing.DOWN && isTopSlab) && state.getBlock() == block)return true;
		return state.getBlock() == block || super.canPlaceBlockOnSide(world,pos,side,player,is);
	}

	private boolean tryPlace(ItemStack is, World world, BlockPos pos, IBlockState state){
		if (state.getBlock() == block){
			if (world.checkNoEntityCollision(fullBlock.getCollisionBoundingBox(world,pos,state)) && world.setBlockState(pos,fullBlock.getDefaultState(),3)){
				world.playSoundEffect(pos.getX()+0.5D,pos.getY()+0.5D,pos.getZ()+0.5D,fullBlock.stepSound.getPlaceSound(),(fullBlock.stepSound.getVolume()+1F)*0.5F,fullBlock.stepSound.getFrequency()*0.8F);
				--is.stackSize;
			}

			return true;
		}
		else return false;
	}
	
	public static interface IBlockSlab{
		public Block getFullBlock();
	}
}
