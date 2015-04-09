package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import chylex.hee.entity.fx.FXHandler;
import chylex.hee.entity.fx.FXType;
import chylex.hee.packets.AbstractClientPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class C20Effect extends AbstractClientPacket{	
	private FXType.Basic type;
	private double x, y, z;
	
	public C20Effect(){}
	
	public C20Effect(FXType.Basic type, double x, double y, double z){
		this.type = type;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public C20Effect(FXType.Basic type, Entity entity){
		this(type,entity.posX,entity.posY,entity.posZ);
	}
	
	public C20Effect(FXType.Basic type, TileEntity tile){
		this(type,tile.xCoord+0.5D,tile.yCoord+0.5D,tile.zCoord+0.5D);
	}

	@Override
	public void write(ByteBuf buffer){
		buffer.writeByte(type.ordinal()).writeDouble(x).writeDouble(y).writeDouble(z);
	}

	@Override
	public void read(ByteBuf buffer){
		byte fxType = buffer.readByte();
		
		if (fxType >= 0 && fxType < FXType.Basic.values.length){
			type = FXType.Basic.values[fxType];
			x = buffer.readDouble();
			y = buffer.readDouble();
			z = buffer.readDouble();
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityClientPlayerMP player){
		if (type != null)FXHandler.handleBasic(player.worldObj,player,type,x,y,z);
	}
}
