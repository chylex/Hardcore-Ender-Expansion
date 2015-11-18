package chylex.hee.system.abstractions.entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;

public final class EntityAttributes{
	public static final IAttribute maxHealth = SharedMonsterAttributes.maxHealth;
	public static final IAttribute attackDamage = SharedMonsterAttributes.attackDamage;
	public static final IAttribute movementSpeed = SharedMonsterAttributes.movementSpeed;
	public static final IAttribute followRange = SharedMonsterAttributes.followRange;
	
	public static void setValue(EntityLivingBase entity, IAttribute attribute, double baseValue){
		entity.getEntityAttribute(attribute).setBaseValue(baseValue);
	}
	
	public static double getValue(EntityLivingBase entity, IAttribute attribute){
		return entity.getEntityAttribute(attribute).getAttributeValue();
	}
	
	public static boolean applyModifier(EntityLivingBase entity, IAttribute attribute, AttributeModifier modifier){
		if (entity.getEntityAttribute(attribute).getModifier(modifier.getID()) == null){
			entity.getEntityAttribute(attribute).applyModifier(modifier);
			return true;
		}
		else return false;
	}
	
	public static boolean removeModifier(EntityLivingBase entity, IAttribute attribute, AttributeModifier modifier){
		if (entity.getEntityAttribute(attribute).getModifier(modifier.getID()) != null){
			entity.getEntityAttribute(attribute).removeModifier(modifier);
			return true;
		}
		else return false;
	}
	
	private EntityAttributes(){}
}
