package chylex.hee.entity.projectile;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import chylex.hee.entity.technical.EntityTechnicalCurseBlock;
import chylex.hee.entity.technical.EntityTechnicalCurseEntity;
import chylex.hee.mechanics.curse.CurseType;

public class EntityProjectileCurse extends EntityThrowable{
	private UUID throwerID;
	private CurseType curseType;
	private boolean eternal;
	
	public EntityProjectileCurse(World world){
		super(world);
	}

	public EntityProjectileCurse(World world, EntityPlayer thrower, CurseType type, boolean eternal){
		super(world,thrower);
		this.throwerID = thrower.getPersistentID();
		this.curseType = type;
		this.eternal = eternal;
	}
	
	public CurseType getType(){
		return curseType;
	}
	
	@Override
	protected void onImpact(MovingObjectPosition mop){
		if (!worldObj.isRemote){
			if (mop.typeOfHit == MovingObjectType.ENTITY){
				for(EntityLivingBase entity:(List<EntityLivingBase>)worldObj.getEntitiesWithinAABB(EntityLivingBase.class,boundingBox.expand(4D,2D,4D))){
					if (getDistanceSqToEntity(entity) < 16D && !entity.getPersistentID().equals(throwerID) && !(entity instanceof IBossDisplayData))worldObj.spawnEntityInWorld(new EntityTechnicalCurseEntity(worldObj,entity,curseType,eternal));
				}
			}
			else if (mop.typeOfHit == MovingObjectType.BLOCK){
				int yy = worldObj.getBlock(mop.blockX,mop.blockY,mop.blockZ).isReplaceable(worldObj,mop.blockX,mop.blockY,mop.blockZ) ? mop.blockY-1 : mop.blockY;
				worldObj.spawnEntityInWorld(new EntityTechnicalCurseBlock(worldObj,mop.blockX,yy,mop.blockZ,throwerID,curseType,eternal));
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
		nbt.setLong("thr1",throwerID.getLeastSignificantBits());
		nbt.setLong("thr2",throwerID.getMostSignificantBits());
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		if ((curseType = CurseType.getFromDamage(nbt.getByte("curse"))) == null)setDead();
		eternal = nbt.getBoolean("eternal");
		throwerID = new UUID(nbt.getLong("thr2"),nbt.getLong("thr1"));
	}
}
