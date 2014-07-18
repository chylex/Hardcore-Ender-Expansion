package chylex.hee.packets.server;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import chylex.hee.gui.ContainerEndPowderEnhancements;
import chylex.hee.packets.AbstractServerPacket;

public class S01GuiEnhancementsClick extends AbstractServerPacket{
	private byte slot;
	
	public S01GuiEnhancementsClick(){}
	
	public S01GuiEnhancementsClick(int slot){
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
	protected void handle(EntityPlayerMP player){
		if (player.openContainer instanceof ContainerEndPowderEnhancements){
			((ContainerEndPowderEnhancements)player.openContainer).onEnhancementSlotClick(slot);
		}
	}
}
