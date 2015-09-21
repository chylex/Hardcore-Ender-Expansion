package chylex.hee.entity.boss.dragon.attacks.special;
import gnu.trove.map.hash.TObjectByteHashMap;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.boss.dragon.attacks.special.event.DamageTakenEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.TargetPositionSetEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.TargetSetEvent;
import chylex.hee.game.commands.DebugBoard;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.util.DragonUtil;

public class DragonAttackDefault extends DragonSpecialAttackBase{
	private byte targetCooldown;
	private int nextTargetTimer = 100;
	private EntityPlayer overrideTarget;
	private boolean isOverriding;
	
	private TObjectByteHashMap<UUID> seesDragon = new TObjectByteHashMap<>();
	private byte seesCheck;
	private boolean stealthInProgress;
	
	public DragonAttackDefault(EntityBossDragon dragon, int attackId){
		super(dragon,attackId,-1);
	}
	
	@Override
	public void init(){
		super.init();
		targetCooldown = 70;
	}
	
	@Override
	public void update(){
		tick++;
		
		/*
		 * TARGET PICKING
		 */
		
		if (!stealthInProgress && dragon.target == null && (targetCooldown == 0 || --targetCooldown == 0)){
			int healthPerc = dragon.attacks.getHealthPercentage();
			int viablePlayers = dragon.attacks.getViablePlayers().size();
			int attackChance = Math.min(170,(dragon.angryStatus ? 45 : 5)+getDifficulty()*5+Math.min(viablePlayers,5)*14+(ModCommonProxy.opMobs ? 10 : 0)+(50-(healthPerc>>1)));
			
			if ((nextTargetTimer < 80 && rand.nextInt(260-attackChance) == 0) || nextTargetTimer <= 0 || --nextTargetTimer <= 0){
				nextTargetTimer = 150+rand.nextInt(40)+(healthPerc>>2)+(dragon.angryStatus ? 0 : 35)-getDifficulty()*6-Math.min(dragon.worldObj.playerEntities.size(),5)*5;
				dragon.trySetTarget(dragon.attacks.getRandomPlayer());
			}
			
			DebugBoard.updateValue("TargetChance",260-attackChance);
			DebugBoard.updateValue("NextTargetTimer",nextTargetTimer);
		}
		
		/*
		 * STEALTH
		 */
		
		if (!dragon.angryStatus){
			if (!stealthInProgress && ++seesCheck > 20 && dragon.target == null){
				for(EntityPlayer player:(List<EntityPlayer>)dragon.worldObj.playerEntities){
					UUID id = player.getUniqueID();
					
					if (getVision(dragon,player) && seesDragon.adjustOrPutValue(id,(byte)1,(byte)1) > 4+(getDifficulty()>>1)){
						overrideTarget = player;
						isOverriding = stealthInProgress = true;
						seesDragon.clear();
						break;
					}
					else seesDragon.remove(id);
				}
				
				seesCheck = 0;
			}
			else if (stealthInProgress && overrideTarget != null && seesCheck%3 == 0 && dragon.dragonPartHead.getDistanceSqToEntity(overrideTarget) < 27D){
				dragon.attacks.biteClosePlayers();
				isOverriding = stealthInProgress = false;
				overrideTarget = null;
				dragon.rewards.addHandicap(0.3F,false);
			}
		}
		else stealthInProgress = false;
		
		if (stealthInProgress && !isPassiveAttackDisabled(EntityBossDragon.ATTACK_FIREBALL))setDisabledPassiveAttacks(EntityBossDragon.ATTACK_FIREBALL);
		else if (!stealthInProgress && isPassiveAttackDisabled(EntityBossDragon.ATTACK_FIREBALL))setDisabledPassiveAttacks();
	}
	
	@Override
	public boolean canStart(){
		return false;
	}

	private boolean getVision(EntityLivingBase target, EntityLivingBase vision){
		return DragonUtil.canEntitySeePoint(vision,target.posX,target.posY,target.posZ,16F);
	}
	
	@Override
	public boolean hasEnded(){
		return true;
	}
	
	@Override
	public void onDamageTakenEvent(DamageTakenEvent event){
		super.onDamageTakenEvent(event);
		nextTargetTimer -= event.damage;
	}
	
	@Override
	public int getNextAttackTimer(){
		return (220+rand.nextInt(70)+((4-getDifficulty())*15));
	}

	@Override
	public float overrideMovementSpeed(){
		return dragon.target != null ? 1.5F : 1F;
	}
	
	@Override
	public float overrideWingSpeed(){
		return 1F;
	}
	
	@Override
	public void onTargetSetEvent(TargetSetEvent event){
		if (isOverriding)event.newTarget = overrideTarget;
	}
	
	@Override
	public void onTargetPositionSetEvent(TargetPositionSetEvent event){
		if (isOverriding)event.cancel();
	}
}