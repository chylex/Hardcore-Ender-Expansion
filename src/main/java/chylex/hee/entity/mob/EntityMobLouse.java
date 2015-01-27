package chylex.hee.entity.mob;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import chylex.hee.api.interfaces.IIgnoreEnderGoo;
import chylex.hee.entity.fx.FXType;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.charms.RuneType;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C20Effect;
import chylex.hee.packets.client.C21EffectEntity;
import chylex.hee.packets.client.C22EffectLine;
import chylex.hee.system.util.BlockPosM;
import chylex.hee.system.util.MathUtil;
import chylex.hee.tileentity.spawner.LouseRavagedSpawnerLogic.LouseSpawnData;
import chylex.hee.tileentity.spawner.LouseRavagedSpawnerLogic.LouseSpawnData.EnumLouseAbility;
import chylex.hee.tileentity.spawner.LouseRavagedSpawnerLogic.LouseSpawnData.EnumLouseAttribute;

public class EntityMobLouse extends EntityMob implements IIgnoreEnderGoo{
	private LouseSpawnData louseData;
	private float armor;
	private byte armorCapacity,armorRegenTimer,regenLevel,regenTimer,healAbility,healTimer,teleportTimer;
	
	public EntityMobLouse(World world){
		super(world);
		setSize(1.1F,0.45F);
	}
	
	public EntityMobLouse(World world, LouseSpawnData louseData){
		this(world);
		this.louseData = louseData;
		updateLouseData();
	}
	
	@Override
	protected void entityInit(){
		super.entityInit();
		dataWatcher.addObject(16,"");
	}
	
	@Override
	protected void applyEntityAttributes(){
		super.applyEntityAttributes();
		updateLouseData();
	}
	
	private void updateLouseData(){
		if (worldObj == null || worldObj.isRemote || louseData == null)return;
		
		int attrHealth = louseData.attribute(EnumLouseAttribute.HEALTH);
		int attrSpeed = louseData.attribute(EnumLouseAttribute.SPEED);
		int attrArmor = louseData.attribute(EnumLouseAttribute.ARMOR);
		
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(14D+(attrHealth > 0 ? 10D+8D*attrHealth : 0D));
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.7D+(attrSpeed > 0 ? 0.1D+0.07D*attrSpeed : 0D));
		getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(7D+3.5D*louseData.attribute(EnumLouseAttribute.ATTACK));
		
		setHealth(getMaxHealth());
		
		if (attrArmor > 0)armor = armorCapacity = (byte)(8+MathUtil.square(attrArmor)+attrArmor*5);
		
		healAbility = (byte)louseData.ability(EnumLouseAbility.HEAL);
		regenLevel = (byte)attrHealth;
		
