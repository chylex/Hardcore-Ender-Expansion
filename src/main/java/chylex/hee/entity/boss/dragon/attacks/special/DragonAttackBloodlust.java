package chylex.hee.entity.boss.dragon.attacks.special;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.boss.dragon.attacks.special.event.DamageTakenEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.TargetPositionSetEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.TargetSetEvent;
import chylex.hee.entity.fx.FXType;
import chylex.hee.entity.mob.EntityMobVampiricBat;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C20Effect;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.MathUtil;

public class DragonAttackBloodlust extends DragonSpecialAttackBase{
	private int targetX, targetZ;
	private float moveSpeed;
	private byte timer,counter;
	
	public DragonAttackBloodlust(EntityBossDragon dragon, int attackId){
		super(dragon,attackId);
	}
	
	@Override
	public void init(){
		super.init();
		timer = (byte)(80+(counter = (byte)(targetX = targetZ = 0)));
		moveSpeed = 1F;
		
		for(EntityPlayer player:(List<EntityPlayer>)dragon.worldObj.playerEntities){
			targetX += player.posX;
			targetZ += player.posZ;
		}
		
		targetX /= dragon.worldObj.playerEntities.size();
		targetZ /= dragon.worldObj.playerEntities.size();
		
		dragon.target = null;
	}
	
	@Override
	public void update(){
		super.update();
		dragon.targetY = 90;
		
		if (phase == 0){
			dragon.targetX = targetX;
			dragon.targetZ = targetZ;
			
			if (MathUtil.distance(targetX-dragon.posX,targetZ-dragon.posZ) < 80D && (moveSpeed -= 0.035F) < 0.01F){
				moveSpeed = 0F;
				phase = 1;
			}
		}
		else if (phase == 1){
			if (--timer == 0){
				++counter;
				timer = (byte)(25+rand.nextInt(30)+7*(4-dragon.getWorldDifficulty())-2*Math.min(5,dragon.worldObj.playerEntities.size()));

				for(EntityPlayer player:(List<EntityPlayer>)dragon.worldObj.playerEntities){
					if (player.capabilities.isCreativeMode)continue;
					
					List<EntityEnderman> endermanList = dragon.worldObj.getEntitiesWithinAABB(EntityEnderman.class,player.boundingBox.expand(64D,96D,64D));
					
					if (endermanList.isEmpty()){
						for(int a = 0; a < 2+rand.nextInt(3)+(dragon.getWorldDifficulty()>>1); a++){
							double pX = 0,pY = 0,pZ = 0,len = 24D;
							for(int attempt = 0; attempt < 18; attempt++){
								double rad = rand.nextDouble()*2D*Math.PI;
								pY = player.posY-6D+rand.nextDouble()*12D;
								pX = player.posX+Math.cos(rad)*len;
								pZ = player.posZ+Math.sin(rad)*len;
								
								if (dragon.worldObj.isAirBlock(MathUtil.floor(pX),MathUtil.floor(pY),MathUtil.floor(pZ)))break;
								--len;
							}
							
							spawnBatAt(pX,pY,pZ,player);
						}
					}
					else{
						EntityEnderman enderman;
						
						Map<EntityEnderman,Double> endermen = new HashMap<>();
						for(EntityEnderman e:endermanList)endermen.put(e,e.getDistanceSqToEntity(player));

						for(Entry<EntityEnderman,Double> entry:DragonUtil.sortMapByValueAscending(endermen)){
							enderman = entry.getKey();
							if (enderman.isDead)continue;
						
							for(int a = 0; a < 2+rand.nextInt(3)+(dragon.getWorldDifficulty()>>1); a++){
								spawnBatAt(enderman.posX,enderman.posY+rand.nextFloat()*enderman.height,enderman.posZ,player);
							}
							
							enderman.setDead();
							PacketPipeline.sendToAllAround(enderman,64D,new C20Effect(FXType.Basic.ENDERMAN_BLOODLUST_TRANSFORMATION,enderman));
							break;
						}
					}
				}
			}
		}
	}
	
	private void spawnBatAt(double x, double y, double z, EntityPlayer target){
		EntityMobVampiricBat bat = new EntityMobVampiricBat(dragon.worldObj);
		bat.setPosition(x,y,z);
		bat.target = target;
		dragon.worldObj.spawnEntityInWorld(bat);
	}
	
	@Override
	public boolean canStart(){
		return dragon.getHealth() < 120 && !dragon.worldObj.playerEntities.isEmpty();
	}
	
	@Override
	public boolean hasEnded(){
		return counter > 4+Math.min(6,dragon.worldObj.playerEntities.size()>>1)+dragon.getWorldDifficulty();
	}
	
	@Override
	public float overrideMovementSpeed(){
		return moveSpeed;
	}
	
	@Override
	public void onDamageTakenEvent(DamageTakenEvent event){
		event.damage = Math.max(event.damage*0.15F,0.6F);
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
