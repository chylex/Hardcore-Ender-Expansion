package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.world.World;
import chylex.hee.packets.AbstractClientPacket;
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class C03ParticleSnowBall extends AbstractClientPacket{
	private double x,y,z;
	
	public C03ParticleSnowBall(){}
	
	public C03ParticleSnowBall(double x, double y, double z){
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
		this.x = buffer.readDouble();
		this.y = buffer.readDouble();
		this.z = buffer.readDouble();
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityClientPlayerMP player){
		World world = player.worldObj;
		
		for(double xx = x-5; xx <= x+5; xx++){
			for(double yy = y-5; yy <= y+5; yy++){
				for(double zz = z-5; zz <= z+5; zz++){
					if (Math.sqrt(MathUtil.square(xx-x)+MathUtil.square(yy-y)+MathUtil.square(zz-z)) > 5D)continue;
					for(int i = 0; i < 2; ++i)world.spawnParticle("snowballpoof",xx+rand.nextDouble()-0.5D,yy,zz+rand.nextDouble()-0.5D,0D,0D,0D);
				}
			}
		}
		
		world.playSound(x,y,z,"hardcoreenderexpansion:environment.random.freeze",1F,rand.nextFloat()*0.1F+0.9F,false);
	}
}
