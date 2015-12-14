package chylex.hee.entity.mob;
import java.util.IdentityHashMap;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import chylex.hee.entity.mob.ai.AIUtil;
import chylex.hee.entity.mob.ai.EntityAIMoveBlocksRandomly;
import chylex.hee.entity.mob.ai.target.EntityAIDirectLookTarget;
import chylex.hee.entity.mob.ai.target.EntityAIDirectLookTarget.ITargetOnDirectLook;
import chylex.hee.entity.mob.ai.target.EntityAIHurtByTargetConsecutively;
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
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.ReflectionPublicizer;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.damage.Damage;
import chylex.hee.system.abstractions.damage.IDamageModifier;
import chylex.hee.system.abstractions.entity.EntityAttributes;
import chylex.hee.system.abstractions.entity.EntityAttributes.Operation;
import chylex.hee.system.abstractions.entity.EntitySelector;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.loot.PercentageLootTable;
import chylex.hee.world.loot.info.LootMobInfo;

public class EntityMobEnderman extends EntityAbstractEndermanCustom implements ITargetOnDirectLook{
	private static final double lookDistance = 64D;
	private static final PercentageLootTable drops = new PercentageLootTable();
	private static final MobTeleporter<EntityMobEnderman> teleportAroundClose = new MobTeleporter<>();
	private static final MobTeleporter<EntityMobEnderman> teleportAroundFull = new MobTeleporter<>();
	
