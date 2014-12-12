package chylex.hee.entity.boss;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.api.interfaces.IIgnoreEnderGoo;
import chylex.hee.entity.RandomNameGenerator;
import chylex.hee.entity.fx.FXType;
import chylex.hee.entity.mob.util.DamageSourceMobUnscaled;
import chylex.hee.entity.projectile.EntityProjectileFiendFireball;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.essence.EssenceType;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C20Effect;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.util.MathUtil;

public class EntityMiniBossFireFiend extends EntityFlying implements IBossDisplayData, IIgnoreEnderGoo{
	private static final byte ATTACK_NONE = 0, ATTACK_FIREBALLS = 1, ATTACK_FLAMES = 2;
	
	private boolean isAngry;
	private byte timer, currentAttack = ATTACK_NONE, prevAttack = ATTACK_NONE;
	public float wingAnimation, wingAnimationStep;
	
	public EntityMiniBossFireFiend(World world){
		super(world);
		setSize(3F,2.6F);
		experienceValue = 40;
		scoreValue = 50;
		isImmuneToFire = true;
		ignoreFrustumCheck = true;
		
		RandomNameGenerator.generateEntityName(this,rand.nextInt(5)+5);
	}
	
	@Override
	protected void entityInit(){
		super.entityInit();
		dataWatcher.addObject(16,Byte.valueOf((byte)0));
		dataWatcher.addObject(17,Byte.valueOf((byte)0));
	}
	
	@Override
	protected void applyEntityAttributes(){
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(ModCommonProxy.opMobs ? 300D : 200D);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(1.8D);
	}
	
	@Override
	public void onLivingUpdate(){
		super.onLivingUpdate();
		
		if (worldObj.isRemote){
			byte attack = dataWatcher.getWatchableObjectByte(16);
			
			if (attack == ATTACK_FIREBALLS){
				
			}
			else if (attack == ATTACK_FLAMES){
				for(int a = 0; a < 5; a++)HardcoreEnderExpansion.fx.flame(worldObj,posX+((rand.nextDouble()-0.5D)*rand.nextDouble())*width,posY+rand.nextDouble()*height,posZ+((rand.nextDouble()-0.5D)*rand.nextDouble())*width,8);
			}
			else timer = 0;
			
			if (dataWatcher.getWatchableObjectByte(17) == 1){
				// TODO angry particles
			}
		}
	}
	
	@Override
	protected void updateEntityActionState(){
		EntityPlayer closest = worldObj.getClosestPlayerToEntity(this,164D);
		if (closest == null)return;
		
		double targetYDiff = posY-(closest.posY+9D);
		
		for(int a = 1; a <= 7; a += 2){
			if (!worldObj.isAirBlock(MathUtil.floor(posX),MathUtil.floor(posY)-a,MathUtil.floor(posZ))){
				targetYDiff = -1.5D;
				break;
			}
		}
		
		if (Math.abs(targetYDiff) > 1D)motionY -= Math.abs(targetYDiff)*0.0045D*Math.signum(targetYDiff);
		
		if (currentAttack == ATTACK_NONE){
			if (++timer > 110-worldObj.difficultySetting.getDifficultyId()*8-(isAngry ? 20 : 0)-(ModCommonProxy.opMobs ? 15 : 0)){
				boolean hasCalledGolems = false;
				
				if (isAngry && rand.nextInt(5) == 0){
					// TODO call golems
					timer >>= 1;
				}
				
				if (!hasCalledGolems){
					currentAttack = rand.nextInt(3) == 0 ? ATTACK_FIREBALLS : ATTACK_FLAMES;
					if (currentAttack == ATTACK_FLAMES && prevAttack == ATTACK_FLAMES)currentAttack = ATTACK_FIREBALLS;
					timer = 0;
				}
			}
		}
		else if (currentAttack == ATTACK_FIREBALLS){
			int amt = ModCommonProxy.opMobs ? 8 : 6, speed = isAngry ? 8 : 12;
			
			if (++timer == 1){
				double ang = 360D/(amt+1);
				for(int a = 0; a < amt; a++)worldObj.spawnEntityInWorld(new EntityProjectileFiendFireball(worldObj,this,posX,posY,posZ,a*ang,speed*(a+1)));
			}
			else if (timer >= amt*speed){
				currentAttack = ATTACK_NONE;
				timer = 0;
			}
		}
		else if (currentAttack == ATTACK_FLAMES){
			if (++timer > (isAngry ? 18 : 26)){
				int fireLength = 3+(worldObj.difficultySetting.getDifficultyId()>>1);
				
				for(EntityPlayer player:getNearbyPlayers()){
					player.setFire(fireLength);
					player.attackEntityFrom(new DamageSourceMobUnscaled(this),DamageSourceMobUnscaled.getDamage(ModCommonProxy.opMobs ? 12F : 8F,worldObj.difficultySetting));
					PacketPipeline.sendToAllAround(player,64D,new C20Effect(FXType.Basic.FIRE_FIEND_FLAME_ATTACK,player));
				}
				
				timer = 0;
				currentAttack = ATTACK_NONE;
			}
		}
		
		if (prevAttack != currentAttack){
			dataWatcher.updateObject(16,currentAttack);
			prevAttack = currentAttack;
		}
		
		//
		
		/*
		if (attackStage == STAGE_REFRESHING){
			if (--fireballAttackTimer < 0){
				fireballOffsets.clear();
				
				for(int a = 0; a < 5+rand.nextInt(4); a++){
					double ang = rand.nextDouble()*Math.PI*2D,len = rand.nextDouble()*2.5D+5D;
					fireballOffsets.add(new float[]{ (float)(Math.cos(ang)*len), rand.nextFloat()*2.5F+4F, (float)(Math.sin(ang)*len) });
				}
				
				attackStage = STAGE_CREATING;
				fireballAttackTimer = 60;
			}
		}
		else if (attackStage == STAGE_CREATING){
			if (--fireballAttackTimer < 0)attackStage = STAGE_SHOOTING;
		}
		else if (attackStage == STAGE_SHOOTING){
			if (--fireballAttackTimer < 0){
				Iterator < float[]> iter = fireballOffsets.iterator();
				if (iter.hasNext()){
					float[] offset = iter.next();
					
					double ballX = target.posX+offset[0],ballY = target.posY+offset[1]+1.5D,ballZ = target.posZ+offset[2];
					worldObj.spawnEntityInWorld(new EntityProjectileGolemFireball(worldObj,this,ballX,ballY,ballZ,target.posX-ballX,target.boundingBox.minY+target.height*0.5F-ballY,target.posZ-ballZ));
					
					iter.remove();
					fireballAttackTimer = (byte)(10+rand.nextInt(10)-worldObj.difficultySetting.getDifficultyId()*2);
				}
				else{
					attackStage = rand.nextInt(5) <= 1?STAGE_TOUCHING:STAGE_REFRESHING;
					damageInflicted = 0;
					touchAttemptTimer = 120;
					fireballAttackTimer = (byte)(80+rand.nextInt(25)-worldObj.difficultySetting.getDifficultyId()*5-Math.min(50,damageTaken*0.3F)-(ModCommonProxy.opMobs?20:0));								
				}
			}
		}
		
		if (attackStage == STAGE_CREATING || attackStage == STAGE_SHOOTING){
			for(float[] offset:fireballOffsets){
				PacketPipeline.sendToAllAround(this,96D,new C12ParticleFireFiendFlames(this,target,offset,Byte.valueOf((byte)((60-fireballAttackTimer)>>2))));
			}
		}
		
		if (attackStage == STAGE_TOUCHING){
			if (damageInflicted > 15+worldObj.difficultySetting.getDifficultyId()*3 || --touchAttemptTimer < -20){
				attackStage = STAGE_REFRESHING;
			}
		}
		*/
		
		//
		
		for(EntityLivingBase e:(List<EntityLivingBase>)worldObj.getEntitiesWithinAABB(EntityLivingBase.class,boundingBox.expand(0.8D,1.65D,0.8D))){
			if (e == this || e.isImmuneToFire())continue;
			e.setFire(2+rand.nextInt(4));
			e.hurtResistantTime = 0;
			e.attackEntityFrom(new DamageSourceMobUnscaled(this),ModCommonProxy.opMobs ? 9F : 5F);
			e.hurtResistantTime = 7;
		}
		
		moveForward *= 0.6F;
		
		wingAnimationStep = 1F;
		if (Math.abs(moveForward) > 0.01D)wingAnimationStep += 1F;
		if (motionY > 0.001D)wingAnimationStep += 1.5F;
		else if (motionY < 0.001D)wingAnimationStep -= 0.75F;
		
		wingAnimation += wingAnimationStep*0.01F;
	}
	
