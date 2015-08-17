package chylex.hee.system.abstractions.damage;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import chylex.hee.system.abstractions.damage.source.DamagedBy;
import chylex.hee.system.abstractions.damage.source.DamagedByEntity;
import chylex.hee.system.util.MathUtil;

public final class Damage{
	public static Damage base(float amount){
		return new Damage(amount);
	}
	
	private float baseAmount;
	private List<IDamageModifier> modifiers = new ArrayList<>();
	private Entity source, indirectSource;
	
	Damage(float baseAmount){
		this.baseAmount = baseAmount;
	}
	
	public Damage addModifier(IDamageModifier modifier){
		this.modifiers.add(modifier);
		return this;
	}
	
	public Damage setSource(Entity directSource){
		this.source = directSource;
		return this;
	}
	
	public Damage setSource(Entity projectile, Entity source){
		this.source = projectile;
		this.indirectSource = source;
		return this;
	}
	
	public boolean deal(Entity target){
		DamageSource damageSource = createDamageSource();
		List<IDamagePostProcessor> postProcessors = new ArrayList<>();

		float amount = baseAmount;
		for(IDamageModifier modifier:modifiers)amount = modifier.modify(amount,target,damageSource,postProcessors);
		
		if (!MathUtil.floatEquals(amount,0F) && target.attackEntityFrom(damageSource,amount)){
			for(IDamagePostProcessor postProcessor:postProcessors)postProcessor.run(amount,target);
			return true;
		}
		else return false;
	}
	
	private DamageSource createDamageSource(){
		if (source != null){
			if (indirectSource == null)return new DamagedByEntity(source);
			else return new DamagedByEntity.Indirect(source,indirectSource);
		}
		else return new DamagedBy("generic");
	}
}
