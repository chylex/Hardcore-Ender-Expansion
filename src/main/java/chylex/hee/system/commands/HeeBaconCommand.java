package chylex.hee.system.commands;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C09SimpleEvent;
import chylex.hee.packets.client.C09SimpleEvent.EventType;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

public class HeeBaconCommand extends HeeCommand{
	public HeeBaconCommand(){
		super("bacon");
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args){
		if (sender instanceof EntityPlayer)PacketPipeline.sendToPlayer((EntityPlayer)sender,new C09SimpleEvent(EventType.BACON_COMMAND));
	}
}
