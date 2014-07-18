package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityClientPlayerMP;
import chylex.hee.packets.AbstractClientPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class C__ extends AbstractClientPacket{
	
	public C__(){}
	
	public C__(Object o){
		
	}
	
	@Override
	public void write(ByteBuf buffer){
		
	}

	@Override
	public void read(ByteBuf buffer){
		
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityClientPlayerMP player){
		
	}
}
