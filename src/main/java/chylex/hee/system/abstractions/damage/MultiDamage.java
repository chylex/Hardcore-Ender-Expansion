package chylex.hee.system.abstractions.damage;
import net.minecraft.entity.Entity;

public class MultiDamage{
	public static MultiDamage from(Damage...instances){
		return new MultiDamage(instances);
	}
	
	private final Damage[] instances;
	
	MultiDamage(Damage[] instances){
		this.instances = instances;
	}
	
	public boolean deal(Entity target){
		for(int inst = 0; inst < instances.length; inst++){
			if (!instances[inst].deal(target))return false;
			if (inst < instances.length-1)target.hurtResistantTime = 0;
		}
		
		return true;
	}
}
