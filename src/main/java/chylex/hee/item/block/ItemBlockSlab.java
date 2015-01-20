package chylex.hee.item.block;
import net.minecraft.block.Block;
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

		boolean isTopSlab = (world.getBlockMetadata(x,y,z)&8) != 0;

		if ((side == EnumFacing.UP && !isTopSlab || side == EnumFacing.DOWN && isTopSlab) && world.getBlock(x,y,z) == block){
			if (world.checkNoEntityCollision(fullBlock.getCollisionBoundingBox(world,pos,world.getBlockState(pos))) && world.setBlock(x,y,z,fullBlock,0,3)){
				world.playSoundEffect(pos.getX()+0.5D,pos.getY()+0.5D,pos.getZ()+0.5D,fullBlock.stepSound.getPlaceSound(),(fullBlock.stepSound.getVolume()+1F)*0.5F,fullBlock.stepSound.getFrequency()*0.8F);
				--is.stackSize;
			}

			return true;
		}
		else return tryPlaceSlab(is,player,world,x,y,z,side) || super.onItemUse(is,player,world,pos,side,hitX,hitY,hitZ);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean func_150936_a(World world, int x, int y, int z, int side, EntityPlayer player, ItemStack is){
		boolean isTopSlab = (world.getBlockMetadata(x,y,z)&8) != 0;

		if ((side == 1 && !isTopSlab || side == 0 && isTopSlab) && world.getBlock(x,y,z) == block)return true;
		
		int origX = x, origY = y, origZ = z;
		
		switch(side){
			case 0: --y; break;
			case 1: ++y; break;
			case 2: --z; break;
			case 3: ++z; break;
			case 4: --x; break;
			case 5: ++x; break;
			default:
		}

		return world.getBlock(x,y,z) == block || super.func_150936_a(world,origX,origY,origZ,side,player,is);
	}

	private boolean tryPlaceSlab(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side){
		switch(side){
			case 0: --y; break;
			case 1: ++y; break;
			case 2: --z; break;
			case 3: ++z; break;
			case 4: --x; break;
			case 5: ++x; break;
			default:
		}

		if (world.getBlock(x,y,z) == block){
			if (world.checkNoEntityCollision(fullBlock.getCollisionBoundingBoxFromPool(world,x,y,z)) && world.setBlock(x,y,z,fullBlock,0,3)){
				world.playSoundEffect(x+0.5D,y+0.5D,z+0.5D,fullBlock.stepSound.func_150496_b(),(fullBlock.stepSound.getVolume()+1F)*0.5F,fullBlock.stepSound.getFrequency()*0.8F);
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
