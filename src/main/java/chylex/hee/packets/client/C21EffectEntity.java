package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import chylex.hee.entity.fx.FXHandler;
import chylex.hee.entity.fx.FXType;
import chylex.hee.packets.AbstractClientPacket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class C21EffectEntity extends AbstractClientPacket{	
	private FXType.Entity type;
	private double x,y,z;
	private float entityWidth,entityHeight;
	
	public C21EffectEntity(){}
	
	public C21EffectEntity(FXType.Entity type, double x, double y, double z, float entityWidth, float entityHeight){
		this.type = type;
		this.x = x;
		this.y = y;
		this.z = z;
		this.entityWidth = entityWidth;
		this.entityHeight = entityHeight;
	}
	
	public C21EffectEntity(FXType.Entity type, Entity entity){
		this(type,entity.posX,entity.posY,entity.posZ,entity.width,entity.height);
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeByte(type.ordinal()).writeDouble(x).writeDouble(y).writeDouble(z).writeFloat(entityWidth).writeFloat(entityHeight);
	}

	@Override
	public void read(ByteBuf buffer){
		byte fxType = buffer.readByte();
		
		if (fxType >= 0 && fxType < FXType.Entity.values.length){
			type = FXType.Entity.values[fxType];
			x = buffer.readDouble();
			y = buffer.readDouble();
			z = buffer.readDouble();
			entityWidth = buffer.readFloat();
			entityHeight = buffer.readFloat();
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(AbstractClientPlayer player){
		if (type != null)FXHandler.handleEntity(player.worldObj,player,type,x,y,z,entityWidth,entityHeight);
	}
}
