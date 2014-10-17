package chylex.hee.mechanics.energy;
import gnu.trove.map.hash.TObjectFloatHashMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;

final class MobEnergy{
	private static final TObjectFloatHashMap<Class<? extends EntityLivingBase>> energy = new TObjectFloatHashMap<>();
	
	static{
		energy.put(EntityEnderman.class, 0.1F);
	}
	
	public static float getEnergy(EntityLivingBase entity){
		float amt = energy.get(entity.getClass());
		return amt == energy.getNoEntryValue() ? -1F : amt;
	}
}
