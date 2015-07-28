package chylex.hee.world.loot.interfaces;
import java.util.Random;

@FunctionalInterface
public interface LootAmountProvider<T>{
	int getAmount(T obj, Random rand);
}
