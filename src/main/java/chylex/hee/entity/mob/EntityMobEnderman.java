package chylex.hee.entity.mob;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Block.SoundType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenEnd;
import chylex.hee.entity.fx.FXType;
import chylex.hee.entity.mob.ai.AIUtil;
import chylex.hee.entity.mob.ai.EntityAIMoveBlocksRandomly;
import chylex.hee.entity.mob.ai.EntityAIWanderRandomly;
import chylex.hee.entity.mob.ai.EntityAIWatchClosest;
import chylex.hee.entity.mob.ai.EntityAIWatchSuspicious;
import chylex.hee.entity.mob.ai.EntityAIWatchSuspicious.IWatchSuspiciousEntities;
import chylex.hee.entity.mob.ai.EntityAIWatchTarget;
import chylex.hee.entity.mob.ai.target.EntityAIDirectLookTarget;
import chylex.hee.entity.mob.ai.target.EntityAIDirectLookTarget.ITargetOnDirectLook;
import chylex.hee.entity.mob.ai.target.EntityAIHurtByTargetConsecutively;
import chylex.hee.entity.mob.ai.target.EntityAIResetTarget;
import chylex.hee.entity.mob.teleport.ITeleportListener;
import chylex.hee.entity.mob.teleport.ITeleportPredicate;
import chylex.hee.entity.mob.teleport.MobTeleporter;
import chylex.hee.entity.mob.teleport.TeleportLocation.ITeleportXZ;
import chylex.hee.entity.mob.teleport.TeleportLocation.ITeleportY;
import chylex.hee.init.BlockList;
import chylex.hee.init.ItemList;
import chylex.hee.mechanics.causatum.Causatum;
import chylex.hee.mechanics.causatum.Causatum.Actions;
import chylex.hee.mechanics.causatum.Causatum.Progress;
import chylex.hee.mechanics.causatum.CausatumEventHandler;
import chylex.hee.mechanics.causatum.events.CausatumEventInstance.EventTypes;
import chylex.hee.mechanics.misc.Baconizer;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C21EffectEntity;
import chylex.hee.packets.client.C22EffectLine;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.ReflectionPublicizer;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.Vec;
import chylex.hee.system.abstractions.damage.Damage;
import chylex.hee.system.abstractions.damage.IDamageModifier;
import chylex.hee.system.abstractions.entity.EntityAttributes;
import chylex.hee.system.abstractions.entity.EntityAttributes.Operation;
import chylex.hee.system.abstractions.entity.EntitySelector;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.loot.PercentageLootTable;
import chylex.hee.world.loot.info.LootMobInfo;

public class EntityMobEnderman extends EntityAbstractEndermanCustom implements ITargetOnDirectLook, IWatchSuspiciousEntities{
	private static final double lookDistance = 64D;
	private static final PercentageLootTable drops = new PercentageLootTable();
	
	private static final MobTeleporter<EntityMobEnderman> teleportAroundClose = new MobTeleporter<>();
	private static final MobTeleporter<EntityMobEnderman> teleportAroundFull = new MobTeleporter<>();
	private static final MobTeleporter<EntityMobEnderman> teleportAvoid = new MobTeleporter<>();
	private static final MobTeleporter<EntityMobEnderman> teleportToEntity = new MobTeleporter<>();
	
	public static final Set<Block> carriableBlocks = new HashSet<>();
	public static final AttributeModifier waterModifier = EntityAttributes.createModifier("Enderman water",Operation.MULTIPLY,0.6D);
	public static final AttributeModifier backStabModifier = EntityAttributes.createModifier("Enderman backstab",Operation.MULTIPLY,1.5D);
	public static final AttributeModifier noMovementModifier = EntityAttributes.createModifier("Enderman stop",Operation.MULTIPLY,0D);
	
