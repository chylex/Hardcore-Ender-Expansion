package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.world.Explosion;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.entity.projectile.EntityProjectileFiendFireball;
import chylex.hee.packets.AbstractClientPacket;
import chylex.hee.proxy.ModCommonProxy;

public class C12FiendFireballExplosion extends AbstractClientPacket{
	private double x, y, z;
	private boolean destroysBlocks, isOP;
	
	public C12FiendFireballExplosion(){}
	
	public C12FiendFireballExplosion(EntityProjectileFiendFireball fireball){
		this.x = fireball.posX;
		this.y = fireball.posY;
		this.z = fireball.posZ;
		this.destroysBlocks = fireball.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing");
		this.isOP = ModCommonProxy.opMobs;
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeDouble(x).writeDouble(y).writeDouble(z).writeBoolean(destroysBlocks).writeBoolean(isOP);
	}

	@Override
	public void read(ByteBuf buffer){
		x = buffer.readDouble();
		y = buffer.readDouble();
		z = buffer.readDouble();
		destroysBlocks = buffer.readBoolean();
		isOP = buffer.readBoolean();
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(AbstractClientPlayer player){
		Explosion explosion = new Explosion(player.worldObj,null,x,y,z,isOP ? 3.4F : 2.8F,false,destroysBlocks);
		explosion.doExplosionA();
		explosion.doExplosionB(true);
	}
}