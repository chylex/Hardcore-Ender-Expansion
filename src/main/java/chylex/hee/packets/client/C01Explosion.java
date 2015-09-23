package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityClientPlayerMP;
import chylex.hee.packets.AbstractClientPacket;
import chylex.hee.system.abstractions.Explosion;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class C01Explosion extends AbstractClientPacket{
	private Explosion explosion;
	private ByteBuf buffer;
	
	public C01Explosion(){}
	
	public C01Explosion(Explosion explosion){
		this.explosion = explosion;
	}
	
	@Override
	public void write(ByteBuf buffer){
		explosion.writePacket(buffer);
	}

	@Override
	public void read(ByteBuf buffer){
		this.buffer = buffer;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityClientPlayerMP player){
		new Explosion(player.worldObj,buffer).triggerClient();
		buffer = null;
	}
}
