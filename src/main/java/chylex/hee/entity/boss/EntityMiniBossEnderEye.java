package chylex.hee.entity.boss;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
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
import chylex.hee.api.interfaces.IIgnoreEnderGoo;
import chylex.hee.block.BlockList;
import chylex.hee.entity.RandomNameGenerator;
import chylex.hee.entity.mob.util.DamageSourceMobUnscaled;
import chylex.hee.item.ItemList;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C07AddPlayerVelocity;
import chylex.hee.packets.client.C08PlaySound;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.achievements.AchievementManager;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.MathUtil;
import chylex.hee.tileentity.TileEntityLaserBeam;

public class EntityMiniBossEnderEye extends EntityFlying implements IBossDisplayData, IIgnoreEnderGoo{
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
		dataWatcher.addObject(16,Byte.valueOf((byte)1));
		dataWatcher.addObject(17,Byte.valueOf((byte)0));
	}
	
	@Override
	protected void applyEntityAttributes(){
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(ModCommonProxy.opMobs?350D:250D);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(1.8D);
		
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
					List<EntityPlayer> nearPlayers = worldObj.getEntitiesWithinAABB(EntityPlayer.class,boundingBox.expand(8D,4D,8D));
					
					if (!nearPlayers.isEmpty()){
						target = nearPlayers.get(0);
						sleepTimer = 0;
					}
					
					attackTimer = (byte)(-65+rand.nextInt(15));
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
							if (worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing")){
								for(int a = 0,hits = 0,x,y,z; a < 200 && hits < 16+worldObj.difficultySetting.getDifficultyId(); a++){
									x = rand.nextInt(14)-7+(int)Math.floor(posX);
									y = rand.nextInt(8)-4+(int)Math.floor(posY);
									z = rand.nextInt(14)-7+(int)Math.floor(posZ);
									
									Block block = worldObj.getBlock(x,y,z);
									if (block.getMaterial() != Material.air && block.getBlockHardness(worldObj,x,y,z) != -1F){
										worldObj.setBlockToAir(x,y,z);
										worldObj.playAuxSFX(2001,x,y,z,Block.getIdFromBlock(Blocks.obsidian));
										++hits;
									}
								}
							}
							
							for(Object o:worldObj.getEntitiesWithinAABB(EntityPlayer.class,boundingBox.expand(6D,6D,6D))){
								EntityPlayer player = (EntityPlayer)o;
								double[] vec = DragonUtil.getNormalizedVector(player.posX-posX,player.posZ-posZ);
								
								boolean blocking = player.isBlocking();
								vec[0] *= (blocking ? 1.4D : 2.4D);
								vec[1] *= (blocking ? 1.4D : 2.4D);
								
								PacketPipeline.sendToPlayer(player,new C07AddPlayerVelocity(vec[0],0.34D,vec[1]));
								
								player.motionX += vec[0];
								player.motionY += 0.34D;
								player.motionZ += vec[1];
								
								player.attackEntityFrom(new DamageSourceMobUnscaled(this),ModCommonProxy.opMobs ? 6F : 3F);
							}
							
							PacketPipeline.sendToAllAround(this,64D,new C08PlaySound(C08PlaySound.ENDEREYE_ATTACK_POOF,posX,posY,posZ,1F,rand.nextFloat()*0.2F+0.9F));
						}
					}
					/*
					 * Nausea
					 */
					else if (attackType == AttackType.Nausea){
						if (attackAnim == 17){
							PotionEffect effNausea = new PotionEffect(Potion.confusion.id,220,0,true);
							for(Object o:worldObj.getEntitiesWithinAABB(EntityPlayer.class,boundingBox.expand(6D,6D,6D)))((EntityPlayer)o).addPotionEffect(effNausea);
						}
						else if (attackAnim == 19){
							PacketPipeline.sendToAllAround(this,64D,new C08PlaySound(C08PlaySound.ENDEREYE_ATTACK_CONFUSION,posX,posY,posZ,1F,rand.nextFloat()*0.2F+0.9F));
						}
						else if (attackAnim == 26){
							PotionEffect effBlind = new PotionEffect(Potion.blindness.id,160,0,true),
										 effSlow = new PotionEffect(Potion.moveSlowdown.id,120,0,true);
							for(Object o:worldObj.getEntitiesWithinAABB(EntityPlayer.class,boundingBox.expand(6D,6D,6D))){
								EntityPlayer player = (EntityPlayer)o;
								player.addPotionEffect(effBlind);
								player.addPotionEffect(effSlow);
							}
						}
					}
					/*
					 * Laser beams
					 */
					else if (attackType == AttackType.LaserBeams){
						if (attackAnim > 35 && attackAnim < 99 && attackAnim%7 == 0){
							int myY = (int)Math.floor(posY), attempt, x, y, z, minY;
							
							for(attempt = 0; attempt < 12; attempt++){
								x = (int)Math.floor(posX)+rand.nextInt(16)-8;
								z = (int)Math.floor(posZ)+rand.nextInt(16)-8;
								
								if (worldObj.getBlock(x,myY,z) != Blocks.air)continue;
								
								for(y = myY; y > myY-6; y--){
									if (worldObj.getBlock(x,y,z) != Blocks.air)break;
									else if (y == myY-4){
										y = -1;
										break;
									}
								}
								
								if (y == -1)continue;
								minY = y;
								
								if (laserTopY == 0)laserTopY = (short)(myY+8);
								
								for(y = minY+1; y < laserTopY; y++){
									if (worldObj.getBlock(x,y,z) != Blocks.air)break;
									worldObj.setBlock(x,y,z,BlockList.laser_beam);
									TileEntityLaserBeam beam = (TileEntityLaserBeam)worldObj.getTileEntity(x,y,z);
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
					
					if (distance >= 3D)setMoveForward((float)getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue());
					else if (Math.abs(yD) < 1D)target.attackEntityFrom(new DamageSourceMobUnscaled(this),3F+worldObj.difficultySetting.getDifficultyId()+(ModCommonProxy.opMobs?3F:0F));
					
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
				for(int a = 0, hits = 0, x, y, z; a < 400 && hits < 5+worldObj.difficultySetting.getDifficultyId()*10+(ModCommonProxy.opMobs ? 30 : 0); a++){
					x = rand.nextInt(14)-7+(int)Math.floor(posX);
					y = rand.nextInt(8)-4+(int)Math.floor(posY);
					z = rand.nextInt(14)-7+(int)Math.floor(posZ);
					
					Block block = worldObj.getBlock(x,y,z);
					
					if (block != Blocks.air){
						float hardness = block.getBlockHardness(worldObj,x,y,z);
						if (hardness != -1F && hardness <= 5F){					
							worldObj.setBlockToAir(x,y,z);
							worldObj.playAuxSFX(2001,x,y,z,Block.getIdFromBlock(Blocks.obsidian));
							++hits;
						}
					}
				}
			}
		}
		
		if (amount < 7F)return true;
		
		amount = 7F+Math.min((amount-7F)*0.5F,5F);
		if (getAttackAnimationTime() > 0)amount *= 0.275F;
		return super.attackEntityFrom(source,amount);
	}
	
	@Override
	public void onDeath(DamageSource source){
		if (source.getEntity() instanceof EntityPlayer){
			((EntityPlayer)source.getEntity()).addStat(AchievementManager.DEAD_VISIONARY,1);
		}
		
		super.onDeath(source);
	}
	
	@Override
	public void knockBack(Entity entity, float damage, double xPower, double zPower){
		isAirBorne = true;
		double dist = Math.sqrt(xPower*xPower+zPower*zPower);
		
		motionX -= xPower/dist*0.1D;
		motionY += 0.005D;
		motionZ -= zPower/dist*0.1D;

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
		dataWatcher.updateObject(16,Byte.valueOf((byte)(isAsleep?1:0)));
	}
	
	public boolean isAsleep(){
		return dataWatcher.getWatchableObjectByte(16) != 0;
	}
	
	public void setAttackAnimationTime(byte time){
		dataWatcher.updateObject(17,time);
	}
	
	public byte getAttackAnimationTime(){
		return dataWatcher.getWatchableObjectByte(17);
	}

	@Override
	public boolean getCanSpawnHere(){
		return true;
	}

	@Override
	protected String getLivingSound(){
		return isAsleep()?null:"hardcoreenderexpansion:mob.endereye.living";
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
		return hasCustomNameTag()?getCustomNameTag():StatCollector.translateToLocal("entity.enderEye.name");
	}
	
	@Override
	protected void despawnEntity(){}
}

enum AttackType{
	Poof(0,35),
	Nausea(1,32),
	LaserBeams(2,104);
	
	private byte id,length;
	
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
