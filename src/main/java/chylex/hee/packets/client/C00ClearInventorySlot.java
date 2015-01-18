package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityClientPlayerMP;
import chylex.hee.packets.AbstractClientPacket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class C00ClearInventorySlot extends AbstractClientPacket{
	private byte slot;
	
	public C00ClearInventorySlot(){}
	
	public C00ClearInventorySlot(int slot){
		this.slot = (byte)slot;
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeByte(slot);
	}

	@Override
	public void read(ByteBuf buffer){
		slot = buffer.readByte();
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityClientPlayerMP player){
		player.inventory.mainInventory[slot] = null;
	}
}