	static{
		drops.addLoot(Items.ender_pearl).<LootMobInfo>setChances(obj -> {
			switch(obj.looting){
				case 0: return new float[]{ 0.60F };
				case 1: return new float[]{ 0.70F };
				case 2: return new float[]{ 0.65F, 0.10F };
				default: return new float[]{ 0.72F, 0.16F };
			}
		});
		
		drops.addLoot(ItemList.ethereum).<LootMobInfo>setChances(obj -> {
			switch(obj.looting){
				case 0: return new float[]{ 0.12F };
				case 1: return new float[]{ 0.16F };
				case 2: return new float[]{ 0.22F };
				default: return new float[]{ 0.25F };
			}
		});
		
		drops.addLoot(BlockList.enderman_head).setChances(obj -> {
			return new float[]{ 0.03F };
		});
		
		teleportAroundClose.setLocationSelector(
			ITeleportXZ.inCircle(7,24), // TODO change to 16?
			ITeleportY.findSolidBottom(ITeleportY.around(12),24)
		);
		
		teleportAroundFull.setLocationSelector(
			ITeleportXZ.inCircle(7,64),
			ITeleportY.findSolidBottom(ITeleportY.around(16),32)
		);
		
		teleportAroundClose.setAttempts(128);
		teleportAroundFull.setAttempts(128);
		teleportAvoid.setAttempts(24);
		teleportToEntity.setAttempts(64);
		
		final ITeleportListener<EntityMobEnderman> tpParticle = ITeleportListener.sendPacket((startPos, endPos) -> new C22EffectLine(FXType.Line.ENDERMAN_TELEPORT_SEPARATE,startPos.x,startPos.y,startPos.z,endPos.x,endPos.y,endPos.z));
		final ITeleportListener<EntityMobEnderman> tpDropCarrying = (entity, startPos, rand) -> { if (rand.nextInt(5) <= 2)entity.dropCarrying(); };
		final ITeleportListener<EntityMobEnderman> tpResetTimer = (entity, startPos, rand) -> entity.timeSinceLastTeleport = 0;
		final ITeleportListener<EntityMobEnderman> tpBackStab = (entity, startPos, rand) -> entity.backStabCooldown = 80;
		
		for(MobTeleporter<EntityMobEnderman> teleporter:new MobTeleporter[]{ teleportAroundClose, teleportAroundFull, teleportAvoid, teleportToEntity }){
			teleporter.addLocationPredicate(ITeleportPredicate.noCollision);
			teleporter.addLocationPredicate(ITeleportPredicate.noLiquid);
			teleporter.onTeleport(tpParticle);
			teleporter.onTeleport(ITeleportListener.skipRenderLerp);
			teleporter.onTeleport(tpDropCarrying);
		}
		
		teleportAroundClose.onTeleport(tpResetTimer);
		teleportAroundFull.onTeleport(tpResetTimer);
		teleportToEntity.onTeleport(tpResetTimer);
		teleportToEntity.onTeleport(tpBackStab);
		
		teleportAroundClose.onTeleport((entity, startPos, rand) -> {
			if (entity.suspiciousEntity != null){
				EntityLivingBase suspicious = entity.suspiciousEntity;
				entity.getLookHelper().setLookPosition(suspicious.posX,suspicious.posY+suspicious.getEyeHeight(),suspicious.posZ,360F,180F);
				entity.getLookHelper().onUpdateLook();
				entity.rotationYaw = entity.rotationYawHead;
			}
		});
		
		carriableBlocks.add(Blocks.gravel);
		carriableBlocks.add(Blocks.clay);
		carriableBlocks.add(Blocks.pumpkin);
		carriableBlocks.add(Blocks.melon_block);
		
		/* TODO make this work somehow? for(Block block:GameRegistryUtil.getBlocks()){
			if (block instanceof IGrowable || block instanceof IPlantable){
				carriableBlocks.add(block);
			}
		}*/
		
		for(BiomeGenBase biome:BiomeGenBase.getBiomeGenArray()){
			if (biome == null || biome instanceof BiomeGenEnd || biome.topBlock == null)continue;
			
			for(Block block:new Block[]{ biome.topBlock, biome.fillerBlock }){
				SoundType sound = block.stepSound;
				
				if (sound == Block.soundTypeGrass || sound == Block.soundTypeGravel || sound == Block.soundTypeSand){
					carriableBlocks.add(block);
				}
			}
		}
		
		ReflectionPublicizer.f__carriable__EntityEnderman(new IdentityHashMap<Block,Boolean>(){
			@Override
			public Boolean get(Object key){
				return Boolean.FALSE;
			}
		});
	}
	
