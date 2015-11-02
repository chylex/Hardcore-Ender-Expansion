package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityClientPlayerMP;
import chylex.hee.gui.GuiEnderCompendium;
import chylex.hee.mechanics.misc.Baconizer;
import chylex.hee.packets.AbstractClientPacket;
import chylex.hee.system.update.UpdateNotificationManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class C09SimpleEvent extends AbstractClientPacket{
	public enum EventType{
		ENDER_DEMON_SCREECH,
		BACON_COMMAND,
		CHECK_UPDATES,
		RESTORE_COMPENDIUM_PAUSE
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
			case ENDER_DEMON_SCREECH: player.worldObj.playSound(player.posX,player.posY+16D,player.posZ,"hardcoreenderexpansion:enderdemon.scream",1.8F,1F,false); break;
			case BACON_COMMAND: Baconizer.runBaconCommand(); break;
			case CHECK_UPDATES: UpdateNotificationManager.tryRunUpdateCheck(); break;
			case RESTORE_COMPENDIUM_PAUSE: GuiEnderCompendium.pausesGame = GuiEnderCompendium.wasPaused; break;
		}
	}
}
