package chylex.hee.entity.boss;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEndPortal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import chylex.hee.api.interfaces.IIgnoreEnderGoo;
import chylex.hee.block.BlockList;
import chylex.hee.entity.block.EntityBlockFallingObsidian;
import chylex.hee.entity.boss.dragon.attacks.passive.DragonAttackBite;
import chylex.hee.entity.boss.dragon.attacks.passive.DragonAttackFireball;
import chylex.hee.entity.boss.dragon.attacks.passive.DragonAttackFreezeball;
import chylex.hee.entity.boss.dragon.attacks.passive.DragonPassiveAttackBase;
import chylex.hee.entity.boss.dragon.attacks.special.DragonAttackBitemadness;
import chylex.hee.entity.boss.dragon.attacks.special.DragonAttackBloodlust;
import chylex.hee.entity.boss.dragon.attacks.special.DragonAttackDefault;
import chylex.hee.entity.boss.dragon.attacks.special.DragonAttackDivebomb;
import chylex.hee.entity.boss.dragon.attacks.special.DragonAttackFreezer;
import chylex.hee.entity.boss.dragon.attacks.special.DragonAttackPunch;
import chylex.hee.entity.boss.dragon.attacks.special.DragonAttackStaynfire;
import chylex.hee.entity.boss.dragon.attacks.special.DragonAttackSummon;
import chylex.hee.entity.boss.dragon.attacks.special.DragonAttackWhirlwind;
import chylex.hee.entity.boss.dragon.attacks.special.DragonSpecialAttackBase;
import chylex.hee.entity.boss.dragon.attacks.special.event.CollisionEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.DamageTakenEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.MotionUpdateEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.TargetPositionSetEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.TargetSetEvent;
import chylex.hee.entity.boss.dragon.managers.DragonAchievementManager;
import chylex.hee.entity.boss.dragon.managers.DragonAttackManager;
import chylex.hee.entity.boss.dragon.managers.DragonRewardManager;
import chylex.hee.entity.boss.dragon.managers.DragonShotManager;
import chylex.hee.entity.mob.EntityMobAngryEnderman;
import chylex.hee.entity.weather.EntityWeatherLightningBoltSafe;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C01ParticleEndPortalCreation;
import chylex.hee.packets.client.C06SetPlayerVelocity;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.achievements.AchievementManager;
import chylex.hee.system.commands.DebugBoard;
import chylex.hee.system.commands.HeeDebugCommand;
import chylex.hee.system.savedata.WorldDataHandler;
import chylex.hee.system.savedata.types.DragonSavefile;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.MathUtil;

public class EntityBossDragon extends EntityLiving implements IBossDisplayData, IEntityMultiPart, IMob, IIgnoreEnderGoo{
	public static long lastUpdate;
	
	public double[][] ringBuffer = new double[64][3];
	public int ringBufferIndex = -1;

	public EntityDragonPart[] dragonPartArray;
	public EntityDragonPart dragonPartHead;
	public EntityDragonPart dragonPartBody;
	public EntityDragonPart dragonPartTail1;
	public EntityDragonPart dragonPartTail2;
	public EntityDragonPart dragonPartTail3;
	public EntityDragonPart dragonPartWing1;
	public EntityDragonPart dragonPartWing2;

	public EntityEnderCrystal healingEnderCrystal;

	public float prevAnimTime;
	public float animTime;

	public boolean forceNewTarget;
	public boolean slowed;
	public int deathTicks;
	
	public Entity target;
	public double targetX, targetY, targetZ;
	public boolean angryStatus, doSpecialAttacks, forceAttackEnd, frozen;
	public int spawnCooldown = 140, nextAttackTicks;
	public double moveSpeedMp = 1D;

	public DragonAttackManager attacks;
	public DragonRewardManager rewards;
	public DragonAchievementManager achievements;

	private DragonSpecialAttackBase lastAttack, currentAttack;
	
	private final DragonPassiveAttackBase FIREBALL,FREEZEBALL,BITE;
	private final DragonSpecialAttackBase DEFAULT,DIVEBOMB,STAYNFIRE,BITEMADNESS,PUNCH,FREEZER,WHIRLWIND,SUMMON,BLOODLUST;

