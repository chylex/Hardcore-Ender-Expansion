package chylex.hee.entity.mob.teleport;
import java.util.Random;
import net.minecraft.entity.Entity;
import chylex.hee.entity.fx.FXType;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C12TeleportEntity;
import chylex.hee.packets.client.C21EffectEntity;
import chylex.hee.system.abstractions.Vec;

@FunctionalInterface
public interface ITeleportListener<T extends Entity>{
	void onTeleport(T entity, Vec startPos, Random rand);
	
	public static final ITeleportListener playSound = (entity, startPos, rand) -> {
		entity.worldObj.playSoundEffect(startPos.x,startPos.y,startPos.z,"mob.endermen.portal",1F,1F);
		entity.playSound("mob.endermen.portal",1F,1F);
	};
	
	public static final ITeleportListener spawnParticlesPrevPos = (entity, startPos, rand) -> {
		PacketPipeline.sendToAllAround(entity.dimension,startPos.x,startPos.y,startPos.z,64D,new C21EffectEntity(FXType.Entity.SIMPLE_TELEPORT_NOSOUND,startPos.x,startPos.y,startPos.z,entity.width,entity.height));
	};
	
	public static final ITeleportListener skipRenderLerp = (entity, startPos, rand) -> {
		PacketPipeline.sendToAllAround(entity,128D,new C12TeleportEntity(entity));
	};
	
	// TODO fx and packets
}