package chylex.hee.packets.server;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import chylex.hee.block.BlockList;
import chylex.hee.packets.AbstractServerPacket;

public class S00DeathFlowerPot extends AbstractServerPacket{
	private int x,y,z;
	
	public S00DeathFlowerPot(){}
	
	public S00DeathFlowerPot(int x, int y, int z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeInt(x).writeInt(y).writeInt(z);
	}

	@Override
	public void read(ByteBuf buffer){
		x = buffer.readInt();
		y = buffer.readInt();
		z = buffer.readInt();
	}

	@Override
	protected void handle(EntityPlayerMP player){
		if (player.worldObj.getBlock(x,y,z) == Blocks.flower_pot && player.worldObj.getBlockMetadata(x,y,z) == 0){
			ItemStack is = player.inventory.getCurrentItem();
			if (is != null && is.getItem() == Item.getItemFromBlock(BlockList.death_flower)){
				if (!player.capabilities.isCreativeMode)--is.stackSize;
				player.worldObj.setBlock(x,y,z,BlockList.death_flower_pot,is.getItemDamage(),3);
			}
		}
	}
}
