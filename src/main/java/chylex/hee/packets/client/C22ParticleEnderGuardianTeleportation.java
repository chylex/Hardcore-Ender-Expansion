package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.entity.mob.EntityMobEnderGuardian;
import chylex.hee.packets.AbstractClientPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class C22ParticleEnderGuardianTeleportation extends AbstractClientPacket{
	private int entityId;

	public C22ParticleEnderGuardianTeleportation(){}
	
	public C22ParticleEnderGuardianTeleportation(EntityMobEnderGuardian guardian){
		this.entityId = guardian.getEntityId();
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeInt(entityId);
	}

	@Override
	public void read(ByteBuf buffer){
		entityId = buffer.readInt();
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityClientPlayerMP player){
		Entity e = player.worldObj.getEntityByID(entityId);
		for(int a = 0; a < 80; a++)HardcoreEnderExpansion.fx.portalBig(player.worldObj,e.posX+(rand.nextDouble()-0.5D)*e.width*0.8D,e.posY+rand.nextDouble()*e.height,e.posZ+(rand.nextDouble()-0.5D)*e.width*0.8D,(rand.nextDouble()-0.5D)*0.25D,rand.nextDouble()*0.2D-0.1D,(rand.nextDouble()-0.5D)*0.25D);
	}
}
