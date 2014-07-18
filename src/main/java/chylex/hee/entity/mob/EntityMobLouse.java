package chylex.hee.entity.mob;
import chylex.hee.mechanics.spawner.LouseSpawnerLogic.LouseSpawnData;
import chylex.hee.mechanics.spawner.LouseSpawnerLogic.LouseSpawnData.EnumLouseAttribute;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityMobLouse extends EntityMob{
	private LouseSpawnData louseData;
	
	public EntityMobLouse(World world){
		super(world);
		setSize(0.5F,0.5F);
	}
	
	@Override
	protected void applyEntityAttributes(){
		super.applyEntityAttributes();
		loadAttributeValues();
	}
	
	private void loadAttributeValues(){
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(12D+8D*louseData.attribute(EnumLouseAttribute.HEALTH));
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.7D+0.06D*louseData.attribute(EnumLouseAttribute.SPEED));
		getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(3D+2.5D*louseData.attribute(EnumLouseAttribute.ATTACK));
	}
	
	@Override
	protected Entity findPlayerToAttack(){
		return worldObj.getClosestVulnerablePlayerToEntity(this,12D);
	}
	
	@Override
	public void onUpdate(){
		super.onUpdate();
		
	}
	
	@Override
	public boolean attackEntityAsMob(Entity entity){
		float dmgAmount = (float)getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();

		if (entity instanceof EntityLivingBase){
			dmgAmount += EnchantmentHelper.getEnchantmentModifierLiving(this,(EntityLivingBase)entity);
		}

		if (entity.attackEntityFrom(DamageSource.causeMobDamage(this),dmgAmount)){
			int knockback = louseData.attribute(EnumLouseAttribute.KNOCKBACK);
			
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
}
