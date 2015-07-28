package chylex.hee.world.loot.interfaces;
import java.util.Random;

@FunctionalInterface
public interface LootDamageProvider<T>{
	int getDamage(T obj, Random rand);
}
