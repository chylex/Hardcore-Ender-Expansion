package chylex.hee.entity.boss;
import java.util.ArrayList;
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
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.api.interfaces.IIgnoreEnderGoo;
import chylex.hee.entity.RandomNameGenerator;
import chylex.hee.entity.fx.FXType;
import chylex.hee.entity.mob.EntityMobFireGolem;
import chylex.hee.entity.mob.util.DamageSourceMobUnscaled;
import chylex.hee.entity.projectile.EntityProjectileFiendFireball;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.essence.EssenceType;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C20Effect;
import chylex.hee.packets.client.C22EffectLine;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.MathUtil;

public class EntityMiniBossFireFiend extends EntityFlying implements IBossDisplayData, IIgnoreEnderGoo{
	private static final byte ATTACK_NONE = 0, ATTACK_FIREBALLS = 1, ATTACK_FLAMES = 2;
	
	private boolean isAngry;
	private byte timer, currentAttack = ATTACK_NONE, prevAttack = ATTACK_NONE;
	private final List<EntityProjectileFiendFireball> controlledFireballs = new ArrayList<>(8);
	
	private float targetAngle;
	private boolean targetAngleChangeDir;
	private byte targetAngleTimer;
	private final Vec3 motionVec = new Vec3(0D,0D,0D);
	public float wingAnimation, wingAnimationStep;
	
	public EntityMiniBossFireFiend(World world){
		super(world);
		setSize(2.7F,2.7F);
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
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(ModCommonProxy.opMobs ? 380D : 300D);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(1.8D);
	}
	
	@Override
	public void onLivingUpdate(){
		super.onLivingUpdate();
		
		if (worldObj.isRemote){
			byte attack = dataWatcher.getWatchableObjectByte(16);
			
			if (attack == ATTACK_FLAMES){
				for(int a = 0; a < 5; a++)HardcoreEnderExpansion.fx.flame(worldObj,posX+((rand.nextDouble()-0.5D)*rand.nextDouble())*width,posY+rand.nextDouble()*height,posZ+((rand.nextDouble()-0.5D)*rand.nextDouble())*width,8);
			}
			else timer = 0;
			
			if (!isAngry && dataWatcher.getWatchableObjectByte(17) == 1)isAngry = true;
			
			if (isAngry){
				for(int a = 0; a < 2; a++)HardcoreEnderExpansion.fx.flame(worldObj,posX+((rand.nextDouble()-0.5D)*rand.nextDouble())*width,posY+rand.nextDouble()*height,posZ+((rand.nextDouble()-0.5D)*rand.nextDouble())*width,12);
			}
			
			renderYawOffset = rotationYaw;
		}
	}
	
