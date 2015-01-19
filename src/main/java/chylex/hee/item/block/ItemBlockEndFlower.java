package chylex.hee.item.block;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.server.S00DeathFlowerPot;

public class ItemBlockEndFlower extends ItemBlock{
	public ItemBlockEndFlower(Block block){
		super(block);
		setHasSubtypes(true);
	}
	
	@Override
	public int getMetadata(int damage){
		return damage;
	}
	
	@Override
	public boolean onItemUseFirst(ItemStack is, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ){
		if (world.isRemote){
			IBlockState state = world.getBlockState(pos);
			
			if (state.getBlock() == Blocks.flower_pot && ((BlockFlowerPot.EnumFlowerType)state.getBlock().getActualState(state,world,pos).getValue(BlockFlowerPot.CONTENTS)) == BlockFlowerPot.EnumFlowerType.EMPTY){
				PacketPipeline.sendToServer(new S00DeathFlowerPot(pos));
				return true;
			}
		}
		
		return super.onItemUseFirst(is,player,world,pos,side,hitX,hitY,hitZ);
	}
}
