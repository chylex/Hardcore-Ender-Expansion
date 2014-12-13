package chylex.hee.world.util;
import java.lang.reflect.InvocationTargetException;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.IMob;
import net.minecraft.world.World;
import chylex.hee.system.collections.weight.IWeightProvider;

public class SpawnEntry implements IWeightProvider{
	private final Class<? extends EntityLiving> mobClass;
	private final byte maxAmount;
	private final byte weight;
	public final boolean isMob;
	
	public SpawnEntry(Class<? extends EntityLiving> mobClass, int maxAmount, int weight){
		this.mobClass = mobClass;
		this.maxAmount = (byte)maxAmount;
		this.weight = (byte)weight;
		this.isMob = IMob.class.isAssignableFrom(mobClass);
	}
	
	public Class<? extends EntityLiving> getMobClass(){
		return mobClass;
	}
	
	public int getMaxAmount(){
		return maxAmount;
	}
	
	public EntityLiving createMob(World world){
		try{
			return mobClass.getConstructor(World.class).newInstance(world);
		}catch(NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e){
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public int getWeight(){
		return weight;
	}
}
