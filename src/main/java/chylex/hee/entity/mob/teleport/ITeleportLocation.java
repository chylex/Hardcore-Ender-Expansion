package chylex.hee.entity.mob.teleport;
import java.util.Random;
import net.minecraft.entity.Entity;
import chylex.hee.system.abstractions.Vec;

@FunctionalInterface
public interface ITeleportLocation<T extends Entity>{
	Vec findPosition(T entity, Vec startPos, Random rand);
}
