package chylex.hee.packets.server;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import chylex.hee.block.BlockDeathFlower;
import chylex.hee.block.BlockList;
import chylex.hee.packets.AbstractServerPacket;

public class S00DeathFlowerPot extends AbstractServerPacket{
	private BlockPos pos;
	
	public S00DeathFlowerPot(){}
	
	public S00DeathFlowerPot(BlockPos pos){
		this.pos = pos;
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeLong(pos.toLong());
	}

	@Override
	public void read(ByteBuf buffer){
		pos = BlockPos.fromLong(buffer.readLong());
	}

	@Override
	protected void handle(EntityPlayerMP player){
		IBlockState state = player.worldObj.getBlockState(pos);
		
		if (state.getBlock() == Blocks.flower_pot && ((Integer)state.getValue(BlockDeathFlower.DECAY)).intValue() == 0){
			ItemStack is = player.inventory.getCurrentItem();
			if (is != null && is.getItem() == Item.getItemFromBlock(BlockList.death_flower)){
				if (!player.capabilities.isCreativeMode)--is.stackSize;
				player.worldObj.setBlockState(pos,BlockList.death_flower_pot.getStateFromMeta(is.getItemDamage()));
			}
		}
	}
}
