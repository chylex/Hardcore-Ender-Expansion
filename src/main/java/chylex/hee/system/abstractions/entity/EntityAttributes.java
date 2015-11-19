package chylex.hee.system.abstractions.entity;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
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
	
	public static AttributeModifierBuilder createModifier(String name){
		return new AttributeModifierBuilder(name);
	}
	
	public static AttributeModifier createModifier(String name, Operation operation, double value){
		return new AttributeModifierBuilder(name).setValue(operation,value).build();
	}
	
	public static class AttributeModifierBuilder{
		private final String name;
		private Operation operation;
		private double value;
		private boolean isSaved; // false by default
		
		private AttributeModifierBuilder(String name){
			this.name = "HEE2 - "+name;
		}
		
		public AttributeModifierBuilder setValue(Operation operation, double value){
			this.operation = operation;
			this.value = value;
			if (this.operation == Operation.MULTIPLY)value -= 1D;
			return this;
		}
		
		public AttributeModifierBuilder setSaved(){
			this.isSaved = true;
			return this;
		}
		
		public AttributeModifier build(){
			AttributeModifier modifier = new AttributeModifier(UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.US_ASCII)),name,value,operation.ordinal());
			modifier.setSaved(isSaved);
			return modifier;
		}
	}
	
	public enum Operation{
		/**
		 * Operation 0, adds a value to the base.<br>
		 * {@code result = base + value}
		 */
		ADD,
		
		/**
		 * Operation 1, adds a multiplies the result of operation 0 by a value and adds it to the base.<br>
		 * {@code result = result + (prevResult * value)}
		 */
		ADD_MULTIPLIED,
		
		/**
		 * Operation 2, multiplies the result of operation 1 by a value.
		 * In vanilla, the value is incremented by one, the AttributeModifierBuilder adjusts the provided value to
		 * be taken as actual multiplication.<br>
		 * {@code result = result * value}
		 */
		MULTIPLY
	}
	
	private EntityAttributes(){}
}
