package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.entity.mob.EntityMobCorporealMirage;
import chylex.hee.packets.AbstractClientPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class C27ParticleMirageHurt extends AbstractClientPacket{
	private int entityId;
	
	public C27ParticleMirageHurt(){}
	
	public C27ParticleMirageHurt(EntityMobCorporealMirage mirage){
		this.entityId = mirage.getEntityId();
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
		
		if (e != null && e instanceof EntityMobCorporealMirage){
			EntityMobCorporealMirage mirage = (EntityMobCorporealMirage)e;
			for(int a = 0; a < 10; a++)HardcoreEnderExpansion.fx.mirageHurt(mirage);
		}
	}
}
