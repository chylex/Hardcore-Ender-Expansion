package chylex.hee.entity.mob;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.entity.projectile.EntityProjectileMinerShot;
import chylex.hee.item.ItemList;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C07AddPlayerVelocity;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.MathUtil;

public class EntityMobHauntedMiner extends EntityFlying implements IMob{
	private static final byte ATTACK_TIMER = 80;
	private static final byte ATTACK_NONE = 0, ATTACK_PROJECTILES = 1, ATTACK_LAVA = 2, ATTACK_BLAST_WAVE = 3;
	
	private AxisAlignedBB bottomBB = AxisAlignedBB.getBoundingBox(0D,0D,0D,0D,0D,0D);
	private EntityLivingBase target;
	private double targetX, targetY, targetZ, targetAngleAdd;
	private boolean targetAngleDir;
	private byte wanderResetTimer = -120, nextAttackTimer = ATTACK_TIMER, currentAttack = ATTACK_NONE, currentAttackTime;
	
	public EntityMobHauntedMiner(World world){
		super(world);
		setSize(2.2F,1.7F);
		isImmuneToFire = true;
		experienceValue = 10;
	}
	
	@Override
	protected void entityInit(){
		super.entityInit();
		dataWatcher.addObject(16,Byte.valueOf(ATTACK_NONE));
	}
	
	@Override
	protected void applyEntityAttributes(){
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(ModCommonProxy.opMobs ? 80D : 55D);
	}
	
	@Override
	protected void updateEntityActionState(){
		if (!worldObj.isRemote && worldObj.difficultySetting == EnumDifficulty.PEACEFUL)setDead();
		despawnEntity();
		
		if (target == null){
			if (--wanderResetTimer < -120 || rand.nextInt(300) == 0){
				wanderResetTimer = 0;
				
				for(int attempt = 0, xx, yy, zz; attempt < 32; attempt++){
					xx = (int)Math.floor(posX)+rand.nextInt(12)-rand.nextInt(12);
					zz = (int)Math.floor(posZ)+rand.nextInt(12)-rand.nextInt(12);
					yy = (int)Math.floor(posY);
					
					if (worldObj.isAirBlock(xx,yy,zz)){
						while(worldObj.isAirBlock(xx,--yy,zz) && Math.abs(posY-yy) < 10);
						if (Math.abs(posY-yy) >= 10)continue;
					}
					else{
						while(!worldObj.isAirBlock(xx,++yy,zz) && Math.abs(posY-yy) < 10);
						if (Math.abs(posY-yy) >= 10)continue;
					}
					
					targetX = xx+rand.nextDouble();
					targetY = yy+rand.nextDouble()*0.2D+3D;
					targetZ = zz+rand.nextDouble();
					wanderResetTimer += 120;
					break;
				}
				
				wanderResetTimer += rand.nextInt(30)+20;
			}
			
			List<Entity> entities = worldObj.getEntitiesWithinAABBExcludingEntity(this,boundingBox.expand(32D,16D,32D));
			
			if (!entities.isEmpty()){
				Entity temp = entities.get(rand.nextInt(entities.size()));
				if (temp instanceof EntityPig)target = (EntityLivingBase)temp;
			} // TODO
		}
		else{			
			targetX = target.posX;
			targetZ = target.posZ;
			targetY = target.posY+2D;
			
			if (!worldObj.isRemote){
				if (currentAttack != ATTACK_NONE){
					boolean hasFinished = false;
					++currentAttackTime;
					
					switch(currentAttack){
						case ATTACK_PROJECTILES:
							if (currentAttackTime == 50){
								Vec3 look = getLookVec();
								
								look.rotateAroundY(MathUtil.toRad(36F));
								worldObj.spawnEntityInWorld(new EntityProjectileMinerShot(worldObj,this,posX+look.xCoord*1.5D,posY+0.7D,posZ+look.zCoord*1.5D,target));
								look.rotateAroundY(MathUtil.toRad(-72F));
								worldObj.spawnEntityInWorld(new EntityProjectileMinerShot(worldObj,this,posX+look.xCoord*1.5D,posY+0.7D,posZ+look.zCoord*1.5D,target));
								hasFinished = true;
							}
							
							break;
							
						case ATTACK_LAVA:
							
							break;
							
						case ATTACK_BLAST_WAVE:
							if (currentAttackTime == 25){
								for(Object o:worldObj.getEntitiesWithinAABBExcludingEntity(this,boundingBox.expand(12D,4D,12D).offset(0D,-2D,0D))){
									Entity entity = (Entity)o;
									
									double dist = MathUtil.distance(entity.posX-posX,entity.posZ-posZ);
									if (dist > 12D)continue;
									
									double[] vec = DragonUtil.getNormalizedVector(entity.posX-posX,entity.posZ-posZ);
									double strength = 1.5D+(12D-dist)*0.25D;
									vec[0] *= strength;
									vec[1] *= strength;
									
									entity.attackEntityFrom(DamageSource.causeMobDamage(this),8F);
									if (entity instanceof EntityPlayer)PacketPipeline.sendToPlayer((EntityPlayer)entity,new C07AddPlayerVelocity(vec[0],0.4D,vec[1]));
									
									entity.motionX += vec[0];
									entity.motionY += 0.4D;
									entity.motionZ += vec[1];
								}
								
								for(int attempt = 0, xx, yy, zz; attempt < 128; attempt++){
									xx = (int)Math.floor(posX)+rand.nextInt(20)-10;
									zz = (int)Math.floor(posZ)+rand.nextInt(20)-10;
									if (MathUtil.distance(xx-posX,zz-posZ) > 10D)continue;
									
									yy = (int)Math.floor(posY)-1;
									
									for(int yAttempt = 0; yAttempt < 4; yAttempt++){
										if (worldObj.isAirBlock(xx,yy,zz) && !worldObj.isAirBlock(xx,yy-1,zz)){
											worldObj.setBlock(xx,yy,zz,Blocks.fire);
											break;
										}
										else --yy;
									}
								}
								
								hasFinished = true;
							}
							
							break;
							
						default: hasFinished = true;
					}
					
					if (hasFinished || currentAttackTime > 120){
						currentAttack = ATTACK_NONE;
						nextAttackTimer = (byte)(ATTACK_TIMER-5*worldObj.difficultySetting.getDifficultyId());
						currentAttackTime = 0;
						dataWatcher.updateObject(16,Byte.valueOf((byte)0));
					}
				}
				else if (--nextAttackTimer <= 0){System.out.println(MathUtil.distance(target.posX-posX,target.posZ-posZ));
					currentAttack = (MathUtil.distance(target.posX-posX,target.posZ-posZ) < 7.5D && rand.nextInt(4) != 0) || rand.nextInt(5) == 0 ? ATTACK_BLAST_WAVE : (rand.nextInt(7) <= 4 ? ATTACK_PROJECTILES : ATTACK_LAVA);
					dataWatcher.updateObject(16,Byte.valueOf(currentAttack));
				}
			}
			
			if (target.isDead || (currentAttack == ATTACK_NONE && getDistanceToEntity(target) > 40D))target = null;
		}
		
		double speed = 0.05D;
		
		if (target != null){
			double dist = getDistanceToEntity(target);
			
			if (dist > 13D)speed = currentAttack == ATTACK_NONE ? 0.2D : 0.05D;
			else if (dist < 9D)speed = 0D;
		}
		
		double[] xz = DragonUtil.getNormalizedVector(targetX-posX,targetZ-posZ);
		motionX = xz[0]*speed;
		motionZ = xz[1]*speed;
		if (Math.abs(targetY-posY) > 1D)motionY = (targetY-posY)*0.02D;
		
		if (target != null)renderYawOffset = rotationYaw = -MathUtil.toDeg((float)Math.atan2(targetX-posX,targetZ-posZ));
		else renderYawOffset = rotationYaw = -MathUtil.toDeg((float)Math.atan2(motionX,motionZ));
	}
	
