package chylex.hee.packets.client;
import gnu.trove.impl.Constants;
import gnu.trove.map.hash.TByteObjectHashMap;
import gnu.trove.map.hash.TObjectByteHashMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.effect.EntityWeatherEffect;
import net.minecraft.world.World;
import chylex.hee.entity.weather.EntityWeatherLightningBoltSafe;
import chylex.hee.packets.AbstractClientPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class C05CustomWeather extends AbstractClientPacket{
	public static interface IWeatherEntityConstructor<T extends EntityWeatherEffect>{
		T construct(World world, double x, double y, double z);
	}
	
	private static final byte invalidType = -1;
	
	private static final TObjectByteHashMap<Class<? extends EntityWeatherEffect>> entityToType = new TObjectByteHashMap<>(4,Constants.DEFAULT_LOAD_FACTOR,invalidType);
	private static final TByteObjectHashMap<IWeatherEntityConstructor<?>> typeToEntity = new TByteObjectHashMap<>(4);
	
	private static <T extends EntityWeatherEffect> void register(Class<T> effectClass, IWeatherEntityConstructor<T> constructor){
		final byte nextId = (byte)entityToType.size();
		entityToType.put(effectClass,nextId);
		typeToEntity.put(nextId,constructor);
	}
	
	static{
		register(EntityWeatherLightningBoltSafe.class, EntityWeatherLightningBoltSafe::new);
	}
	
	private byte entityType;
	private int entityId;
	private double x, y, z;
	
	public C05CustomWeather(){}
	
	public C05CustomWeather(EntityWeatherEffect eff){
		this.entityType = entityToType.get(eff.getClass());
		if (this.entityType == invalidType)throw new IllegalArgumentException("Weather effect "+eff.getClass().getName()+" is not registered in the packet handler!");
		
		this.entityId = eff.getEntityId();
		this.x = eff.posX;
		this.y = eff.posY;
		this.z = eff.posZ;
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeByte(entityType).writeInt(entityId).writeDouble(x).writeDouble(y).writeDouble(z);
	}

	@Override
	public void read(ByteBuf buffer){
		entityType = buffer.readByte();
		entityId = buffer.readInt();
		x = buffer.readDouble();
		y = buffer.readDouble();
		z = buffer.readDouble();
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityClientPlayerMP player){
		EntityWeatherEffect eff = typeToEntity.get(entityType).construct(player.worldObj,x,y,z);
		eff.serverPosX = (int)(x*32D);
		eff.serverPosY = (int)(y*32D);
		eff.serverPosZ = (int)(z*32D);
		eff.rotationYaw = eff.rotationPitch = 0F;
		eff.setEntityId(entityId);
		player.worldObj.addWeatherEffect(eff);
	}
}