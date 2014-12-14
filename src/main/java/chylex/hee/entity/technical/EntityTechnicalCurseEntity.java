package chylex.hee.entity.technical;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.mechanics.curse.CurseType;
import chylex.hee.mechanics.curse.CurseType.EnumCurseUse;
import chylex.hee.mechanics.curse.ICurseCaller;

public class EntityTechnicalCurseEntity extends EntityTechnicalBase implements ICurseCaller{
	private CurseType curseType;
	private boolean eternal;
	private byte usesLeft;
	private UUID targetID;
	private EntityLivingBase target;
	
	public EntityTechnicalCurseEntity(World world){
		super(world);
	}
	
	public EntityTechnicalCurseEntity(World world, EntityLivingBase target, CurseType type, boolean eternal){
		super(world);
		setPosition(target.posX,target.posY,target.posZ);
		this.curseType = type;
		this.eternal = eternal;
		this.usesLeft = (byte)(target instanceof EntityPlayer ? ((eternal ? 2 : 1)*type.getUses(EnumCurseUse.PLAYER,rand)) : (eternal ? -1 : type.getUses(EnumCurseUse.ENTITY,rand)));
		this.targetID = target.getPersistentID();
		this.target = target;
	}

	@Override
	protected void entityInit(){}
	
	@Override
	public void onUpdate(){
		if (worldObj.isRemote){
			
			return;
		}
		
		if (target != null){
			if (target.dimension != dimension){
				setPosition(target.posX,target.posY,target.posZ);
				travelToDimension(target.dimension);
				return;
			}
			else if (target.isDead){
				HardcoreEnderExpansion.notifications.report("Deleted curse entity"); // TODO debug
				setDead();
			}
			
			setPosition(target.posX,target.posY,target.posZ);
			if (curseType.handler.tickEntity(target,this) && (usesLeft != -1 && --usesLeft <= 0))setDead();
		}
		else if (ticksExisted < 20){
			for(Entity entity:(List<Entity>)worldObj.getEntitiesWithinAABBExcludingEntity(this,boundingBox)){
				if (entity.getPersistentID().equals(targetID) && entity instanceof EntityLivingBase){
					target = (EntityLivingBase)entity;
					break;
				}
			}
		}
		else{
			HardcoreEnderExpansion.notifications.report("Deleted curse entity"); // TODO debug
			setDead();
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt){
		nbt.setByte("curse",curseType.damage);
		nbt.setBoolean("eternal",eternal);
		nbt.setByte("usesLeft",usesLeft);
		nbt.setLong("targ1",targetID.getLeastSignificantBits());
		nbt.setLong("targ2",targetID.getMostSignificantBits());
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt){
		if ((curseType = CurseType.getFromDamage(nbt.getByte("curse"))) == null)setDead();
		eternal = nbt.getBoolean("eternal");
		usesLeft = nbt.getByte("usesLeft");
		targetID = new UUID(nbt.getLong("targ2"),nbt.getLong("targ1"));
	}
}