	@Override
	public void onLivingUpdate(){
		super.onLivingUpdate();
		
		if (worldObj.isRemote){
			for(int a = 0; a < 2; a++)HardcoreEnderExpansion.fx.flame(worldObj,posX+(rand.nextDouble()-0.5D)*0.2D,posY,posZ+(rand.nextDouble()-0.5D)*0.2D,0D,-0.05D,0D,8);
			
			byte attack = dataWatcher.getWatchableObjectByte(16);
			
			if (attack != ATTACK_NONE){
				rotationYaw = renderYawOffset = rotationYawHead;
				Vec3 look = getLookVec();
				
				look.rotateAroundY(MathUtil.toRad(36F));
				worldObj.spawnParticle("mobSpell",posX+look.xCoord*1.5D+(rand.nextDouble()-0.5D)*0.2D,posY+0.7D,posZ+look.zCoord*1.5D+(rand.nextDouble()-0.5D)*0.2D,0.9D,0.6D,0D);
				look.rotateAroundY(MathUtil.toRad(-72F));
				worldObj.spawnParticle("mobSpell",posX+look.xCoord*1.5D+(rand.nextDouble()-0.5D)*0.2D,posY+0.7D,posZ+look.zCoord*1.5D+(rand.nextDouble()-0.5D)*0.2D,0.9D,0.6D,0D);
				
				++currentAttackTime;
				
				switch(attack){
					case ATTACK_BLAST_WAVE:
						if (currentAttackTime == 24){
							for(int flame = 0; flame < 180; flame++)HardcoreEnderExpansion.fx.flame(worldObj,posX+(rand.nextDouble()-0.5D)*0.2D,posY+height*0.5D,posZ+(rand.nextDouble()-0.5D)*0.2D,(rand.nextDouble()-0.5D)*2D,(rand.nextDouble()-0.5D)*2D,(rand.nextDouble()-0.5D)*2D,5+rand.nextInt(20));
						}
						
						break;
						
					default:
				}
			}
			else currentAttackTime = 0;
		}
		else{
			List<Entity> nearEntities = worldObj.getEntitiesWithinAABBExcludingEntity(this,bottomBB.setBounds(posX-1.65D,posY-3,posZ-1.65D,posX+1.65D,posY,posZ+1.65D));
			
			for(Entity entity:nearEntities){
				if (entity instanceof EntityMobHauntedMiner)continue;
				entity.attackEntityFrom(DamageSource.causeMobDamage(this),2F);
				entity.setFire(5);
				entity.hurtResistantTime -= 2;
			}
			
			if (currentAttack != ATTACK_NONE)rotationYaw = rotationYawHead;
		}
	}
	
	@Override
	public void setRevengeTarget(EntityLivingBase newTarget){
		if (target == null || newTarget.getDistanceSqToEntity(this) < target.getDistanceSqToEntity(this)){
			target = newTarget;
			nextAttackTimer = ATTACK_TIMER;
		}
    }
	
	@Override
	public void knockBack(Entity entity, float damage, double xPower, double zPower){}
	
	@Override
	public void dropFewItems(boolean recentlyHit, int looting){
		for(int a = 0; a < rand.nextInt(2+rand.nextInt(2)+looting); a++)dropItem(ItemList.fire_shard,1);
	}
	
	@Override
	public String getCommandSenderName(){
		return StatCollector.translateToLocal("entity.hauntedMiner.name");
	}
}
