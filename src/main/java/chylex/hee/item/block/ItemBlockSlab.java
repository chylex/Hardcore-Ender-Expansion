package chylex.hee.item.block;
import net.minecraft.block.Block;
import net.minecraft.block.Block.SoundType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import chylex.hee.system.abstractions.BlockInfo;
import chylex.hee.system.abstractions.Pos;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockSlab extends ItemBlock{ // TODO test
	private final BlockInfo full;
	
	public ItemBlockSlab(Block block){
		super(block);
		setHasSubtypes(true);
		setUnlocalizedName(block.getUnlocalizedName());
		full = ((IBlockSlab)field_150939_a).getFullBlock();
	}
	
	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ){
		if (is.stackSize == 0 || !player.canPlayerEdit(x,y,z,side,is))return false;
		
		boolean isTopSlab = (Pos.at(x,y,z).getMetadata(world)&8) != 0;
		
		return ((side == 1 && !isTopSlab || side == 0 && isTopSlab) && tryPlaceSlab(is,player,world,x,y,z,-1)) ||
			   tryPlaceSlab(is,player,world,x,y,z,side) ||
			   super.onItemUse(is,player,world,x,y,z,side,hitX,hitY,hitZ);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean func_150936_a(World world, int x, int y, int z, int side, EntityPlayer player, ItemStack is){
		Pos pos = Pos.at(x,y,z);
		boolean isTopSlab = (pos.getMetadata(world)&8) != 0;
		
		return ((side == 1 && !isTopSlab || side == 0 && isTopSlab) && pos.getBlock(world) == field_150939_a) ||
				pos.offset(side).getBlock(world) == field_150939_a ||
			   super.func_150936_a(world,x,y,z,side,player,is);
	}

	private boolean tryPlaceSlab(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side){
		Pos pos = Pos.at(x,y,z);
		if (side != -1)pos = pos.offset(side);

		if (pos.getBlock(world) == field_150939_a){
			if (world.checkNoEntityCollision(full.block.getCollisionBoundingBoxFromPool(world,x,y,z)) && pos.setBlock(world,full)){
				SoundType sound = full.block.stepSound;
				world.playSoundEffect(x+0.5D,y+0.5D,z+0.5D,sound.func_150496_b(),(sound.getVolume()+1F)*0.5F,sound.getPitch()*0.8F);
				--is.stackSize;
			}

			return true;
		}
		else return false;
	}
	
	public static interface IBlockSlab{
		public BlockInfo getFullBlock();
	}
}
