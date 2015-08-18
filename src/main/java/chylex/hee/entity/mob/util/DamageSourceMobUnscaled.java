package chylex.hee.entity.mob.util;
import net.minecraft.entity.Entity;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.world.EnumDifficulty;

@Deprecated
public class DamageSourceMobUnscaled extends EntityDamageSource{
	@Deprecated
	public DamageSourceMobUnscaled(Entity entity){
		super("mob",entity);
	}

	@Override
	public boolean isDifficultyScaled(){
		return false;
	}
	
	public static float getDamage(float damage, EnumDifficulty difficulty){
		return difficulty == EnumDifficulty.PEACEFUL ? damage/2F+1F :
			   difficulty == EnumDifficulty.NORMAL ? damage*3F/2F :
			   difficulty == EnumDifficulty.HARD ? damage*2F : damage;
	}
}