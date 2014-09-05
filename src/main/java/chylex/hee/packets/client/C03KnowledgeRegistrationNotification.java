package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityClientPlayerMP;
import chylex.hee.packets.AbstractClientPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class C03KnowledgeRegistrationNotification extends AbstractClientPacket{
	private byte lengthOrStatus;
	private String registration;
	
	public C03KnowledgeRegistrationNotification(){}
	
	public C03KnowledgeRegistrationNotification(int status){
		this.lengthOrStatus = (byte)status;
	}
	
	public C03KnowledgeRegistrationNotification(String registration){
		this.lengthOrStatus = (byte)registration.length();
		this.registration = registration;
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeByte(lengthOrStatus);
		if (registration != null){
			for(int a = 0; a < lengthOrStatus; a++)buffer.writeChar(registration.charAt(a));
		}
	}

	@Override
	public void read(ByteBuf buffer){
		lengthOrStatus = buffer.readByte();
		if (lengthOrStatus > 0){
			StringBuilder build = new StringBuilder();
			for(int a = 0; a < lengthOrStatus; a++)build.append(buffer.readChar());
			registration = build.toString();
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityClientPlayerMP player){
		// TODO
		/*if (lengthOrStatus > 0){
			KnowledgeRegistration reg = KnowledgeRegistration.lookup.get(registration);
			if (reg != null)OverlayManager.addNotification("Unlocked new knowledge about "+reg.getRenderer().getTooltip());
		}
		else if (lengthOrStatus == -1)OverlayManager.addNotification("Unlocked new knowledge, but there was nothing to write it on");
		else if (lengthOrStatus == -2)OverlayManager.addNotification("Ender Compendium is required to read the fragment.");
		else if (lengthOrStatus == -3)OverlayManager.addNotification("There is nothing you can learn from this fragment.");*/
	}
}
