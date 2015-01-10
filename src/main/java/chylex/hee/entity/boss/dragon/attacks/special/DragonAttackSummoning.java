package chylex.hee.entity.boss.dragon.attacks.special;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.boss.dragon.attacks.special.event.TargetSetEvent;
import chylex.hee.entity.mob.EntityMobAngryEnderman;
import chylex.hee.entity.weather.EntityWeatherLightningBoltSafe;
import chylex.hee.proxy.ModCommonProxy;
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
		
		if (++summonTimer > 35-Math.min(viablePlayers.size()*4+(ModCommonProxy.opMobs ? 5 : 0),20)){
			summonTimer = 0;
			boolean didSummon = false;
			
			for(int amt = MathUtil.clamp(MathUtil.ceil(viablePlayers.size()*(0.2D+rand.nextDouble()*0.25D)),1,viablePlayers.size()), aggro = 0; amt > 0; amt--){
				EntityPlayer player = viablePlayers.remove(rand.nextInt(viablePlayers.size()));
				
				for(EntityMobAngryEnderman enderman:(List<EntityMobAngryEnderman>)dragon.worldObj.getEntitiesWithinAABB(EntityMobAngryEnderman.class,player.boundingBox.expand(7D,3D,7D))){
					if (enderman.getEntityToAttack() == player)++aggro;
				}
				
				if (aggro < 1+getDifficulty()){
					for(int a = 0; a < 3+rand.nextInt(2+getDifficulty()); a++){
						double x = player.posX+(rand.nextDouble()-0.5D)*11D, z = player.posZ+(rand.nextDouble()-0.5D)*11D;
						int y = 1+DragonUtil.getTopBlockY(dragon.worldObj,Blocks.end_stone,MathUtil.floor(x),MathUtil.floor(z),MathUtil.floor(player.posY+8));
						
						EntityMobAngryEnderman enderman = new EntityMobAngryEnderman(dragon.worldObj);
						enderman.setPosition(x,y,z);
						enderman.setTarget(player);
						
						if ((getDifficulty() > 1 || ModCommonProxy.opMobs) && rand.nextInt(100) < 5+getDifficulty()*10+(ModCommonProxy.opMobs ? 25 : 0)){
							enderman.addPotionEffect(new PotionEffect(Potion.damageBoost.id,2400,0,true));
						}
						
						dragon.worldObj.addWeatherEffect(new EntityWeatherLightningBoltSafe(dragon.worldObj,x,y,z));
						dragon.worldObj.spawnEntityInWorld(enderman);
					}
					
					didSummon = true;
				}
			}
			
			if (didSummon && ++summoned > 5+getDifficulty())ended = true;
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
	public void onTargetSetEvent(TargetSetEvent event){
		event.newTarget = null;
	}
}