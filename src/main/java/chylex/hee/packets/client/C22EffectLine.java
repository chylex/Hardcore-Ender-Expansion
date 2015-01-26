package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import chylex.hee.entity.fx.FXHandler;
import chylex.hee.entity.fx.FXType;
import chylex.hee.packets.AbstractClientPacket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class C22EffectLine extends AbstractClientPacket{	
	private FXType.Line type;
	private double x1,y1,z1,x2,y2,z2;
	
	public C22EffectLine(){}
	
	public C22EffectLine(FXType.Line type, double x1, double y1, double z1, double x2, double y2, double z2){
		this.type = type;
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
		this.x2 = x2;
		this.y2 = y2;
		this.z2 = z2;
	}
	
	public C22EffectLine(FXType.Line type, Entity entity1, Entity entity2){
		this(type,entity1.posX,entity1.posY+entity2.height*0.5F,entity1.posZ,entity2.posX,entity2.posY+entity2.height*0.5F,entity2.posZ);
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeByte(type.ordinal()).writeDouble(x1).writeDouble(y1).writeDouble(z1).writeDouble(x2).writeDouble(y2).writeDouble(z2);
	}

	@Override
	public void read(ByteBuf buffer){
		byte fxType = buffer.readByte();
		
		if (fxType >= 0 && fxType < FXType.Line.values.length){
			type = FXType.Line.values[fxType];
			x1 = buffer.readDouble();
			y1 = buffer.readDouble();
			z1 = buffer.readDouble();
			x2 = buffer.readDouble();
			y2 = buffer.readDouble();
			z2 = buffer.readDouble();
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(AbstractClientPlayer player){
		if (type != null)FXHandler.handleLine(player.worldObj,player,type,x1,y1,z1,x2,y2,z2);
	}
}
