package chylex.hee.system.abstractions.damage;
import java.util.Arrays;
import java.util.List;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import net.minecraft.world.EnumDifficulty;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C06SetPlayerVelocity;
import chylex.hee.system.abstractions.Vec;
import chylex.hee.system.util.MathUtil;
import chylex.hee.system.util.RandUtil;

@FunctionalInterface
public interface IDamageModifier{
	public static final IDamageModifier
		peacefulExclusion = (amount, target, source, postProcessors) -> target.worldObj.difficultySetting == EnumDifficulty.PEACEFUL ? 0F : amount,
		
		difficultyScaling = (amount, target, source, postProcessors) -> {
			switch(target.worldObj.difficultySetting){
				case PEACEFUL: return amount*0.4F;
				case EASY: return amount*0.7F;
				default: return amount;
				case HARD: return amount*1.4F;
			}
		},
		
		armorProtection = (amount, target, source, postProcessors) -> {
			int armor = target instanceof EntityLivingBase ? ((EntityLivingBase)target).getTotalArmorValue() : 0;
			
			if (armor > 0){
				postProcessors.add(finalAmount -> {
					if (target instanceof EntityPlayer){
						ItemStack[] armorIS = Arrays.stream(((EntityPlayer)target).inventory.armorInventory).filter(is -> is != null && is.getItem() instanceof ItemArmor).toArray(ItemStack[]::new);
						
						if (armorIS.length > 0){
							int damage = MathUtil.ceil(finalAmount*0.33F);
							RandUtil.anyOf(target.worldObj.rand,armorIS).damageItem(damage,(EntityPlayer)target);
							RandUtil.anyOf(target.worldObj.rand,armorIS).damageItem(damage,(EntityPlayer)target);
						}
					}
				});
			}
			
			return amount*((26F-Math.min(20,armor))/26F);
		},
		
		magicDamage = (amount, target, source, postProcessors) -> {
			source.setMagicDamage();
			source.damageType = "magic";
			return amount;
		},
		
		enchantmentProtection = (amount, target, source, postProcessors) -> {
			if (target instanceof EntityPlayer){
				int maxValue = 0;
				
				for(int attempt = 0; attempt < 3; attempt++){
					maxValue = Math.max(maxValue,EnchantmentHelper.getEnchantmentModifierDamage(((EntityPlayer)target).inventory.armorInventory,source));
				}
				
				if (maxValue > 0){
					amount *= 1F-((float)Math.pow(Math.min(25,maxValue),1.1D)/44F);
					
					ItemStack[] enchArmor = Arrays.stream(((EntityPlayer)target).inventory.armorInventory).filter(is -> {
						return is != null && EnchantmentHelper.getEnchantments(is).keySet().stream().anyMatch(enchID -> Enchantment.enchantmentsList[(int)enchID] instanceof EnchantmentProtection);
					}).toArray(ItemStack[]::new);
					
					if (enchArmor.length > 0){
						RandUtil.anyOf(target.worldObj.rand,enchArmor).damageItem(1,(EntityPlayer)target);
					}
				}
			}
			
			return amount;
		},
		
		potionProtection = (amount, target, source, postProcessors) -> {
			if (target instanceof EntityLivingBase && ((EntityLivingBase)target).isPotionActive(Potion.resistance)){
				amount *= 1F-0.15F*Math.min(((EntityLivingBase)target).getActivePotionEffect(Potion.resistance).getAmplifier()+1,5);
			}
			
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
				default: return amount;
				case 3: return amount*1.3F;
				case 2: return amount*1.7F;
				case 1: return amount*2.3F;
				case 0: return amount*3.2F;
			}
		},
		
		blocking = (amount, target, source, postProcessors) -> {
			return amount; // TODO
		},
		
		thorns = (amount, target, source, postProcessors) -> {
			return amount; // TODO
		};
	
	public static IDamageModifier rapidDamage(final int decreaseBy){
		return (amount, target, source, postProcessors) -> {
			if (target instanceof EntityLivingBase){
				postProcessors.add(finalAmount -> {
					target.hurtResistantTime = ((EntityLivingBase)target).maxHurtResistantTime-decreaseBy;
				});
			}
			
			return amount;
		};
	}
	
	public static IDamageModifier overrideKnockback(final float multiplier){
		return (amount, target, source, postProcessors) -> {
			Entity sourceEntity = source.getEntity();
			
			if (sourceEntity != null && target instanceof EntityLivingBase){
				final EntityLivingBase livingTarget = (EntityLivingBase)target;
				final double motX = livingTarget.motionX, motY = livingTarget.motionY, motZ = livingTarget.motionZ;
				
				postProcessors.add(finalAmount -> {
					Vec vec = Vec.xz(target.posX-sourceEntity.posX,target.posZ-sourceEntity.posZ).normalized();
					
					target.motionX = motX+vec.x*0.5D*multiplier;
					target.motionY = motY+(MathUtil.floatEquals(multiplier,0F) ? 0F : multiplier < 1F ? 0.25D+0.15D*multiplier : 0.4D+0.1D*multiplier);
					target.motionZ = motZ+vec.z*0.5D*multiplier;
					if (target instanceof EntityPlayer)PacketPipeline.sendToPlayer((EntityPlayer)target,new C06SetPlayerVelocity(target.motionX,target.motionY,target.motionZ));
					
					sourceEntity.motionX *= 0.6D;
					sourceEntity.motionZ *= 0.6D;
				});
			}
			
			return amount;
		};
	}
	
	float modify(float amount, Entity target, DamageSource source, List<IDamagePostProcessor> postProcessors);
}
