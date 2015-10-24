package chylex.hee.packets.server;
import net.minecraft.entity.player.EntityPlayerMP;
import chylex.hee.game.achievements.AchievementManager;
import chylex.hee.packets.AbstractServerPacket;
import io.netty.buffer.ByteBuf;

public class S03SimpleEvent extends AbstractServerPacket{
	public enum EventType{
		OPEN_COMPENDIUM,
		OPEN_COMPENDIUM_HELP
	}
	
	private EventType type;
	
	public S03SimpleEvent(){}
	
	public S03SimpleEvent(EventType type){
		this.type = type;
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeByte(type.ordinal());
	}

	@Override
	public void read(ByteBuf buffer){
		byte typeId = buffer.readByte();
		if (typeId >= 0 && typeId < EventType.values().length)type = EventType.values()[typeId];
	}

	@Override
	protected void handle(EntityPlayerMP player){
		if (type == null)return;
		
		switch(type){
			case OPEN_COMPENDIUM: player.addStat(AchievementManager.ENDER_COMPENDIUM,1); break;
			case OPEN_COMPENDIUM_HELP: /* TODO CompendiumEvents.getPlayerData(player).setSeenHelp();*/ break;
		}
	}
}
