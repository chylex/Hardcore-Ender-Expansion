package chylex.hee.entity.boss.dragon.attacks.special;
import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.boss.dragon.attacks.special.event.DamageTakenEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.MotionUpdateEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.TargetPositionSetEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.TargetSetEvent;
import chylex.hee.entity.mob.EntityMobAngryEnderman;
import chylex.hee.entity.weather.EntityWeatherLightningBoltSafe;
import chylex.hee.mechanics.knowledge.KnowledgeRegistrations;
import chylex.hee.mechanics.knowledge.util.ObservationUtil;
import chylex.hee.system.util.DragonUtil;

public class DragonAttackSummon extends DragonSpecialAttackBase{
	private EntityLivingBase target;
	private boolean ended = false;
	private int summonTimer = 0;
	private short summoned = 0;
	private float speed = 1F;
	
	public DragonAttackSummon(EntityBossDragon dragon, int attackId){
		super(dragon,attackId);
	}
	
	@Override
	public void init(){
		super.init();
		ended = false;
		summonTimer = 0;
		summoned = 0;
		speed = 1F;
	}
	
	@Override
	public void update(){
		super.update();
		if (target == null)target = dragon.attacks.getWeakPlayer();
		if (target == null){
			ended = true;
			return;
		}
		dragon.targetX = target.posX;
		dragon.targetY = 125;
		dragon.targetZ = target.posZ;
		double dist = dragon.getDistance(target.posX,125,target.posZ);
		speed = (dist < 9D ? 0 : 1);
		if (phase == 0){
			if (dist < 10D)phase = 1;
		}
		else if (phase == 1){
			if (Math.abs(dragon.motionX) < 0.5D && Math.abs(dragon.motionZ) < 0.5D)phase = 2;
			if (summonTimer++ > 150){
				summonTimer = 0;
				phase = 2;
			}
		}
		else if (phase == 2){
			if (summonTimer-- < 0){
				int rageDiff = dragon.getWorldDifficulty(),amount = 0;
				summonTimer = 114-rageDiff*8;
				if ((rageDiff > 2 && rand.nextInt(4) == 0) || (rageDiff > 1 && rand.nextInt(6) == 0))amount = 1;
				
				List<?> nearbyPlayers = target.worldObj.getEntitiesWithinAABB(EntityPlayer.class,target.boundingBox.expand(48D,64D,48D));
				for(int a = 0; a < amount+Math.floor(nearbyPlayers.size()/2); a++){
					int x = rand.nextInt(30)-15+(int)target.posX,z = rand.nextInt(30)-15+(int)target.posZ;
					int y = 1+DragonUtil.getTopBlock(dragon.worldObj,Blocks.end_stone,x,z);
					
					EntityMobAngryEnderman buddy = new EntityMobAngryEnderman(dragon.worldObj);
					buddy.setPosition(x,y,z);
					buddy.setTarget((EntityPlayer)nearbyPlayers.get(rand.nextInt(nearbyPlayers.size())));
					
					dragon.worldObj.addWeatherEffect(new EntityWeatherLightningBoltSafe(dragon.worldObj,x,y,z));
					dragon.worldObj.spawnEntityInWorld(buddy);
				}
				
				summoned++;
				
				if (rand.nextBoolean()){
					for(EntityPlayer observer:ObservationUtil.getAllObservers(dragon,250D))KnowledgeRegistrations.ENDER_DRAGON.tryUnlockFragment(observer,0.41F,new short[]{ 7,13 });
				}
			}
			if ((summoned > 4 && rand.nextInt(12)+6 < damageTaken) || summoned > 13)ended = true;
		}
	}
	
	@Override
	public void end(){
		super.end();
		if (target != null && target.getHealth() < 8)dragon.doFatalityAttack(target);
	}
	
	@Override
	public boolean canStart(){
		return dragon.getWorldDifficulty() > 0;
	}
	
	@Override
	public boolean hasEnded(){
		return ended || (target != null && target.isDead);
	}
	
	@Override
	public float overrideMovementSpeed(){
		return speed;
	}
	
	@Override
	public short getNextAttackTimer(){
		return (short)(super.getNextAttackTimer()+80);
	}
	
	@Override
	public void onDamageTakenEvent(DamageTakenEvent event){
		super.onDamageTakenEvent(event);
		if (effectivness < 0)summoned += 1;
	}
	
	@Override
	public void onMotionUpdateEvent(MotionUpdateEvent event){
		super.onMotionUpdateEvent(event);
		if (phase == 0)return;
		if (dragon.motionX > 0.3)dragon.motionX = 0.25;
		else if (dragon.motionX < -0.3)dragon.motionX = -0.25;
		if (dragon.motionZ > 0.3)dragon.motionZ = 0.25;
		else if (dragon.motionZ < -0.3)dragon.motionZ = -0.25;
	}
	
	@Override
	public void onTargetSetEvent(TargetSetEvent event){
		event.newTarget = null;
	}
	
	@Override
	public void onTargetPositionSetEvent(TargetPositionSetEvent event){
		event.cancel();
	}
}