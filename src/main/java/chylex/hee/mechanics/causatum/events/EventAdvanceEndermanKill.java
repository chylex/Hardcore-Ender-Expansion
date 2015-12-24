package chylex.hee.mechanics.causatum.events;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import chylex.hee.entity.fx.FXType;
import chylex.hee.entity.mob.EntityMobEnderman;
import chylex.hee.entity.mob.ai.AIUtil;
import chylex.hee.entity.mob.ai.EntityAIWatchSuspicious;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C08PlaySound;
import chylex.hee.packets.client.C21EffectEntity;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.system.abstractions.Vec;
import chylex.hee.system.abstractions.entity.EntityAttributes;
import chylex.hee.system.util.MathUtil;

public class EventAdvanceEndermanKill extends CausatumEventInstance{
	private final List<EntityMobEnderman> endermen = new ArrayList<>();
	private final int totalAmount;
	private Vec deadEnderman;
	
	private boolean isDespawning;
	private int timer;
	
	EventAdvanceEndermanKill(EventTypes eventType, EntityPlayerMP player){
		super(eventType,player);
		this.totalAmount = 17+rand.nextInt(6);
	}
	
	@Override
	protected void onParams(Object...params){
		this.deadEnderman = Vec.pos((Entity)params[0]);
	}

	@Override
	protected void onUpdate(){
		if (isDespawning){
			if (endermen.isEmpty()){
				updateState(EventState.FINISHED);
			}
			else if (++timer > rand.nextInt(4)){
				timer = 0;
				
				for(int a = 0; a < 1+rand.nextInt(3) && !endermen.isEmpty(); a++){
					endermen.remove(rand.nextInt(endermen.size())).teleportDespawn(true);
				}
			}
		}
		else if (!hasPlayer()){
			startDespawning();
		}
		else{
			if (endermen.size() > 0 && endermen.stream().anyMatch(enderman -> enderman.isDead || enderman.lastDamage != 0F)){
				startDespawning();
				return;
			}
			
			if (timer == 0){
				updateState(EventState.SATISFIED); // only ran once, even if it immediately stops
			}
			else if (timer == 18){
				spawnEnderman();
			}
			else if (timer == 28){
				EntityPlayerMP player = getPlayer();
				PacketPipeline.sendToAllAround(player,64D,new C08PlaySound(C08PlaySound.ENDERMAN_STARE,player.posX,player.posY,player.posZ,1.4F,1F));
			}
			else if (timer == 300){
				startDespawning();
				return;
			}
			
			if (timer >= 31 && timer < 200+rand.nextInt(40)){
				if (endermen.size() < totalAmount && (timer == 18 || timer%27 == 0 || (timer%5 == 0 && rand.nextInt(Math.max(3,12-timer/10)) == 0))){
					spawnEnderman();
				}
			}
			
			++timer;
		}
	}
	
	private void startDespawning(){
		isDespawning = true;
		timer = 0;
	}
	
	private boolean spawnEnderman(){
		EntityPlayerMP player = getPlayer();
		World world = player.worldObj;
		
		EntityMobEnderman enderman = new EntityMobEnderman(world);
		PosMutable mpos = new PosMutable();
		
		// position
		for(int attempt = 0; attempt <= 2000; attempt++){
			if (attempt == 2000)return false;
			
			final Vec offset = Vec.xzRandom(rand);
			final double dist = 5D+3D*rand.nextDouble();
			enderman.setPosition(deadEnderman.x+offset.x*dist,MathUtil.floor(deadEnderman.y)+5,deadEnderman.z+offset.z*dist);
			
			for(int yOff = 0; yOff < 8; yOff++){
				enderman.setPosition(enderman.posX,enderman.posY-1,enderman.posZ);
				if (mpos.set(enderman).moveDown().getMaterial(world).blocksMovement())break;
			}
			
			if (world.checkNoEntityCollision(enderman.boundingBox) && world.getCollidingBoundingBoxes(enderman,enderman.boundingBox).isEmpty() &&
				!world.isAnyLiquid(enderman.boundingBox) && enderman.getDistanceSqToEntity(player) > 49D)break; // 7 blocks
		}
		
		// behavior
		enderman.setControlledExternally();
		enderman.setAggressive(true);
		
		enderman.getLookHelper().setLookPosition(player.posX,player.posY+player.getEyeHeight(),player.posZ,360F,180F);
		enderman.getLookHelper().onUpdateLook();
		enderman.rotationYaw = enderman.prevRotationYaw = enderman.rotationYawHead;
		
		AIUtil.clearEntityTasks(enderman);
		EntityAITasks tasks = enderman.tasks;
		
		tasks.addTask(1,new EntityAISwimming(enderman));
		tasks.addTask(2,new EntityAIWatchSuspicious(enderman,this::getPlayer));
		
		EntityAttributes.applyModifier(enderman,EntityAttributes.movementSpeed,EntityMobEnderman.noMovementModifier);
		
		// spawn
		world.spawnEntityInWorld(enderman);
		endermen.add(enderman);
		PacketPipeline.sendToAllAround(enderman,64D,new C21EffectEntity(FXType.Entity.SIMPLE_TELEPORT,enderman));
		return true;
	}

	@Override
	protected void onPlayerDisconnected(){}

	@Override
	protected void onPlayerDied(){}

	@Override
	protected void onPlayerChangedDimension(){}
}
