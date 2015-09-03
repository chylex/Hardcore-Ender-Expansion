package chylex.hee.packets.client;
import java.util.Random;
import net.minecraft.client.entity.EntityClientPlayerMP;
import chylex.hee.entity.fx.EntityAltarOrbFX;
import chylex.hee.mechanics.essence.EssenceType;
import chylex.hee.packets.AbstractClientPacket;
import chylex.hee.proxy.FXClientProxy;
import chylex.hee.tileentity.TileEntityEssenceAltar;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class C11ParticleAltarOrb extends AbstractClientPacket{
	private double startX, startY, startZ;
	private double targetX, targetY, targetZ;
	private byte essenceId, amount;
	private float width, height;
	
	public C11ParticleAltarOrb(){}
	
	public C11ParticleAltarOrb(TileEntityEssenceAltar altar, double targetX, double targetY, double targetZ){
		this(altar,targetX,targetY,targetZ,(byte)1);
	}
	
	public C11ParticleAltarOrb(TileEntityEssenceAltar altar, double targetX, double targetY, double targetZ, byte amount){
		this.startX = altar.xCoord+0.5D;
		this.startY = altar.yCoord+0.5D;
		this.startZ = altar.zCoord+0.5D;
		this.targetX = targetX;
		this.targetY = targetY;
		this.targetZ = targetZ;
		this.essenceId = altar.getEssenceType().id;
		this.amount = amount;
	}
	
	public C11ParticleAltarOrb(double startX, double startY, double startZ, double targetX, double targetY, double targetZ, byte essenceId, byte amount){
		this.startX = startX;
		this.startY = startY;
		this.startZ = startZ;
		this.targetX = targetX;
		this.targetY = targetY;
		this.targetZ = targetZ;
		this.essenceId = essenceId;
		this.amount = amount;
	}
	
	public C11ParticleAltarOrb(double startX, double startY, double startZ, double targetX, double targetY, double targetZ, byte essenceId, byte amount, float width, float height){
		this(startX,startY,startZ,targetX,targetY,targetZ,essenceId,amount);
		this.width = width;
		this.height = height;
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeDouble(startX).writeDouble(startY).writeDouble(startZ);
		buffer.writeDouble(targetX).writeDouble(targetY).writeDouble(targetZ);
		buffer.writeByte(essenceId).writeByte(amount);
	}

	@Override
	public void read(ByteBuf buffer){
		startX = buffer.readDouble();
		startY = buffer.readDouble();
		startZ = buffer.readDouble();
		targetX = buffer.readDouble();
		targetY = buffer.readDouble();
		targetZ = buffer.readDouble();
		essenceId = buffer.readByte();
		amount = buffer.readByte();
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityClientPlayerMP player){
		EssenceType essence = EssenceType.getById(essenceId);
		Random rand = player.worldObj.rand;
		
		for(int a = 0; a < amount; a++){
			FXClientProxy.spawn(new EntityAltarOrbFX(player.worldObj,startX+width*(rand.nextFloat()-rand.nextFloat()),startY+height*rand.nextFloat(),startZ+width*(rand.nextFloat()-rand.nextFloat()),targetX,targetY,targetZ,essence));
		}
	}
}