	// ENTITY
	
	private int waterTimer, waterResetCooldown, waterModifierCooldown;
	private int timeSinceLastTeleport, teleportFailTimer;
	private int backStabCooldown;
	private int extraDespawnOffset;
	private boolean isControlledExternally;
	
	private EntityLivingBase suspiciousEntity;
	private int suspiciousTimer;
	
	private boolean waitingForVulnerability;
	private int vulnerabilityTimer;
	
	public EntityMobEnderman(World world){
		super(world);
		AIUtil.clearEntityTasks(this);
		
		tasks.addTask(1,new EntityAISwimming(this));
		tasks.addTask(2,new EntityAIAttackOnCollide(this,1D,false));
		tasks.addTask(3,new EntityAIWanderRandomly(this,1D).setChancePerTick(1F/70F));
		tasks.addTask(4,new EntityAIWatchTarget(this));
		tasks.addTask(5,new EntityAIWatchSuspicious(this,this));
		tasks.addTask(6,new EntityAIWatchClosest<>(this,EntityPlayer.class).setChancePerTick(1F/25F).setWatchTime(target -> canAttackPlayer(target) ? 120+rand.nextInt(160) : 0));
		tasks.addTask(7,new EntityAILookIdle(this));
		tasks.addTask(8,new EntityAIMoveBlocksRandomly(this,this,carriableBlocks));
		
		targetTasks.addTask(1,new EntityAIResetTarget(this).stopIfTooFar(64D).stopIfCreative());
		targetTasks.addTask(2,new EntityAIHurtByTargetConsecutively(this).setCounter(n -> n >= 2+rand.nextInt(3)).setTimer(300));
		targetTasks.addTask(3,new EntityAIDirectLookTarget(this,this).setMaxDistance(lookDistance));
		
		experienceValue = 10;

		timeSinceLastTeleport = 80+rand.nextInt(100);
	}
	
	@Override
	protected void applyEntityAttributes(){
		super.applyEntityAttributes();
		EntityAttributes.setValue(this,EntityAttributes.maxHealth,40D);
		EntityAttributes.setValue(this,EntityAttributes.movementSpeed,0.3D);
		EntityAttributes.setValue(this,EntityAttributes.attackDamage,5D);
	}
	
	@Override
	protected boolean isAIEnabled(){
		return true;
	}
	
	// BEHAVIOR
	
