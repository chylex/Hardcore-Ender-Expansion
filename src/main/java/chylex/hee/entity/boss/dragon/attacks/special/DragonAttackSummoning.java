package chylex.hee.entity.boss.dragon.attacks.special;
import gnu.trove.map.hash.TObjectByteHashMap;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.boss.dragon.attacks.special.event.TargetSetEvent;
import chylex.hee.entity.mob.EntityMobAngryEnderman;
import chylex.hee.entity.weather.EntityWeatherLightningBoltSafe;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.MathUtil;

public class DragonAttackSummoning extends DragonSpecialAttackBase{
	private final TObjectByteHashMap<UUID> lastStriked = new TObjectByteHashMap<>(6);
	private byte summonTimer;
	private byte summoned;
	private boolean ended;
	
	public DragonAttackSummoning(EntityBossDragon dragon, int attackId){
		super(dragon,attackId);
	}
	
	@Override
	public void init(){
		super.init();
		lastStriked.clear();
		summonTimer = 0;
		summoned = 0;
		ended = false;
		dragon.target = null;
	}
	
	@Override
	public void update(){
		super.update();
		
		List<EntityPlayer> viablePlayers = dragon.attacks.getViablePlayers();
		
		if (++summonTimer > 35-Math.min(viablePlayers.size()*4+(ModCommonProxy.opMobs ? 5 : 0),20)){
			summonTimer = 0;
			boolean didSummon = false;
			
			for(int amt = MathUtil.clamp(MathUtil.ceil(viablePlayers.size()*(0.2D+rand.nextDouble()*0.25D)),1,viablePlayers.size()), aggro = 0, total = 0; amt > 0; amt--){
				EntityPlayer player = viablePlayers.remove(rand.nextInt(viablePlayers.size()));
				
				for(EntityMobAngryEnderman enderman:(List<EntityMobAngryEnderman>)dragon.worldObj.getEntitiesWithinAABB(EntityMobAngryEnderman.class,player.boundingBox.expand(7D,3D,7D))){
					if (enderman.getEntityToAttack() == player)++aggro;
					++total;
				}
				
				if (aggro < 1+getDifficulty() && total < 7+getDifficulty()){
					boolean flying = true;
					
					for(int a = 0, xx = MathUtil.floor(player.posX), zz = MathUtil.floor(player.posZ), testY = MathUtil.floor(player.posY)-1; a < 4; a++){
						if (!dragon.worldObj.isAirBlock(xx,testY-a,zz)){
							flying = false;
							break;
						}
					}
					
					if (flying){
						if (lastStriked.adjustOrPutValue(player.getPersistentID(),(byte)-1,(byte)0) <= 0){
							player.attackEntityFrom(DamageSource.magic,1F);
							player.hurtResistantTime = 0;
							player.attackEntityFrom(DamageSource.causeMobDamage(dragon),7F);
							player.setFire(5);
							
							dragon.worldObj.addWeatherEffect(new EntityWeatherLightningBoltSafe(dragon.worldObj,player.posX,player.posY,player.posZ));
							lastStriked.put(player.getPersistentID(),(byte)(2+rand.nextInt(3)));
						}
						continue;
					}
					
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
		return 0.7F;
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