	public EntityBossDragon(World world){
		super(world);
		
		dragonPartArray = new EntityDragonPart[]{
			dragonPartHead = new EntityDragonPart(this,"head",6F,6F), dragonPartBody = new EntityDragonPart(this,"body",8F,8F),
			dragonPartTail1 = new EntityDragonPart(this,"tail",4F,4F), dragonPartTail2 = new EntityDragonPart(this,"tail",4F,4F),
			dragonPartTail3 = new EntityDragonPart(this,"tail",4F,4F), dragonPartWing1 = new EntityDragonPart(this,"wing",4F,4F),
			dragonPartWing2 = new EntityDragonPart(this,"wing",4F,4F)
		};
		
		setHealth(getMaxHealth());
		setSize(16F,8F);
		noClip = true;
		isImmuneToFire = true;
		targetY = 100D;
		ignoreFrustumCheck = true;
		renderDistanceWeight = 5D;
		
		attacks = new DragonAttackManager(this);
		rewards = new DragonRewardManager(this);
		achievements = new DragonAchievementManager(this);
		
		FIREBALL = new DragonAttackFireball(this,0);
		FREEZEBALL = new DragonAttackFreezeball(this,1);
		BITE = new DragonAttackBite(this,2);
		
		DEFAULT = new DragonAttackDefault(this,0);
		DIVEBOMB = new DragonAttackDivebomb(this,10).setDisabledPassiveAttacks(FIREBALL);
		STAYNFIRE = new DragonAttackStaynfire(this,4).setDisabledPassiveAttacks(FIREBALL,BITE);
		BITEMADNESS = new DragonAttackBitemadness(this,5).setDisabledPassiveAttacks(BITE);
		PUNCH = new DragonAttackPunch(this,6).setDisabledPassiveAttacks(FIREBALL);
		FREEZER = new DragonAttackFreezer(this,7).setDisabledPassiveAttacks(FIREBALL);
		WHIRLWIND = new DragonAttackWhirlwind(this,8).setDisabledPassiveAttacks(FIREBALL,FREEZEBALL,BITE).setDisabled();
		SUMMON = new DragonAttackSummon(this,9).setDisabledPassiveAttacks(FIREBALL,FREEZEBALL,BITE);
		BLOODLUST = new DragonAttackBloodlust(this,3).setDisabledPassiveAttacks(FIREBALL,FREEZEBALL,BITE);
	}

	@Override
	protected void applyEntityAttributes(){
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(250D+Math.min(50,WorldDataHandler.<DragonSavefile>get(DragonSavefile.class).getDragonDeathAmount()*8)+(ModCommonProxy.opMobs ? 80D : 0D));
	}

	@Override
	protected void entityInit(){
		super.entityInit();
		dataWatcher.addObject(17,Byte.valueOf((byte)0));
		dataWatcher.addObject(18,Byte.valueOf((byte)2));
		dataWatcher.addObject(19,1F);
	}

	/**
	 * Returns a double[3] array with movement offsets, used to calculate trailing tail/neck positions.
	 * [0] = yaw offset, [1] = y offset, [2] = unused, always 0.
	 * Parameters: buffer index offset, partial ticks.
	 */
	public double[] getMovementOffsets(int offset, float partialTickTime){
		if (getHealth() <= 0F)partialTickTime = 0F;
		
		partialTickTime = 1F-partialTickTime;
		int minY = ringBufferIndex-offset*1&63;
		int k = ringBufferIndex-offset*1-1&63;
		double[] adouble = new double[3];
		double d0 = ringBuffer[minY][0];
		double d1 = MathHelper.wrapAngleTo180_double(ringBuffer[k][0]-d0);
		adouble[0] = d0+d1*partialTickTime;
		d0 = ringBuffer[minY][1];
		d1 = ringBuffer[k][1]-d0;
		adouble[1] = d0+d1*partialTickTime;
		adouble[2] = ringBuffer[minY][2]+(ringBuffer[k][2]-ringBuffer[minY][2])*partialTickTime;
		return adouble;
	}

