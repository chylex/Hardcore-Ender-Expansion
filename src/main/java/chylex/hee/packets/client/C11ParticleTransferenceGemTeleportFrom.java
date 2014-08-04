package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import chylex.hee.packets.AbstractClientPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class C11ParticleTransferenceGemTeleportFrom extends AbstractClientPacket{
	private double x,y,z;
	private float width,height;
	
	public C11ParticleTransferenceGemTeleportFrom(){}
	
	public C11ParticleTransferenceGemTeleportFrom(Entity entity){
		this.x = entity.posX;
		this.y = entity.posY;
		this.z = entity.posZ;
		this.width = entity.width;
		this.height = entity.height;
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
		float width = this.width*1.2F, hwidth = width*0.5F, height = this.height*0.9F;

		for(int a = 0; a < 20; a++)player.worldObj.spawnParticle("largesmoke",x+rand.nextDouble()*width-hwidth,y+rand.nextDouble()*height,z+rand.nextDouble()*width-hwidth,0D,0.04D,0D);
		player.worldObj.playSound(x+0.5D,y+1D,z+0.5D,"mob.endermen.portal",1.2F,player.worldObj.rand.nextFloat()*0.05F+0.85F,false);
	}
}
