package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityWeatherEffect;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.entity.weather.EntityWeatherLightningBoltDemon;
import chylex.hee.packets.AbstractClientPacket;

public class C05CustomWeather extends AbstractClientPacket{
	private int entityId;
	private double x,y,z;
	private byte weatherType;
	
	public C05CustomWeather(){}
	
	public C05CustomWeather(EntityWeatherEffect eff, byte weatherType){
		this.entityId = eff.getEntityId();
		this.x = eff.posX;
		this.y = eff.posY;
		this.z = eff.posZ;
		this.weatherType = weatherType;
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeInt(entityId).writeDouble(x).writeDouble(y).writeDouble(z).writeByte(weatherType);
	}

	@Override
	public void read(ByteBuf buffer){
		entityId = buffer.readInt();
		x = buffer.readDouble();
		y = buffer.readDouble();
		z = buffer.readDouble();
		weatherType = buffer.readByte();
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(AbstractClientPlayer player){
		World world = Minecraft.getMinecraft().theWorld;
		Entity e = null;
		
		if (weatherType == 0)e = new EntityWeatherLightningBoltDemon(world,x,y,z);
		else return;
		
		e.serverPosX = (int)(x*32D);
		e.serverPosY = (int)(y*32D);
		e.serverPosZ = (int)(z*32D);
		e.rotationYaw = e.rotationPitch = 0F;
		e.setEntityId(entityId);
		world.addWeatherEffect(e);
	}
}
