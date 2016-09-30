package chylex.hee.packets.server;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import chylex.hee.gui.helpers.IContainerEventHandler;
import chylex.hee.packets.AbstractServerPacket;

public class S04ContainerEvent extends AbstractServerPacket{
	private int eventID;
	
	public S04ContainerEvent(){}
	
	public S04ContainerEvent(int eventID){
		this.eventID = eventID;
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeShort(eventID);
	}

	@Override
	public void read(ByteBuf buffer){
		eventID = buffer.readShort();
	}

	@Override
	protected void handle(EntityPlayerMP player){
		if (player.openContainer instanceof IContainerEventHandler)((IContainerEventHandler)player.openContainer).onEvent(player, eventID);
	}
}