		dataWatcher.updateObject(16,louseData.serializeToString());
	}
	
	@Override
	protected Entity findPlayerToAttack(){
		return worldObj.getClosestPlayerToEntity(this,12D);
	}
	
	@Override
	public void onUpdate(){
		super.onUpdate();
		
		if (louseData == null){
			if (worldObj.isRemote){
				String data = dataWatcher.getWatchableObjectString(16);
				if (!data.isEmpty())louseData = new LouseSpawnData(data);
			}
			else{
				louseData = new LouseSpawnData((byte)rand.nextInt(LouseSpawnData.maxLevel),getRNG());
				updateLouseData();
			}
		}
		
		if (armorCapacity > 0){			
			if (armorRegenTimer > 0)--armorRegenTimer;
			else if (armor < armorCapacity){
				if (++armor >= armorCapacity)armor = armorCapacity;
				else armorRegenTimer = (byte)(10-2*louseData.attribute(EnumLouseAttribute.ARMOR));
			}
		}
		
		if (teleportTimer > 0)--teleportTimer;
		
		if (regenLevel > 1 && getHealth() < getMaxHealth() && ++regenTimer >= 32-8*regenLevel && !isDead){
			setHealth(getHealth()+1);
			regenTimer = 0;
			PacketPipeline.sendToAllAround(this,64D,new C21EffectEntity(FXType.Entity.LOUSE_REGEN,this));
		}
		
		if (healAbility > 0 && --healTimer <= 0){
			healTimer = 25;
			List<EntityLiving> list = worldObj.getEntitiesWithinAABB(EntityLiving.class,boundingBox.expand(5D,1D,5D));
			
			if (!list.isEmpty()){
				for(int attempt = 0, amt = list.size(); attempt < 15; attempt++){
					EntityLiving entity = list.get(rand.nextInt(amt));
					
					if (entity.getHealth() < entity.getMaxHealth() && !entity.isDead){
						entity.setHealth(entity.getHealth()+1+2*healAbility);
						PacketPipeline.sendToAllAround(this,64D,new C22EffectLine(FXType.Line.LOUSE_HEAL_ENTITY,this,entity));
						break;
					}
				}
			}
		}
		
		Entity entityToAttack = getAttackTarget();
		
		if (entityToAttack != null && entityToAttack.posY > posY+1.3D && MathUtil.distance(posX-entityToAttack.posX,posZ-entityToAttack.posZ) <= 3D && (getDistanceToEntity(entityToAttack) < 8D || canEntityBeSeen(entityToAttack))){
			jump();
		}
	}
	
	@Override
	public boolean attackEntityAsMob(Entity entity){
		float dmgAmount = (float)getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();

		if (entity.attackEntityFrom(DamageSource.causeMobDamage(this),dmgAmount)){
			int knockback = louseData.ability(EnumLouseAbility.KNOCKBACK);
			
			if (knockback > 0){
				entity.addVelocity((-MathHelper.sin(MathUtil.toRad(rotationYaw))*knockback*1.25F),0.1D,(MathHelper.cos(MathUtil.toRad(rotationYaw))*knockback*1.25F));
				motionX *= 0.6D;
				motionZ *= 0.6D;
			}
			
			int magicDamage = louseData.ability(EnumLouseAbility.MAGICDMG);
			
			if (magicDamage > 0){
				entity.hurtResistantTime = 0;
				entity.attackEntityFrom(DamageSource.magic,1F+2F*(magicDamage-1));
			}

			if (entity instanceof EntityLivingBase)EnchantmentHelper.func_151384_a((EntityLivingBase)entity,this);
			EnchantmentHelper.func_151385_b(this,entity);
			
			return true;
		}
		else return false;
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount){
		if (louseData == null)return false;
		
		int teleportLevel = louseData.attribute(EnumLouseAttribute.TELEPORT);
		
		if (teleportLevel > 0 && teleportTimer == 0){
			teleport(teleportLevel);
			return false;
		}
		
		healTimer = 45;
		armorRegenTimer = (byte)(100-louseData.attribute(EnumLouseAttribute.ARMOR)*15);
		
		if (armor > 0F && hurtResistantTime == 0 && source.getSourceOfDamage() instanceof EntityLivingBase){
			setAttackTarget((EntityLivingBase)source.getSourceOfDamage());
			playSound("random.anvil_land",0.5F,1.2F);
			PacketPipeline.sendToAllAround(this,64D,new C20Effect(FXType.Basic.LOUSE_ARMOR_HIT,this));
			
			if ((armor -= amount) <= 0F)armor = 0F;
			hurtTime = maxHurtTime = 10;
			hurtResistantTime = 10;
			
			return true;
		}
		
		return super.attackEntityFrom(source,amount);
	}
	
	private void teleport(int level){
		teleportTimer = (byte)(80-level*10);
		
		if (worldObj.isRemote){
			for(int a = 0; a < 64; a++){
				worldObj.spawnParticle(EnumParticleTypes.PORTAL,posX+(rand.nextDouble()-rand.nextDouble())*width,posY+rand.nextDouble()*height,posZ+(rand.nextDouble()-rand.nextDouble())*width,(rand.nextFloat()-0.5F)*0.2F,(rand.nextFloat()-0.5F)*0.2F,(rand.nextFloat()-0.5F)*0.2F);
			}

			return;
		}
		
		double oldPosX = posX;
		double oldPosY = posY;
		double oldPosZ = posZ;
		int maxDist = 3+level;
		
		boolean hasTeleported = false;
		BlockPosM testPos = new BlockPosM();
		
		for(int attempt = 0; attempt < 32 && !hasTeleported; attempt++){
			posX = oldPosX+rand.nextInt(maxDist)-rand.nextInt(maxDist);
			posY = oldPosY+1;
			posZ = oldPosZ+rand.nextInt(maxDist)-rand.nextInt(maxDist);
			
			if (MathUtil.distance(posX-oldPosX,posZ-oldPosZ) < 2D)continue;
			
			testPos.moveTo(posX,posY,posZ);
			
			for(int py = 0; py < 3; py++){
				if (!testPos.isAir(worldObj)){
					testPos.moveDown();
					--posY;
				}
				else if (!testPos.moveDown().isAir(worldObj)){
					setPosition(posX,posY+0.1D,posZ);
					hasTeleported = true;
					break;
				}
				else --posY;
			}
		}
		
		if (!hasTeleported)return;
		
		for(int a = 0; a < 64; a++){
			worldObj.spawnParticle(EnumParticleTypes.PORTAL,oldPosX+(rand.nextDouble()-rand.nextDouble())*width,oldPosY+rand.nextDouble()*height,oldPosZ+(rand.nextDouble()-rand.nextDouble())*width,(rand.nextFloat()-0.5F)*0.2F,(rand.nextFloat()-0.5F)*0.2F,(rand.nextFloat()-0.5F)*0.2F);
		}

		worldObj.playSoundEffect(oldPosX,oldPosY,oldPosZ,"mob.endermen.portal",1F,1F);
		playSound("mob.endermen.portal",1F,1F);
	}
	
	@Override
	protected void jump(){
		motionY = 0.5D;
		if (isPotionActive(Potion.jump))motionY += ((getActivePotionEffect(Potion.jump).getAmplifier()+1)*0.1F);

		isAirBorne = true;
		ForgeHooks.onLivingJump(this);
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt){
		super.writeEntityToNBT(nbt);
		if (louseData != null)nbt.setTag("louseData",louseData.writeToNBT(new NBTTagCompound()));
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		if (nbt.hasKey("louseData"))louseData = new LouseSpawnData(nbt.getCompoundTag("louseData"));
		updateLouseData();
	}
	
	@Override
	protected void addRandomArmor(){ // OBFUSCATED dropRareDrop?!
		int nearbyLice = worldObj.getEntitiesWithinAABB(EntityMobLouse.class,boundingBox.expand(4D,4D,4D)).size();
		
		if (rand.nextInt(1+(nearbyLice>>3)) == 0){
			Set<EnumLouseAttribute> attributes = louseData.getAttributeSet();
			Set<EnumLouseAbility> abilities = louseData.getAbilitySet();

			if (!abilities.isEmpty() && rand.nextBoolean())entityDropItem(new ItemStack(ItemList.rune,1,RuneType.VOID.ordinal()),0F);
			else if (!attributes.isEmpty()){
				int n = rand.nextInt(attributes.size());
				
				for(Iterator<EnumLouseAttribute> iter = attributes.iterator(); iter.hasNext();){
					EnumLouseAttribute attribute = iter.next();
					
					if (--n < 0){
						entityDropItem(new ItemStack(ItemList.rune,1,attribute.ordinal()),0F);
						break;
					}
				}
			}
		}
	}

	@Override
	protected String getHurtSound(){
		return "hardcoreenderexpansion:mob.louse.hit";
	}

	@Override
	protected String getDeathSound(){
		return "hardcoreenderexpansion:mob.louse.hit";
	}
	
	@Override
	public EnumCreatureAttribute getCreatureAttribute(){
		return EnumCreatureAttribute.ARTHROPOD;
	}
	
	@Override
	protected boolean isValidLightLevel(){
		return true;
	}
	
	@Override
	public String getName(){
		return hasCustomName() ? getCustomNameTag() : StatCollector.translateToLocal("entity.louse.name");
	}
	
	public LouseSpawnData getSpawnData(){
		return louseData;
	}
}
