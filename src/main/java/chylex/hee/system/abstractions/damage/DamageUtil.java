package chylex.hee.system.abstractions.damage;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;

public final class DamageUtil{
	public static boolean isCriticalHit(EntityLivingBase source){
		return source != null &&
			   source.fallDistance > 0F &&
			   !source.onGround &&
			   !source.isOnLadder() &&
			   !source.isInWater() &&
			   !source.isPotionActive(Potion.blindness) &&
			   source.ridingEntity == null;
	}
	
	private DamageUtil(){}
}
