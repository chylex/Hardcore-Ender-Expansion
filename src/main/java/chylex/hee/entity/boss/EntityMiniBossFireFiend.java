package chylex.hee.entity.boss;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import chylex.hee.entity.RandomNameGenerator;
import chylex.hee.entity.mob.util.DamageSourceMobUnscaled;
import chylex.hee.entity.projectile.EntityProjectileGolemFireball;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.essence.EssenceType;
import chylex.hee.mechanics.knowledge.KnowledgeRegistrations;
import chylex.hee.mechanics.knowledge.util.ObservationUtil;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C12ParticleFireFiendFlames;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.util.DragonUtil;

public class EntityMiniBossFireFiend extends EntityFlying implements IBossDisplayData{
	private static final byte STAGE_AFTERFLIGHT = -1, STAGE_REFRESHING = 0, STAGE_CREATING = 1, STAGE_SHOOTING = 2, STAGE_TOUCHING = 3;
	
	private EntityLivingBase target;
	private byte attackStage = STAGE_AFTERFLIGHT, fireballAttackTimer, touchAttemptTimer;
	private Set<float[]> fireballOffsets = new HashSet<>();
	private int spawnY;
  
	public byte spawnTimer = 80, damageInflicted;
	public float wingAnimation, wingAnimationStep, damageTaken;
	
	public EntityMiniBossFireFiend(World world){
		super(world);
		setSize(3F,2.6F);
		experienceValue = 40;
		scoreValue = 50;
		isImmuneToFire = true;
		ignoreFrustumCheck = true;
		
		RandomNameGenerator.generateEntityName(this,rand.nextInt(5)+5);
	}
	
	@Override
	protected void entityInit(){
		super.entityInit();
	}
	
	@Override
	protected void applyEntityAttributes(){
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(ModCommonProxy.opMobs?300D:200D);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(1.8D);
	}
	
	@Override
	public void onLivingUpdate(){
		super.onLivingUpdate();
		
		if (worldObj.isRemote){
			if (spawnTimer > 22 && --spawnTimer%4 == 0 && rand.nextInt(5) <= 1){
				worldObj.playSound(posX,posY,posZ,"dig.stone",17F-spawnTimer*0.1F,0.9F+rand.nextFloat()*0.15F,false);
			}
			worldObj.spawnParticle("flame",posX+(rand.nextDouble()-0.5D)*width,posY+rand.nextDouble()*height,posZ+(rand.nextDouble()-0.5D)*width,0D,0D,0D);
			return;
		}
		
		if (spawnTimer > 1){
			motionX = motionY = motionZ = 0D;
			noClip = true;
			
			--spawnTimer;
			
			int iy = (int)Math.floor(posY);
			if (spawnTimer == 20){
				breakBlocksAround(2,iy,iy+2);
				breakBlocksAround(1,iy+3,iy+4);
			}
			else if (spawnTimer == 6){
				breakBlocksAround(2,iy+3,iy+4);
				spawnY = (int)Math.floor(posY);
			}
			
			if (spawnTimer <= 7){
				noClip = false;
				motionY += 0.1D;
			}
		}
		else if (spawnTimer == 1){
			motionY += 0.05D;
			int iy = (int)Math.floor(posY);
			breakBlocksAround(1,iy+3,iy+3);
			
			boolean hasSpace = true;
			for(int xx = (int)Math.floor(posX)-1; xx <= Math.floor(posX+1); xx++){
				for(int zz = (int)Math.floor(posZ)-1; zz <= Math.floor(posZ+1); zz++){
					for(int yy = (int)Math.floor(posY)+1; yy <= (int)Math.floor(posY+9); yy++){
						if (!worldObj.isAirBlock(xx,yy,zz)){
							hasSpace = false;
							xx += 3;
							zz += 3;
							break;
						}
					}
				}
			}
			
			if (hasSpace){
				spawnTimer = 0;
				motionY += 0.4D;
			}
			
			for(EntityPlayer observer:ObservationUtil.getAllObservers(this,20D))KnowledgeRegistrations.FIRE_FIEND.tryUnlockFragment(observer,1F,new byte[]{ 0,1 });
		}
	}
	
	private void breakBlocksAround(int rad, int minY, int maxY){
		Block block;
		int xx, yy, zz, meta;
		
		for(xx = (int)Math.floor(posX)-rad; xx <= Math.floor(posX+rad); xx++){
			for(zz = (int)Math.floor(posZ)-rad; zz <= Math.floor(posZ+rad); zz++){
				for(yy = minY; yy <= maxY; yy++){
					if ((block = worldObj.getBlock(xx,yy,zz)).getMaterial() == Material.air)continue;
					
					if (block.getItemDropped(meta = worldObj.getBlockMetadata(xx,yy,zz),rand,0) != Item.getItemFromBlock(Blocks.air))block.dropBlockAsItem(worldObj,xx,yy,zz,meta,0);
					worldObj.setBlockToAir(xx,yy,zz);
					worldObj.playAuxSFX(2001,xx,yy,zz,Block.getIdFromBlock(block));
				}
			}
		}
	}
	
