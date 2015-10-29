package chylex.hee.gui.helpers;
import net.minecraft.entity.player.EntityPlayer;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.server.S04ContainerEvent;

public interface IContainerEventHandler{
	void onEvent(EntityPlayer player, int eventID);
	
	public static void sendEvent(int id){
		PacketPipeline.sendToServer(new S04ContainerEvent(id));
	}
}
