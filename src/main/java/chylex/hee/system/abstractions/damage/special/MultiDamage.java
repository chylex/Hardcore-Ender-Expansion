package chylex.hee.system.abstractions.damage.special;
import net.minecraft.entity.Entity;
import chylex.hee.system.abstractions.damage.IDamage;

public class MultiDamage implements IDamage{
	public static MultiDamage from(IDamage...instances){
		return new MultiDamage(instances);
	}
	
	private final IDamage[] instances;
	
	private MultiDamage(IDamage[] instances){
		this.instances = instances;
	}
	
	@Override
	public boolean deal(Entity target){
		for(int inst = 0; inst < instances.length; inst++){
			if (!instances[inst].deal(target))return false;
			if (inst < instances.length-1)target.hurtResistantTime = 0;
		}
		
		return true;
	}
}