	private List<EntityPlayer> getNearbyPlayers(){
		List<EntityPlayer> allNearby = worldObj.getEntitiesWithinAABB(EntityPlayer.class,boundingBox.expand(164D,164D,164D));
		
		for(Iterator<EntityPlayer> iter = allNearby.iterator(); iter.hasNext();){
			EntityPlayer player = iter.next();
			if (player.getDistanceToEntity(this) > 164D || player.isDead)iter.remove();
		}
		
		return allNearby;
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount){
		if (source.isFireDamage() || source.isExplosion())amount *= 0.1F;
		if (isAngry)amount *= 0.75F;
		return super.attackEntityFrom(source,Math.min(15,amount));
	}
	
	@Override
	public void setHealth(float newHealth){
		super.setHealth(newHealth);
		
		if (getHealth() <= getMaxHealth()*0.4F){
			isAngry = true;
			dataWatcher.updateObject(17,Byte.valueOf((byte)1));
		}
	}
	
	@Override
	protected void dropFewItems(boolean recentlyHit, int looting){
		for(int a = 0; a < 80; a++)entityDropItem(new ItemStack(ItemList.essence,3,EssenceType.FIERY.getItemDamage()),rand.nextFloat()*height);
	}
	
	@Override
	public void knockBack(Entity entity, float damage, double xPower, double zPower){
		super.knockBack(entity,damage,xPower,zPower);
		motionX *= 0.4D;
		motionY *= 0.4D;
		motionZ *= 0.4D;
	}
	
	@Override
	protected String getLivingSound(){
		return "fire.fire";
	}

	@Override
	protected String getHurtSound(){
		return "hardcoreenderexpansion:mob.firefiend.hurt";
	}

	@Override
	protected String getDeathSound(){
		return "hardcoreenderexpansion:mob.firefiend.hurt";
	}
	
	@Override
	protected float getSoundVolume(){
		return 1.8F;
	}
	
	@Override
	protected float getSoundPitch(){
		return 0.8F+rand.nextFloat()*0.1F;
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt){
		super.writeEntityToNBT(nbt);
		nbt.setBoolean("isAngry",isAngry);
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		if ((isAngry = nbt.getBoolean("isAngry")) == true)dataWatcher.updateObject(17,Byte.valueOf((byte)1));
	}
	
	@Override
	public String getCommandSenderName(){
		return hasCustomNameTag() ? getCustomNameTag() : StatCollector.translateToLocal("entity.fireFiend.name");
	}
	
	@Override
	protected void despawnEntity(){}
}
