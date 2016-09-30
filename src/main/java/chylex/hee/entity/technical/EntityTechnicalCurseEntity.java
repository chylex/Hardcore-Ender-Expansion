package chylex.hee.entity.technical;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.mechanics.curse.CurseEvents;
import chylex.hee.mechanics.curse.CurseType;
import chylex.hee.mechanics.curse.CurseType.EnumCurseUse;
import chylex.hee.mechanics.curse.ICurseCaller;
import chylex.hee.system.abstractions.entity.EntityDataWatcher;
import chylex.hee.system.util.MathUtil;

public class EntityTechnicalCurseEntity extends EntityTechnicalBase implements ICurseCaller{
	private enum Data{ CURSE_TYPE, TARGET_WIDTH, TARGET_HEIGHT }
	
	private EntityDataWatcher entityData;
	private CurseType curseType;
	private boolean eternal;
	private byte usesLeft;
	private UUID targetID;
	private EntityLivingBase target;
	private float targetWidth, targetHeight;
	
	public EntityTechnicalCurseEntity(World world){
		super(world);
	}
	
	public EntityTechnicalCurseEntity(World world, EntityLivingBase target, CurseType type, boolean eternal){
		super(world);
		setPosition(target.posX, target.posY, target.posZ);
		this.curseType = type;
		this.eternal = eternal;
		this.usesLeft = (byte)(target instanceof EntityPlayer ? ((eternal ? 2 : 1)*type.getUses(EnumCurseUse.PLAYER, rand)) : (eternal ? -1 : type.getUses(EnumCurseUse.ENTITY, rand)));
		this.targetID = target.getUniqueID();
		this.target = target;
	}

	@Override
	protected void entityInit(){
		entityData = new EntityDataWatcher(this);
		entityData.addByte(Data.CURSE_TYPE);
		entityData.addFloat(Data.TARGET_WIDTH);
		entityData.addFloat(Data.TARGET_HEIGHT);
	}
	
	@Override
	public void onUpdate(){
		if (worldObj.isRemote){
			if (curseType == null){
				curseType = CurseType.getFromDamage(entityData.getByte(Data.CURSE_TYPE)-1);
				
				if (curseType != null){
					targetWidth = entityData.getFloat(Data.TARGET_WIDTH);
					targetHeight = entityData.getFloat(Data.TARGET_HEIGHT);
				}
			}
			
			if (curseType != null){
				double dist = HardcoreEnderExpansion.proxy.getClientSidePlayer().getDistanceToEntity(this);
				if (dist > 32D)return;
				
				if (rand.nextInt(dist > 16D ? 3 : 2) == 0)HardcoreEnderExpansion.fx.curse(posX+(rand.nextDouble()-0.5D)*targetWidth*1.5D, posY+rand.nextDouble()*targetHeight, posZ+(rand.nextDouble()-0.5D)*targetWidth*1.5D, curseType);
			}
			
			return;
		}
		else if (ticksExisted == 1)entityData.setByte(Data.CURSE_TYPE, curseType.damage+1);
		
		if (target != null){
			if (MathUtil.floatEquals(targetWidth, 0F)){
				entityData.setFloat(Data.TARGET_WIDTH, targetWidth = target.width);
				entityData.setFloat(Data.TARGET_HEIGHT, targetHeight = target.height);
			}
			
			if (target.dimension != dimension){
				setPosition(target.posX, target.posY, target.posZ);
				travelToDimension(target.dimension);
				return;
			}
			else if (target.isDead)setDead();
			
			setPosition(target.posX, target.posY, target.posZ);
			
			if (curseType.handler.tickEntity(target, this) && usesLeft != -1 && (--usesLeft <= 0 || (CurseEvents.hasAmulet(target) && --usesLeft <= 0))){
				curseType.handler.end(target, this);
				setDead();
			}
		}
		else if (ticksExisted < 20){
			for(Entity entity:(List<Entity>)worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox)){
				if (entity.getUniqueID().equals(targetID) && entity instanceof EntityLivingBase){
					target = (EntityLivingBase)entity;
					break;
				}
			}
		}
		else setDead();
	}
	
	public boolean compareTarget(EntityLivingBase entity){
		return entity == target;
	}
	
	@Override
	public void onPurify(){
		if (curseType != null && target != null)curseType.handler.end(target, this);
	}
	
	@Override
	public Entity getEntity(){
		return this;
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt){
		nbt.setByte("curse", curseType.damage);
		nbt.setBoolean("eternal", eternal);
		nbt.setByte("usesLeft", usesLeft);
		nbt.setLong("targ1", targetID.getLeastSignificantBits());
		nbt.setLong("targ2", targetID.getMostSignificantBits());
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt){
		if ((curseType = CurseType.getFromDamage(nbt.getByte("curse"))) == null)setDead();
		eternal = nbt.getBoolean("eternal");
		usesLeft = nbt.getByte("usesLeft");
		targetID = new UUID(nbt.getLong("targ2"), nbt.getLong("targ1"));
	}
}
