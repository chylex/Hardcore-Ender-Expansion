package chylex.hee.entity.mob;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.knowledge.KnowledgeRegistrations;
import chylex.hee.mechanics.knowledge.util.ObservationUtil;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C16ParticleMirageHurt;
import chylex.hee.system.ReflectionPublicizer;
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityMobCorporealMirage extends EntityLiving implements IEntityOwnable{
	@SideOnly(Side.CLIENT)
	private ResourceLocation skin;
	@SideOnly(Side.CLIENT)
	private boolean triedDownloadingSkin;
	public double angle,usedMotionY;
	private short hurtTimer;
	private byte checkTimer;
	
	public EntityMobCorporealMirage(World world){
		super(world);
		angle = rand.nextDouble()*2D*Math.PI;
	}
	
	public EntityMobCorporealMirage(World world, double x, double y, double z, String throwerUUID){
		super(world);
		setPosition(x,y,z);
		setCustomNameTag(throwerUUID);
	}
	
	@Override
	protected void applyEntityAttributes(){
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(8D);
	}
	
	@Override
	public void onLivingUpdate(){
		if (newPosRotationIncrements > 0){
			setPosition(posX,posY+(newPosY-posY)/newPosRotationIncrements,posZ);
			setRotation((float)(rotationYaw+MathHelper.wrapAngleTo180_double(newRotationYaw-rotationYaw)/newPosRotationIncrements),(float)(rotationPitch+(newRotationPitch-rotationPitch)/newPosRotationIncrements));
			rotationYawHead = renderYawOffset = rotationYaw;
			--newPosRotationIncrements;
		}
		
		posY += usedMotionY *= 0.9D;
		if (Math.abs(usedMotionY) < 0.005D)usedMotionY = 0D;
		setPosition(posX,posY,posZ);
		
		motionX = motionY = motionZ = 0D;
		
		if (worldObj.isRemote)return;
		
		if (--checkTimer < 0){
			List<?> entities = worldObj.selectEntitiesWithinAABB(EntityLiving.class,boundingBox.expand(20D,8D,20D),IMob.mobSelector);
			Collections.shuffle(entities,rand);
			String name = getCustomNameTag();
			
			for(Object o:entities){
				if (o instanceof EntityMob){
					EntityMob e = (EntityMob)o;
					if (!e.canEntityBeSeen(this))continue;
					
					if ((e.getEntityToAttack() instanceof EntityPlayer && ((EntityPlayer)e.getEntityToAttack()).getCommandSenderName().equals(name))||
						(e.getAttackTarget() instanceof EntityPlayer && ((EntityPlayer)e.getAttackTarget()).getCommandSenderName().equals(name))){
						e.setTarget(this);
						e.setAttackTarget(this);
						e.attackEntityFrom(DamageSource.causeMobDamage(this),0F);
						faceEntity(e,360F,360F);
						
						for(EntityPlayer observer:ObservationUtil.getAllObservers(this,12D))KnowledgeRegistrations.CORPOREAL_MIRAGE_ORB.tryUnlockFragment(observer,0.1F);
						break;
					}
				}
				else if (o instanceof EntityGhast){
					EntityGhast e = (EntityGhast)o;
					Entity target = (Entity)ReflectionPublicizer.get(ReflectionPublicizer.entityGhastTarget,e);
					
					if (target instanceof EntityPlayer && ((EntityPlayer)target).getCommandSenderName().equals(name)){
						ReflectionPublicizer.set(ReflectionPublicizer.entityGhastTarget,e,this);
						faceEntity(e,360F,360F);
						
						for(EntityPlayer observer:ObservationUtil.getAllObservers(this,12D))KnowledgeRegistrations.CORPOREAL_MIRAGE_ORB.tryUnlockFragment(observer,0.1F);
						break;
					}
				}
			}
			
			checkTimer = 6;
		}
		
		if (++hurtTimer > 600+rand.nextInt(800)){
			attackEntityFrom(DamageSource.magic,-1F);
			hurtTimer = 0;
		}
	}
	
	@Override
	protected void onDeathUpdate(){
		if (deathTime < 5){
			for(int a = 0; a < 8; a++)HardcoreEnderExpansion.fx.mirageDeath(this);
		}
		
		if (!worldObj.isRemote && ++deathTime == 11){
			dropItem(ItemList.ectoplasm,1);
			setDead();
		}
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount){
		if (source == DamageSource.drown || source == DamageSource.fallingBlock||
			source == DamageSource.inWall)return false;
		
		if (super.attackEntityFrom(source,MathUtil.floatEquals(amount,-1F) ? 1F : 2F)){
			PacketPipeline.sendToAllAround(this,64D,new C16ParticleMirageHurt(this));

			for(EntityPlayer observer:ObservationUtil.getAllObservers(this,12D))KnowledgeRegistrations.CORPOREAL_MIRAGE_ORB.tryUnlockFragment(observer,0.15F);
			return true;
		}
		else return false;
	}
	
	@Override
	public void knockBack(Entity entity, float damage, double xPower, double zPower){}

	@Override
	public void addVelocity(double xVelocity, double yVelocity, double zVelocity){}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt){
		super.writeEntityToNBT(nbt);
		nbt.setShort("curHurtTimer",hurtTimer);
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		hurtTimer = nbt.getShort("curHurtTimer");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void handleHealthUpdate(byte type){
		if (type == 2){
			hurtResistantTime = maxHurtResistantTime;
			hurtTime = maxHurtTime = 10;
			attackedAtYaw = 0F;
			playSound(getHurtSound(),getSoundVolume(),(rand.nextFloat()-rand.nextFloat())*0.2F+1F);
			attackEntityFrom(DamageSource.generic,0F);
		}
		else if (type == 3){
			playSound(getDeathSound(),getSoundVolume(),(rand.nextFloat()-rand.nextFloat())*0.2F+1F);
			setHealth(0F);
			onDeath(DamageSource.generic);
		}
		else super.handleHealthUpdate(type);
	}

	@SideOnly(Side.CLIENT)
	public ResourceLocation getMirageSkin(){
		if (!triedDownloadingSkin && !getCustomNameTag().isEmpty()){
			skin = AbstractClientPlayer.getLocationSkin(getCustomNameTag());
			AbstractClientPlayer.getDownloadImageSkin(skin,getCustomNameTag());
			triedDownloadingSkin = true;
		}
		return skin == null ? AbstractClientPlayer.locationStevePng : skin;
	}

	@Override
	public String func_152113_b(){ // OBFUSCATED getOwnerUUID
		return getCustomNameTag();
	}

	@Override
	public Entity getOwner(){
		try{
			UUID uuid = UUID.fromString(func_152113_b());
			return uuid == null ? null : worldObj.func_152378_a(uuid);
		}catch(IllegalArgumentException e){
			return null;
		}
	}
	
	@Override
	public void mountEntity(Entity entity){
		ridingEntity = null;
	}
	
	@Override
	public boolean allowLeashing(){
		return false;
	}
	
	@Override
	protected void despawnEntity(){}
}