	@Override
	public void onLivingUpdate(){
		entityToAttack = null;
		super.onLivingUpdate();
		
		if (!worldObj.isRemote){
			++timeSinceLastTeleport;
			
			if (teleportFailTimer > 0)--teleportFailTimer;
			if (backStabCooldown > 0)--backStabCooldown;
			
			// water handling
			if (isEndermanWet()){
				++waterTimer;
				waterResetCooldown = 0;
				
				if (waterTimer == 1){
					setAggressive(true);
					
					waterModifierCooldown = 120+rand.nextInt(40);
					EntityAttributes.applyModifier(this,EntityAttributes.attackDamage,waterModifier);
				}
				
				if (waterTimer%3 == 0 && isInRainOrSnow() && rand.nextInt(17) == 0){
					teleportDespawn(false);
				}
				
				if (waterTimer > 80){
					attackEntityFrom(DamageSource.drown,2F);
					setAttackTarget(null);
					
					if (isInRainOrSnow() || (!teleportAround(true) && rand.nextInt(5) == 0)){
						teleportDespawn(false);
					}
				}
			}
			else if (waterTimer > 0 && ++waterResetCooldown > 10){
				waterTimer = 0;
				setAggressive(getAttackTarget() != null);
			}
			else if (waterTimer == 0 && waterModifierCooldown > 0 && --waterModifierCooldown == 0){
				EntityAttributes.removeModifier(this,EntityAttributes.attackDamage,waterModifier);
			}
			
			// despawning
			if (extraDespawnOffset > 0 && ticksExisted%3 == 0 && rand.nextBoolean()){
				--extraDespawnOffset;
			}
			
			if (ticksExisted%15 == 0){
				int despawnChance = 300;
				despawnChance -= (11-worldObj.skylightSubtracted)*15; // skylightSubtracted goes from 0 (day) to 11 (night)
				despawnChance -= isCarrying() ? 120 : 0;
				despawnChance -= extraDespawnOffset;
				despawnChance /= isInRainOrSnow() ? 8 : 1;
				
				if (rand.nextInt(Math.max(10,despawnChance)) == 0){
					teleportDespawn(false);
				}
			}
			
			// suspicious entity
			if (suspiciousTimer > 0){
				if (suspiciousEntity == null){
					suspiciousTimer = 0;
					EntityAttributes.removeModifier(this,EntityAttributes.movementSpeed,noMovementModifier);
				}
				else if (--suspiciousTimer == 0){
					if (suspiciousEntity == getAttackTarget()){
						dropCarrying();
						
						int prevTime = timeSinceLastTeleport;
						timeSinceLastTeleport = Integer.MAX_VALUE;
						if (!teleportBehind(getAttackTarget()))timeSinceLastTeleport = prevTime;
					}
					
					suspiciousEntity = null;
					if (!waitingForVulnerability)EntityAttributes.removeModifier(this,EntityAttributes.movementSpeed,noMovementModifier);
				}
			}
			
			// teleportation
			if (getAttackTarget() == null && timeSinceLastTeleport > 1800-rand.nextInt(1600)*rand.nextDouble()){ // 10-90 seconds
				teleportAround(true);
			}
			else if (getAttackTarget() != null && !waitingForVulnerability && (timeSinceLastTeleport > 400-rand.nextInt(260)*rand.nextDouble() || rand.nextInt(1000) == 0)){ // 7-20 seconds
				teleportBehind(getAttackTarget());
			}
			
			if (ticksExisted%12 == 0 && rand.nextInt(3) == 0){
				for(EntityPlayer player:EntitySelector.players(worldObj,boundingBox.expand(4D,4D,4D))){
					if (!canAttackPlayer(player)){
						teleportAround(false);
						break;
					}
				}
			}
			
			// vulnerable player
			if (waitingForVulnerability){
				if (getAttackTarget() == null || !isTargetProtected(getAttackTarget())){
					EntityAttributes.removeModifier(this,EntityAttributes.movementSpeed,noMovementModifier);
					waitingForVulnerability = false;
					
					if (getAttackTarget() != null)teleportInFront(getAttackTarget());
				}
			}
			else if (getAttackTarget() != null && rand.nextInt(4) == 0 && isTargetProtected(getAttackTarget()) && ++vulnerabilityTimer == 4){
				vulnerabilityTimer = 0;
				waitingForVulnerability = true;
				EntityAttributes.applyModifier(this,EntityAttributes.movementSpeed,noMovementModifier);
			}
		}
		else if (ticksExisted == 1){
			prevRenderYawOffset = renderYawOffset = rotationYaw;
		}
		
		if (getAttackTarget() != null){
			faceEntity(getAttackTarget(),100F,100F);
		}
	}
	