	@Override
	protected void updateEntityActionState(){
		if (spawnTimer > 0)return;

		if (attackStage == STAGE_AFTERFLIGHT){
			List<EntityPlayer> players = worldObj.getEntitiesWithinAABB(EntityPlayer.class,AxisAlignedBB.getBoundingBox(posX-5D,spawnY-10D,posZ-5D,posX+5D,spawnY+9D,posZ+5D));

			if (players.isEmpty() || damageTaken > 20F){
				fireballAttackTimer = 60;
				attackStage = STAGE_REFRESHING;
			}

			if (--fireballAttackTimer < 0){
				fireballAttackTimer = (byte)(23-worldObj.difficultySetting.getDifficultyId()*2);
				EntityPlayer target = players.get(rand.nextInt(players.size()));
				worldObj.spawnEntityInWorld(new EntityProjectileGolemFireball(worldObj,this,posX,posY-0.2D,posZ,target.posX-posX,target.posY-posY,target.posZ-posZ));
				
				if (rand.nextBoolean() && rand.nextBoolean()){
					for(EntityPlayer observer:ObservationUtil.getAllObservers(this,60D))KnowledgeRegistrations.FIRE_FIEND.tryUnlockFragment(observer,0.67F,new byte[]{ 0,1,2 });
				}
			}

			return;
		}
		
		if (target == null || target.isDead){
			target = worldObj.getClosestPlayerToEntity(this,128D);
		}
		else{
			double diffX = posX-target.posX;
			double diffY = posY-(target.posY+target.height*0.5D);
			double diffZ = posZ-target.posZ;
			
			double distance = Math.sqrt(diffX*diffX+diffZ*diffZ);
			
			rotationYaw = DragonUtil.rotateSmoothly(rotationYaw,(float)(Math.atan2(diffZ,diffX)*180D/Math.PI)-270F,6F);
			rotationPitch = DragonUtil.rotateSmoothly(rotationPitch,(float)(-(Math.atan2(diffY,distance)*180D/Math.PI)),8F);
			if (distance > 11D || (attackStage == STAGE_TOUCHING && distance > 2D))setMoveForward((float)getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue()*(attackStage == STAGE_TOUCHING?3F:1F));
			
			double targetYDiff = posY-(target.posY+target.height*0.5D+(attackStage == STAGE_TOUCHING?0D:5D));
			for(int a = 1; a < 6; a++){
				if (!worldObj.isAirBlock((int)Math.floor(posX),(int)Math.floor(posY)-a,(int)Math.floor(posZ))){
					targetYDiff = -1.5D;
					break;
				}
			}
			if (Math.abs(targetYDiff) > 1D)motionY -= Math.abs(targetYDiff)*0.0045D*Math.signum(targetYDiff);

			if (attackStage == STAGE_REFRESHING){
				if (--fireballAttackTimer < 0){
					fireballOffsets.clear();
					
					for(int a = 0; a < 5+rand.nextInt(4); a++){
						double ang = rand.nextDouble()*Math.PI*2D,len = rand.nextDouble()*2.5D+5D;
						fireballOffsets.add(new float[]{ (float)(Math.cos(ang)*len), rand.nextFloat()*2.5F+4F, (float)(Math.sin(ang)*len) });
					}
					
					attackStage = STAGE_CREATING;
					fireballAttackTimer = 60;
				}
			}
			else if (attackStage == STAGE_CREATING){
				if (--fireballAttackTimer < 0)attackStage = STAGE_SHOOTING;
				
				if (rand.nextInt(18) == 0){
					for(EntityPlayer observer:ObservationUtil.getAllObservers(this,60D))KnowledgeRegistrations.FIRE_FIEND.tryUnlockFragment(observer,0.15F,new byte[]{ 0,1,2,3 });
				}
			}
			else if (attackStage == STAGE_SHOOTING){
				if (--fireballAttackTimer < 0){
					Iterator < float[]> iter = fireballOffsets.iterator();
					if (iter.hasNext()){
						float[] offset = iter.next();
						
						double ballX = target.posX+offset[0],ballY = target.posY+offset[1]+1.5D,ballZ = target.posZ+offset[2];
						worldObj.spawnEntityInWorld(new EntityProjectileGolemFireball(worldObj,this,ballX,ballY,ballZ,target.posX-ballX,target.boundingBox.minY+target.height*0.5F-ballY,target.posZ-ballZ));
						
						if (rand.nextInt(10) == 0){
							for(EntityPlayer observer:ObservationUtil.getAllObservers(this,60D))KnowledgeRegistrations.FIRE_FIEND.tryUnlockFragment(observer,0.3F,new byte[]{ 0,1,2,3,4 });
						}
						
						iter.remove();
						fireballAttackTimer = (byte)(10+rand.nextInt(10)-worldObj.difficultySetting.getDifficultyId()*2);
					}
					else{
						attackStage = rand.nextInt(5) <= 1?STAGE_TOUCHING:STAGE_REFRESHING;
						damageInflicted = 0;
						touchAttemptTimer = 120;
						fireballAttackTimer = (byte)(80+rand.nextInt(25)-worldObj.difficultySetting.getDifficultyId()*5-Math.min(50,damageTaken*0.3F)-(ModCommonProxy.opMobs?20:0));								
					}
				}
			}
			
			if (attackStage == STAGE_CREATING || attackStage == STAGE_SHOOTING){
				for(float[] offset:fireballOffsets){
					PacketPipeline.sendToAllAround(this,96D,new C12ParticleFireFiendFlames(this,target,offset,Byte.valueOf((byte)((60-fireballAttackTimer)>>2))));
				}
			}
			
			if (attackStage == STAGE_TOUCHING){
				if (damageInflicted > 15+worldObj.difficultySetting.getDifficultyId()*3 || --touchAttemptTimer<-20){
					attackStage = STAGE_REFRESHING;
				}
			}
		}
		
		for(Object o:worldObj.getEntitiesWithinAABB(EntityLivingBase.class,boundingBox.expand(0.6D,1.65D,0.6D))){
			if (o == this)continue;
			EntityLivingBase e = (EntityLivingBase)o;
			e.setFire(2+rand.nextInt(3));
			e.attackEntityFrom(new DamageSourceMobUnscaled(this),ModCommonProxy.opMobs?9F:5F);
			++damageInflicted;
			
			if (e instanceof EntityPlayer && rand.nextInt(15) == 0){
				KnowledgeRegistrations.FIRE_FIEND.tryUnlockFragment((EntityPlayer)e,0.1F,new byte[]{ 0,1,2,3,4 });
			}
		}
		
		moveForward *= 0.6F;
		
		wingAnimationStep = 1F;
		if (Math.abs(moveForward) > 0.01D)wingAnimationStep += 1F;
		if (motionY>0.001D)wingAnimationStep += 1.5F;
		else if (motionY<0.001D)wingAnimationStep -= 0.75F;
		
		wingAnimation += wingAnimationStep*0.01F;
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount){
		if (spawnTimer > 0)return false;
		if (source.isFireDamage() || source.isExplosion())amount *= 0.1F;
		damageTaken += amount;
		
		if (attackStage == STAGE_REFRESHING && source.getEntity() instanceof EntityLivingBase){
			this.target = (EntityLivingBase)source.getEntity();
		}
		
		if (attackStage == STAGE_TOUCHING)amount *= 0.5F;
		
		return super.attackEntityFrom(source,Math.min(15,amount));
	}
	
