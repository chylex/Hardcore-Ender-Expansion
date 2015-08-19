package chylex.hee.entity.technical;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public abstract class EntityTechnicalBase extends Entity{
	public EntityTechnicalBase(World world){
		super(world);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount){
		return false;
	}
	
	@Override
	public boolean isBurning(){
		return false;
	}
	
	@Override
	public boolean canAttackWithItem(){
		return false;
	}
	
	@Override
	public void setInPortal(){}
}
