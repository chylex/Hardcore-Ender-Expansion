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
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import chylex.hee.block.BlockList;
import chylex.hee.entity.GlobalMobData.IIgnoreEnderGoo;
import chylex.hee.entity.block.EntityBlockFallingObsidian;
import chylex.hee.entity.boss.dragon.attacks.passive.DragonAttackBite;
import chylex.hee.entity.boss.dragon.attacks.passive.DragonAttackFireball;
import chylex.hee.entity.boss.dragon.attacks.special.DragonAttackBloodlust;
import chylex.hee.entity.boss.dragon.attacks.special.DragonAttackDefault;
import chylex.hee.entity.boss.dragon.attacks.special.DragonAttackDivebomb;
import chylex.hee.entity.boss.dragon.attacks.special.DragonAttackFireburst;
import chylex.hee.entity.boss.dragon.attacks.special.DragonAttackPunch;
import chylex.hee.entity.boss.dragon.attacks.special.DragonAttackSummoning;
import chylex.hee.entity.boss.dragon.attacks.special.DragonSpecialAttackBase;
import chylex.hee.entity.boss.dragon.attacks.special.event.CollisionEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.DamageTakenEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.MotionUpdateEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.TargetPositionSetEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.TargetSetEvent;
import chylex.hee.entity.boss.dragon.managers.DragonAchievementManager;
import chylex.hee.entity.boss.dragon.managers.DragonAttackManager;
import chylex.hee.entity.boss.dragon.managers.DragonChunkManager;
import chylex.hee.entity.boss.dragon.managers.DragonDebugManager;
import chylex.hee.entity.boss.dragon.managers.DragonRewardManager;
import chylex.hee.entity.boss.dragon.managers.DragonShotManager;
import chylex.hee.entity.mob.EntityMobAngryEnderman;
import chylex.hee.entity.weather.EntityWeatherLightningBoltSafe;
import chylex.hee.mechanics.causatum.CausatumMeters;
import chylex.hee.mechanics.causatum.CausatumUtils;
import chylex.hee.mechanics.misc.Baconizer;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C01ParticleEndPortalCreation;
import chylex.hee.packets.client.C06SetPlayerVelocity;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.achievements.AchievementManager;
import chylex.hee.system.commands.DebugBoard;
import chylex.hee.system.commands.HeeDebugCommand;
import chylex.hee.system.logging.Log;
import chylex.hee.system.savedata.WorldDataHandler;
import chylex.hee.system.savedata.types.DragonSavefile;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.MathUtil;

public class EntityBossDragon extends EntityLiving implements IBossDisplayData, IEntityMultiPart, IMob, IIgnoreEnderGoo{
	public static final byte ATTACK_FIREBALL = 0, ATTACK_BITE = 1;
	public static long lastUpdate;
	
	private double[][] movementBuffer = new double[64][2];
	private int movementBufferIndex = -1;

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
	public boolean angryStatus, forceAttackEnd, noPlayers, frozen;
	public int nextAttackTicks;
	public byte dragonHurtTime;
	
	public int spawnCooldown = 1200, lastAttackInterruption = -600;
	public byte loadTimer = 10;
	public double moveSpeedMp = 1D;

	public final DragonAttackManager attacks;
	public final DragonShotManager shots;
	public final DragonRewardManager rewards;
	public final DragonAchievementManager achievements;
	
	private final DragonSpecialAttackBase defaultAttack;
	private DragonSpecialAttackBase lastAttack, currentAttack;

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
		shots = new DragonShotManager(this);
		rewards = new DragonRewardManager(this);
		achievements = new DragonAchievementManager(this);
		
		attacks.registerPassive(new DragonAttackFireball(this,ATTACK_FIREBALL));
		attacks.registerPassive(new DragonAttackBite(this,ATTACK_BITE));
		