	@Override
	protected void dropFewItems(boolean recentlyHit, int looting){
		dropCarrying();
		
		LootMobInfo lootInfo = new LootMobInfo(this,recentlyHit,looting);
		
		if (lootInfo.getAttacker() instanceof EntityPlayerMP){
			EntityPlayerMP attacker = (EntityPlayerMP)lootInfo.getAttacker();
			
			Causatum.trigger(attacker,Actions.KILL_ENDERMAN);
			
			if (Causatum.progress(attacker,Progress.ENDERMAN_KILLED,Actions.STAGE_ADVANCE_TO_ENDERMAN_KILLED)){
				for(EntityPlayer nearbyPlayer:EntitySelector.players(worldObj,boundingBox.expand(12D,4D,12D))){
					Causatum.progress(nearbyPlayer,Progress.ENDERMAN_KILLED);
				}
				
				entityDropItem(new ItemStack(Items.ender_pearl),0F);
				CausatumEventHandler.tryStartEvent(attacker,EventTypes.STAGE_ADVANCE_TO_ENDERMAN_KILLED,new Object[]{ this });
				return;
			}
		}
		
		for(ItemStack drop:drops.generateLoot(lootInfo,rand))entityDropItem(drop,0F);
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount){
		if (isEntityInvulnerable() || !handleDamageSource(source))return false;
		
		Entity sourceEntity = source.getEntity();
		
		if (sourceEntity != null){
			boolean isProjectile = sourceEntity != source.getSourceOfDamage();
			
			if (sourceEntity instanceof EntityPlayer && !canAttackPlayer((EntityPlayer)sourceEntity)){
				if ((isProjectile && teleportAvoid(source.getSourceOfDamage()) || (!isProjectile && teleportAround(false)))){
					if (isProjectile && rand.nextInt(4) == 0 && canEntityBeSeen(sourceEntity) && MathUtil.distance(posX-sourceEntity.posX,posZ-sourceEntity.posZ) <= 32D){
						setAttackTarget((EntityPlayer)sourceEntity);
					}
					
					return true;
				}
			}
			
			if (isProjectile && teleportAvoid(source.getSourceOfDamage()))return true;
		}
		
		return onEndermanAttackedFrom(source,amount);
	}
	
	@Override
	protected void damageEntity(DamageSource source, float amount){
		if (handleDamageSource(source))super.damageEntity(source,amount);
		if (getAttackTarget() == null)extraDespawnOffset += MathUtil.ceil(amount*10F);
	}
	
	private boolean handleDamageSource(DamageSource source){
		if (isControlledExternally){
			teleportDespawn(true);
			return false;
		}
		
		if (source == DamageSource.cactus || source == DamageSource.inFire || source == DamageSource.lava || source == DamageSource.inWall){
			if (teleportAround(false))return false;
		}
		
		return true;
	}
	
	@Override
	public boolean attackEntityAsMob(Entity target){
		dropCarrying();
		
		if (backStabCooldown > 0 && target instanceof EntityLivingBase && canBackStab((EntityLivingBase)target)){
			EntityAttributes.applyModifier(this,EntityAttributes.attackDamage,backStabModifier);
		}
		
		boolean result = Damage.vanillaMob(this).addModifier(IDamageModifier.nudityDanger).deal(target);
		
		if (backStabCooldown > 0){
			backStabCooldown = 0;
			EntityAttributes.removeModifier(this,EntityAttributes.attackDamage,backStabModifier);
		}
		
		return result;
	}
	
	@Override
	public boolean canTargetOnDirectLook(EntityPlayer target, double distance){
		if (distance <= (Causatum.hasReached(target,Progress.ENDERMAN_KILLED) ? lookDistance : lookDistance*0.5D)){
			suspiciousEntity = target;
			suspiciousTimer = 180+rand.nextInt(200);
			return true;
		}
		else return false;
	}
	
	@Override
	public @Nullable Pos findBlockStealPosition(EntityCreature entity){
		if (dimension == 1 || worldObj.getClosestPlayerToEntity(this,14D) != null)return null;
		
		Pos pos = super.findBlockStealPosition(entity);
		if (worldObj.getSavedLightValue(EnumSkyBlock.Block,pos.getX(),pos.getY(),pos.getZ()) > 1)return null;
		
		return pos;
	}
	
	@Override
	public @Nullable Pos findBlockPlacePosition(EntityCreature entity){
		return worldObj.getClosestPlayerToEntity(this,16D) != null ? null : super.findBlockPlacePosition(entity);
	}
	
	@Override
	public @Nullable EntityLivingBase getSuspiciousEntity(){
		if (suspiciousEntity != null && (suspiciousEntity.isDead || getDistanceSqToEntity(suspiciousEntity) > 400D))suspiciousEntity = null; // 20 blocks
		return suspiciousEntity;
	}
	
