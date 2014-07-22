package chylex.hee.world.util;
import java.lang.reflect.InvocationTargetException;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;
import chylex.hee.system.weight.IWeightProvider;

public class SpawnEntry implements IWeightProvider{
	private Class<? extends EntityLiving> mobClass;
	private byte maxAmount;
	private short weight;
	
	public SpawnEntry(Class<? extends EntityLiving> mobClass, int maxAmount, int weight){
		this.mobClass = mobClass;
		this.maxAmount = (byte)maxAmount;
		this.weight = (short)weight;
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
	public short getWeight(){
		return weight;
	}
}
