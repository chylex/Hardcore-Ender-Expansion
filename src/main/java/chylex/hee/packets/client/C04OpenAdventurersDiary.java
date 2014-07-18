package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityClientPlayerMP;
import chylex.hee.mechanics.misc.LoreTexts;
import chylex.hee.packets.AbstractClientPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class C04OpenAdventurersDiary extends AbstractClientPacket{
	private byte[] pages;
	
	public C04OpenAdventurersDiary(){}
	
	public C04OpenAdventurersDiary(byte[] pages){
		this.pages = pages;
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeByte(pages.length);
		buffer.writeBytes(pages);
	}

	@Override
	public void read(ByteBuf buffer){
		pages = new byte[buffer.readByte()];
		buffer.readBytes(pages);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityClientPlayerMP player){
		LoreTexts.openBook(player,pages);
	}
}
