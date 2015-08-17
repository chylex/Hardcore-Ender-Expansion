package chylex.hee.system.abstractions.damage;
import net.minecraft.entity.Entity;

@FunctionalInterface
public interface IDamagePostProcessor{
	void run(float amount, Entity target);
}
