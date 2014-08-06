package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.entity.mob.EntityMobFireGolem;
import chylex.hee.packets.AbstractClientPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class C15ParticleFireGolemFlame extends AbstractClientPacket{
	private int entityId;
	private byte amount;
	
	public C15ParticleFireGolemFlame(){}
	
	public C15ParticleFireGolemFlame(EntityMobFireGolem golem, byte amount){
		this.entityId = golem.getEntityId();
		this.amount = amount;
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeInt(entityId).writeByte(amount);
	}

	@Override
	public void read(ByteBuf buffer){
		entityId = buffer.readInt();
		amount = buffer.readByte();
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityClientPlayerMP player){
		World world = player.worldObj;
		Entity golem = world.getEntityByID(entityId);
		if (golem == null)return;
		
		Vec3 look = golem.getLookVec();
		double xx = golem.posX+look.xCoord*0.8D, zz = golem.posZ+look.zCoord*0.8D;
		for(int a = 0; a < rand.nextInt(2+amount); a++)HardcoreEnderExpansion.fx.flame(world,xx,golem.posY+1D,zz,3);
	}
}
