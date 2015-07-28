package chylex.hee.world.loot.info;
import net.minecraft.entity.EntityLiving;

public class LootMobInfo<T extends EntityLiving>{
	public final T entity;
	public final boolean recentlyHit;
	public final int looting;
	
	public LootMobInfo(T entity, boolean recentlyHit, int looting){
		this.entity = entity;
		this.recentlyHit = recentlyHit;
		this.looting = looting;
	}
}
