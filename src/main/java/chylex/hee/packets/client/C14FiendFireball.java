package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import chylex.hee.entity.projectile.EntityProjectileFiendFireball;
import chylex.hee.packets.AbstractClientPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class C14FiendFireball extends AbstractClientPacket{
	private int entityId;
	private double x, z;
	
	public C14FiendFireball(){}
	
	public C14FiendFireball(EntityProjectileFiendFireball fuckball, double x, double z){
		this.entityId = fuckball.getEntityId();
		this.x = x;
		this.z = z;
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeInt(entityId).writeDouble(x).writeDouble(z);
	}

	@Override
	public void read(ByteBuf buffer){
		entityId = buffer.readInt();
		x = buffer.readDouble();
		z = buffer.readDouble();
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityClientPlayerMP player){
		Entity e = player.worldObj.getEntityByID(entityId);
		
		if (e != null){
			EntityProjectileFiendFireball fuckball = (EntityProjectileFiendFireball)e;
			fuckball.prevActualPosX = fuckball.actualPosX;
			fuckball.prevActualPosZ = fuckball.actualPosZ;
			fuckball.actualPosX = x;
			fuckball.actualPosZ = z;
		}
	}
}
