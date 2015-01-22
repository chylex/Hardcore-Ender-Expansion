package chylex.hee.entity.projectile;
import chylex.hee.system.util.BlockPosM;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityProjectileExpBottleConsistent extends EntityExpBottle{
	public EntityProjectileExpBottleConsistent(World world){
		super(world);
	}
	
	public EntityProjectileExpBottleConsistent(World world, EntityLivingBase thrower){
		super(world,thrower);
	}
	
	@Override
	protected void onImpact(MovingObjectPosition mop){
		if (!worldObj.isRemote){
			worldObj.playAuxSFX(2002,new BlockPosM(this),0);
			worldObj.spawnEntityInWorld(new EntityXPOrb(worldObj,posX,posY,posZ,5));
			setDead();
		}
	}
}