	@Override
	protected void updateEntityActionState(){
		EntityPlayer closest = worldObj.getClosestPlayerToEntity(this,164D);
		if (closest == null)return;
		
		rotationYaw = MathUtil.toDeg((float)Math.atan2(posZ-closest.posZ,posX-closest.posX))+90F;
		rotationPitch = MathUtil.toDeg((float)Math.atan2(posY-(closest.posY+closest.getEyeHeight()),MathUtil.distance(posX-closest.posX,posZ-closest.posZ)));
		
		double targetYDiff = posY-(closest.posY+9D);
		
		for(int a = 1; a <= 7; a += 2){
			if (!worldObj.isAirBlock(MathUtil.floor(posX),MathUtil.floor(posY)-a,MathUtil.floor(posZ))){
				targetYDiff = -1.5D;
				break;
			}
		}
		
		if (Math.abs(targetYDiff) > 1D)motionY -= Math.abs(targetYDiff)*0.0045D*Math.signum(targetYDiff);
		
		if (ticksExisted == 1)targetAngleChangeDir = rand.nextBoolean();
		else if (rand.nextInt(195) == 0 || (targetAngleTimer > 122 || (targetAngleTimer += rand.nextInt(2)) > 122)){
			targetAngleChangeDir = !targetAngleChangeDir;
			targetAngleTimer = 0;
		}
		
		targetAngle += (targetAngleChangeDir ? 1 : -1)*0.02F;
		double[] vec = DragonUtil.getNormalizedVector((closest.posX+MathHelper.cos(targetAngle)*40D)-posX+(rand.nextDouble()-0.5D)*4D,(closest.posZ+MathHelper.sin(targetAngle)*40D)-posZ+(rand.nextDouble()-0.5D)*4D);
		motionVec.xCoord = vec[0]*0.5D;
		motionVec.zCoord = vec[1]*0.5D;
		
		motionX = motionVec.xCoord*0.1D+motionX*0.9D;
		motionZ = motionVec.zCoord*0.1D+motionZ*0.9D;
		
		if (currentAttack == ATTACK_NONE){
			if (++timer > 125-worldObj.getDifficulty().getDifficultyId()*7-(isAngry ? 18 : 0)-(ModCommonProxy.opMobs ? 12 : 0)){
				boolean hasCalledGolems = false;
				
				if (isAngry && worldObj.getDifficulty() != EnumDifficulty.PEACEFUL && rand.nextInt(5) == 0){
					for(EntityPlayer player:getNearbyPlayers()){
						int targeted = 0;
						List<EntityMobFireGolem> golems = worldObj.getEntitiesWithinAABB(EntityMobFireGolem.class,player.boundingBox.expand(32D,32D,32D));
						
						for(EntityMobFireGolem golem:golems){
							if (golem.getAttackTarget() == player && ++targeted >= 2)break;
						}
						
						if (targeted >= 2)continue;
						
						golems = worldObj.getEntitiesWithinAABB(EntityMobFireGolem.class,player.boundingBox.expand(16D,16D,16D));
						if (golems.isEmpty())continue;
						
						for(int attempt = 0, called = ModCommonProxy.opMobs ? 3 : 2; attempt < 3 && !golems.isEmpty() && called > 0; attempt++){
							EntityMobFireGolem golem = golems.remove(rand.nextInt(golems.size()));
							
							if (player.getDistanceToEntity(golem) <= 16D){
								golem.setAttackTarget(player);
								PacketPipeline.sendToAllAround(this,128D,new C22EffectLine(FXType.Line.FIRE_FIEND_GOLEM_CALL,this,golem));
								called -= rand.nextInt(2)+1;
							}
						}
					}
					
					timer >>= 1;
				}
				
				if (!hasCalledGolems){
					currentAttack = rand.nextInt(3) != 0 ? ATTACK_FIREBALLS : ATTACK_FLAMES;
					if (currentAttack == ATTACK_FLAMES && prevAttack == ATTACK_FLAMES)currentAttack = ATTACK_FIREBALLS;
					dataWatcher.updateObject(16,currentAttack);
					prevAttack = currentAttack;
					timer = 0;
				}
			}
		}
		else if (currentAttack == ATTACK_FIREBALLS){
			int amt = ModCommonProxy.opMobs ? 8 : 6, speed = isAngry ? 8 : 12;

			if (++timer == 1){
				double ang = 360D/amt;
				
				for(int a = 0; a < amt; a++){
					controlledFireballs.add(new EntityProjectileFiendFireball(worldObj,this,posX,posY+height*0.5F,posZ,-a*ang,speed*(a+2)));
					worldObj.spawnEntityInWorld(controlledFireballs.get(a));
				}
			}
			else if (timer >= (amt+2)*speed){
				dataWatcher.updateObject(16,currentAttack = ATTACK_NONE);
				timer = 0;
				controlledFireballs.clear();
			}else if (timer >= 2){
				for(EntityProjectileFiendFireball fireball:controlledFireballs){
					if (fireball.timer > 1)fireball.updateCenter(this);
					else if (fireball.timer == 1){
						List<EntityPlayer> players = getNearbyPlayers();
						fireball.shootAt(players.isEmpty() ? null : players.get(rand.nextInt(players.size())));
					}
				}
			}
		}
		else if (currentAttack == ATTACK_FLAMES){
			if (++timer > (isAngry ? 18 : 26)){
				int fireLength = 3+(worldObj.getDifficulty().getDifficultyId()>>1);
				
				for(EntityPlayer player:getNearbyPlayers()){
					player.setFire(fireLength);
					player.attackEntityFrom(new DamageSourceMobUnscaled(this),DamageSourceMobUnscaled.getDamage(ModCommonProxy.opMobs ? 12F : 8F,worldObj.getDifficulty()));
					PacketPipeline.sendToAllAround(player,64D,new C20Effect(FXType.Basic.FIRE_FIEND_FLAME_ATTACK,player));
				}
				
				timer = 0;
				dataWatcher.updateObject(16,currentAttack = ATTACK_NONE);
			}
		}
		
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
	public String getName(){
		return hasCustomName() ? getCustomNameTag() : StatCollector.translateToLocal("entity.fireFiend.name");
	}
	
	@Override
	protected void despawnEntity(){}
}
