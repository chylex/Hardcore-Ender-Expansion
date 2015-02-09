package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.packets.AbstractClientPacket;

public class C06SetPlayerVelocity extends AbstractClientPacket{
	private double velX,velY,velZ;
	
	public C06SetPlayerVelocity(){}
	
	public C06SetPlayerVelocity(double velocityX, double velocityY, double velocityZ){
		this.velX = velocityX;
		this.velY = velocityY;
		this.velZ = velocityZ;
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeDouble(velX).writeDouble(velY).writeDouble(velZ);
	}

	@Override
	public void read(ByteBuf buffer){
		velX = buffer.readDouble();
		velY = buffer.readDouble();
		velZ = buffer.readDouble();
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(AbstractClientPlayer player){
		player.motionX = velX;
		player.motionY = velY;
		player.motionZ = velZ;
	}
}
