package chylex.hee.entity.boss.dragon.attacks.special;
import gnu.trove.map.hash.TObjectByteHashMap;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.boss.dragon.attacks.special.event.DamageTakenEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.TargetSetEvent;
import chylex.hee.entity.weather.EntityWeatherLightningBoltSafe;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.MathUtil;

public class DragonAttackSummoning extends DragonSpecialAttackBase{
	private final TObjectByteHashMap<UUID> lastStriked = new TObjectByteHashMap<>(6);
	private byte summonTimer;
	private byte summoned;
	private int totalTimer;
	private boolean ended;
	
	public DragonAttackSummoning(EntityBossDragon dragon, int attackId, int weight){
		super(dragon, attackId, weight);
	}
	
	@Override
	public void init(){
		super.init();
		lastStriked.clear();
		summonTimer = summoned = 0;
		totalTimer = 1200;
		ended = false;
		dragon.target = null;
	}
	
	@Override
	public void update(){
		super.update();
		
		List<EntityPlayer> viablePlayers = dragon.attacks.getViablePlayers();
		
		if (++summonTimer > 35-Math.min(viablePlayers.size()*4+(ModCommonProxy.opMobs ? 5 : 0), 20)){
			summonTimer = 0;
			boolean didSummon = false;
			
			for(int amt = MathUtil.clamp(MathUtil.ceil(viablePlayers.size()*(0.2D+rand.nextDouble()*0.25D)), 1, viablePlayers.size()), aggro = 0, total = 0; amt > 0; amt--){
				EntityPlayer player = viablePlayers.remove(rand.nextInt(viablePlayers.size()));
				
				/* TODO
				for(EntityMobAngryEnderman enderman:(List<EntityMobAngryEnderman>)dragon.worldObj.getEntitiesWithinAABB(EntityMobAngryEnderman.class, player.boundingBox.expand(14D, 5D, 14D))){
					if (enderman.getEntityToAttack() == player)++aggro;
					++total;
				}*/
				
				if (aggro < getDifficulty() && total < 6+getDifficulty()){
					Pos playerPos = Pos.at(player);
					boolean flying = !Pos.allBlocksMatch(playerPos, playerPos.offset(-5), pos -> pos.isAir(dragon.worldObj));
					
					if (flying){
						if (lastStriked.adjustOrPutValue(player.getPersistentID(), (byte)-1, (byte)0) <= 0){
							// TODO MultiDamage.from(dragon).addMagic(2F).addUnscaled(11F).attack(player);
							player.setFire(5);
							
							dragon.worldObj.addWeatherEffect(new EntityWeatherLightningBoltSafe(dragon.worldObj, player.posX, player.posY, player.posZ));
							lastStriked.put(player.getPersistentID(), (byte)(4+rand.nextInt(3)));
						}
						
						continue;
					}
					
					for(int a = 0; a < 3+rand.nextInt(getDifficulty()); a++){
						double x = player.posX+(rand.nextDouble()-0.5D)*13D, z = player.posZ+(rand.nextDouble()-0.5D)*13D;
						int y = 1+DragonUtil.getTopBlockY(dragon.worldObj, Blocks.end_stone, MathUtil.floor(x), MathUtil.floor(z), MathUtil.floor(player.posY+8));
						
						/* TODO EntityMobAngryEnderman enderman = new EntityMobAngryEnderman(dragon.worldObj);
						enderman.setPosition(x, y, z);
						// TODO no longer works enderman.setTarget(player);
						
						if ((getDifficulty() > 1 || ModCommonProxy.opMobs) && rand.nextInt(100) < 5+getDifficulty()*10+(ModCommonProxy.opMobs ? 25 : 0)){
							enderman.addPotionEffect(new PotionEffect(Potion.damageBoost.id, 2400, 0, true));
						}
						
						dragon.worldObj.addWeatherEffect(new EntityWeatherLightningBoltSafe(dragon.worldObj, x, y, z));
						dragon.worldObj.spawnEntityInWorld(enderman);*/
					}
					
					didSummon = true;
				}
			}
			
			if (didSummon && ++summoned > 2+getDifficulty()+(ModCommonProxy.opMobs ? 1 : 0))ended = true;
		}
		
		if (--totalTimer < 0)ended = true;
		
		if (dragon.ticksExisted%10 == 0){
			if (MathUtil.distance(dragon.posX, dragon.posZ) > 100D){
				dragon.targetX = (rand.nextDouble()-0.5D)*60;
				dragon.targetZ = (rand.nextDouble()-0.5D)*60;
			}
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
	public int getNextAttackTimer(){
		return super.getNextAttackTimer()+100;
	}
	
	@Override
	public float overrideMovementSpeed(){
		return 0.7F;
	}
	
	@Override
	public void onDamageTakenEvent(DamageTakenEvent event){
		totalTimer -= 40;
	}
	
	@Override
	public void onTargetSetEvent(TargetSetEvent event){
		event.newTarget = null;
	}
}