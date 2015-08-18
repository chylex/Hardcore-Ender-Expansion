package chylex.hee.entity.mob.util;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import org.apache.commons.lang3.tuple.Pair;

@Deprecated
public class MultiDamage{
	@Deprecated
	public static MultiDamage from(EntityLivingBase source){
		return new MultiDamage(source);
	}

	@Deprecated
	public static MultiDamage generic(){
		return new MultiDamage(null);
	}
	
	private final EntityLivingBase source;
	private final List<Pair<DamageSource,Float>> damageList = new ArrayList<>();

	@Deprecated
	private MultiDamage(EntityLivingBase source){
		this.source = source;
	}
	
	/**
	 * Adds mob damage that is scaled by difficulty using vanilla settings.
	 */
	public MultiDamage addScaled(float damage){
		damageList.add(Pair.of(source == null ? DamageSource.generic : DamageSource.causeMobDamage(source),damage));
		return this;
	}
	
	/**
	 * Adds mob damage that is scaled by difficulty, but not ignored on Peaceful.
	 */
	public MultiDamage addUnscaled(float damage){
		damageList.add(Pair.of(source == null ? DamageSource.generic : new DamageSourceMobUnscaled(source),damage));
		return this;
	}
	
	/**
	 * Adds magic damage that bypasses armor, but converts it into mob damage.
	 */
	public MultiDamage addMagic(float damage){
		damageList.add(Pair.of(source == null ? DamageSource.magic : new EntityDamageSource("mob",source).setDamageBypassesArmor().setMagicDamage(),damage));
		return this;
	}
	
	public boolean attack(Entity target){
		for(int a = 0; a < damageList.size(); a++){
			Pair<DamageSource,Float> pair = damageList.get(a);
			if (!target.attackEntityFrom(pair.getLeft(),pair.getRight().floatValue()))return false;
			
			if (a < damageList.size()-1)target.hurtResistantTime = 0;
		}
		
		return true;
	}
}
