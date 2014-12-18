package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import chylex.hee.entity.projectile.EntityProjectileFiendFireball;
import chylex.hee.packets.AbstractClientPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class C69FiendFuckball extends AbstractClientPacket{
	private int enTittyId;
	private double fuX, ballZ;
	
	public C69FiendFuckball(){}
	
	public C69FiendFuckball(EntityProjectileFiendFireball fuckball, double x, double z){
		this.enTittyId = fuckball.getEntityId();
		this.fuX = x;
		this.ballZ = z;
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeInt(enTittyId).writeDouble(fuX).writeDouble(ballZ);
	}

	@Override
	public void read(ByteBuf buffer){
		enTittyId = buffer.readInt();
		fuX = buffer.readDouble();
		ballZ = buffer.readDouble();
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityClientPlayerMP player){
		Entity e = player.worldObj.getEntityByID(enTittyId);
		
		if (e != null){
			EntityProjectileFiendFireball fuckball = (EntityProjectileFiendFireball)e;
			fuckball.prevActualPosX = fuckball.actualPosX;
			fuckball.prevActualPosZ = fuckball.actualPosZ;
			fuckball.actualPosX = fuX;
			fuckball.actualPosZ = ballZ;
		}
	}
}
