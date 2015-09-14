package chylex.hee.system.abstractions.damage;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.DamageSource;
import chylex.hee.system.abstractions.damage.source.DamagedBy;
import chylex.hee.system.abstractions.damage.source.DamagedByEntity;
import chylex.hee.system.util.MathUtil;

public final class Damage implements IDamage{
	public static Damage base(float amount){
		return new Damage(amount);
	}
	
	public static Damage hostileMob(EntityMob cause){
		return new Damage((float)cause.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue())
			.addModifiers(IDamageModifier.peacefulExclusion,IDamageModifier.difficultyScaling)
			.addModifiers(IDamageModifier.armorProtection,IDamageModifier.enchantmentProtection,IDamageModifier.potionProtection)
			.setSource(cause);
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
	
	public Damage addModifiers(IDamageModifier...modifiers){
		for(IDamageModifier modifier:modifiers)this.modifiers.add(modifier);
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
	
	public float calculateAmount(Entity target, DamageSource damageSource, List<IDamagePostProcessor> postProcessors){
		float amount = baseAmount;
		
		for(IDamageModifier modifier:modifiers){
			amount = modifier.modify(amount,target,damageSource,postProcessors);
			if (MathUtil.floatEquals(amount,0F))break;
		}
		
		return amount;
	}
	
	@Override
	public boolean deal(Entity target){
		if (target.hurtResistantTime > 10)return false;
		
		DamageSource damageSource = createDamageSource();
		List<IDamagePostProcessor> postProcessors = new ArrayList<>();
		
		float amount = calculateAmount(target,damageSource,postProcessors);
		
		if (!MathUtil.floatEquals(amount,0F) && target.attackEntityFrom(damageSource,amount)){
			for(IDamagePostProcessor postProcessor:postProcessors)postProcessor.run(amount);
			return true;
		}
		else return false;
	}
	
	private DamageSource createDamageSource(){
		if (source != null){
			if (indirectSource == null)return new DamagedByEntity(source);
			else return new DamagedByEntity.Indirect(source,indirectSource).setProjectile();
		}
		else return new DamagedBy("generic");
	}
}
