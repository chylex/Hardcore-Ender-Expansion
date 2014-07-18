package chylex.hee.entity.boss.dragon.attacks.special;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.boss.dragon.attacks.passive.DragonPassiveAttackBase;
import chylex.hee.entity.boss.dragon.attacks.special.event.DamageTakenEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.TargetPositionSetEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.TargetSetEvent;
import chylex.hee.entity.boss.dragon.managers.DragonShotManager.ShotType;
import chylex.hee.system.commands.DebugBoard;
import chylex.hee.system.util.DragonUtil;

public class DragonAttackDefault extends DragonSpecialAttackBase{
	private short attackCooldown = 140, nextTargetTimer = 100;
	private EntityPlayer overrideTarget = null;
	private boolean isOverriding = false;
	
	private Map<String,Short> seesDragon = new HashMap<>();
	private byte seesCheck = 0;
	private boolean stealthInProgress = false;
	
	public DragonAttackDefault(EntityBossDragon dragon, int attackId){
		super(dragon,attackId);
	}
	
	@Override
	public void init(){
		super.init();
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
			int iangry = Math.min(140,(dragon.angryStatus?40:15)+dragon.getWorldDifficulty()*4+dragon.worldObj.playerEntities.size()*14+(50-(healthPerc>>1)));
			DebugBoard.updateValue("TargetChance",250-iangry);
			DebugBoard.updateValue("HealthPerc",healthPerc);
			
			if (rand.nextInt(250-iangry) == 0 || (nextTargetTimer = (short)Math.max(0,nextTargetTimer-1)) <= 0){
				nextTargetTimer = (short)(rand.nextInt(1+(healthPerc>>1))+healthPerc+120+(dragon.angryStatus?0:50)-dragon.getWorldDifficulty()*8-Math.min(5,dragon.worldObj.playerEntities.size())*5);
				dragon.trySetTarget(dragon.attacks.getRandomPlayer());
			}
		}
		DebugBoard.updateValue("NextTargetTimer",nextTargetTimer);
		
		/*
		 * STEALTH
		 */
		DragonPassiveAttackBase fireball = dragon.attacks.getPassiveAttackById(0);
		
		if (!dragon.angryStatus){
			if (!stealthInProgress && ++seesCheck > 20 && dragon.target == null){
				for(Object o:dragon.worldObj.playerEntities){
					EntityPlayer p = (EntityPlayer)o;
					Short cur = seesDragon.get(p.getCommandSenderName());
					cur = (short)(cur == null || getVision(dragon,p)?0:cur+1);

					if (cur > 4+(dragon.getWorldDifficulty() <= 1?1:0)){
						overrideTarget = p;
						isOverriding = stealthInProgress = true;
						seesDragon.clear();
						break;
					}
					
					seesDragon.put(p.getCommandSenderName(),cur);
				}
				seesCheck = 0;
			}
			else if (stealthInProgress && overrideTarget != null && seesCheck%3 == 0){
				if (dragon.dragonPartHead.getDistanceSqToEntity(overrideTarget) < 27D){
					dragon.attacks.biteClosePlayer();
					isOverriding = stealthInProgress = false;
					overrideTarget = null;
					dragon.rewards.addHandicap(0.3F,false);
				}
			}
		}
		else stealthInProgress = false;
		
		if (stealthInProgress && !isPassiveAttackDisabled(fireball))setDisabledPassiveAttacks(fireball);
		else if (!stealthInProgress && isPassiveAttackDisabled(fireball))setDisabledPassiveAttacks();
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
	public short getNextAttackTimer(){
		return (short)(120+rand.nextInt(60)+((4-dragon.getWorldDifficulty())*20));
	}

	@Override
	public float overrideMovementSpeed(){
		return dragon.target != null?1.5F:1F;
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
		
		if (isOverriding)event.newTarget = attackCooldown > 1?null:overrideTarget;
		
		if (event.newTarget != null && !event.newTarget.equals(event.oldTarget) && rand.nextInt(5-dragon.getWorldDifficulty()) == 0){
			dragon.initShot().setTarget(event.newTarget).setType(ShotType.FREEZEBALL).shoot();
		}
	}
	
	@Override
	public void onTargetPositionSetEvent(TargetPositionSetEvent event){
		if (isOverriding)event.cancel();
	}
}