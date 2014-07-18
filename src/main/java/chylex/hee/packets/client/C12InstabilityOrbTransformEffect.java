package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityClientPlayerMP;
import chylex.hee.packets.AbstractClientPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class C12InstabilityOrbTransformEffect extends AbstractClientPacket{
	private double x,y,z;
	private float width,height;
	
	public C12InstabilityOrbTransformEffect(){}
	
	public C12InstabilityOrbTransformEffect(double x, double y, double z, float width, float height){
		this.x = x;
		this.y = y;
		this.z = z;
		this.width = width;
		this.height = height;
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeDouble(x).writeDouble(y).writeDouble(z).writeFloat(width).writeFloat(height);
	}

	@Override
	public void read(ByteBuf buffer){
		x = buffer.readDouble();
		y = buffer.readDouble();
		z = buffer.readDouble();
		width = buffer.readFloat();
		height = buffer.readFloat();
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityClientPlayerMP player){
		for(int a = 0; a < 18; a++){
			player.worldObj.spawnParticle("largesmoke",x+(rand.nextDouble()*width*2D)-width,y+0.1D+rand.nextDouble()*height,z+(rand.nextDouble()*width*2D)-width,0D,0D,0D);
		}
		
		player.worldObj.playSound(x,y,z,"hardcoreenderexpansion:block.random.transform",1.4F,1F+player.worldObj.rand.nextFloat()*0.2F,false);
	}
}
