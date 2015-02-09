package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.packets.AbstractClientPacket;

public class __ extends AbstractClientPacket{
	
	public __(){}
	
	public __(Object o){
		
	}
	
	@Override
	public void write(ByteBuf buffer){
		
	}

	@Override
	public void read(ByteBuf buffer){
		
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(AbstractClientPlayer player){
		
	}
}
