package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.block.BlockList;
import chylex.hee.packets.AbstractClientPacket;

public class C08PlaySound extends AbstractClientPacket{
	public static final byte ENDEREYE_ATTACK_POOF = 0,
							 ENDEREYE_ATTACK_CONFUSION = 1,
							 ENDEREYE_ATTACK_LASER_ADD = 2,
							 ENDEREYE_ATTACK_LASER_END = 3,
							 GLASS_BREAK = 4,
							 GHOST_SPAWN = 5,
							 SPAWN_FIREBALL = 6,
							 GHOST_DEATH = 7,
							 STARDUST_TRANSFORMATION = 8,
							 RANDOM_BREAK = 9,
							 EXP_ORB = 10,
							 GHOST_MOVE = 11,
							 PERSEGRIT_FOOTSTEPS = 12,
							 CHEST_OPEN = 13,
							 CHEST_CLOSE = 14,
							 GRASS_BREAK = 15,
							 GRAVEL_BREAK = 16,
							 SAND_BREAK = 17,
							 WOOD_BREAK = 18,
							 PIG_HURT = 19,
							 PIG_DIE = 20,
							 COW_HURT = 21,
							 COW_DIE = 22,
							 SHEEP_HURT = 23,
							 SHEEP_DIE = 24,
							 CHICKEN_HURT = 25,
							 CHICKEN_DIE = 26,
							 ZOMBIE_HURT = 27,
							 ZOMBIE_DIE = 28,
							 SKELETON_HURT = 29,
							 SKELETON_DIE = 30,
							 HAUNTEDMINER_ATTACK_BLAST = 31;
	
	private static final String[] soundNames = new String[]{
		/*  0 */ "hardcoreenderexpansion:mob.endereye.attack.poof",
		/*  1 */ "hardcoreenderexpansion:mob.endereye.attack.confusion",
		/*  2 */ "hardcoreenderexpansion:mob.endereye.attack.laseradd",
		/*  3 */ "hardcoreenderexpansion:mob.endereye.attack.laserend",
		/*  4 */ "dig.glass",
		/*  5 */ "hardcoreenderexpansion:mob.ghost.spawn",
		/*  6 */ "mob.ghast.fireball",
		/*  7 */ "hardcoreenderexpansion:mob.ghost.death",
		/*  8 */ "hardcoreenderexpansion:block.random.transform",
		/*  9 */ "random.break",
		/* 10 */ "random.orb",
		/* 11 */ "hardcoreenderexpansion:mob.ghost.move",
		/* 12 */ BlockList.persegrit.stepSound.getStepSound(),
		/* 13 */ "random.chestopen",
		/* 14 */ "random.chestclosed",
		/* 15 */ Block.soundTypeGrass.getBreakSound(),
		/* 16 */ Block.soundTypeGravel.getBreakSound(),
		/* 17 */ Block.soundTypeSand.getBreakSound(),
		/* 18 */ Block.soundTypeWood.getBreakSound(),
		/* 19 */ "mob.pig.say",
		/* 20 */ "mob.pig.death",
		/* 21 */ "mob.cow.hurt",
		/* 22 */ "mob.cow.hurt",
		/* 23 */ "mob.sheep.say",
		/* 24 */ "mob.sheep.say",
		/* 25 */ "mob.chicken.hurt",
		/* 26 */ "mob.chicken.hurt",
		/* 27 */ "mob.zombie.hurt",
		/* 28 */ "mob.zombie.death",
		/* 29 */ "mob.skeleton.hurt",
		/* 30 */ "mob.skeleton.death",
		/* 31 */ "hardcoreenderexpansion:mob.hauntedminer.attack.blast"
	};
	
	private byte soundId;
	private double x, y, z;
	private float volume, pitch;
	
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
		if (soundId >= 0 && soundId < soundNames.length)player.worldObj.playSound(x,y,z,soundNames[soundId],volume,pitch,false);
	}
}
