package chylex.hee.system.abstractions.damage;

@FunctionalInterface
public interface IDamagePostProcessor{
	void run(float finalAmount);
}
