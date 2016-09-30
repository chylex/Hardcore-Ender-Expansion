package chylex.hee.system.abstractions.damage.special;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import chylex.hee.system.abstractions.damage.Damage;
import chylex.hee.system.abstractions.damage.IDamage;
import chylex.hee.system.abstractions.damage.IDamagePostProcessor;
import chylex.hee.system.util.MathUtil;

public class ForcedDamage implements IDamage{
	public static ForcedDamage from(Damage damage){
		return new ForcedDamage(damage);
	}
	
	private final Damage damage;
	
	private ForcedDamage(Damage damage){
		this.damage = damage;
	}
	
	@Override
	public boolean deal(Entity target){
		List<IDamagePostProcessor> postProcessors = new ArrayList<>();
		float amount = damage.calculateAmount(target, DamageSource.magic, postProcessors);
		
		if (!MathUtil.floatEquals(amount, 0F) && target instanceof EntityLivingBase){
			EntityLivingBase living = (EntityLivingBase)target;
			
			if (target instanceof EntityPlayer){
				EntityPlayer player = (EntityPlayer)target;
				if (player.capabilities.isCreativeMode && player.getHealth() <= amount)return false;
				player.addStat(StatList.damageTakenStat, Math.round(amount*10F));
			}
			
			living.prevHealth = living.getHealth();
			living.setHealth(living.getHealth()-amount);
			living.func_110142_aN().func_94547_a(DamageSource.magic, living.prevHealth, amount); // OBFUSCATED combat tracker, second method
			
			if (living.getHealth() <= 0F)living.onDeath(DamageSource.magic);
			
			for(IDamagePostProcessor postProcessor:postProcessors)postProcessor.run(amount);
			return true;
		}
		
		return false;
	}
}
