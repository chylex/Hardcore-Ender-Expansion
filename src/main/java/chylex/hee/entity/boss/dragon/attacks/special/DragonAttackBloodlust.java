package chylex.hee.entity.boss.dragon.attacks.special;
import java.util.List;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
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
	private byte timer, counter;
	private boolean ended;
	
	public DragonAttackBloodlust(EntityBossDragon dragon, int attackId, int weight){
		super(dragon,attackId,weight);
	}
	
	@Override
	public void init(){
		super.init();
		timer = 40;
		counter = 0;
		ended = false;
		dragon.target = null;
		dragon.targetY = 86D;
	}
	
	@Override
	public void update(){
		super.update();
		
		if (++timer > 125-getDifficulty()*10){
			timer = -10;
			
			for(EntityPlayer player:dragon.attacks.getViablePlayers()){
				EntityEnderman enderman = DragonUtil.getClosestEntity(player,(List<EntityEnderman>)dragon.worldObj.getEntitiesWithinAABB(EntityEnderman.class,player.boundingBox.expand(18D,8D,18D)));
				
				if (enderman == null){
					int px = 0, py = 0, pz = 0;
					
					for(int attempt = 0; attempt < 40; attempt++){
						float rad = rand.nextFloat()*(float)Math.PI*2F;
						double len = 10D+rand.nextDouble()*8D;
						
						px = (int)Math.round(player.posX+MathHelper.sin(rad)*len);
						py = (int)Math.round(player.posY-(rand.nextDouble()-0.5D)*6D);
						pz = (int)Math.round(player.posZ+MathHelper.cos(rad)*len);
						
						if (dragon.worldObj.isAirBlock(px,py,pz))break;
					}
					
					for(int a = 0, xx, yy, zz; a < 2+rand.nextInt(3)+(getDifficulty()>>1); a++){
						for(int attempt = 0; attempt < 6; attempt++){
							if (dragon.worldObj.isAirBlock(xx = px+rand.nextInt(3)-1,yy = py+rand.nextInt(3)-1,zz = pz+rand.nextInt(3)-1))break;
						}
						
						spawnBatAt(px,py,pz,player);
					}
				}
				else{
					for(int a = 0; a < 2+rand.nextInt(3)+(getDifficulty()>>1); a++){
						spawnBatAt(enderman.posX+rand.nextDouble()-0.5D,enderman.posY+rand.nextDouble()*enderman.height,enderman.posZ+rand.nextDouble()-0.5D,player);
					}
					
					enderman.setDead();
					PacketPipeline.sendToAllAround(enderman,64D,new C20Effect(FXType.Basic.ENDERMAN_BLOODLUST_TRANSFORMATION,enderman));
				}
			}
			
			if (++counter > 2+(getDifficulty()>>1)+rand.nextInt(4))ended = true;
		}
		
		if (dragon.ticksExisted%10 == 0){
			if (MathUtil.distance(dragon.posX,dragon.posZ) > 100D){
				dragon.targetX = (rand.nextDouble()-0.5D)*60;
				dragon.targetZ = (rand.nextDouble()-0.5D)*60;
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
		return dragon.attacks.getHealthPercentage() < 40;
	}
	
	@Override
	public boolean hasEnded(){
		return ended;
	}
	
	@Override
	public int getNextAttackTimer(){
		return super.getNextAttackTimer()+70;
	}
	
	@Override
	public float overrideMovementSpeed(){
		return 0.6F;
	}
	
	@Override
	public void onDamageTakenEvent(DamageTakenEvent event){
		event.damage *= 0.2F;
	}
	
	@Override
	public void onTargetPositionSetEvent(TargetPositionSetEvent event){
		event.newTargetY = 80D+rand.nextDouble()*12D;
	}
	
	@Override
	public void onTargetSetEvent(TargetSetEvent event){
		event.newTarget = null;
	}
}