	@Override
	protected void dropFewItems(boolean recentlyHit, int looting){
		for(int a = 0; a < 60; a++)entityDropItem(new ItemStack(ItemList.essence,2,EssenceType.FIERY.getItemDamage()),rand.nextFloat()*height);
		
		for(EntityPlayer observer:ObservationUtil.getAllObservers(this,60D))KnowledgeRegistrations.FIRE_FIEND.tryUnlockFragment(observer,1F,new byte[]{ 5,6 });
	}
	
	@Override
	public void knockBack(Entity entity, float damage, double xPower, double zPower){
		super.knockBack(entity,damage,xPower,zPower);
		motionX *= 0.4D;
		motionY *= 0.4D;
		motionZ *= 0.4D;
	}
	
	@Override
	public boolean canBePushed(){
		return spawnTimer == 0 && super.canBePushed();
	}
	
	@Override
	protected void collideWithEntity(Entity entity){
		if (canBePushed())entity.applyEntityCollision(this);
	}
	
	@Override
	protected String getLivingSound(){
		return "fire.fire";
	}

	@Override
	protected String getHurtSound(){
		return "hardcoreenderexpansion:mob.firefiend.hurt";
	}

	@Override
	protected String getDeathSound(){
		return "hardcoreenderexpansion:mob.firefiend.hurt";
	}
	
	@Override
	protected float getSoundVolume(){
		return 1.8F;
	}
	
	@Override
	protected float getSoundPitch(){
		return 0.8F+rand.nextFloat()*0.1F;
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt){
		super.writeEntityToNBT(nbt);
		nbt.setByte("spawnTimer",spawnTimer);
		nbt.setInteger("spawnY",spawnY);
		nbt.setByte("attackStage",attackStage);
		nbt.setFloat("damageTaken",damageTaken);
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		spawnTimer = nbt.getByte("spawnTimer");
		spawnY = nbt.getInteger("spawnY");
		attackStage = (byte)Math.min(STAGE_REFRESHING,nbt.getByte("attackStage"));
		damageTaken = nbt.getFloat("damageTaken");
	}
	
	@Override
	public String getCommandSenderName(){
		return hasCustomNameTag()?getCustomNameTag():StatCollector.translateToLocal("entity.fireFiend.name");
	}
	
	@Override
	protected void despawnEntity(){}
}
