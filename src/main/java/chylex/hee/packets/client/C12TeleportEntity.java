package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import chylex.hee.packets.AbstractClientPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class C12TeleportEntity extends AbstractClientPacket{
	private int entityId;
	private double posX, posY, posZ;
	
	public C12TeleportEntity(){}
	
	public C12TeleportEntity(Entity entity){
		this.entityId = entity.getEntityId();
		this.posX = entity.posX;
		this.posY = entity.posY;
		this.posZ = entity.posZ;
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeInt(entityId).writeDouble(posX).writeDouble(posY).writeDouble(posZ);
	}

	@Override
	public void read(ByteBuf buffer){
		entityId = buffer.readInt();
		posX = buffer.readDouble();
		posY = buffer.readDouble();
		posZ = buffer.readDouble();
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityClientPlayerMP player){
		Entity entity = player.worldObj.getEntityByID(entityId);
		if (entity != null)entity.setPosition(posX,posY,posZ);
	}
}
