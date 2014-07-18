package chylex.hee.entity.mob;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import chylex.hee.mechanics.minions.MinionData;
import chylex.hee.mechanics.minions.handlers.AbstractAbilityHandler;
import chylex.hee.mechanics.minions.properties.MinionAttributes;
import chylex.hee.system.util.MathUtil;

public class EntityMobMinion extends EntityFlying/* implements IEntityOwnable*/{
	private String owner;
	private MinionData minionData;
	
	private double targetX,targetY,targetZ;
	private byte targetChangeTimer = 0;
	private AbstractAbilityHandler currentMovementOverrider = null;
	
	private byte regenTimer = 0;
	private float armorValue = 0;
	private byte armorRegenTimer = 0;
	
	private float loadedHealth;
	
	public EntityMobMinion(World world){
		super(world);
		setSize(0.5F,0.5F);
		scoreValue = experienceValue = 0;
		noClip = true;
	}

	@Override
	protected void applyEntityAttributes(){
		super.applyEntityAttributes();
		setupAttributes();
	}
	
	private void setupAttributes(){
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20D+10D*minionData.getAttributeLevel(MinionAttributes.HEALTH));
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.6D+0.2D*minionData.getAttributeLevel(MinionAttributes.SPEED));
	}
	
	@Override
	protected void entityInit(){
		super.entityInit();
		minionData = new MinionData(this);
		dataWatcher.addObject(16,0F);
	}
	
	private Entity getOwner(){
		return null;
	}
	
	@Override
	public void onLivingUpdate(){
		super.onLivingUpdate();
		
		setDead();
		/*if (OverlayManager.minion == null && Minecraft.getMinecraft().thePlayer != null){
			if (getDistanceToEntity(Minecraft.getMinecraft().thePlayer) < 20D)OverlayManager.minion = this;
		}
		else if (OverlayManager.minion.getDistanceToEntity(Minecraft.getMinecraft().thePlayer) > 100D)OverlayManager.minion = null;*/ // TODO tweak? ahem
		
		/*
		 * MOVEMENT
		 */
		
		Entity owner = getOwner();
		if (owner == null || !(owner instanceof EntityPlayerMP)){
			if (!worldObj.isRemote)setDead(); // TODO
			return;
		}
		
		double dist = MathUtil.distance(targetX-posX,targetY-posY,targetZ-posZ);
		//System.out.println("dist "+dist+" // "+MathUtil.distance(owner.posX-posX,owner.posY-posY,owner.posZ-posZ));
		
		/*if (dist > 60D || !((EntityPlayerMP)owner).playerNetServerHandler.func_147362_b().isChannelOpen()){
			setDead();
		}*/
		
		if (currentMovementOverrider == null && (--targetChangeTimer < 0 || MathUtil.distance(owner.posX-posX,owner.posY-posY,owner.posZ-posZ) > 10D)){
			targetX = owner.posX+(rand.nextDouble()-rand.nextDouble())*1.5D;
			targetY = owner.posY+owner.height+0.2D+rand.nextDouble()*0.5D;
			targetZ = owner.posZ+(rand.nextDouble()-rand.nextDouble())*1.5D;
			targetChangeTimer = (byte)(5+rand.nextInt(15));
			System.out.println("changing target "+(currentMovementOverrider == null?"null":currentMovementOverrider));
		}
		else getLookHelper().setLookPosition(targetX,targetY,targetZ,30F,30F);
		
		if (dist > 0.5D){
			double spd = getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue();
			Vec3 vec = Vec3.createVectorHelper(targetX-posX,targetY-posY,targetZ-posZ).normalize();
			double sqrtDist = Math.sqrt(dist);
			motionX = vec.xCoord*0.1D*spd*sqrtDist;
			motionY = vec.yCoord*0.1D*spd*sqrtDist;
			motionZ = vec.zCoord*0.1D*spd*sqrtDist;
		}
		
		/*
		 * STATS
		 */
		
		if (getHealth() < getMaxHealth() && ++regenTimer > 60-15*minionData.getAttributeLevel(MinionAttributes.REGENERATION)){
			regenTimer = 0;
			setHealth(getHealth()+1F);
		}
		
		int armorLevel = minionData.getAttributeLevel(MinionAttributes.ARMOR);
		float armor = getArmor();
		if (armorLevel > 0 && armor < 4+armorLevel*2 && ++armorRegenTimer > 30-armorLevel*4){
			armorRegenTimer = 0;
			setArmor(armor+1F);
		}
		
		/*
		 * ABILITIES
		 */
		
		minionData.updateAbilities();
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount){
		if (source == DamageSource.inWall)return false;
		
		float armor = getArmor();
		if (armor > 0){
			float lower = Math.min(armor,amount);
			setArmor(armor-lower);
			armorRegenTimer = -30;
			if ((amount -= lower) <= 0)return true;
		}
		
		return super.attackEntityFrom(source,amount);
	}
	
	@Override
	protected void onDeathUpdate(){
		super.onDeathUpdate();
		if (deathTime == 5)minionData.updateAbilitiesOnDeath();
	}
	
	public MinionData getMinionData(){
		return minionData;
	}
	
	public void setArmor(float armor){
		dataWatcher.updateObject(16,armor);
	}
	
	public float getArmor(){
		return dataWatcher.getWatchableObjectFloat(16);
	}
	
	public boolean lockTargetForAbility(AbstractAbilityHandler handler, double x, double y, double z){
		if (currentMovementOverrider == null || currentMovementOverrider == handler){
			targetX = x;
			targetY = y;
			targetZ = z;
			currentMovementOverrider = handler;
			return true;
		}
		else return false;
	}
	
	public boolean isTargetLocked(){
		return currentMovementOverrider != null;
	}
	
	public boolean isTargetLockedBy(AbstractAbilityHandler abilityHandler){
		return currentMovementOverrider == abilityHandler;
	}
	
	public void unlockTarget(AbstractAbilityHandler handler){
		unlockTarget(handler,false);
	}
	
	public void unlockTarget(AbstractAbilityHandler handler, boolean force){
		if (handler == currentMovementOverrider || force)currentMovementOverrider = null;
	}
	
	public double getDistanceFromTarget(){
		return MathUtil.distance(targetX-posX,targetY-posY,targetZ-posZ);
	}
	
	public void addExperience(int experience){
		experienceValue += experience;
	}

	public void setExperience(int experience){
		experienceValue = experience;
	}
	
	public int getExperience(){
		return experienceValue;
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt){
		super.writeEntityToNBT(nbt);
		nbt.setString("owner",owner);
		nbt.setByte("regenTim",regenTimer);
		nbt.setFloat("armor",getArmor());
		nbt.setByte("armorRegenTim",armorRegenTimer);
		
		NBTTagCompound dataTag = new NBTTagCompound();
		minionData.writeToNBT(dataTag);
		nbt.setTag("minionData",dataTag);
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		owner = nbt.getString("owner");
		regenTimer = nbt.getByte("regenTim");
		setArmor(nbt.getFloat("armor"));
		armorRegenTimer = nbt.getByte("armorRegenTim");
		
		NBTTagCompound dataTag = nbt.getCompoundTag("minionData");
		minionData.readFromNBT(dataTag);
		
		setupAttributes();
		if (nbt.hasKey("HealF"))setHealth(nbt.getFloat("HealF"));
	}
	
	public void loadEntityFromItem(ItemStack is){
		minionData.readFromNBT(is.stackTagCompound == null?new NBTTagCompound():is.stackTagCompound.getCompoundTag("minion"));
		setupAttributes();
	}
	
	/*public void setOwner(EntityPlayer player){
		this.owner = player.getCommandSenderName();
	}

	@Override
	public String getOwnerName(){
		return owner;
	}

	@Override
	public Entity getOwner(){
		return owner == null?null:worldObj.getPlayerEntityByName(owner);
	}*/
	
	@Override
	protected void despawnEntity(){}
}
