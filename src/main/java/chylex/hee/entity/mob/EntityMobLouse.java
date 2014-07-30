package chylex.hee.entity.mob;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.charms.RuneType;
import chylex.hee.system.util.MathUtil;
import chylex.hee.tileentity.spawner.LouseRavagedSpawnerLogic.LouseSpawnData;
import chylex.hee.tileentity.spawner.LouseRavagedSpawnerLogic.LouseSpawnData.EnumLouseAbility;
import chylex.hee.tileentity.spawner.LouseRavagedSpawnerLogic.LouseSpawnData.EnumLouseAttribute;

public class EntityMobLouse extends EntityMob{
	private LouseSpawnData louseData;
	private float armor;
	private byte armorCapacity,armorRegenTimer,teleportTimer;
	
	public EntityMobLouse(World world){
		super(world);
		setSize(1F,0.4F);
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
	
	@Override
	protected boolean isAIEnabled(){
		return false;
	}
	
	private void updateLouseData(){
		if (worldObj == null || worldObj.isRemote || louseData == null)return;
		
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(15D+8D*louseData.attribute(EnumLouseAttribute.HEALTH));
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.7D+0.06D*louseData.attribute(EnumLouseAttribute.SPEED));
		getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(3D+2.5D*louseData.attribute(EnumLouseAttribute.ATTACK));
		
		armorCapacity = (byte)MathUtil.square(louseData.attribute(EnumLouseAttribute.ARMOR));
		if (armorCapacity > 0)armorCapacity *= 6;
		
		dataWatcher.updateObject(16,louseData.serializeToString());
	}
	
	@Override
	protected Entity findPlayerToAttack(){
		return worldObj.getClosestVulnerablePlayerToEntity(this,12D);
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
	}
	
	@Override
	public boolean attackEntityAsMob(Entity entity){
		float dmgAmount = (float)getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();

		if (entity instanceof EntityLivingBase){
			dmgAmount += EnchantmentHelper.getEnchantmentModifierLiving(this,(EntityLivingBase)entity);
		}

		if (entity.attackEntityFrom(DamageSource.causeMobDamage(this),dmgAmount)){
			int knockback = louseData.ability(EnumLouseAbility.KNOCKBACK);
			
			if (knockback > 0){
				entity.addVelocity((-MathHelper.sin(MathUtil.toRad(rotationYaw))*knockback*1.25F),0.1D,(MathHelper.cos(MathUtil.toRad(rotationYaw))*knockback*1.25F));
				motionX *= 0.6D;
				motionZ *= 0.6D;
			}
			
			int magicDamage = louseData.ability(EnumLouseAbility.MAGICDMG);
			if (magicDamage > 0)entity.attackEntityFrom(DamageSource.magic,4F+2F*(magicDamage-1));

			if (entity instanceof EntityLivingBase)EnchantmentHelper.func_151384_a((EntityLivingBase)entity,this);
			EnchantmentHelper.func_151385_b(this,entity);
			
			return true;
		}
		else return false;
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount){
		int teleportLevel = louseData.attribute(EnumLouseAttribute.TELEPORT);
		
		if (teleportLevel > 0 && teleportTimer == 0){
			teleport(teleportLevel);
			return false;
		}
		
		if (armor > 0F){
			entityToAttack = source.getEntity();
			playSound("random.anvil_land",0.5F,1.2F);
			
			if ((armor -= amount) <= 0F)armor = 0F;
			armorRegenTimer = (byte)(100-louseData.attribute(EnumLouseAttribute.ARMOR)*15);
			
			return true;
		}
		
		return super.attackEntityFrom(source,amount);
	}
	
	private void teleport(int level){
		teleportTimer = (byte)(80-level*10);
		
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt){
		super.writeEntityToNBT(nbt);
		nbt.setTag("louseData",louseData.writeToNBT(new NBTTagCompound()));
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		louseData = new LouseSpawnData(nbt.getCompoundTag("louseData"));
		updateLouseData();
	}
	
	@Override
	protected void dropRareDrop(int looting){ // TODO recheck the parameter name?
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
		return "mob.louse.hit";
	}

	@Override
	protected String getDeathSound(){
		return "mob.louse.hit";
	}
	
	@Override
	public EnumCreatureAttribute getCreatureAttribute(){
		return EnumCreatureAttribute.ARTHROPOD;
	}
	
	@Override
	protected boolean isValidLightLevel(){
		return true;
	}
	
	public LouseSpawnData getSpawnData(){
		return louseData;
	}
}
