package chylex.hee.world.loot.info;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;

public class LootMobInfo<T extends EntityLiving>{
	public final T entity;
	public final boolean recentlyHit;
	public final int looting;
	private EntityLivingBase attacker;
	
	public LootMobInfo(T entity, boolean recentlyHit, int looting){
		this.entity = entity;
		this.recentlyHit = recentlyHit;
		this.looting = looting;
	}
	
	public @Nullable EntityLivingBase getAttacker(){
		if (attacker == null)attacker = entity.func_94060_bK(); // OBFUSCATED getLastAttacker
		return attacker;
	}
}
