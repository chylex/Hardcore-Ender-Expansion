package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.world.Explosion;
import chylex.hee.entity.projectile.EntityProjectileFiendFireball;
import chylex.hee.packets.AbstractClientPacket;
import chylex.hee.proxy.ModCommonProxy;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class C12FiendFireballExplosion extends AbstractClientPacket{
	private double x, y, z;
	private boolean isOP;
	
	public C12FiendFireballExplosion(){}
	
	public C12FiendFireballExplosion(EntityProjectileFiendFireball fireball){
		this.x = fireball.posX;
		this.y = fireball.posY;
		this.z = fireball.posZ;
		this.isOP = ModCommonProxy.opMobs;
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeDouble(x).writeDouble(y).writeDouble(z).writeBoolean(isOP);
	}

	@Override
	public void read(ByteBuf buffer){
		x = buffer.readDouble();
		y = buffer.readDouble();
		z = buffer.readDouble();
		isOP = buffer.readBoolean();
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityClientPlayerMP player){
		Explosion explosion = new Explosion(player.worldObj,null,x,y,z,isOP ? 3.4F : 2.8F);
		explosion.doExplosionA();
		explosion.doExplosionB(true);
	}
}