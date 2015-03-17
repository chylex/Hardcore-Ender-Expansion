package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityClientPlayerMP;
import chylex.hee.entity.fx.FXEvents;
import chylex.hee.mechanics.misc.Baconizer;
import chylex.hee.packets.AbstractClientPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class C09SimpleEvent extends AbstractClientPacket{
	public enum EventType{
		BEGIN_TEMPLE_SMOKE,
		ENDER_DEMON_SCREECH,
		BACON_COMMAND,
		SHOW_VOID_CHEST
	}
	
	private EventType type;
	
	public C09SimpleEvent(){}
	
	public C09SimpleEvent(EventType type){
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
	@SideOnly(Side.CLIENT)
	protected void handle(EntityClientPlayerMP player){
		if (type == null)return;
		
		switch(type){
			case BEGIN_TEMPLE_SMOKE: FXEvents.beginTempleSmoke(); break;
			case ENDER_DEMON_SCREECH: player.worldObj.playSound(player.posX,player.posY+16D,player.posZ,"hardcoreenderexpansion:enderdemon.scream",1.8F,1F,false); break;
			case BACON_COMMAND: Baconizer.runBaconCommand(); break;
			case SHOW_VOID_CHEST: // TODO
		}
	}
}
