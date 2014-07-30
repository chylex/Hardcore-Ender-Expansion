package chylex.hee.entity.mob;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import chylex.hee.system.util.MathUtil;
import chylex.hee.tileentity.spawner.LouseRavagedSpawnerLogic.LouseSpawnData;
import chylex.hee.tileentity.spawner.LouseRavagedSpawnerLogic.LouseSpawnData.EnumLouseAbility;
import chylex.hee.tileentity.spawner.LouseRavagedSpawnerLogic.LouseSpawnData.EnumLouseAttribute;

public class EntityMobLouse extends EntityMob{
	private LouseSpawnData louseData;
	private byte armor,armorCapacity;
	
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
	
	private void updateLouseData(){ // TODO AI
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
				entity.addVelocity((-MathHelper.sin(rotationYaw*(float)Math.PI/180F)*knockback*0.5F),0.1D,(MathHelper.cos(rotationYaw*(float)Math.PI/180F)*knockback*0.5F));
				motionX *= 0.6D;
				motionZ *= 0.6D;
			}

			if (entity instanceof EntityLivingBase)EnchantmentHelper.func_151384_a((EntityLivingBase)entity,this);
			EnchantmentHelper.func_151385_b(this,entity);
			
			return true;
		}
		else return false;
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
	protected String getLivingSound(){
		return "mob.silverfish.say";
	}

	@Override
	protected String getHurtSound(){
		return "mob.silverfish.hit";
	}

	@Override
	protected String getDeathSound(){
		return "mob.silverfish.kill";
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
