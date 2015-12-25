package chylex.hee.entity.boss;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import chylex.hee.entity.GlobalMobData.IIgnoreEnderGoo;
import chylex.hee.entity.RandomNameGenerator;
import chylex.hee.game.achievements.AchievementManager;
import chylex.hee.init.BlockList;
import chylex.hee.init.ItemList;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C07AddPlayerVelocity;
import chylex.hee.packets.client.C08PlaySound;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.abstractions.Vec;
import chylex.hee.system.abstractions.entity.EntityAttributes;
import chylex.hee.system.abstractions.entity.EntityDataWatcher;
import chylex.hee.system.abstractions.entity.EntitySelector;
import chylex.hee.system.util.BlockPosM;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.MathUtil;
import chylex.hee.system.util.WorldUtil;
import chylex.hee.system.util.WorldUtil.GameRule;
import chylex.hee.tileentity.TileEntityLaserBeam;

public class EntityMiniBossEnderEye extends EntityFlying implements IBossDisplayData, IIgnoreEnderGoo{
	private enum Data{ ASLEEP, ANIMATION_TIME }
	
	private EntityDataWatcher entityData;
	private byte sleepTimer, healTimer, attackTimer;
	private short laserTopY;
	private AttackType attackType, lastAttackType;
	public EntityLivingBase target;
	
	public EntityMiniBossEnderEye(World world){
		super(world);
		setSize(1.25F,1.25F);
		experienceValue = 35;
		scoreValue = 25;
		isImmuneToFire = true;
		ignoreFrustumCheck = true;
		
		RandomNameGenerator.generateEntityName(this,rand.nextInt(3)+4);
	}
	
	public EntityMiniBossEnderEye(World world, double x, double y, double z){
		this(world);
		setPosition(x,y,z);
	}
	
	@Override
	protected void entityInit(){
		super.entityInit();
		entityData = new EntityDataWatcher(this);
		entityData.addBoolean(Data.ASLEEP,true);
		entityData.addByte(Data.ANIMATION_TIME);
	}
	
	@Override
	protected void applyEntityAttributes(){
		super.applyEntityAttributes();
		EntityAttributes.setValue(this,EntityAttributes.maxHealth,ModCommonProxy.opMobs ? 350D : 250D);
		EntityAttributes.setValue(this,EntityAttributes.movementSpeed,1.8D);
		
	}
	
