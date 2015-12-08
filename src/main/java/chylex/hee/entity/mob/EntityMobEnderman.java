package chylex.hee.entity.mob;
import java.util.IdentityHashMap;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import chylex.hee.entity.mob.ai.AIUtil;
import chylex.hee.entity.mob.ai.EntityAIMoveBlocksRandomly;
import chylex.hee.entity.mob.ai.target.EntityAIDirectLookTarget;
import chylex.hee.entity.mob.ai.target.EntityAIDirectLookTarget.ITargetOnDirectLook;
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
import chylex.hee.system.abstractions.damage.Damage;
import chylex.hee.system.abstractions.damage.IDamageModifier;
import chylex.hee.system.abstractions.entity.EntityAttributes;
import chylex.hee.system.abstractions.entity.EntitySelector;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.loot.PercentageLootTable;
import chylex.hee.world.loot.info.LootMobInfo;

public class EntityMobEnderman extends EntityAbstractEndermanCustom implements ITargetOnDirectLook{
	private static final double lookDistance = 64D;
	private static final PercentageLootTable drops = new PercentageLootTable();
	private static final MobTeleporter<EntityMobEnderman> teleportAround = new MobTeleporter<>();
	
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
		
		teleportAround.setLocationSelector(
			ITeleportXZ.inCircle(64),
			ITeleportY.findSolidBottom(ITeleportY.around(16),8)
		);

		teleportAround.setAttempts(128);
		teleportAround.addLocationPredicate(ITeleportPredicate.noCollision);
		teleportAround.addLocationPredicate(ITeleportPredicate.noLiquid);
		teleportAround.onTeleport(ITeleportListener.playSound);
		
		ReflectionPublicizer.f__carriable__EntityEnderman(new IdentityHashMap<Block,Boolean>(){
			@Override
			public Boolean get(Object key){
				return Boolean.FALSE;
			}
		});
	}
	
	private int waterTimer;
	
	public EntityMobEnderman(World world){
		super(world);
		AIUtil.clearEntityTasks(this);
		
		tasks.addTask(1,new EntityAISwimming(this));
		tasks.addTask(2,new EntityAIAttackOnCollide(this,1D,false));
		tasks.addTask(3,new EntityAIWander(this,1D));
		tasks.addTask(4,new EntityAIWatchClosest(this,EntityPlayer.class,8F));
		tasks.addTask(4,new EntityAILookIdle(this));
		tasks.addTask(5,new EntityAIMoveBlocksRandomly(this,this,new Block[0]));
		
		targetTasks.addTask(1,new EntityAIHurtByTarget(this,false));
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
	
	@Override
	public void onLivingUpdate(){
		super.onLivingUpdate();
		
		if (isEndermanWet()){
			++waterTimer;
			
			if (waterTimer == 1){
				setAggressive(true);
			}
			
			if (waterTimer > 80){
				attackEntityFrom(DamageSource.drown,2F);
				setAttackTarget(null);
			}
		}
		else if (waterTimer > 0){
			waterTimer = 0;
			setAggressive(getAttackTarget() != null);
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
	public boolean attackEntityAsMob(Entity target){
		return Damage.vanillaMob(this).addModifier(IDamageModifier.nudityDanger).deal(target);
	}
	
	@Override
	public boolean canTargetOnDirectLook(EntityPlayer target, double distance){
		return distance <= (Causatum.hasReached(target,Progress.ENDERMAN_KILLED) ? lookDistance : lookDistance*0.5D);
	}

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
	
	@Override
	public boolean getCanSpawnHere(){
		return super.getCanSpawnHere() && (worldObj.provider.dimensionId != 0 || worldObj.skylightSubtracted <= 5);
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
