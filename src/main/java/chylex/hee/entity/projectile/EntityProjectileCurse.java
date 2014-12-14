package chylex.hee.entity.projectile;
import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import chylex.hee.entity.technical.EntityTechnicalCurseEntity;
import chylex.hee.mechanics.curse.CurseType;

public class EntityProjectileCurse extends EntityPotion{
	private CurseType curseType;
	private boolean eternal;
	
	public EntityProjectileCurse(World world){
		super(world);
	}

	public EntityProjectileCurse(World world, EntityLivingBase thrower, ItemStack curse, CurseType type, boolean eternal){
		super(world,thrower,curse);
		this.curseType = type;
		this.eternal = eternal;
	}
	
	@Override
	protected void onImpact(MovingObjectPosition mop){
		if (!worldObj.isRemote){
			if (mop.typeOfHit == MovingObjectType.ENTITY){
				for(EntityLivingBase entity:(List<EntityLivingBase>)worldObj.getEntitiesWithinAABB(EntityLivingBase.class,boundingBox.expand(4D,2D,4D))){
					if (getDistanceSqToEntity(entity) < 16D)worldObj.spawnEntityInWorld(new EntityTechnicalCurseEntity(worldObj,entity,curseType,eternal));
				}
			}
			else if (mop.typeOfHit == MovingObjectType.BLOCK){
				// TODO
			}

			// TODO
			setDead();
		}
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt){
		super.writeEntityToNBT(nbt);
		nbt.setByte("curse",curseType.damage);
		nbt.setBoolean("eternal",eternal);
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		if ((curseType = CurseType.getFromDamage(nbt.getByte("curse"))) == null)setDead();
		eternal = nbt.getBoolean("eternal");
	}
}
