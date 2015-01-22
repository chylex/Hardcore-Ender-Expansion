package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.mechanics.essence.EssenceType;
import chylex.hee.packets.AbstractClientPacket;
import chylex.hee.tileentity.TileEntityEssenceAltar;

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
		this.startX = altar.getPos().getX()+0.5D;
		this.startY = altar.getPos().getY()+0.5D;
		this.startZ = altar.getPos().getZ()+0.5D;
		this.targetX = targetX;
		this.targetY = targetY;
		this.targetZ = targetZ;
		this.essenceId = altar.getEssenceType().getId();
		this.amount = amount;
	}
	
	public C11ParticleAltarOrb(double startX, double startY, double startZ, double targetX, double targetY, double targetZ, byte essenceId, byte amount){
		this.startX = startX;
		this.startY = startX;
		this.startZ = startX;
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

		for(int a = 0; a < amount; a++){
			HardcoreEnderExpansion.fx.altarOrb(player.worldObj,startX+width*(rand.nextFloat()-rand.nextFloat()),startY+height*rand.nextFloat(),startZ+width*(rand.nextFloat()-rand.nextFloat()),targetX,targetY,targetZ,essence);
		}
	}
}