	public static final AttributeModifier waterModifier = EntityAttributes.createModifier("Enderman water",Operation.MULTIPLY,0.6D);
	
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
			ITeleportXZ.inCircle(32),
			ITeleportY.findSolidBottom(ITeleportY.around(16),8)
		);
		
		teleportAroundFull.setLocationSelector(
			ITeleportXZ.inCircle(64),
			ITeleportY.findSolidBottom(ITeleportY.around(16),8)
		);
		
		for(MobTeleporter teleporter:new MobTeleporter[]{ teleportAroundClose, teleportAroundFull }){
			teleporter.setAttempts(128);
			teleporter.addLocationPredicate(ITeleportPredicate.noCollision);
			teleporter.addLocationPredicate(ITeleportPredicate.noLiquid);
			teleporter.onTeleport(ITeleportListener.playSound);
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
	private int timeSinceLastTeleport;
	private int extraDespawnOffset;
	
	public EntityMobEnderman(World world){
		super(world);
		AIUtil.clearEntityTasks(this);
		
		tasks.addTask(1,new EntityAISwimming(this));
		tasks.addTask(2,new EntityAIAttackOnCollide(this,1D,false));
		tasks.addTask(3,new EntityAIWander(this,1D));
		tasks.addTask(4,new EntityAIWatchClosest(this,EntityPlayer.class,8F));
		tasks.addTask(4,new EntityAILookIdle(this));
		tasks.addTask(5,new EntityAIMoveBlocksRandomly(this,this,new Block[0]));
		
		targetTasks.addTask(1,new EntityAIHurtByTargetConsecutively(this).setCounter(n -> n >= 2+rand.nextInt(3)).setTimer(300));
		targetTasks.addTask(2,new EntityAIDirectLookTarget(this,this).setMaxDistance(lookDistance));
		
		experienceValue = 10;
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
		super.onLivingUpdate();
		
		if (!worldObj.isRemote){
			++timeSinceLastTeleport;
			
			if (isEndermanWet()){
				++waterTimer;
				waterResetCooldown = 0;
				
				if (waterTimer == 1){
					setAggressive(true);
					
					waterModifierCooldown = 120+rand.nextInt(40);
					EntityAttributes.applyModifier(this,EntityAttributes.attackDamage,waterModifier);
				}
				
				if (waterTimer > 80){
					attackEntityFrom(DamageSource.drown,2F);
					setAttackTarget(null);
					teleportAround(true);
				}
			}
			else if (waterTimer > 0 && ++waterResetCooldown > 10){
				waterTimer = 0;
				setAggressive(getAttackTarget() != null);
			}
			else if (waterTimer == 0 && waterModifierCooldown > 0 && --waterModifierCooldown == 0){
				EntityAttributes.removeModifier(this,EntityAttributes.attackDamage,waterModifier);
			}
			
			if (extraDespawnOffset > 0 && ticksExisted%4 == 0 && rand.nextBoolean()){
				--extraDespawnOffset;
			}
			
			if (ticksExisted%15 == 0){
				int despawnChance = 300;
				despawnChance -= (11-worldObj.skylightSubtracted)*8; // skylightSubtracted goes from 0 (day) to 11 (night)
				despawnChance -= isCarrying() ? 50 : 0;
				despawnChance -= extraDespawnOffset;
				
				if (rand.nextInt(Math.max(10,despawnChance)) == 0){
					teleportDespawn();
				}
			}
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
				CausatumEventHandler.tryStartEvent(attacker,EventTypes.STAGE_ADVANCE_TO_ENDERMAN_KILLED);
				return;
			}
		}
		
		for(ItemStack drop:drops.generateLoot(lootInfo,rand))entityDropItem(drop,0F);
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount){
		if (isEntityInvulnerable())return false;
		
		Entity sourceEntity = source.getEntity();
		
		if (sourceEntity != null){
			if (sourceEntity instanceof EntityPlayer && !Causatum.hasReached((EntityPlayer)sourceEntity,Progress.ENDERMAN_KILLED) && teleportAround(false)){
				if (sourceEntity != source.getSourceOfDamage() && rand.nextInt(4) == 0)setAttackTarget((EntityPlayer)sourceEntity);
				return true;
			}
			
			if (sourceEntity != source.getSourceOfDamage() && teleportAround(false))return true;
		}
		else{
			if (source == DamageSource.cactus || source == DamageSource.inFire || source == DamageSource.lava || source == DamageSource.inWall){
				if (teleportAround(false))return true;
			}
			
			if (getAttackTarget() == null)extraDespawnOffset += MathUtil.ceil(amount*4F);
		}
		
		return onEndermanAttackedFrom(source,amount);
	}
	
	@Override
	public boolean attackEntityAsMob(Entity target){
		dropCarrying();
		return Damage.vanillaMob(this).addModifier(IDamageModifier.nudityDanger).deal(target);
	}
	
	@Override
	public boolean canTargetOnDirectLook(EntityPlayer target, double distance){
		return distance <= (Causatum.hasReached(target,Progress.ENDERMAN_KILLED) ? lookDistance : lookDistance*0.5D);
	}
	
	@Override
	public void setAttackTarget(EntityLivingBase target){
		if (target instanceof EntityPlayer && !Causatum.hasReached((EntityPlayer)target,Progress.ENDERMAN_KILLED))return;
		super.setAttackTarget(target);
	}
	
	// ABILITIES
	
	private boolean canTeleport(){
		if (getAttackTarget() instanceof EntityPlayer){
			if (Causatum.hasReached((EntityPlayer)getAttackTarget(),Progress.ENDERMAN_KILLED)){
				return timeSinceLastTeleport >= 140+rand.nextInt(20); // 7-8 seconds
			}
			else{
				return timeSinceLastTeleport >= 80+rand.nextInt(20); // 4-5 seconds
			}
		}
		
		return timeSinceLastTeleport >= 200-rand.nextInt(100)*rand.nextDouble(); // 5-10 seconds, little hacky solution to make it appear linear when called repeatedly
	}
	
	public boolean teleportAround(boolean fullDistance){
		if (canTeleport() && (fullDistance ? teleportAroundFull : teleportAroundClose).teleport(this,rand)){
			timeSinceLastTeleport = 0;
			return true;
		}
		else return false;
	}
	
	public boolean teleportDespawn(){
		if (!canTeleport())return false;
		
		// TODO fx
		setDead();
		return true;
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
		return super.getCanSpawnHere() && (worldObj.provider.dimensionId != 0 || worldObj.skylightSubtracted >= 9-rand.nextInt(7)*rand.nextDouble());
	}
	
	@Override
	protected void despawnEntity(){
		if (isNoDespawnRequired()){
			entityAge = 0;
			return;
		}
		
		EntityPlayer closest = worldObj.getClosestPlayerToEntity(this,-1D);
		
		if (closest == null || MathUtil.distanceSquared(closest.posX-posX,closest.posY-posY,closest.posZ-posZ) > 25600D){ // 160 blocks
			setDead();
			return;
		}
	}
}
