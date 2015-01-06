package chylex.hee.entity.boss.dragon.attacks.special;
import gnu.trove.map.hash.TObjectIntHashMap;
import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.boss.dragon.attacks.special.event.DamageTakenEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.TargetPositionSetEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.TargetSetEvent;
import chylex.hee.system.commands.DebugBoard;
import chylex.hee.system.util.DragonUtil;

public class DragonAttackDefault extends DragonSpecialAttackBase{
	private int attackCooldown = 140, nextTargetTimer = 100;
	private EntityPlayer overrideTarget;
	private boolean isOverriding;
	
	private TObjectIntHashMap<String> seesDragon = new TObjectIntHashMap<>();
	private byte seesCheck;
	private boolean stealthInProgress;
	
	public DragonAttackDefault(EntityBossDragon dragon, int attackId){
		super(dragon,attackId);
	}
	
	@Override
	public void update(){
		tick++;
		if (attackCooldown > 0){
			attackCooldown -= 1;
			return;
		}
		
		/*
		 * TARGET PICKING
		 */
		if (!stealthInProgress && dragon.target == null){
			byte healthPerc = (byte)dragon.attacks.getHealthPercentage();
			int iangry = Math.min(140,(dragon.angryStatus?40:15)+getDifficulty()*4+dragon.worldObj.playerEntities.size()*14+(50-(healthPerc>>1)));
			DebugBoard.updateValue("TargetChance",250-iangry);
			
			if (rand.nextInt(250-iangry) == 0 || (nextTargetTimer = Math.max(0,nextTargetTimer-1)) <= 0){
				nextTargetTimer = rand.nextInt(1+(healthPerc>>1))+healthPerc+120+(dragon.angryStatus?0:50)-getDifficulty()*8-Math.min(5,dragon.worldObj.playerEntities.size())*5;
				dragon.trySetTarget(dragon.attacks.getRandomPlayer());
			}
		}
		DebugBoard.updateValue("NextTargetTimer",nextTargetTimer);
		
		/*
		 * STEALTH
		 */
		
		if (!dragon.angryStatus){
			if (!stealthInProgress && ++seesCheck > 20 && dragon.target == null){
				for(EntityPlayer player:(List<EntityPlayer>)dragon.worldObj.playerEntities){
					int cur = seesDragon.get(player.getCommandSenderName());
					cur = (cur == seesDragon.getNoEntryValue() || getVision(dragon,player) ? 0 : cur+1);

					if (cur > 4+(getDifficulty() <= 1?1:0)){
						overrideTarget = player;
						isOverriding = stealthInProgress = true;
						seesDragon.clear();
						break;
					}
					
					seesDragon.put(player.getCommandSenderName(),cur);
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
		return (120+rand.nextInt(60)+((4-getDifficulty())*20));
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
		if (attackCooldown > 1){
			event.newTarget = null;
			return;
		}
		
		if (isOverriding)event.newTarget = attackCooldown > 1 ? null : overrideTarget;
	}
	
	@Override
	public void onTargetPositionSetEvent(TargetPositionSetEvent event){
		if (isOverriding)event.cancel();
	}
}