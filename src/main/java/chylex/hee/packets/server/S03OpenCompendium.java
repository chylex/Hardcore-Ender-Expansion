package chylex.hee.packets.server;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import chylex.hee.packets.AbstractServerPacket;
import chylex.hee.system.achievements.AchievementManager;

public class S03OpenCompendium extends AbstractServerPacket{
	public S03OpenCompendium(){}
	
	@Override
	public void write(ByteBuf buffer){}

	@Override
	public void read(ByteBuf buffer){}

	@Override
	protected void handle(EntityPlayerMP player){
		player.addStat(AchievementManager.ENDER_COMPENDIUM,1);
	}
}