	@Override
	public void onLivingUpdate(){
		if (currentAttack == null)currentAttack = DEFAULT;
		angryStatus = isAngry();

		if (!worldObj.isRemote){
			if (worldObj.difficultySetting.getDifficultyId() != getDifficulty())setDifficulty(worldObj.difficultySetting.getDifficultyId());
			
			if (spawnCooldown == 2){
				for(int chunkX = -6; chunkX <= 6; chunkX++){
					for(int chunkZ = -6; chunkZ <= 6; chunkZ++)worldObj.getChunkFromChunkCoords(chunkX,chunkZ);
				}
			}
			
			if (spawnCooldown <= 4 && !angryStatus && ticksExisted%10 == 0){
				DragonSavefile save = WorldDataHandler.get(DragonSavefile.class);
				if (save.countCrystals() <= 2+save.getDragonDeathAmount() || attacks.getHealthPercentage() < 80)setAngry(true);
			}
			
			if (spawnCooldown > 1)DebugBoard.updateValue("SpawnCooldown",--spawnCooldown);
			DebugBoard.updateValue("HasTarget",target == null ? 0 : 1);
			DebugBoard.updateValue("NextAttackTicks",nextAttackTicks);
			
			if (angryStatus && !doSpecialAttacks){
				if (getWorldDifficulty() > 1 || (getWorldDifficulty() == 1 && attacks.getHealthPercentage() < 60))doSpecialAttacks = true;
			}
			
			currentAttack.update();
			if (doSpecialAttacks){
				DebugBoard.updateValue("AttackId",currentAttack.id);
				if (currentAttack.equals(DEFAULT)){
					if (nextAttackTicks-- <= 0 && target == null){
						lastAttack = currentAttack;
						if ((currentAttack = attacks.pickSpecialAttack(lastAttack)) == null)nextAttackTicks = (currentAttack = DEFAULT).getNextAttackTimer();
						currentAttack.init();
					}
				}
				else if (currentAttack.hasEnded() || forceAttackEnd){
					forceAttackEnd = false;
					currentAttack.end();
					nextAttackTicks = MathUtil.ceil(currentAttack.getNextAttackTimer()*(0.5D+attacks.getHealthPercentage()/200D));
					(currentAttack = DEFAULT).init();
				}
			}

			if (getHealth() > 0){
				rewards.updateManager();
				achievements.updateManager();
				
				double spd = currentAttack.overrideMovementSpeed();
				if (moveSpeedMp > spd)moveSpeedMp = moveSpeedMp < 0.2D && spd == 0D ? 0D : Math.max(spd,moveSpeedMp-0.0175D);
				else if (moveSpeedMp < spd)moveSpeedMp = Math.min(spd,moveSpeedMp+0.0175D);

				float wng = frozen ? HeeDebugCommand.overrideWingSpeed : currentAttack.overrideWingSpeed(), curWng = getWingSpeed();
				if (curWng > wng)curWng = Math.max(wng,curWng-0.015F);
				else if (curWng < wng)curWng = Math.min(wng,curWng+0.015F);
				
				if (curWng != getWingSpeed())setWingSpeed(curWng);
				
				if (ticksExisted%2 == 0){
					int perc = attacks.getHealthPercentage();
					if (perc < 40 && rand.nextInt(500-(50-perc)*8) == 0){
						int x = (int)posX+rand.nextInt(301)-150,z = (int)posZ+rand.nextInt(301)-150;
						int y = 1+DragonUtil.getTopBlock(worldObj,Blocks.end_stone,x,z);
						
						EntityMobAngryEnderman buddy = new EntityMobAngryEnderman(worldObj);
						buddy.setPosition(x,y,z);
						
						worldObj.addWeatherEffect(new EntityWeatherLightningBoltSafe(worldObj,x,y,z));
						worldObj.spawnEntityInWorld(buddy);
					}
					
					lastUpdate = System.currentTimeMillis();
				}
			}
		}

		if (worldObj.isRemote && MathHelper.cos(prevAnimTime*(float)Math.PI*2F) <= -0.3F && MathHelper.cos(animTime*(float)Math.PI*2F) >= -0.3F){
			worldObj.playSound(posX,posY,posZ,"mob.enderdragon.wings",5F,0.8F+rand.nextFloat()*0.3F,false);
		}

		prevAnimTime = animTime;

		if (getHealth() <= 0F){
			worldObj.spawnParticle("largeexplode",posX+(rand.nextFloat()-0.5F)*8F,posY+2D+(rand.nextFloat()-0.5F)*4F,posZ+(rand.nextFloat()-0.5F)*8F,0D,0D,0D);
		}
		else{
			updateDragonEnderCrystal();
			
			float animAdvance = 0.2F/(MathHelper.sqrt_double(motionX*motionX+motionZ*motionZ)*10F+1F);
			animAdvance *= (float)Math.pow(2D,motionY);
			animAdvance *= getWingSpeed();

			animTime += slowed ? animAdvance*0.5F : animAdvance;

			rotationYaw = MathHelper.wrapAngleTo180_float(rotationYaw);

			if (ringBufferIndex < 0){
				for(int minX = 0; minX < ringBuffer.length; ++minX){
					ringBuffer[minX][0] = rotationYaw;
					ringBuffer[minX][1] = posY;
				}
			}

			if (++ringBufferIndex == ringBuffer.length){
				ringBufferIndex = 0;
			}

			ringBuffer[ringBufferIndex][0] = rotationYaw;
			ringBuffer[ringBufferIndex][1] = posY;

			if (worldObj.isRemote){
				if (newPosRotationIncrements > 0){
					double finalPosX = posX+(newPosX-posX)/newPosRotationIncrements,
						   finalPosY = posY+(newPosY-posY)/newPosRotationIncrements,
						   finalPosZ = posZ+(newPosZ-posZ)/newPosRotationIncrements;
					rotationYaw = (float)(rotationYaw+MathHelper.wrapAngleTo180_double(newRotationYaw-rotationYaw)/newPosRotationIncrements);
					rotationPitch = (float)(rotationPitch+(newRotationPitch-rotationPitch)/newPosRotationIncrements);
					--newPosRotationIncrements;
					setPosition(finalPosX,finalPosY,finalPosZ);
					setRotation(rotationYaw,rotationPitch);
				}
			}
			else{
				double xDiff = targetX-posX,
					   yDiff = targetY-posY,
					   zDiff = targetZ-posZ;
				double distFromTarget = xDiff*xDiff+yDiff*yDiff+zDiff*zDiff;

				if (target != null){
					targetX = target.posX;
					targetZ = target.posZ;
					double finalTargetY = 0.4D+Math.sqrt(Math.pow(targetX-posX,2)+Math.pow(targetZ-posZ,2))/80D-1D;

					if (finalTargetY>10D){
						finalTargetY = 10D;
					}

					targetY = target.boundingBox.minY+finalTargetY;
				}
				else trySetTargetPosition(targetX+rand.nextGaussian()*2D,targetY,targetZ+rand.nextGaussian()*2D);

				if ((target != null && target.isDead) || distFromTarget > 22500D)forceAttackEnd = forceNewTarget = true;

				if (forceNewTarget || distFromTarget < 100D || distFromTarget > 22500D || isCollidedHorizontally || isCollidedVertically){
					setNewTarget();
				}

				yDiff /= MathHelper.sqrt_double(xDiff*xDiff+zDiff*zDiff);
				if (yDiff < -0.6F)yDiff = -0.6F;
				if (yDiff > 0.6F)yDiff = 0.6F;

				motionY += yDiff*0.1D;
				rotationYaw = MathHelper.wrapAngleTo180_float(rotationYaw);
				double d8 = 180D-Math.atan2(xDiff,zDiff)*180D/Math.PI;
				double d9 = MathHelper.wrapAngleTo180_double(d8-rotationYaw);

				if (d9 > 50D)d9 = 50D;
				if (d9 < -50D)d9 = -50D;

				Vec3 targetDiffVec = Vec3.createVectorHelper(targetX-posX,targetY-posY,targetZ-posZ).normalize();
				Vec3 rotationVec = Vec3.createVectorHelper(MathHelper.sin(rotationYaw*(float)Math.PI/180F),motionY,(-MathHelper.cos(rotationYaw*(float)Math.PI/180F))).normalize();
				
				float f4 = (float)(rotationVec.dotProduct(targetDiffVec)+0.5D)/1.5F;
				if (f4 < 0F)f4 = 0F;

				randomYawVelocity *= 0.8F;
				float f5 = MathHelper.sqrt_double(motionX*motionX+motionZ*motionZ)+1F;
				double d10 = Math.sqrt(motionX*motionX+motionZ*motionZ)+1D;

				if (d10 > 40D){
					d10 = 40D;
				}

				randomYawVelocity = (float)(randomYawVelocity+d9*(0.7D/d10/f5));
				rotationYaw += randomYawVelocity*0.1F;
				float f6 = (float)(2D/(d10+1D));
				moveFlying(0F,-1F,0.06F*(f4*f6+(1F-f6)));
				
				if (frozen)motionX = motionY = motionZ = 0D;
				
				MotionUpdateEvent event = new MotionUpdateEvent(motionX,motionY,motionZ);
				currentAttack.onMotionUpdateEvent(event);
				motionX = event.motionX;
				motionY = event.motionY;
				motionZ = event.motionZ;

				if (slowed)moveEntity(motionX*moveSpeedMp*0.8D,motionY*moveSpeedMp*0.8D,motionZ*moveSpeedMp*0.8D);
				else moveEntity(motionX*moveSpeedMp,motionY*moveSpeedMp,motionZ*moveSpeedMp);

				Vec3 normalizedMotion = Vec3.createVectorHelper(motionX,motionY,motionZ).normalize();
				double motionMultiplier = 0.8D+0.15D*((normalizedMotion.dotProduct(rotationVec)+1D)/2D);
				motionX *= motionMultiplier;
				motionZ *= motionMultiplier;
				motionY *= 0.91D;
			}

			renderYawOffset = rotationYaw;
			dragonPartHead.width = dragonPartHead.height = 3F;
			dragonPartTail1.width = dragonPartTail1.height = 2F;
			dragonPartTail2.width = dragonPartTail2.height = 2F;
			dragonPartTail3.width = dragonPartTail3.height = 2F;
			dragonPartBody.width = 5F;
			dragonPartBody.height = 3F;
			dragonPartWing1.width = 4F;
			dragonPartWing1.height = 2F;
			dragonPartWing2.width = 4F;
			dragonPartWing2.height = 3F;
			
			float offsetAngle = (float)(getMovementOffsets(5,1F)[1]-getMovementOffsets(10,1F)[1])*10F/180F*(float)Math.PI;
			float angleCos = MathHelper.cos(offsetAngle);
			float angleSin = -MathHelper.sin(offsetAngle);
			float yawRad = MathUtil.toRad(rotationYaw);
			float yawSin = MathHelper.sin(yawRad);
			float yawCos = MathHelper.cos(yawRad);
			dragonPartBody.onUpdate();
			dragonPartBody.setLocationAndAngles(posX+(yawSin*0.5F),posY,posZ-(yawCos*0.5F),0F,0F);
			dragonPartWing1.onUpdate();
			dragonPartWing1.setLocationAndAngles(posX+(yawCos*4.5F),posY+2D,posZ+(yawSin*4.5F),0F,0F);
			dragonPartWing2.onUpdate();
			dragonPartWing2.setLocationAndAngles(posX-(yawCos*4.5F),posY+2D,posZ-(yawSin*4.5F),0F,0F);

			collideWithEntities(worldObj.getEntitiesWithinAABBExcludingEntity(this,dragonPartWing1.boundingBox.expand(3.5D,2D,3.5D).offset(0D,-2D,0D)));
			collideWithEntities(worldObj.getEntitiesWithinAABBExcludingEntity(this,dragonPartWing2.boundingBox.expand(3.5D,2D,3.5D).offset(0D,-2D,0D)));
			collideWithEntities(worldObj.getEntitiesWithinAABBExcludingEntity(this,dragonPartHead.boundingBox.expand(0.7D,1D,0.7D)));

			double[] adouble = getMovementOffsets(5,1F);
			double[] adouble1 = getMovementOffsets(0,1F);
			float moveX = MathHelper.sin(rotationYaw*(float)Math.PI/180F-randomYawVelocity*0.01F);
			float moveZ = MathHelper.cos(rotationYaw*(float)Math.PI/180F-randomYawVelocity*0.01F);
			dragonPartHead.onUpdate();
			dragonPartHead.setLocationAndAngles(posX+(moveX*5.5F*angleCos),posY+(adouble1[1]-adouble[1])+(angleSin*5.5F),posZ-(moveZ*5.5F*angleCos),0F,0F);

			for(int part = 0; part < 3; ++part){
				EntityDragonPart tailPart = part == 0 ? dragonPartTail1 : part == 1 ? dragonPartTail2 : dragonPartTail3;
				
				double[] adouble2 = getMovementOffsets(12+part*2,1F);
				float f14 = MathUtil.toRad(rotationYaw)+MathUtil.toRad((float)MathHelper.wrapAngleTo180_double(adouble2[0]-adouble[0]));
				float f15 = MathHelper.sin(f14);
				float f16 = MathHelper.cos(f14);
				float f17 = 1.5F;
				float f18 = (part+1)*2F;
				tailPart.onUpdate();
				tailPart.setLocationAndAngles(posX-((yawSin*f17+f15*f18)*angleCos),posY+(adouble2[1]-adouble[1])-((f18+f17)*angleSin)+1.5D,posZ+((yawCos*f17+f16*f18)*angleCos),0F,0F);
			}

			if (!worldObj.isRemote){
				slowed = destroyBlocksInAABB(dragonPartHead.boundingBox)|destroyBlocksInAABB(dragonPartBody.boundingBox);
				attacks.updatePassiveAttacks(currentAttack);
				
				if (currentAttack.equals(DIVEBOMB)){
					slowed |= destroyBlocksInAABB(dragonPartWing1.boundingBox)|destroyBlocksInAABB(dragonPartWing2.boundingBox)|destroyBlocksInAABB(dragonPartBody.boundingBox.expand(1D,1D,1D));
				}
			}
		}
	}

