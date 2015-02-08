package chylex.hee.packets.server;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import chylex.hee.mechanics.compendium.events.CompendiumEvents;
import chylex.hee.packets.AbstractServerPacket;

public class S05OpenCompendiumHelp extends AbstractServerPacket{
	public S05OpenCompendiumHelp(){}
	
	@Override
	public void write(ByteBuf buffer){}

	@Override
	public void read(ByteBuf buffer){}

	@Override
	protected void handle(EntityPlayerMP player){
		CompendiumEvents.getPlayerData(player).setSeenHelp();
	}
}
