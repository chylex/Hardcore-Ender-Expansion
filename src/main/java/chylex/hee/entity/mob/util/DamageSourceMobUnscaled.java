package chylex.hee.entity.mob.util;
import net.minecraft.entity.Entity;
import net.minecraft.util.EntityDamageSource;

public class DamageSourceMobUnscaled extends EntityDamageSource{
	public DamageSourceMobUnscaled(Entity entity){
		super("mob",entity);
	}

	@Override
	public boolean isDifficultyScaled(){
		return false;
	}
	
	public static float getScaledDamage(float damage, int difficulty){
		return difficulty == 0?damage/2F+1F:
			   difficulty == 2?damage*3F/2F:
			   difficulty == 3?damage*4F/2F:damage;
	}
}