	private void updateDragonEnderCrystal(){
		if (healingEnderCrystal != null){
			if (healingEnderCrystal.isDead){
				if (!worldObj.isRemote){
					attackEntityFromPart(dragonPartHead,DamageSource.setExplosionSource(null),10F);
					if (target == null)trySetTarget(attacks.getRandomPlayer());
				}

				healingEnderCrystal = null;
			}
			else if (ticksExisted%10 == 0 && getHealth() < getMaxHealth())setHealth(getHealth()+1F);
		}

		if (rand.nextInt(10) == 0){
			float dist = 30F+4F*worldObj.difficultySetting.getDifficultyId()+(ModCommonProxy.opMobs ? 8F : 0F);
			List<EntityEnderCrystal> crystals = worldObj.getEntitiesWithinAABB(EntityEnderCrystal.class,boundingBox.expand(dist,dist,dist));
			double closestDist = Double.MAX_VALUE, currentDist;
			EntityEnderCrystal closestCrystal = null;
			
			for(EntityEnderCrystal crystal:crystals){
				if ((currentDist = crystal.getDistanceSqToEntity(this)) < closestDist){
					closestDist = currentDist;
					closestCrystal = crystal;
				}
			}

			healingEnderCrystal = closestCrystal;
		}
	}

	private void collideWithEntities(List<? extends Entity> list){
		double bodyCenterX = (dragonPartBody.boundingBox.minX+dragonPartBody.boundingBox.maxX)*0.5D;
		double bodyCenterZ = (dragonPartBody.boundingBox.minZ+dragonPartBody.boundingBox.maxZ)*0.5D;
		
		for(Entity entity:list){
			if (entity instanceof EntityLivingBase || entity instanceof EntityBlockFallingObsidian){
				double[] vec = DragonUtil.getNormalizedVector(entity.posX-bodyCenterX,entity.posZ-bodyCenterZ);
				CollisionEvent event = new CollisionEvent(entity,vec[0]*2D,0.2D,vec[1]*2D);
				currentAttack.onCollisionEvent(event);
				event.collidedEntity.motionX = event.velocityX;
				event.collidedEntity.motionY = event.velocityY;
				event.collidedEntity.motionZ = event.velocityZ;
				
				if (entity instanceof EntityPlayerMP)PacketPipeline.sendToPlayer((EntityPlayerMP)entity,new C06SetPlayerVelocity(event.velocityX,event.velocityY,event.velocityZ));
			}
		}
	}

