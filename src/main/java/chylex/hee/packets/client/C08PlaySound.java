package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityClientPlayerMP;
import chylex.hee.packets.AbstractClientPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class C08PlaySound extends AbstractClientPacket{
	public static final byte ENDEREYE_ATTACK_POOF = 0,
							 ENDEREYE_ATTACK_CONFUSION = 1,
							 ENDEREYE_ATTACK_LASER_ADD = 2,
							 ENDEREYE_ATTACK_LASER_END = 3,
							 GLASS_BREAK = 4,
							 GHOST_SPAWN = 5,
							 SPAWN_FIREBALL = 6,
							 GHOST_DEATH = 7,
							 STARDUST_TRANSFORMATION = 8;
	
	private byte soundId;
	private double x,y,z;
	private float volume,pitch;
	
	public C08PlaySound(){}
	
	public C08PlaySound(byte soundId, double x, double y, double z, float volume, float pitch){
		this.soundId = soundId;
		this.x = x;
		this.y = y;
		this.z = z;
		this.volume = volume;
		this.pitch = pitch;
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeByte(soundId).writeDouble(x).writeDouble(y).writeDouble(z).writeFloat(volume).writeFloat(pitch);
	}

	@Override
	public void read(ByteBuf buffer){
		soundId = buffer.readByte();
		x = buffer.readDouble();
		y = buffer.readDouble();
		z = buffer.readDouble();
		volume = buffer.readFloat();
		pitch = buffer.readFloat();
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityClientPlayerMP player){
		String effectStr = null;

		if (soundId == 0)effectStr = "hardcoreenderexpansion:mob.endereye.attack.poof";
		else if (soundId == 1)effectStr = "hardcoreenderexpansion:mob.endereye.attack.confusion";
		else if (soundId == 2)effectStr = "hardcoreenderexpansion:mob.endereye.attack.laseradd";
		else if (soundId == 3)effectStr = "hardcoreenderexpansion:mob.endereye.attack.laserend";
		else if (soundId == 4)effectStr = "dig.glass";
		else if (soundId == 5)effectStr = "hardcoreenderexpansion:mob.ghost.spawn";
		else if (soundId == 6)effectStr = "mob.ghast.fireball";
		else if (soundId == 7)effectStr = "hardcoreenderexpansion:mob.ghost.death";
		else if (soundId == 8)effectStr = "hardcoreenderexpansion:block.random.transform";
		else if (soundId == 9)effectStr = "random.break";

		if (effectStr != null)player.worldObj.playSound(x,y,z,effectStr,volume,pitch,false);
	}
}
