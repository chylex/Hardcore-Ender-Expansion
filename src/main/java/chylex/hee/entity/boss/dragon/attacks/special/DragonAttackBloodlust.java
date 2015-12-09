package chylex.hee.entity.boss.dragon.attacks.special;
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
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.system.abstractions.entity.EntitySelector;
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
				EntityEnderman enderman = DragonUtil.getClosestEntity(player,EntitySelector.type(dragon.worldObj,EntityEnderman.class,player.boundingBox.expand(18D,8D,18D)));
				
				if (enderman == null){
					PosMutable mpos = new PosMutable();
					
					for(int attempt = 0; attempt < 40; attempt++){
						float rad = rand.nextFloat()*(float)Math.PI*2F;
						double len = 10D+rand.nextDouble()*8D;
						
						mpos.set(player.posX+Math.sin(rad)*len,player.posY-(rand.nextDouble()-0.5D)*6D,player.posZ+Math.cos(rad)*len);
						if (mpos.isAir(dragon.worldObj))break;
					}
					
					for(int a = 0; a < 2+rand.nextInt(3)+(getDifficulty()>>1); a++){
						for(int attempt = 0; attempt < 6; attempt++){
							if (mpos.move(rand.nextInt(3)-1,rand.nextInt(3)-1,rand.nextInt(3)-1).isAir(dragon.worldObj))break;
						}
						
						spawnBatAt(mpos.x,mpos.y,mpos.z,player);
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