	private void setNewTarget(){
		forceNewTarget = false;

		if (rand.nextBoolean())trySetTarget(attacks.getWeakPlayer());
		else{
			double newTargetX,newTargetY,newTargetZ;
			
			while(true){
				newTargetX = newTargetZ = 0D;
				newTargetY = (70F+rand.nextFloat()*50F);
				
				newTargetX += (rand.nextFloat()*120F-60F);
				newTargetZ += (rand.nextFloat()*120F-60F);
				if (Math.pow(posX-newTargetX,2)+Math.pow(posY-newTargetY,2)+Math.pow(posZ-newTargetZ,2) > 100D)break;
			}

			trySetTargetPosition(newTargetX,newTargetY,newTargetZ);
		}
	}

	private boolean destroyBlocksInAABB(AxisAlignedBB aabb){
		if (!worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"))return false;

		int minX = MathHelper.floor_double(aabb.minX);
		int minY = MathHelper.floor_double(aabb.minY);
		int minZ = MathHelper.floor_double(aabb.minZ);
		int maxX = MathHelper.floor_double(aabb.maxX);
		int maxY = MathHelper.floor_double(aabb.maxY);
		int maxZ = MathHelper.floor_double(aabb.maxZ);
		boolean wasBlocked = false;
		boolean spawnParticles = false;
		
		minX -= Math.min(3,rand.nextGaussian()*2.5D-0.25D); maxX += Math.min(3,rand.nextGaussian()*2.5D-0.25D);
		minY -= Math.min(3,rand.nextGaussian()*2.5D-0.25D); maxY += Math.min(3,rand.nextGaussian()*2.5D-0.25D);
		minZ -= Math.min(3,rand.nextGaussian()*2.5D-0.25D); maxZ += Math.min(3,rand.nextGaussian()*2.5D-0.25D);
		
		int cx = (int)((aabb.maxX-aabb.minX)*0.5D+aabb.minX),
			cy = (int)((aabb.maxY-aabb.minY)*0.5D+aabb.minY),
			cz = (int)((aabb.maxZ-aabb.minZ)*0.5D+aabb.minZ);
		double rad = 2.8D+Math.min((aabb.maxX-aabb.minX)/2D,(aabb.maxZ-aabb.minZ)/2D);

		for(int xx = minX; xx <= maxX; ++xx){
			for(int yy = minY; yy <= maxY; ++yy){
				for(int zz = minZ; zz <= maxZ; ++zz){
					Block block = worldObj.getBlock(xx,yy,zz);

					if (angryStatus && block == BlockList.obsidian_falling){
						worldObj.setBlockToAir(xx,yy,zz);
						EntityBlockFallingObsidian obsidian = new EntityBlockFallingObsidian(worldObj,xx,yy,zz);
						obsidian.motionY = -0.2;
						worldObj.spawnEntityInWorld(obsidian);
						spawnParticles = true;
					}
					else if (block == Blocks.bedrock || (!angryStatus && (block == Blocks.obsidian || block == BlockList.obsidian_falling || (block == Blocks.iron_bars && worldObj.getBlock(xx,yy-1,zz) == BlockList.obsidian_falling)))){
						wasBlocked = true;
					}
					else if (Math.sqrt(Math.pow(xx-cx,2)+Math.pow(yy-cy,2)+Math.pow(zz-cz,2)) <= rad+0.3D*rand.nextGaussian()){
						spawnParticles = worldObj.setBlockToAir(xx,yy,zz) || spawnParticles;
					}
				}
			}
		}

		if (spawnParticles){
			double partX = aabb.minX+(aabb.maxX-aabb.minX)*rand.nextFloat();
			double partY = aabb.minY+(aabb.maxY-aabb.minY)*rand.nextFloat();
			double partZ = aabb.minZ+(aabb.maxZ-aabb.minZ)*rand.nextFloat();
			worldObj.spawnParticle("largeexplode",partX,partY,partZ,0D,0D,0D);
		}

		return wasBlocked;
	}

	@Override
	public boolean attackEntityFromPart(EntityDragonPart dragonPart, DamageSource source, float amount){
		if (source.isExplosion() && source.getEntity() == this)return false;
		
		if (dragonPart != dragonPartHead)amount = amount/3+1;
		int plam = Math.min(5,MathUtil.floor(worldObj.playerEntities.size()*0.5F))+(ModCommonProxy.opMobs ? 2 : 0);
		if (plam > 1)amount = Math.max(1F,amount/(plam/1.5F));
		
		amount = Math.min(amount,ModCommonProxy.opMobs ? 10F : 13F);
		
		DamageTakenEvent event = new DamageTakenEvent(source,amount);
		currentAttack.onDamageTakenEvent(event);
		currentAttack.onDamageTaken(event.damage);
		amount = event.damage;
		
		boolean shouldChangeTarget = (target != null && getDistanceSqToEntity(target) > 600D);
		if (shouldChangeTarget)trySetTarget(null);

		float yaw = rotationYaw*(float)Math.PI/180F;
		float yawSin = MathHelper.sin(yaw);
		float yawCos = MathHelper.cos(yaw);
		
		if (shouldChangeTarget){
			trySetTargetPosition(posX+(yawSin*5F)+((rand.nextFloat()-0.5F)*2F),
								 posY+(rand.nextFloat()*3F)+1D,
								 posZ-(yawCos*5F)+((rand.nextFloat()-0.5F)*2F));
		}

		if (source.getEntity() instanceof EntityPlayer || source.isExplosion())super.attackEntityFrom(source,amount); 
		return true;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount){
		return false;
	}

	@Override
	protected void onDeathUpdate(){
		++deathTicks;
		
 		if (!worldObj.isRemote){
 			if (deathTicks == 1){
 				PacketPipeline.sendToDimension(dimension,new C01ParticleEndPortalCreation(MathHelper.floor_double(posX),MathHelper.floor_double(posZ)));
 				achievements.onBattleFinished();
 				WorldDataHandler.<DragonSavefile>get(DragonSavefile.class).setDragonDead(true);
				worldObj.playBroadcastSound(1018,(int)posX,(int)posY,(int)posZ,0);
 			}
 			else if (deathTicks == 20 || deathTicks == 140){ // double check
 				List<Entity> entities = worldObj.loadedEntityList;
 				
 				for(Entity entity:entities){
 					if (MathUtil.distance(entity.posX,entity.posZ) > 180D)continue;
 					
 					if (entity instanceof EntityEnderman)((EntityEnderman)entity).setTarget(null);
 					else if (entity instanceof EntityMobAngryEnderman)((EntityMobAngryEnderman)entity).setHealth(0F);
 				}
 			}
 			else if (deathTicks > 4 && deathTicks < 70 && deathTicks%4 == 0){
 				for(int a = 0, xx, yy, zz; a < 250; a++){
 					xx = MathUtil.floor(posX)+rand.nextInt(51)-25;
 					zz = MathUtil.floor(posZ)+rand.nextInt(51)-25;
 					yy = DragonUtil.getTopBlock(worldObj,Blocks.end_stone,xx,zz,65);
 					
 					if (yy > 40 && worldObj.getBlock(xx,yy,zz) == Blocks.fire)worldObj.setBlockToAir(xx,yy,zz);
 				}
 			}
 			else if (deathTicks > 150 && deathTicks%5 == 0)DragonUtil.spawnXP(this,550+(250*(rewards.getFinalDifficulty()>>2)));
 			else if (deathTicks == 191){
 				for(EntityPlayer player:(List<EntityPlayer>)worldObj.playerEntities)player.addStat(AchievementManager.TIME_FOR_NEW_ADVENTURES,1);
 			}
 			else if (deathTicks == 200)DragonUtil.spawnXP(this,4000);
 			
 			if (deathTicks > 40 && deathTicks < 140)rewards.spawnEssence(worldObj,(int)posX,(int)posZ);
		}
 		else if (deathTicks > 20){
 			int amount = 1+deathTicks/40, xx = DragonUtil.portalEffectX, zz = DragonUtil.portalEffectZ;
 			byte bottomY = 64, portalSize = 4;

 			for(int iy = bottomY-1; iy <= bottomY+32; ++iy){
 				for(int ix = xx-portalSize; ix <= xx+portalSize; ++ix){
 					for(int iz = zz-portalSize; iz <= zz+portalSize; ++iz){
 						double len = MathUtil.square(ix-xx)+MathUtil.square(iz-zz);
 						
 						if (len <= (portalSize-0.5D)*(portalSize-0.5D)){
 							if ((iy < bottomY && len <= ((portalSize-1)-0.5D)*((portalSize-1)-0.5D)) || iy > bottomY)continue;
 							for(int a = 0; a < rand.nextInt(amount); a++)worldObj.spawnParticle("portal",ix+rand.nextDouble(),iy+rand.nextDouble()-0.5D,iz+rand.nextDouble(),0D,0D,0D);
 						}
 					}
 				}
 			}
 			
 			for(int minX = 2; minX < 5; minX++){
 				for(int a = 0; a < rand.nextInt(amount); a++)worldObj.spawnParticle("portal",xx+rand.nextDouble(),bottomY+a+rand.nextDouble()-0.5D,zz+rand.nextDouble(),0D,0D,0D);
 			}
 		}

		if (deathTicks >= 180 && deathTicks <= 200){
			worldObj.spawnParticle("hugeexplosion",posX+(rand.nextFloat()-0.5F)*8F,posY+2D+(rand.nextFloat()-0.5F)*4F,posZ+(rand.nextFloat()-0.5F)*8F,0D,0D,0D);
		}

		moveEntity(0D,0.1D,0D);
		renderYawOffset = rotationYaw += 20F;

		if (deathTicks == 200 && !worldObj.isRemote){
			int xp = 2000, tmpSplit;

			while(xp > 0){
				tmpSplit = EntityXPOrb.getXPSplit(xp);
				xp -= tmpSplit;
				worldObj.spawnEntityInWorld(new EntityXPOrb(worldObj,posX,posY,posZ,tmpSplit));
			}

			createEnderPortal(MathHelper.floor_double(posX),MathHelper.floor_double(posZ));
			setDead();
		}
	}

	private void createEnderPortal(int x, int z){
		BlockEndPortal.field_149948_a = true;
		byte portalSize = 4, bottomY = 64;

		for(int yy = bottomY-1; yy <= bottomY+32; ++yy){
			for(int xx = x-portalSize; xx <= x+portalSize; ++xx){
				for(int zz = z-portalSize; zz <= z+portalSize; ++zz){
					double dist = MathUtil.square(xx-x)+MathUtil.square(zz-z);

					if (dist <= (portalSize-0.5D)*(portalSize-0.5D)){
						if (yy < bottomY){
							if (dist <= ((portalSize-1)-0.5D)*((portalSize-1)-0.5D)){
								worldObj.setBlock(xx,yy,zz,Blocks.bedrock);
							}
						}
						else if (yy > bottomY)worldObj.setBlockToAir(xx,yy,zz);
						else if (dist > ((portalSize-1)-0.5D)*((portalSize-1)-0.5D))worldObj.setBlock(xx,yy,zz,Blocks.bedrock);
						else worldObj.setBlock(xx,yy,zz,Blocks.end_portal);
					}
				}
			}
		}

		worldObj.setBlock(x,bottomY,z,Blocks.bedrock);
		worldObj.setBlock(x,bottomY+1,z,Blocks.bedrock);
		worldObj.setBlock(x,bottomY+2,z,Blocks.bedrock);
		worldObj.setBlock(x-1,bottomY+2,z,Blocks.torch);
		worldObj.setBlock(x+1,bottomY+2,z,Blocks.torch);
		worldObj.setBlock(x,bottomY+2,z-1,Blocks.torch);
		worldObj.setBlock(x,bottomY+2,z+1,Blocks.torch);
		worldObj.setBlock(x,bottomY+3,z,Blocks.bedrock);
		worldObj.setBlock(x,bottomY+4,z,Blocks.dragon_egg);
		BlockEndPortal.field_149948_a = false;
		
		WorldDataHandler.<DragonSavefile>get(DragonSavefile.class).getPortalEggLocation().set(x,bottomY+4,z);
	}
	
	public void trySetTarget(Entity entity){
		if (entity != null && entity.isDead && (DragonAttackManager.nocreative && entity instanceof EntityPlayer && ((EntityPlayer)entity).capabilities.isCreativeMode))return;
		forceNewTarget = false;
		
		TargetSetEvent event = new TargetSetEvent(target,entity);
		currentAttack.onTargetSetEvent(event);
		target = event.newTarget;
	}
	
	public void trySetTargetPosition(double newTargetX, double newTargetY, double newTargetZ){
		TargetPositionSetEvent event = new TargetPositionSetEvent(target,targetX,targetY,targetZ,newTargetX,newTargetY,newTargetZ);
		currentAttack.onTargetPositionSetEvent(event);
		if (event.isCancelled() && event.currentEntityTarget != null)target = event.currentEntityTarget;
		else{
			targetX = event.newTargetX;
			targetY = event.newTargetY;
			targetZ = event.newTargetZ;
			target = null;
		}
	}
	
	public void doFatalityAttack(Entity entity){
		if (entity.isDead)return;
		forceNewTarget = false;
		target = entity;
	}
	
	public void forceSpecialAttack(DragonSpecialAttackBase newAttack){
		lastAttack = currentAttack;
		if (currentAttack != null)currentAttack.end();
		currentAttack = newAttack;
		currentAttack.init();
	}

	public DragonShotManager initShot(){
		return new DragonShotManager(this);
	}

	public String getEntityNameForStatus(){
		if (angryStatus){
			switch(getDifficulty()){
				case 3: return "entity.dragon.doom.name";
				case 2: return "entity.dragon.angry.name";
				case 1: return "entity.dragon.baby.name";
				default: return "entity.dragon.derpy.name";
			}
		}
		else{
			switch(getDifficulty()){
				case 1: return "entity.dragon.baby.name";
				case 0: return "entity.dragon.derpy.name";
				default: return "entity.EnderDragon.name";
			}
		}
	}
	
	@Override
	public String getCommandSenderName(){
		return StatCollector.translateToLocal("entity.EnderDragon.name");
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt){
		super.writeEntityToNBT(nbt);
		nbt.setBoolean("angry",isAngry());
		nbt.setShort("nat",(short)Math.max(120,nextAttackTicks));
		nbt.setShort("dth",(short)deathTicks);

		nbt.setTag("att",attacks.writeToNBT());
		nbt.setTag("rwr",rewards.writeToNBT());
		nbt.setTag("acv",achievements.writeToNBT());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		 
		setAngry(nbt.getBoolean("angry"));
		nextAttackTicks = nbt.getShort("nat");
		deathTicks = nbt.getShort("dth");
		spawnCooldown = nbt.hasKey("nat") ? 50 : 140;
		
		attacks.readFromNBT(nbt.getCompoundTag("att"));
		rewards.readFromNBT(nbt.getCompoundTag("rwr"));
		achievements.readFromNBT(nbt.getCompoundTag("acv"));
	}

	public void setAngry(boolean angry){
		dataWatcher.updateObject(17,Byte.valueOf(angry ? (byte)1 : (byte)0));
	}

	public boolean isAngry(){
		return (dataWatcher.getWatchableObjectByte(17)&1) != 0;
	}

	public void setDifficulty(int diff){
		dataWatcher.updateObject(18,(byte)diff);
	}

	public int getDifficulty(){
		return dataWatcher.getWatchableObjectByte(18);
	}

	public int getWorldDifficulty(){
		return worldObj.isRemote ? getDifficulty() : worldObj.difficultySetting.getDifficultyId();
	}

	public void setWingSpeed(float wingSpeed){
		dataWatcher.updateObject(19,wingSpeed);
	}

	public float getWingSpeed(){
		return dataWatcher.getWatchableObjectFloat(19);
	}

	@Override
	protected void despawnEntity(){}

	@Override
	public Entity[] getParts(){
		return dragonPartArray;
	}

	@Override
	public boolean canBeCollidedWith(){
		return false;
	}

	@Override
	public World func_82194_d(){ // OBFUSCATED get world obj
		return worldObj;
	}

	@Override
	protected String getLivingSound(){
		return "mob.enderdragon.growl";
	}

	@Override
	protected String getHurtSound(){
		return "mob.enderdragon.hit";
	}

	@Override
	protected float getSoundVolume(){
		return angryStatus ? 6.5F : 5F;
	}
}