	@Override
	public void setAttackTarget(EntityLivingBase target){
		if (isControlledExternally)return;
		
		if (target instanceof EntityPlayer && !canAttackPlayer((EntityPlayer)target)){
			teleportAround(false);
			return;
		}
		
		if (waitingForVulnerability){
			waitingForVulnerability = false;
			EntityAttributes.removeModifier(this,EntityAttributes.movementSpeed,noMovementModifier);
		}
		
		if (target == suspiciousEntity){
			EntityAttributes.applyModifier(this,EntityAttributes.movementSpeed,noMovementModifier);
			suspiciousTimer = 25+rand.nextInt(20);
		}
		
		setAggressive(target != null || isEndermanWet());
		super.setAttackTarget(target);
	}
	
	public void setControlledExternally(){
		this.isControlledExternally = true;
	}
	
	// ABILITIES
	
	private boolean canAttackPlayer(EntityPlayer player){
		return Causatum.hasReached(player,Progress.ENDERMAN_KILLED);
	}
	
	private boolean canTeleport(){
		if (worldObj.isRemote || isControlledExternally)return false;
		if (EntityAttributes.hasModifier(this,EntityAttributes.movementSpeed,noMovementModifier))return false;
		
		if (getAttackTarget() instanceof EntityPlayer){
			if (Causatum.hasReached((EntityPlayer)getAttackTarget(),Progress.ENDERMAN_KILLED)){
				return timeSinceLastTeleport >= 80+rand.nextInt(20); // 4-5 seconds
			}
			else{
				return timeSinceLastTeleport >= 140+rand.nextInt(20); // 7-8 seconds
			}
		}
		
		return timeSinceLastTeleport >= 200-rand.nextInt(100)*rand.nextDouble(); // 5-10 seconds, little hacky solution to make it appear linear when called repeatedly
	}
	
	private void onTeleportFail(){
		if (teleportFailTimer == 0 && !EntityAttributes.hasModifier(this,EntityAttributes.movementSpeed,noMovementModifier) && !isControlledExternally){
			teleportFailTimer = 30;
			PacketPipeline.sendToAllAround(this,64D,new C21EffectEntity(FXType.Entity.ENDERMAN_TP_FAIL,this));
		}
	}
	
	private boolean tryTeleport(MobTeleporter<EntityMobEnderman> teleporter, boolean checkCanTeleport){
		if ((!checkCanTeleport || canTeleport()) && teleporter.teleport(this,rand))return true;
		else{
			onTeleportFail();
			return false;
		}
	}
	
	public boolean teleportAround(boolean fullDistance){
		return tryTeleport(fullDistance ? teleportAroundFull : teleportAroundClose,true);
	}
	
	public boolean teleportAvoid(Entity projectile){ // ignores teleportation timer since it's just a small "step aside"
		if (worldObj.isRemote)return true;
		
		final Vec perpendicular = Vec.xz(-projectile.motionZ,projectile.motionX);
		final Vec offset = Vec.xzRandom(rand).multiplied(rand.nextDouble());
		
		teleportAvoid.setLocationSelector(
			(entity, startPos, rand) -> startPos.offset(perpendicular,(1D+0.5D*rand.nextDouble())*(rand.nextBoolean() ? -1 : 1)).offset(offset),
			ITeleportY.findSolidBottom(ITeleportY.around(3),6)
		);
		
		return tryTeleport(teleportAvoid,false);
	}
	
	public boolean teleportInFront(EntityLivingBase target){
		if (worldObj.isRemote || isControlledExternally)return true;
		
		final Vec look = Vec.xzLook(target);
		final Vec targetPos = Vec.pos(target);
		
		return teleportNearEntity(target,(entity, startPos, rand) -> targetPos.offset(look,1.25D+0.5D*rand.nextDouble()));
	}
	
	public boolean teleportBehind(EntityLivingBase target){
		if (worldObj.isRemote || isControlledExternally)return true;
		
		final Vec look = Vec.xzLook(target);
		final Vec targetPos = Vec.pos(target);
		
		return teleportNearEntity(target,(entity, startPos, rand) -> targetPos.offset(look,-2D).offset(look.offset(Vec.xzRandom(rand),0.5D).normalized(),-(4D+3D*rand.nextDouble())));
	}
	
	private boolean teleportNearEntity(EntityLivingBase target, final ITeleportXZ<EntityMobEnderman> xz){
		teleportToEntity.setLocationSelector(xz,ITeleportY.findSolidBottom(ITeleportY.around(3,1),7));
		return tryTeleport(teleportToEntity,true);
	}
	
