package chylex.hee.entity.mob;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import chylex.hee.entity.projectile.EntityProjectileFlamingBall;
import chylex.hee.init.ItemList;
import chylex.hee.mechanics.causatum.CausatumMeters;
import chylex.hee.mechanics.causatum.CausatumUtils;
import chylex.hee.mechanics.essence.EssenceType;
import chylex.hee.proxy.ModCommonProxy;

public class EntityMobScorchingLens extends EntityMob{
	public EntityMobScorchingLens(World world){
		super(world);
		isImmuneToFire = true;
		experienceValue = 6;
	}
	
	@Override
	protected void applyEntityAttributes(){
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.42D);
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(ModCommonProxy.opMobs ? 32D : 18D);
	}
	
	@Override
	protected Entity findPlayerToAttack(){
		EntityPlayer player = worldObj.getClosestVulnerablePlayerToEntity(this,12D);
		return player != null && canEntityBeSeen(player)?player:null;
	}
	
	@Override
	public void onLivingUpdate(){
		super.onLivingUpdate();
		
		if (!worldObj.isRemote && entityToAttack != null){
			if (getDistanceSqToEntity(entityToAttack) > 180D)entityToAttack = null;
			else if ((ticksExisted&1) == 1 && getHealth() > 0 && canEntityBeSeen(entityToAttack)){
				Vec3 look = getLookVec();
				
				worldObj.spawnEntityInWorld(new EntityProjectileFlamingBall(
					worldObj,this,posX+look.xCoord*0.5F,posY+look.yCoord*0.5F+0.5D,posZ+look.zCoord*0.5F,
					entityToAttack.posX-posX,entityToAttack.posY-posY+rand.nextDouble()*0.4D,entityToAttack.posZ-posZ
				));
			}
		}
	}
	
	@Override
	public boolean attackEntityAsMob(Entity entity){
		return true;
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount){
		if (super.attackEntityFrom(source,amount)){
			if (entityToAttack instanceof IBossDisplayData)entityToAttack = null;
			CausatumUtils.increase(source,CausatumMeters.END_MOB_DAMAGE,amount*0.25F);
			return true;
		}
		return false;
	}
	
	@Override
	protected void dropFewItems(boolean recentlyHit, int looting){
		dropItem(ItemList.igneous_rock,rand.nextInt(2)+rand.nextInt(2));
		if (recentlyHit)entityDropItem(new ItemStack(ItemList.essence,rand.nextInt(5+looting),EssenceType.FIERY.getItemDamage()),0F);
	}
	
	@Override
	protected String getLivingSound(){
		return "hardcoreenderexpansion:mob.scorchinglens.living";
	}

	@Override
	protected String getHurtSound(){
		return "hardcoreenderexpansion:mob.scorchinglens.hurt";
	}

	@Override
	protected String getDeathSound(){
		return "hardcoreenderexpansion:mob.scorchinglens.death";
	}
	
	@Override
	public String getCommandSenderName(){
		return hasCustomNameTag() ? getCustomNameTag() : StatCollector.translateToLocal("entity.scorchingLens.name");
	}
}
