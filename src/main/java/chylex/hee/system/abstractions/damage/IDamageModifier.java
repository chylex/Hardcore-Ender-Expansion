package chylex.hee.system.abstractions.damage;
import java.util.Arrays;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.util.DamageSource;
import net.minecraft.world.EnumDifficulty;

@FunctionalInterface
public interface IDamageModifier{
	public static final IDamageModifier
		peacefulExclusion = (amount, target, source, postProcessors) -> target.worldObj.difficultySetting == EnumDifficulty.PEACEFUL ? 0F : amount,
		
		armorProtection = (amount, target, source, postProcessors) -> {
			int armor = target instanceof EntityLivingBase ? ((EntityLivingBase)target).getTotalArmorValue() : 0;
			// TODO
			return amount;
		},
		
		nudityDanger = (amount, target, source, postProcessors) -> {
			int count = 4;
			
			if (target instanceof EntityPlayer){
				count = (int)Arrays.stream(((EntityPlayer)target).inventory.armorInventory).filter(is -> is != null && is.getItem() instanceof ItemArmor).count();
			}
			else if (target instanceof EntityLiving){
				count = (int)Arrays.stream(((EntityLiving)target).getLastActiveItems()).filter(is -> is != null && is.getItem() instanceof ItemArmor).count();
			}
			
			switch(count){
				case 3: return amount*1.3F;
				case 2: return amount*1.7F;
				case 1: return amount*2.3F;
				case 0: return amount*3.2F;
				default: return amount;
			}
		},
		
		rapidDamage = (amount, target, source, postProcessors) -> {
			target.hurtResistantTime = 5;
			return amount;
		};
	
	float modify(float amount, Entity target, DamageSource source, List<IDamagePostProcessor> postProcessors);
}