		attacks.registerSpecial(defaultAttack = new DragonAttackDefault(this,0));
		attacks.registerSpecial(new DragonAttackDivebomb(this,1,10).setDisabledPassiveAttacks(ATTACK_FIREBALL));
		attacks.registerSpecial(new DragonAttackFireburst(this,2,10).setDisabledPassiveAttacks(ATTACK_FIREBALL,ATTACK_BITE));
		attacks.registerSpecial(new DragonAttackPunch(this,3,10).setDisabledPassiveAttacks(ATTACK_FIREBALL));
		attacks.registerSpecial(new DragonAttackSummoning(this,4,7).setDisabledPassiveAttacks(ATTACK_FIREBALL,ATTACK_BITE));
		attacks.registerSpecial(new DragonAttackBloodlust(this,5,7).setDisabledPassiveAttacks(ATTACK_FIREBALL,ATTACK_BITE));
	}

	@Override
	protected void applyEntityAttributes(){
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(250D+Math.min(90,WorldDataHandler.<DragonSavefile>get(DragonSavefile.class).getDragonDeathAmount()*15)+(ModCommonProxy.opMobs ? 80D : 0D));
	}

	@Override
	protected void entityInit(){
		super.entityInit();
		dataWatcher.addObject(17,Byte.valueOf((byte)0));
		dataWatcher.addObject(18,1F);
	}

	@Override
	public void onLivingUpdate(){
		if (noPlayers){
			if (ticksExisted%10 == 0 && !attacks.getViablePlayers().isEmpty())noPlayers = false;
			else return;
		}
		else if (ticksExisted%40 == 0 && attacks.getViablePlayers().isEmpty()){
			noPlayers = true;
			DragonChunkManager.release(this);
			return;
		}
		
		if (currentAttack == null)currentAttack = defaultAttack;
		angryStatus = isAngry();

		if (!worldObj.isRemote){
			if (spawnCooldown > 0 && --spawnCooldown > 0 && ticksExisted%20 == 0){
				for(EntityPlayer player:attacks.getViablePlayers()){
					if (worldObj.getBlock(MathUtil.floor(player.posX),MathUtil.floor(player.posY)-1,MathUtil.floor(player.posZ)) == Blocks.end_stone){
						spawnCooldown = 0;
						break;
					}
				}
			}
			
			if (loadTimer > 0 && --loadTimer == 1){
				for(int chunkX = -6; chunkX <= 6; chunkX++){
					for(int chunkZ = -6; chunkZ <= 6; chunkZ++)worldObj.getChunkFromChunkCoords(chunkX,chunkZ);
				}
			}
			
			if (loadTimer == 0 && !angryStatus && ticksExisted%10 == 0){
				DragonSavefile save = WorldDataHandler.get(DragonSavefile.class);
				
				if (save.countCrystals() <= 2+save.getDragonDeathAmount() || attacks.getHealthPercentage() <= 80){
					setAngry(true);
					spawnCooldown = 0;
				}
			}
			
			currentAttack.update();
			
			if (angryStatus){
				DebugBoard.updateValue("AttackId",currentAttack.id);
				
				if (currentAttack.equals(defaultAttack)){
					if (nextAttackTicks-- <= 0 && target == null){
						lastAttack = currentAttack;
						if ((currentAttack = attacks.pickSpecialAttack(lastAttack)) == null)nextAttackTicks = (currentAttack = defaultAttack).getNextAttackTimer();
						currentAttack.init();
					}
				}
				else if (currentAttack.hasEnded() || forceAttackEnd){
					forceAttackEnd = false;
					currentAttack.end();
					nextAttackTicks = MathUtil.ceil(currentAttack.getNextAttackTimer()*(0.5D+attacks.getHealthPercentage()/200D));
					(currentAttack = defaultAttack).init();
				}
			}

			if (getHealth() > 0){
				rewards.updateManager();
				achievements.updateManager();
				DragonChunkManager.ping(this);
				
				if (dragonHurtTime > 0)--dragonHurtTime;
				
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
						int x = (int)posX+rand.nextInt(301)-150, z = (int)posZ+rand.nextInt(301)-150;
						int y = 1+DragonUtil.getTopBlockY(worldObj,Blocks.end_stone,x,z);
						
						EntityMobAngryEnderman buddy = new EntityMobAngryEnderman(worldObj);
						buddy.setPosition(x,y,z);
						
						worldObj.addWeatherEffect(new EntityWeatherLightningBoltSafe(worldObj,x,y,z));
						worldObj.spawnEntityInWorld(buddy);
					}
					
					lastUpdate = worldObj.getTotalWorldTime();
				}
			}
			
			if (Log.isDebugEnabled())DragonDebugManager.updateBoard(this);
		}

		if (worldObj.isRemote && MathHelper.cos(prevAnimTime*(float)Math.PI*2F) <= -0.3F && MathHelper.cos(animTime*(float)Math.PI*2F) >= -0.3F){
			worldObj.playSound(posX,posY,posZ,"mob.enderdragon.wings",5F,0.8F+rand.nextFloat()*0.3F,false);
		}

		prevAnimTime = animTime;

		if (getHealth() <= 0F)worldObj.spawnParticle("largeexplode",posX+(rand.nextFloat()-0.5F)*8F,posY+2D+(rand.nextFloat()-0.5F)*4F,posZ+(rand.nextFloat()-0.5F)*8F,0D,0D,0D);
		else{
			updateEnderCrystal();
			
			float animAdvance = 0.2F/(MathHelper.sqrt_double(motionX*motionX+motionZ*motionZ)*10F+1F);
			animAdvance *= (float)Math.pow(2D,motionY);
			animAdvance *= getWingSpeed();

			animTime += slowed ? animAdvance*0.5F : animAdvance;

			rotationYaw = MathHelper.wrapAngleTo180_float(rotationYaw);

			if (movementBufferIndex < 0){
				for(int index = 0; index < movementBuffer.length; ++index){
					movementBuffer[index][0] = rotationYaw;
					movementBuffer[index][1] = posY;
				}
			}

			if (++movementBufferIndex == movementBuffer.length)movementBufferIndex = 0;

			movementBuffer[movementBufferIndex][0] = rotationYaw;
			movementBuffer[movementBufferIndex][1] = posY;

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
				double xDiff = targetX-posX, yDiff = targetY-posY, zDiff = targetZ-posZ;
				double distFromTargetSq = xDiff*xDiff+yDiff*yDiff+zDiff*zDiff;

				if (target != null){
					targetX = target.posX;
					targetZ = target.posZ;
					targetY = target.boundingBox.minY+Math.min(0.4D+Math.sqrt(Math.pow(targetX-posX,2)+Math.pow(targetZ-posZ,2))/80D-1D,10D);
				}
				else trySetTargetPosition(targetX+rand.nextGaussian()*2D,targetY,targetZ+rand.nextGaussian()*2D);

				if ((target != null && target.isDead) || distFromTargetSq > 22500D)forceAttackEnd = forceNewTarget = true;

				if (forceNewTarget || distFromTargetSq < 90D || distFromTargetSq > 22500D || isCollidedHorizontally || isCollidedVertically){
					setNewTarget();
				}

				yDiff = MathUtil.clamp(yDiff/MathUtil.distance(xDiff,zDiff),-0.6F,0.6F);

				motionY += yDiff*0.1D;
				rotationYaw = MathHelper.wrapAngleTo180_float(rotationYaw);
				double d9 = MathUtil.clamp(MathHelper.wrapAngleTo180_double(180D-MathUtil.toDeg(Math.atan2(xDiff,zDiff))-rotationYaw),-50D,50D);

				Vec3 targetDiffVec = Vec3.createVectorHelper(targetX-posX,targetY-posY,targetZ-posZ).normalize();
				Vec3 rotationVec = Vec3.createVectorHelper(MathHelper.sin(MathUtil.toRad(rotationYaw)),motionY,(-MathHelper.cos(MathUtil.toRad(rotationYaw)))).normalize();
				
				float f4 = Math.max((float)(rotationVec.dotProduct(targetDiffVec)+0.5D)/1.5F,0F);

				randomYawVelocity *= 0.8F;
				float speed = MathHelper.sqrt_double(motionX*motionX+motionZ*motionZ)+1F;
				double speedLimited = Math.min(Math.sqrt(motionX*motionX+motionZ*motionZ)+1D,40D);

				randomYawVelocity = (float)(randomYawVelocity+d9*(0.7D/speedLimited/speed));
				rotationYaw += randomYawVelocity*0.1F;
				float f6 = (float)(2D/(speedLimited+1D));
				moveFlying(0F,-1F,0.06F*(f4*f6+(1F-f6)));
				
				if (frozen)motionX = motionY = motionZ = 0D;
				
				MotionUpdateEvent event = new MotionUpdateEvent(motionX,motionY,motionZ);
				currentAttack.onMotionUpdateEvent(event);
				motionX = event.motionX;
				motionY = event.motionY;
				motionZ = event.motionZ;

				if (slowed)moveEntity(motionX*moveSpeedMp*0.8D,motionY*moveSpeedMp*0.8D,motionZ*moveSpeedMp*0.8D);
				else moveEntity(motionX*moveSpeedMp,motionY*moveSpeedMp,motionZ*moveSpeedMp);

				double motionMultiplier = 0.8D+0.15D*((Vec3.createVectorHelper(motionX,motionY,motionZ).normalize().dotProduct(rotationVec)+1D)*0.5D);
				motionX *= motionMultiplier;
				motionZ *= motionMultiplier;
				motionY *= 0.91D;
			}

			renderYawOffset = rotationYaw;
			dragonPartHead.width = dragonPartHead.height = 3F;
			dragonPartTail1.width = dragonPartTail1.height = 2F;
			dragonPartTail2.width = dragonPartTail2.height = 2F;
			dragonPartTail3.width = dragonPartTail3.height = 2F;
			dragonPartBody.width = 5F; dragonPartBody.height = 3F;
			dragonPartWing1.width = 4F; dragonPartWing1.height = 2F;
			dragonPartWing2.width = 4F; dragonPartWing2.height = 3F;
			
			float offsetAngle = MathUtil.toRad((float)(getMovementOffsets(5,1F)[1]-getMovementOffsets(10,1F)[1])*10F);
			float angleCos = MathHelper.cos(offsetAngle);
			float angleSin = -MathHelper.sin(offsetAngle);
			float yawRad = MathUtil.toRad(rotationYaw);
			float yawSin = MathHelper.sin(yawRad);
			float yawCos = MathHelper.cos(yawRad);
			dragonPartBody.onUpdate();
			dragonPartBody.setLocationAndAngles(posX+yawSin*0.5F,posY,posZ-yawCos*0.5F,0F,0F);
			dragonPartWing1.onUpdate();
			dragonPartWing1.setLocationAndAngles(posX+yawCos*4.5F,posY+2D,posZ+yawSin*4.5F,0F,0F);
			dragonPartWing2.onUpdate();
			dragonPartWing2.setLocationAndAngles(posX-yawCos*4.5F,posY+2D,posZ-yawSin*4.5F,0F,0F);
			
			collideWithEntities(worldObj.getEntitiesWithinAABBExcludingEntity(this,dragonPartWing1.boundingBox.expand(1.5D,2D,1.5D).offset(0D,-2D,0D)));
			collideWithEntities(worldObj.getEntitiesWithinAABBExcludingEntity(this,dragonPartWing2.boundingBox.expand(1.5D,2D,1.5D).offset(0D,-2D,0D)));
			collideWithEntities(worldObj.getEntitiesWithinAABBExcludingEntity(this,dragonPartTail3.boundingBox.expand(0.8D,1D,0.8D)));
			collideWithEntities(worldObj.getEntitiesWithinAABBExcludingEntity(this,dragonPartHead.boundingBox.expand(0.6D,1D,0.6D)));

			double[] oldOffsets = getMovementOffsets(5,1F), currentOffsets = getMovementOffsets(0,1F);
			float moveX = MathHelper.sin(MathUtil.toRad(rotationYaw)-randomYawVelocity*0.01F);
			float moveZ = MathHelper.cos(MathUtil.toRad(rotationYaw)-randomYawVelocity*0.01F);
			dragonPartHead.onUpdate();
			dragonPartHead.setLocationAndAngles(posX+moveX*5.5F*angleCos,posY+currentOffsets[1]-oldOffsets[1]+angleSin*5.5F,posZ-moveZ*5.5F*angleCos,0F,0F);

			for(int part = 0; part < 3; part++){
				EntityDragonPart tailPart = part == 0 ? dragonPartTail1 : part == 1 ? dragonPartTail2 : dragonPartTail3;
				
				double[] partOffsets = getMovementOffsets(12+part*2,1F);
				float partYaw = MathUtil.toRad(rotationYaw)+MathUtil.toRad((float)MathHelper.wrapAngleTo180_double(partOffsets[0]-oldOffsets[0]));
				float partYawSin = MathHelper.sin(partYaw);
				float partYawCos = MathHelper.cos(partYaw);
				float partMp = (part+1)*2F;
				tailPart.onUpdate();
				tailPart.setLocationAndAngles(posX-((yawSin*1.5F+partYawSin*partMp)*angleCos),posY+(partOffsets[1]-oldOffsets[1])-((partMp+1.5F)*angleSin)+1.5D,posZ+((yawCos*1.5F+partYawCos*partMp)*angleCos),0F,0F);
			}

			if (!worldObj.isRemote){
				slowed = destroyBlocksInAABB(dragonPartHead.boundingBox)|destroyBlocksInAABB(dragonPartBody.boundingBox);
				if (currentAttack.id == 1)slowed |= destroyBlocksInAABB(dragonPartWing1.boundingBox.expand(0.5D,0.5D,0.5D))|destroyBlocksInAABB(dragonPartWing2.boundingBox.expand(0.5D,0.5D,0.5D));
				
				attacks.updatePassiveAttacks(currentAttack);
			}
		}
	}

	@Override
	public boolean attackEntityFromPart(EntityDragonPart dragonPart, DamageSource source, float amount){
		if ((source.isExplosion() && source.getEntity() == this) || dragonHurtTime > 0)return false;
		spawnCooldown = 0;
		
		if (dragonPart != dragonPartHead)amount = amount/3+1;
		amount = Math.min(amount,ModCommonProxy.opMobs ? 14F : 18F);
		
		int players = attacks.getViablePlayers().size();
		if (players > 1)amount = amount*(1F-Math.max(0.5F,(players-1)*0.05F));
		
		DamageTakenEvent event = new DamageTakenEvent(source,amount);
		currentAttack.onDamageTakenEvent(event);
		currentAttack.onDamageTaken(event.damage);
		amount = event.damage;
		
		boolean shouldChangeTarget = (target != null && getDistanceSqToEntity(target) < 4600D && !angryStatus);
		
		if (shouldChangeTarget && ticksExisted-lastAttackInterruption >= 600){
			trySetTarget(null);
			lastAttackInterruption = ticksExisted;
		}

		if (shouldChangeTarget){
			float yawRad = MathUtil.toRad(rotationYaw);
			
			trySetTargetPosition(posX+(MathHelper.sin(yawRad)*5F)+((rand.nextFloat()-0.5F)*2F),
								 posY+(rand.nextFloat()*3F)+1D,
								 posZ-(MathHelper.cos(yawRad)*5F)+((rand.nextFloat()-0.5F)*2F));
		}

		if ((source.getEntity() instanceof EntityPlayer || source.isExplosion()) && super.attackEntityFrom(source,amount))hurtResistantTime = (dragonHurtTime = (byte)(hurtTime = 15))+10;
		CausatumUtils.increase(source,CausatumMeters.DRAGON_DAMAGE,amount*50F);
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
 				PacketPipeline.sendToDimension(dimension,new C01ParticleEndPortalCreation(MathUtil.floor(posX),MathUtil.floor(posZ)));
 				achievements.onBattleFinished();
 				WorldDataHandler.<DragonSavefile>get(DragonSavefile.class).setDragonDead(true);
				worldObj.playBroadcastSound(1018,(int)posX,(int)posY,(int)posZ,0);
 			}
 			else if (deathTicks == 20 || deathTicks == 140){ // double check
 				for(Entity entity:(List<Entity>)worldObj.loadedEntityList){
 					if (MathUtil.distance(entity.posX,entity.posZ) > 180D)continue;
 					
 					if (entity instanceof EntityEnderman)((EntityEnderman)entity).setTarget(null);
 					else if (entity instanceof EntityMobAngryEnderman)((EntityMobAngryEnderman)entity).setHealth(0F);
 				}
 			}
 			else if (deathTicks > 4 && deathTicks < 70 && deathTicks%4 == 0){
 				for(int a = 0, xx, yy, zz; a < 250; a++){
 					xx = MathUtil.floor(posX)+rand.nextInt(51)-25;
 					zz = MathUtil.floor(posZ)+rand.nextInt(51)-25;
 					yy = 1+DragonUtil.getTopBlockY(worldObj,Blocks.end_stone,xx,zz,65);
 					
 					if (yy > 40 && worldObj.getBlock(xx,yy,zz) == Blocks.fire)worldObj.setBlockToAir(xx,yy,zz);
 				}
 			}
 			else if (deathTicks > 150 && deathTicks%5 == 0)DragonUtil.spawnXP(this,550+(250*(rewards.getFinalDifficulty()>>2)));
 			else if (deathTicks == 191){
 				for(EntityPlayer player:(List<EntityPlayer>)worldObj.playerEntities)player.addStat(AchievementManager.GO_INTO_THE_END,1);
 			}
 			else if (deathTicks == 200)DragonUtil.spawnXP(this,4000);
 			
 			if (deathTicks > 40 && deathTicks < 140)rewards.spawnEssence(worldObj,(int)posX,(int)posZ);
		}
 		else if (deathTicks > 20 && worldObj.isRemote){
 			int amount = 1+deathTicks/40, xx = DragonUtil.portalEffectX, zz = DragonUtil.portalEffectZ;
 			byte bottomY = 64, portalSize = 4;

 			for(int iy = bottomY-1; iy <= bottomY+32; ++iy){
 				for(int ix = xx-portalSize; ix <= xx+portalSize; ix++){
 					for(int iz = zz-portalSize; iz <= zz+portalSize; iz++){
 						double distSq = MathUtil.square(ix-xx)+MathUtil.square(iz-zz);
 						
 						if (distSq <= (portalSize-0.5D)*(portalSize-0.5D)){
 							if ((iy < bottomY && distSq <= ((portalSize-1)-0.5D)*((portalSize-1)-0.5D)) || iy > bottomY)continue;
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
			DragonUtil.spawnXP(this,2000);
			createEnderPortal(MathUtil.floor(posX),MathUtil.floor(posZ));
			DragonChunkManager.release(this);
			setDead();
		}
	}

	private void createEnderPortal(int x, int z){
		BlockEndPortal.field_149948_a = true;
		byte portalSize = 4, bottomY = 64;

		for(int yy = bottomY-1; yy <= bottomY+32; yy++){
			for(int xx = x-portalSize; xx <= x+portalSize; xx++){
				for(int zz = z-portalSize; zz <= z+portalSize; zz++){
					double distSq = MathUtil.square(xx-x)+MathUtil.square(zz-z);

					if (distSq <= (portalSize-0.5D)*(portalSize-0.5D)){
						if (yy < bottomY && distSq <= MathUtil.square((portalSize-1)-0.5D))worldObj.setBlock(xx,yy,zz,Blocks.bedrock);
						else if (yy > bottomY)worldObj.setBlockToAir(xx,yy,zz);
						else if (distSq > MathUtil.square((portalSize-1)-0.5D))worldObj.setBlock(xx,yy,zz,Blocks.bedrock);
						else worldObj.setBlock(xx,yy,zz,Blocks.end_portal);
					}
				}
			}
		}
		
		for(int yy = bottomY; yy <= bottomY+3; yy++)worldObj.setBlock(x,yy,z,Blocks.bedrock);
		for(int dir = 0; dir < 4; dir++)worldObj.setBlock(x+Direction.offsetX[dir],bottomY+2,z+Direction.offsetZ[dir],Blocks.torch);
		worldObj.setBlock(x,bottomY+4,z,Blocks.dragon_egg);
		
		BlockEndPortal.field_149948_a = false;
		WorldDataHandler.<DragonSavefile>get(DragonSavefile.class).getPortalEggLocation().set(x,bottomY+4,z);
	}

	public double[] getMovementOffsets(int offset, float partialTickTime){
		partialTickTime = getHealth() <= 0F ? 0F : 1F-partialTickTime;
		int index = movementBufferIndex-offset&63, prevIndex = movementBufferIndex-offset-1&63;
		
		return new double[]{
			movementBuffer[index][0]+MathHelper.wrapAngleTo180_double(movementBuffer[prevIndex][0]-movementBuffer[index][0])*partialTickTime,
			movementBuffer[index][1]+(movementBuffer[prevIndex][1]-movementBuffer[index][1])*partialTickTime
		};
	}

	private void updateEnderCrystal(){
		if (healingEnderCrystal != null){
			if (healingEnderCrystal.isDead){
				if (!worldObj.isRemote){
					attackEntityFromPart(dragonPartHead,DamageSource.setExplosionSource(null),10F);
					if (target == null)trySetTarget(attacks.getRandomPlayer());
				}

				healingEnderCrystal = null;
			}
			else if (ticksExisted%10 == 0 && getHealth() < getMaxHealth())setHealth(getHealth()+(ModCommonProxy.opMobs ? 2F : 1F));
		}

		if (rand.nextInt(10) == 0){
			float dist = 30F+4F*worldObj.difficultySetting.getDifficultyId()+(ModCommonProxy.opMobs ? 8F : 0F);
			healingEnderCrystal = DragonUtil.getClosestEntity(this,(List<EntityEnderCrystal>)worldObj.getEntitiesWithinAABB(EntityEnderCrystal.class,boundingBox.expand(dist,dist,dist)));
		}
	}

	private void collideWithEntities(List<? extends Entity> list){
		double bodyCenterX = (dragonPartBody.boundingBox.minX+dragonPartBody.boundingBox.maxX)*0.5D;
		double bodyCenterZ = (dragonPartBody.boundingBox.minZ+dragonPartBody.boundingBox.maxZ)*0.5D;
		
		for(Entity entity:list){
			if (entity instanceof EntityLivingBase || entity instanceof EntityBlockFallingObsidian){
				while(entity.ridingEntity != null)entity = entity.ridingEntity;
				
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

	private boolean destroyBlocksInAABB(AxisAlignedBB aabb){
		if (!worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"))return false;

		boolean wasBlocked = false;
		boolean spawnParticles = false;
		int minX = MathUtil.floor(aabb.minX+0.5D-rand.nextDouble()*rand.nextDouble()*5D);
		int minY = MathUtil.floor(aabb.minY+0.5D-rand.nextDouble()*rand.nextDouble()*5D);
		int minZ = MathUtil.floor(aabb.minZ+0.5D-rand.nextDouble()*rand.nextDouble()*5D);
		int maxX = MathUtil.floor(aabb.maxX-0.5D+rand.nextDouble()*rand.nextDouble()*5D);
		int maxY = MathUtil.floor(aabb.maxY-0.5D+rand.nextDouble()*rand.nextDouble()*5D);
		int maxZ = MathUtil.floor(aabb.maxZ-0.5D+rand.nextDouble()*rand.nextDouble()*5D);
		double rad = 2.8D+Math.min((aabb.maxX-aabb.minX)*0.5D,(aabb.maxZ-aabb.minZ)*0.5D);
		int cx = (int)((aabb.maxX-aabb.minX)*0.5D+aabb.minX);
		int cy = (int)((aabb.maxY-aabb.minY)*0.5D+aabb.minY);
		int cz = (int)((aabb.maxZ-aabb.minZ)*0.5D+aabb.minZ);

		for(int xx = minX; xx <= maxX; xx++){
			for(int yy = minY; yy <= maxY; yy++){
				for(int zz = minZ; zz <= maxZ; zz++){
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
					else if (MathUtil.distance(xx-cx,yy-cy,zz-cz) <= rad+(0.9D*rand.nextDouble()-0.4D)){
						spawnParticles = worldObj.setBlockToAir(xx,yy,zz) || spawnParticles;
					}
				}
			}
		}

		if (spawnParticles)worldObj.spawnParticle("largeexplode",aabb.minX+(aabb.maxX-aabb.minX)*rand.nextFloat(),aabb.minY+(aabb.maxY-aabb.minY)*rand.nextFloat(),aabb.minZ+(aabb.maxZ-aabb.minZ)*rand.nextFloat(),0D,0D,0D);
		
		return wasBlocked;
	}

	private void setNewTarget(){
		forceNewTarget = false;
		if (rand.nextBoolean() && trySetTarget(attacks.getWeakPlayer()))return;
		
		for(double newTargetX, newTargetY, newTargetZ;;){
			newTargetX = (rand.nextFloat()*120F-60F);
			newTargetY = (70F+rand.nextFloat()*50F);
			newTargetZ = (rand.nextFloat()*120F-60F);
			
			if (MathUtil.square(posX-newTargetX)+MathUtil.square(posY-newTargetY)+MathUtil.square(posZ-newTargetZ) > 100D){
				trySetTargetPosition(newTargetX,newTargetY,newTargetZ);
				break;
			}
		}
	}
	
	public boolean trySetTarget(Entity entity){
		if (entity != null && (entity.isDead || (entity instanceof EntityPlayer && !attacks.getViablePlayers().contains(entity)) || spawnCooldown > 0))return false;
		forceNewTarget = false;
		
		TargetSetEvent event = new TargetSetEvent(target,entity);
		currentAttack.onTargetSetEvent(event);
		target = event.newTarget;
		return target != null;
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
	
	public void forceSpecialAttack(DragonSpecialAttackBase newAttack){
		lastAttack = currentAttack;
		
		if (currentAttack != null){
			currentAttack.end();
			nextAttackTicks = currentAttack.getNextAttackTimer();
		}
		
		currentAttack = newAttack;
		currentAttack.init();
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt){
		super.writeEntityToNBT(nbt);
		nbt.setBoolean("angry",isAngry());
		nbt.setShort("nat",(short)Math.max(120,nextAttackTicks));
		nbt.setShort("dth",(short)deathTicks);
		nbt.setShort("scd",(short)Math.max(200,spawnCooldown));
		nbt.setByte("load",loadTimer);

		nbt.setTag("atk",attacks.writeToNBT());
		nbt.setTag("rwr",rewards.writeToNBT());
		nbt.setTag("acv",achievements.writeToNBT());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		 
		setAngry(nbt.getBoolean("angry"));
		nextAttackTicks = nbt.getShort("nat");
		deathTicks = nbt.getShort("dth");
		spawnCooldown = nbt.getShort("scd");
		loadTimer = nbt.hasKey("load") ? nbt.getByte("load") : loadTimer;
		
		attacks.readFromNBT(nbt.getCompoundTag("atk"));
		rewards.readFromNBT(nbt.getCompoundTag("rwr"));
		achievements.readFromNBT(nbt.getCompoundTag("acv"));
	}

	public void setAngry(boolean angry){
		dataWatcher.updateObject(17,Byte.valueOf(angry ? (byte)1 : (byte)0));
	}

	public boolean isAngry(){
		return (dataWatcher.getWatchableObjectByte(17)&1) != 0;
	}

	public void setWingSpeed(float wingSpeed){
		dataWatcher.updateObject(18,wingSpeed);
	}

	public float getWingSpeed(){
		return dataWatcher.getWatchableObjectFloat(18);
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
	public String getCommandSenderName(){
		return StatCollector.translateToLocal(Baconizer.mobName("entity.dragon.name"));
	}

	@Override
	protected String getLivingSound(){
		return "mob.enderdragon.growl";
	}

	@Override
	protected String getHurtSound(){
		return Baconizer.soundNormal("mob.enderdragon.hit");
	}

	@Override
	protected float getSoundVolume(){
		return angryStatus ? 6.5F : 5F;
	}
}