	public boolean teleportDespawn(boolean force){
		if (force || canTeleport()){
			PacketPipeline.sendToAllAround(this,96D,new C21EffectEntity(FXType.Entity.ENDERMAN_DESPAWN,this));
			setDead();
			return true;
		}
		else{
			onTeleportFail();
			return false;
		}
	}
	
	private boolean canBackStab(EntityLivingBase target){
		double px = target.posX,
			   pz = target.posZ,
			   ry = -target.rotationYaw;
		
		int t1x = (int)(px+MathUtil.lendirx(40,ry-120));
		int t1z = (int)(pz+MathUtil.lendiry(40,ry-120));
		int t2x = (int)(px+MathUtil.lendirx(40,ry+120));
		int t2z = (int)(pz+MathUtil.lendiry(40,ry+120));
		
		return MathUtil.triangle((int)posX,(int)posZ,(int)px,(int)pz,t1x,t1z,t2x,t2z); // "reverse frustum" that checks behind the target instead of the front
	}
	
	private boolean isTargetProtected(EntityLivingBase target){
		return Pos.at(target).offset(0,2,0).getMaterial(worldObj).blocksMovement();
	}
	
	// FX AND DISPLAY

	@Override
	protected String getLivingSound(){
		return Baconizer.soundNormal("mob.endermen.idle");
	}
	
	@Override
	protected String getHurtSound(){
		return Baconizer.soundNormal("mob.endermen.hit");
	}
	
	@Override
	protected String getDeathSound(){
		return Baconizer.soundDeath("mob.endermen.death");
	}
	
	@Override
	public String getCommandSenderName(){
		return ModCommonProxy.hardcoreEnderbacon ? StatCollector.translateToLocal("entity.enderman.bacon.name") : super.getCommandSenderName();
	}
	
	// SPAWNING AND DESPAWNING
	
	private boolean isInRainOrSnow(){
		if (worldObj.isRaining() && worldObj.canBlockSeeTheSky(MathUtil.floor(posX),MathUtil.floor(posY),MathUtil.floor(posZ))){
			BiomeGenBase biome = worldObj.getBiomeGenForCoords(MathUtil.floor(posX),MathUtil.floor(posZ));
			return biome.canSpawnLightningBolt() || biome.getEnableSnow();
		}
		else return false;
	}
	
	@Override
	public float getBlockPathWeight(int x, int y, int z){
		return 1F; // in Endermen code and AI, it is only used when checking light on spawn
	}
	
	@Override
	protected boolean isValidLightLevel(){
		Pos pos = Pos.at(this);
		return worldObj.getSavedLightValue(EnumSkyBlock.Block,pos.getX(),pos.getY(),pos.getZ())+rand.nextInt(6) < 8;
	}
	
	@Override
	public boolean getCanSpawnHere(){
		// skylightSubtracted goes from 0 (day) to 11 (night)
		// Endermen start appearing sooner than other monsters, but with smaller chance at first to avoid filling up the spawn limits
		return super.getCanSpawnHere() && (worldObj.provider.dimensionId != 0 || worldObj.skylightSubtracted >= 9-rand.nextInt(7)*(0.5D+rand.nextDouble()*0.5D));
	}
	
	@Override
	protected void despawnEntity(){
		if (ticksExisted < 32 || isControlledExternally)return;
		
		if (isNoDespawnRequired()){
			entityAge = 0;
			return;
		}
		
		EntityPlayer closest = worldObj.getClosestPlayerToEntity(this,-1D);
		
		if (closest == null){
			setDead();
			return;
		}
		
		double distSq = MathUtil.distanceSquared(closest.posX-posX,closest.posY-posY,closest.posZ-posZ);
		
		if (distSq > 25600D || (distSq > 10000D && rand.nextInt(200) == 0)){ // 160 & 100 blocks
			setDead();
			return;
		}
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt){
		super.writeEntityToNBT(nbt);
		if (isControlledExternally)nbt.setBoolean("preventLoad",true);
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		if (nbt.getBoolean("preventLoad"))setDead();
	}
}
