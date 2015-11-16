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
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.entity.technical.EntityTechnicalCurseBlock;
import chylex.hee.entity.technical.EntityTechnicalCurseEntity;
import chylex.hee.mechanics.curse.CurseType;
import chylex.hee.system.abstractions.entity.EntityDataWatcher;
import chylex.hee.system.util.BlockPosM;

public class EntityProjectileCurse extends EntityThrowable{
	private enum Data{ CURSE_TYPE }
	
	private EntityDataWatcher entityData;
	private UUID throwerID;
	private CurseType curseType;
	private boolean eternal;
	
	public EntityProjectileCurse(World world){
		super(world);
	}

	public EntityProjectileCurse(World world, EntityPlayer thrower, CurseType type, boolean eternal){
		super(world,thrower);
		this.throwerID = thrower.getUniqueID();
		this.curseType = type;
		this.eternal = eternal;
	}
	
	public CurseType getType(){
		return curseType == null ? (curseType = CurseType.getFromDamage(entityData.getByte(Data.CURSE_TYPE)-1)) : curseType;
	}
	
	@Override
	protected void entityInit(){
		super.entityInit();
		entityData = new EntityDataWatcher(this);
		entityData.addByte(Data.CURSE_TYPE);
	}
	
	@Override
	public void onUpdate(){
		super.onUpdate();
		
		if (worldObj.isRemote){
			getType();
			
			if (curseType != null){
				for(int a = 0; a < 1+rand.nextInt(2); a++)HardcoreEnderExpansion.fx.curse(posX+(rand.nextDouble()-0.5D)*0.15D,posY+(rand.nextDouble()-0.5D)*0.15D,posZ+(rand.nextDouble()-0.5D)*0.15D,curseType);
			}
		}
		else if (ticksExisted == 1)entityData.setByte(Data.CURSE_TYPE,curseType.damage+1);
	}
	
	@Override
	protected void onImpact(MovingObjectPosition mop){
		if (!worldObj.isRemote){
			if (mop.typeOfHit == MovingObjectType.ENTITY){
				for(EntityLivingBase entity:(List<EntityLivingBase>)worldObj.getEntitiesWithinAABB(EntityLivingBase.class,boundingBox.expand(4D,2D,4D))){
					if (getDistanceSqToEntity(entity) < 16D && !entity.getUniqueID().equals(throwerID) && !(entity instanceof IBossDisplayData))worldObj.spawnEntityInWorld(new EntityTechnicalCurseEntity(worldObj,entity,curseType,eternal));
				}
			}
			else if (mop.typeOfHit == MovingObjectType.BLOCK){
				int yy = BlockPosM.tmp(mop.blockX,mop.blockY,mop.blockZ).getBlock(worldObj).isReplaceable(worldObj,mop.blockX,mop.blockY,mop.blockZ) ? mop.blockY-1 : mop.blockY;
				worldObj.spawnEntityInWorld(new EntityTechnicalCurseBlock(worldObj,mop.blockX,yy,mop.blockZ,throwerID,curseType,eternal));
			}

			setDead();
		}
		else if (curseType != null){
			worldObj.playSound(posX,posY,posZ,"hardcoreenderexpansion:mob.random.curse",0.8F,0.9F+rand.nextFloat()*0.2F,false);
			for(int a = 0; a < 40; a++)HardcoreEnderExpansion.fx.curse(posX+(rand.nextDouble()-0.5D)*1.5D,posY+(rand.nextDouble()-0.5D)*1.5D,posZ+(rand.nextDouble()-0.5D)*1.5D,curseType);
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
