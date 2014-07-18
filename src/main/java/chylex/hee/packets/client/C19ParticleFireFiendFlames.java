package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.entity.boss.EntityMiniBossFireFiend;
import chylex.hee.packets.AbstractClientPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class C19ParticleFireFiendFlames extends AbstractClientPacket{
	private int fiendEntityId,targetEntityId;
	private float offsetX,offsetY,offsetZ;
	private byte amount;
	
	public C19ParticleFireFiendFlames(){}
	
	public C19ParticleFireFiendFlames(EntityMiniBossFireFiend fiend, EntityLivingBase target, float[] offsets, byte amount){
		this.fiendEntityId = fiend.getEntityId();
		this.targetEntityId = target.getEntityId();
		this.offsetX = offsets[0];
		this.offsetY = offsets[1];
		this.offsetZ = offsets[2];
		this.amount = amount;
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeInt(fiendEntityId).writeInt(targetEntityId);
		buffer.writeFloat(offsetX).writeFloat(offsetY).writeFloat(offsetZ);
		buffer.writeByte(amount);
	}

	@Override
	public void read(ByteBuf buffer){
		fiendEntityId = buffer.readInt();
		targetEntityId = buffer.readInt();
		offsetX = buffer.readFloat();
		offsetY = buffer.readFloat();
		offsetZ = buffer.readFloat();
		amount = buffer.readByte();
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityClientPlayerMP player){
		World world = player.worldObj;
		Entity fiend = world.getEntityByID(fiendEntityId),target = world.getEntityByID(targetEntityId);
		if (fiend == null || target == null)return;
		
		Vec3 fiendLook = fiend.getLookVec();
		
		for(int a = 0; a < amount; a++){
			HardcoreEnderExpansion.fx.flame(world,target.posX+offsetX,target.posY+offsetY,target.posZ+offsetZ,3);
			HardcoreEnderExpansion.fx.flame(world,fiend.posX+fiendLook.xCoord,fiend.posY+1.5D,fiend.posZ+fiendLook.zCoord,3);
		}
	}
}
