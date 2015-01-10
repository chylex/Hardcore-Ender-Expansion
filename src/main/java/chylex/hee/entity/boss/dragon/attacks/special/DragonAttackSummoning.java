package chylex.hee.entity.boss.dragon.attacks.special;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.boss.dragon.attacks.special.event.MotionUpdateEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.TargetSetEvent;
import chylex.hee.entity.mob.EntityMobAngryEnderman;
import chylex.hee.entity.weather.EntityWeatherLightningBoltSafe;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.MathUtil;

public class DragonAttackSummoning extends DragonSpecialAttackBase{
	private int summonTimer;
	private short summoned;
	private float speed;
	private boolean ended;
	
	public DragonAttackSummoning(EntityBossDragon dragon, int attackId){
		super(dragon,attackId);
	}
	
	@Override
	public void init(){
		super.init();
		summonTimer = 0;
		summoned = 0;
		speed = 1F;
		ended = false;
	}
	
	@Override
	public void update(){
		super.update();
		
		List<EntityPlayer> viablePlayers = dragon.attacks.getViablePlayers();
		
		if (++summonTimer > 35-Math.min(viablePlayers.size()*4,20)){
			summonTimer = 0;
			
			for(int amt = MathUtil.clamp(MathUtil.ceil(viablePlayers.size()*(0.2D+rand.nextDouble()*0.25D)),1,viablePlayers.size()); amt > 0; amt--){
				EntityPlayer player = viablePlayers.remove(rand.nextInt(viablePlayers.size()));
				// TODO
			}
		}
		
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
				int rageDiff = getDifficulty(), amount = 0;
				summonTimer = 114-rageDiff*8;
				if ((rageDiff > 2 && rand.nextInt(4) == 0) || (rageDiff > 1 && rand.nextInt(6) == 0))amount = 1;
				
				List<EntityPlayer> nearbyPlayers = target.worldObj.getEntitiesWithinAABB(EntityPlayer.class,target.boundingBox.expand(48D,64D,48D));
				
				for(int a = 0; a < amount+Math.floor(nearbyPlayers.size()>>1); a++){
					int x = rand.nextInt(31)-15+(int)target.posX,z = rand.nextInt(31)-15+(int)target.posZ;
					int y = 1+DragonUtil.getTopBlockY(dragon.worldObj,Blocks.end_stone,x,z);
					
					EntityMobAngryEnderman buddy = new EntityMobAngryEnderman(dragon.worldObj);
					buddy.setPosition(x,y,z);
					buddy.setTarget(nearbyPlayers.get(rand.nextInt(nearbyPlayers.size())));
					
					dragon.worldObj.addWeatherEffect(new EntityWeatherLightningBoltSafe(dragon.worldObj,x,y,z));
					dragon.worldObj.spawnEntityInWorld(buddy);
				}
				
				++summoned;
			}
			if ((summoned > 4 && rand.nextInt(12)+6 < damageTaken) || summoned > 13)ended = true;
		}
	}
	
	@Override
	public boolean canStart(){
		return getDifficulty() > 0;
	}
	
	@Override
	public boolean hasEnded(){
		return ended;
	}
	
	@Override
	public float overrideMovementSpeed(){
		return speed;
	}
	
	@Override
	public int getNextAttackTimer(){
		return super.getNextAttackTimer()+80;
	}
	
	@Override
	public void onMotionUpdateEvent(MotionUpdateEvent event){
		super.onMotionUpdateEvent(event);
		/*if (phase == 0)return;
		
		if (dragon.motionX > 0.3)dragon.motionX = 0.25;
		else if (dragon.motionX < -0.3)dragon.motionX = -0.25;
		if (dragon.motionZ > 0.3)dragon.motionZ = 0.25;
		else if (dragon.motionZ < -0.3)dragon.motionZ = -0.25;*/
	}
	
	@Override
	public void onTargetSetEvent(TargetSetEvent event){
		event.newTarget = null;
	}
}