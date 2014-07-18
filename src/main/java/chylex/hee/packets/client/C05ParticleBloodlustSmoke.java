package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.monster.EntityEnderman;
import chylex.hee.packets.AbstractClientPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class C05ParticleBloodlustSmoke extends AbstractClientPacket{
	private double x,y,z;
	
	public C05ParticleBloodlustSmoke(){}
	
	public C05ParticleBloodlustSmoke(EntityEnderman enderman){
		this.x = enderman.posX;
		this.y = enderman.posY;
		this.z = enderman.posZ;
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
		for(int a = 0; a < 20; a++)player.worldObj.spawnParticle("largesmoke",x+rand.nextDouble(),y+rand.nextDouble()*2.8D,z+rand.nextDouble(),0D,0D,0D);
	}
}