	@Override
	protected void updateEntityActionState(){
		if (isAsleep()){
			if (++healTimer >= 7-worldObj.difficultySetting.getDifficultyId() && getHealth() < getMaxHealth()){
				healTimer = 0;
				heal(1);
			}
			
			sleepTimer = 0;
			motionX = motionY = motionZ = 0D;
		}
		else{
			if (target == null){
				if (Math.abs(motionX) > 0D)motionX *= 0.25D;
				if (Math.abs(motionY) > 0D)motionY *= 0.25D;
				if (Math.abs(motionZ) > 0D)motionZ *= 0.25D;
				
				if (++sleepTimer == 60)motionX = motionY = motionZ = 0D;
				if (sleepTimer > 80){
					setIsAsleep(true);
					setAttackAnimationTime((byte)0);
					attackType = null;
				}
				else{
					List<EntityPlayer> nearPlayers = EntitySelector.players(worldObj,boundingBox.expand(8D,4D,8D));
					
					if (!nearPlayers.isEmpty()){
						target = nearPlayers.get(0);
						sleepTimer = 0;
					}
					
					attackTimer = (byte)(-65+rand.nextInt(15)); // TODO fix this mess
				}
			}
			else if (getDistanceSqToEntity(target) < 60D){
				double diffX = posX-target.posX;
				double diffY = posY-(target.posY+target.height*0.5D);
				double diffZ = posZ-target.posZ;
				
				double distance = Math.sqrt(diffX*diffX+diffZ*diffZ);
				
				rotationYaw = DragonUtil.rotateSmoothly(rotationYaw,(float)(MathUtil.toDeg(Math.atan2(diffZ,diffX)))-270F,6F);
				rotationPitch = DragonUtil.rotateSmoothly(rotationPitch,(float)(-(MathUtil.toDeg(Math.atan2(diffY,distance)))),8F);
				
				byte attackAnim = getAttackAnimationTime();
				if (attackAnim == 0 && --attackTimer<-100){
					attackTimer += 38+rand.nextInt(20);
					
					for(int attempt = 0; attempt < 3; attempt++){
						attackType = AttackType.getById(rand.nextInt(AttackType.values().length));
						if (attackType != lastAttackType)break;
					}
					lastAttackType = attackType;
					
					setAttackAnimationTime((byte)1);
				}
				else if (attackAnim >= 1){
					setMoveForward(0F);
					if (attackAnim < attackType.getLength())setAttackAnimationTime((byte)(attackAnim+1));
					else setAttackAnimationTime((byte)0);
					
					/*
					 * Poof attack
					 */
					if (attackType == AttackType.Poof){
						if (attackAnim == 34){
							if (WorldUtil.getRuleBool(worldObj,GameRule.MOB_GRIEFING)){
								BlockPosM tmpPos = BlockPosM.tmp();
								
								for(int a = 0, hits = 0; a < 200 && hits < 16+worldObj.difficultySetting.getDifficultyId(); a++){
									Block block = tmpPos.set(this).move(rand.nextInt(15)-7,rand.nextInt(8)-4,rand.nextInt(15)-7).getBlock(worldObj);
									
									if (block.getMaterial() != Material.air && block.getBlockHardness(worldObj,tmpPos.x,tmpPos.y,tmpPos.z) != -1F){
										tmpPos.setAir(worldObj);
										worldObj.playAuxSFX(2001,tmpPos.x,tmpPos.y,tmpPos.z,Block.getIdFromBlock(Blocks.obsidian));
										++hits;
									}
								}
							}
							
							for(EntityPlayer player:EntitySelector.players(worldObj,boundingBox.expand(6D,6D,6D))){
								Vec vec = Vec.between(this,player).normalized().multiplied(player.isBlocking() ? 1.4D : 2.4D);
								
								PacketPipeline.sendToPlayer(player,new C07AddPlayerVelocity(vec.x,0.34D,vec.z));
								player.motionX += vec.x;
								player.motionY += 0.34D;
								player.motionZ += vec.z;
								
								// TODO player.attackEntityFrom(new DamageSourceMobUnscaled(this),DamageSourceMobUnscaled.getDamage(ModCommonProxy.opMobs ? 7F : 4F,worldObj.difficultySetting));
							}
							
							PacketPipeline.sendToAllAround(this,64D,new C08PlaySound(C08PlaySound.ENDEREYE_ATTACK_POOF,posX,posY,posZ,1F,rand.nextFloat()*0.2F+0.9F));
						}
					}
					/*
					 * Nausea
					 */
					else if (attackType == AttackType.Nausea){
						if (attackAnim == 17){
							for(EntityPlayer player:EntitySelector.players(worldObj,boundingBox.expand(6D,6D,6D)))player.addPotionEffect(new PotionEffect(Potion.confusion.id,220,0,true));
						}
						else if (attackAnim == 19){
							PacketPipeline.sendToAllAround(this,64D,new C08PlaySound(C08PlaySound.ENDEREYE_ATTACK_CONFUSION,posX,posY,posZ,1F,rand.nextFloat()*0.2F+0.9F));
						}
						else if (attackAnim == 26){
							for(EntityPlayer player:EntitySelector.players(worldObj,boundingBox.expand(6D,6D,6D))){
								player.addPotionEffect(new PotionEffect(Potion.blindness.id,160,0,true));
								player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id,120,0,true));
							}
						}
					}
					/*
					 * Laser beams
					 */
					else if (attackType == AttackType.LaserBeams){
						if (attackAnim > 35 && attackAnim < 99 && attackAnim%7 == 0){
							BlockPosM tmpPos = BlockPosM.tmp(this);
							int myY = MathUtil.floor(posY), attempt, minY;
							
							for(attempt = 0; attempt < 12; attempt++){
								tmpPos.set(MathUtil.floor(posX)+rand.nextInt(17)-8,-1,MathUtil.floor(posZ)+rand.nextInt(17)-8);
								
								if (!tmpPos.setY(myY).isAir(worldObj))continue;
								
								for(tmpPos.y = myY; tmpPos.y > myY-6; tmpPos.y--){
									if (!tmpPos.isAir(worldObj))break;
									else if (tmpPos.y == myY-4){
										tmpPos.y = -1;
										break;
									}
								}
								
								if (tmpPos.y == -1)continue;
								minY = tmpPos.y;
								
								if (laserTopY == 0)laserTopY = (short)(myY+8);
								
								for(tmpPos.y = minY+1; tmpPos.y < laserTopY; tmpPos.y++){
									if (!tmpPos.isAir(worldObj))break;
									tmpPos.setBlock(worldObj,BlockList.laser_beam);
									TileEntityLaserBeam beam = (TileEntityLaserBeam)tmpPos.getTileEntity(worldObj);
									if (beam != null)beam.setTicksLeft(102-attackAnim);
								}
								
								PacketPipeline.sendToAllAround(this,64D,new C08PlaySound(C08PlaySound.ENDEREYE_ATTACK_LASER_ADD,posX,posY,posZ,0.85F,rand.nextFloat()*0.1F+0.95F));
								break;
							}
						}
						else if (attackAnim == 102){
							laserTopY = 0;
							PacketPipeline.sendToAllAround(this,64D,new C08PlaySound(C08PlaySound.ENDEREYE_ATTACK_LASER_END,posX,posY,posZ,1F,rand.nextFloat()*0.2F+0.9F));
						}
					}
					/*
					 * End
					 */
				}
				
				if (attackAnim == 0){
					double yD = (posY+height*0.5F)-(target.boundingBox.minY+target.height+0.4D);
					if (Math.abs(yD) >= 0.8D)motionY -= Math.abs(yD)*0.005D*Math.signum(yD);
					
					if (distance >= 3D)setMoveForward((float)EntityAttributes.getValue(this,EntityAttributes.movementSpeed));
					else if (Math.abs(yD) < 1D)/*TODO*/;//target.attackEntityFrom(new DamageSourceMobUnscaled(this),DamageSourceMobUnscaled.getDamage(ModCommonProxy.opMobs ? 6F : 3F,worldObj.difficultySetting));
					
					if (target.isDead)target = null;
				}
				
			}
			else target = null;
		}
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount){
		if (isEntityInvulnerable() || source == DamageSource.inWall || source == DamageSource.drown || source == DamageSource.cactus || source.isFireDamage()||
			source.isMagicDamage() || source.isProjectile())return false; // you need manly strength to penetrate through the obsidian armor; min iron sword
		
		if (isAsleep()){
			setIsAsleep(false);
			
			if (worldObj.difficultySetting.getDifficultyId() > 1 || ModCommonProxy.opMobs){
				BlockPosM tmpPos = BlockPosM.tmp();
				
				for(int a = 0, hits = 0, x, y, z; a < 400 && hits < 5+worldObj.difficultySetting.getDifficultyId()*10+(ModCommonProxy.opMobs ? 30 : 0); a++){
					tmpPos.set(posX+rand.nextInt(15)-7,posY+rand.nextInt(8)-4,posZ+rand.nextInt(15)-7);
					
					Block block = tmpPos.getBlock(worldObj);
					
					if (block != Blocks.air){
						float hardness = block.getBlockHardness(worldObj,tmpPos.x,tmpPos.y,tmpPos.z);
						
						if (hardness != -1F && hardness <= 5F){
							tmpPos.setAir(worldObj);
							worldObj.playAuxSFX(2001,tmpPos.x,tmpPos.y,tmpPos.z,Block.getIdFromBlock(Blocks.obsidian));
							++hits;
						}
					}
				}
			}
		}
		
		if (amount < 7F)return true;
		
		amount = 7F+Math.min((amount-7F)*0.5F,5F);
		if (getAttackAnimationTime() > 0)amount *= 0.275F;
		
		if (super.attackEntityFrom(source,amount)){
			// TODO CausatumUtils.increase(source,CausatumMeters.ENDER_EYE_DAMAGE,amount*2F);
			return true;
		}
		else return false;
	}
	
	@Override
	public void onDeath(DamageSource source){
		if (source.getEntity() instanceof EntityPlayer){
			((EntityPlayer)source.getEntity()).addStat(AchievementManager.ENDER_EYE_KILL,1);
		}
		
		super.onDeath(source);
	}
	
	@Override
	public void knockBack(Entity entity, float damage, double xPower, double zPower){
		isAirBorne = true;
		double dist = Math.sqrt(xPower*xPower+zPower*zPower);
		
		motionX -= xPower/dist*0.04D;
		motionY += 0.005D;
		motionZ -= zPower/dist*0.04D;

		if (motionY > 0.05D)motionY = 0.05D;
	}
	
	@Override
	public void addVelocity(double xVelocity, double yVelocity, double zVelocity){
		super.addVelocity(xVelocity/10D,yVelocity/10D,zVelocity/10D);
	}
	
	@Override
	protected void dropFewItems(boolean recentlyHit, int looting){
		dropItem(Items.ender_eye,1);
		dropItem(Item.getItemFromBlock(Blocks.obsidian),rand.nextInt(4+looting)+3);
		dropItem(ItemList.spatial_dash_gem,1);
	}
	
	@Override
	public boolean canBePushed(){
		return !(isAsleep() && rotationPitch == 0f);
	}
	
	@Override
	protected void collideWithEntity(Entity entity){
		if (canBePushed())entity.applyEntityCollision(this);
	}
	
	public void setIsAsleep(boolean isAsleep){
		entityData.setBoolean(Data.ASLEEP,isAsleep);
	}
	
	public boolean isAsleep(){
		return entityData.getBoolean(Data.ASLEEP);
	}
	
	public void setAttackAnimationTime(byte time){
		entityData.setByte(Data.ANIMATION_TIME,time);
	}
	
	public byte getAttackAnimationTime(){
		return entityData.getByte(Data.ANIMATION_TIME);
	}

	@Override
	public boolean getCanSpawnHere(){
		return true;
	}

	@Override
	protected String getLivingSound(){
		return isAsleep() ? null : "hardcoreenderexpansion:mob.endereye.living";
	}

	@Override
	protected String getHurtSound(){
		return "hardcoreenderexpansion:mob.endereye.hurt";
	}

	@Override
	protected String getDeathSound(){
		return "hardcoreenderexpansion:mob.endereye.death";
	}
	
	@Override
	public float getSoundVolume(){
		return 0.75F;
	}
	
	@Override
	public float getSoundPitch(){
		return (rand.nextFloat()*0.25F)+0.875F;
	}
	
	@Override
	public String getCommandSenderName(){
		return hasCustomNameTag() ? getCustomNameTag() : StatCollector.translateToLocal("entity.enderEye.name");
	}
	
	@Override
	protected void despawnEntity(){}
}

enum AttackType{
	Poof(0,35),
	Nausea(1,32),
	LaserBeams(2,104);
	
	private byte id, length;
	
	AttackType(int id, int length){
		this.id = (byte)id;
		this.length = (byte)length;
	}
	
	public int getLength(){
		return length;
	}
	
	public static AttackType getById(int id){
		for(AttackType attack:values()){
			if (attack.id == id)return attack;
		}
		return null;
	}
}
