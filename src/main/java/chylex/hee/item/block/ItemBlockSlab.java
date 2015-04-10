package chylex.hee.item.block;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import chylex.hee.system.util.BlockPosM;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockSlab extends ItemBlock{
	private final Block fullBlock;
	
	public ItemBlockSlab(Block block){
		super(block);
		setHasSubtypes(true);
		setUnlocalizedName(block.getUnlocalizedName());
		
		fullBlock = ((IBlockSlab)field_150939_a).getFullBlock();
	}
	
	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ){
		if (is.stackSize == 0 || !player.canPlayerEdit(x,y,z,side,is))return false;

		boolean isTopSlab = (BlockPosM.tmp(x,y,z).getMetadata(world)&8) != 0;

		if ((side == 1 && !isTopSlab || side == 0 && isTopSlab) && BlockPosM.tmp(x,y,z).getBlock(world) == field_150939_a){
			if (world.checkNoEntityCollision(fullBlock.getCollisionBoundingBoxFromPool(world,x,y,z)) && BlockPosM.tmp(x,y,z).setBlock(world,fullBlock)){
				world.playSoundEffect(x+0.5D,y+0.5D,z+0.5D,fullBlock.stepSound.func_150496_b(),(fullBlock.stepSound.getVolume()+1F)*0.5F,fullBlock.stepSound.getPitch()*0.8F);
				--is.stackSize;
			}

			return true;
		}
		else return tryPlaceSlab(is,player,world,x,y,z,side) || super.onItemUse(is,player,world,x,y,z,side,hitX,hitY,hitZ);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean func_150936_a(World world, int x, int y, int z, int side, EntityPlayer player, ItemStack is){
		boolean isTopSlab = (BlockPosM.tmp(x,y,z).getMetadata(world)&8) != 0;
		
		if ((side == 1 && !isTopSlab || side == 0 && isTopSlab) && BlockPosM.tmp(x,y,z).getBlock(world) == field_150939_a)return true;
		
		return BlockPosM.tmp(x,y,z).move(side).getBlock(world) == field_150939_a || super.func_150936_a(world,x,y,z,side,player,is);
	}

	private boolean tryPlaceSlab(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side){
		BlockPosM tmpPos = BlockPosM.tmp(x,y,z).move(side);

		if (tmpPos.getBlock(world) == field_150939_a){
			if (world.checkNoEntityCollision(fullBlock.getCollisionBoundingBoxFromPool(world,x,y,z)) && tmpPos.setBlock(world,fullBlock)){
				world.playSoundEffect(x+0.5D,y+0.5D,z+0.5D,fullBlock.stepSound.func_150496_b(),(fullBlock.stepSound.getVolume()+1F)*0.5F,fullBlock.stepSound.getPitch()*0.8F);
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
