package chylex.hee.mechanics.energy;
import gnu.trove.map.hash.TObjectFloatHashMap;
import net.minecraft.entity.EntityLivingBase;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.boss.EntityMiniBossEnderEye;
import chylex.hee.entity.mob.EntityMobAngryEnderman;
import chylex.hee.entity.mob.EntityMobBabyEnderman;
import chylex.hee.entity.mob.EntityMobEndermage;
import chylex.hee.entity.mob.EntityMobEnderman;
import chylex.hee.entity.mob.EntityMobHomelandEnderman;

final class MobEnergy{
	private static final TObjectFloatHashMap<Class<? extends EntityLivingBase>> energy = new TObjectFloatHashMap<>();
	
	static{
		energy.put(EntityMobEnderman.class, 0.1F);
		energy.put(EntityMobAngryEnderman.class, 0.1F);
		energy.put(EntityMobHomelandEnderman.class, 0.1F);
		energy.put(EntityMobBabyEnderman.class, 0.05F);
		energy.put(EntityMobEndermage.class, 0.15F);
		energy.put(EntityMiniBossEnderEye.class, 0.35F);
		energy.put(EntityBossDragon.class, 2F);
	}
	
	public static float getEnergy(EntityLivingBase entity){
		float amt = energy.get(entity.getClass());
		return amt == energy.getNoEntryValue() ? -1F : amt;
	}
	
	private MobEnergy(){}
}
