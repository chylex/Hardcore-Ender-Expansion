package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityClientPlayerMP;
import chylex.hee.packets.AbstractClientPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class C21ParticleIgneousRockFlame extends AbstractClientPacket{
	private double x,y,z;
	
	public C21ParticleIgneousRockFlame(){}
	
	public C21ParticleIgneousRockFlame(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeDouble(x).writeDouble(y).writeDouble(z);
	}

	@Override
	public void read(ByteBuf buffer){
		x = buffer.readDouble();
		y = buffer.readDouble();
		z = buffer.readDouble();
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityClientPlayerMP player){
		for(int a = 0; a < 6; a++){
			player.worldObj.spawnParticle("flame",x+0.6F*(rand.nextFloat()-0.5F),y+0.6F*rand.nextFloat(),z+0.6F*(rand.nextFloat()-0.5F),0D,0.04D,0D);
		}
		player.worldObj.playSoundEffect(x,y,z,"random.fizz",0.6F,2.6F+(rand.nextFloat()-rand.nextFloat())*0.8F);
	}
}
