package chylex.hee.entity.boss;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.effect.EntityWeatherEffect;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import chylex.hee.api.interfaces.IIgnoreEnderGoo;
import chylex.hee.block.BlockList;
import chylex.hee.entity.mob.EntityMobAngryEnderman;
import chylex.hee.entity.mob.util.DamageSourceMobUnscaled;
import chylex.hee.entity.weather.EntityWeatherLightningBoltDemon;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C05CustomWeather;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.util.DragonUtil;

public class EntityBossEnderDemon extends EntityFlying implements IBossDisplayData, IIgnoreEnderGoo{
	private static final PotionEffect endermanStrength = new PotionEffect(Potion.damageBoost.id,600,2,true);
	
	private byte healthRegenTimer = 10, lightningStartCounter = 30, lightningCounter,
				 endermanSpawnTimer = 25, obsidianSpawnTimer = 69;
	private EntityPlayerMP lastAttacker;
	private EntityPlayer lightningTarget;
	
	public EntityBossEnderDemon(World world){
		super(world);
		setSize(2F,5F);
		experienceValue = 70;
		scoreValue = 100;
		ignoreFrustumCheck = true;
		isImmuneToFire = true;
		rotationPitch = -90F;
	}
	
	public EntityBossEnderDemon(World world, double x, double y, double z){
		this(world);
		setPosition(x,y,z);
	}
	
	@Override
	protected void entityInit(){
		super.entityInit();
	}
	
	@Override
	protected void applyEntityAttributes(){
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(400D);
	}
	
	@Override
	protected void updateEntityActionState(){
		if (lastAttacker != null && (lastAttacker.isDead || !lastAttacker.playerNetServerHandler.func_147362_b().isChannelOpen()))lastAttacker = null; // OBFUSCATED get network manager
		
		float health = getHealth();
		if (health <= 0)return;
		if (lightningTarget == null && --healthRegenTimer < 0 && health < getMaxHealth())setHealth(health+1);
		if (healthRegenTimer < 0)healthRegenTimer = 9;
		
		if (lightningStartCounter <= 0){
			lightningStartCounter = 40;
			lightningCounter = 0;
			lightningTarget = lastAttacker == null?worldObj.getClosestPlayerToEntity(this,512D):lastAttacker;
		}
		
		if (lightningTarget != null){
			if (ticksExisted%18 == 0){
				double xx = lightningTarget.posX+(rand.nextDouble()-0.5D)*1.5D,
					   yy = lightningTarget.posY,
					   zz = lightningTarget.posZ+(rand.nextDouble()-0.5D)*1.5D;
				
				lightningTarget.attackEntityFrom(new DamageSourceMobUnscaled(this),ModCommonProxy.opMobs?7F:4F);

				EntityWeatherEffect bolt = new EntityWeatherLightningBoltDemon(worldObj,xx,yy,zz,this,false);
				worldObj.weatherEffects.add(bolt);
				PacketPipeline.sendToAllAround(bolt,512D,new C05CustomWeather(bolt,(byte)0));
				
				if (++lightningCounter >= 6)lightningTarget = null;
			}
		}
		else{
			if (--endermanSpawnTimer<-100){
				endermanSpawnTimer = (byte)(125-rand.nextInt(40));
				if (obsidianSpawnTimer<-105)obsidianSpawnTimer += 20;
				
				for(Object o:worldObj.getEntitiesWithinAABB(EntityPlayer.class,boundingBox.expand(128D,64D,128D))){
					EntityPlayer player = (EntityPlayer)o;
					int attempt, ix, iy, iz;
					
					for(attempt = 0; attempt < 40; attempt++){
						double ang = rand.nextDouble()*Math.PI*2D,len = 3.5D+rand.nextDouble()*2D;
						
						ix = (int)Math.floor(player.posX+Math.cos(ang)*len);
						iz = (int)Math.floor(player.posZ+Math.sin(ang)*len);
						for(iy = (int)Math.floor(player.posY)-2; iy < player.posY+3; iy++){
							if (worldObj.isAirBlock(ix,iy,iz) && worldObj.isAirBlock(ix,iy+1,iz) && worldObj.isAirBlock(ix,iy+2,iz)){
								for(int a = 0; a<(ModCommonProxy.opMobs?4:3); a++){
									EntityMobAngryEnderman enderman = new EntityMobAngryEnderman(worldObj,ix+rand.nextDouble(),iy,iz+rand.nextDouble());
									enderman.rotationYaw = rand.nextFloat()*360F;
									enderman.setTarget(player);
									enderman.addPotionEffect(endermanStrength);
									worldObj.spawnEntityInWorld(enderman);
									attempt = 999;
								}
								
								EntityWeatherEffect bolt = new EntityWeatherLightningBoltDemon(worldObj,ix+0.5D,iy,iz+0.5D,this,false);
								worldObj.addWeatherEffect(bolt);
								PacketPipeline.sendToAllAround(bolt,512D,new C05CustomWeather(bolt,(byte)0));
								break;
							}
						}
					}
				}
			}
			
			if (--obsidianSpawnTimer<-120){
				obsidianSpawnTimer = (byte)(20+rand.nextInt(80));
				List<EntityPlayer> list = worldObj.getEntitiesWithinAABB(EntityPlayer.class,boundingBox.expand(128D,64D,128D));
				
				if (!list.isEmpty()){
					EntityPlayer player = list.get(rand.nextInt(list.size()));
					
					for(int attempt = 0, placed = 0, xx, yy, zz; attempt < 25 && placed < 12+worldObj.difficultySetting.getDifficultyId()*2; attempt++){
						xx = (int)Math.floor(player.posX)+rand.nextInt(9)-4;
						yy = (int)Math.floor(player.posY)+9+rand.nextInt(6);
						zz = (int)Math.floor(player.posZ)+rand.nextInt(9)-4;
						
						if (worldObj.isAirBlock(xx,yy,zz) && worldObj.isAirBlock(xx,yy-1,zz)){
							worldObj.setBlock(xx,yy,zz,BlockList.obsidian_falling,0,3);
							++placed;
						}
						
						if (placed > 5 && rand.nextInt(15) <= 1)break;
					}
				}
			}
		}
		
		if (worldObj.isRemote)return;
		
		boolean hasBlockBelow = false;
		for(int ix = (int)Math.floor(posX),iz = (int)Math.floor(posZ),yy = (int)Math.floor(posY); yy > posY-22; yy--){
			if (!worldObj.isAirBlock(ix,yy,iz)){
				hasBlockBelow = true;
				break;
			}
		}
		
		if (hasBlockBelow){
			motionY *= 0.9D;
			if (Math.abs(motionY) < 0.04D)motionY = 0D;
		}
		else{
			motionY = -0.3D;
			++endermanSpawnTimer;
			++obsidianSpawnTimer;
			rotationPitch = -90F;
			lastAttacker = (EntityPlayerMP)worldObj.getClosestPlayerToEntity(this,512D);
		}
		
		if (lastAttacker != null){
			double diffX = posX-lastAttacker.posX;
			double diffY = posY-lastAttacker.posY;
			double diffZ = posZ-lastAttacker.posZ;
			
			double distance = Math.sqrt(diffX*diffX+diffZ*diffZ);
			
			rotationYaw = DragonUtil.rotateSmoothly(rotationYaw,(float)(Math.atan2(diffZ,diffX)*180D/Math.PI)-270F,2F);
			rotationPitch = DragonUtil.rotateSmoothly(rotationPitch,(float)(-(Math.atan2(diffY,distance)*180D/Math.PI)),8F);
		}
		else if (rotationPitch < 0F)rotationPitch -= 2F;
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount){
		amount = source == DamageSource.drown?amount:Math.min(5F,amount*0.45F);
		
		if (super.attackEntityFrom(source,amount)){
			if (source == DamageSource.drown){
				lightningTarget = null;
				healthRegenTimer = 60;
				return true;
			}
			
			if (source.getEntity() instanceof EntityPlayerMP)lastAttacker = (EntityPlayerMP)source.getEntity();
			if (lightningTarget == null)lightningStartCounter -= (int)amount;
			
			return true;
		}
		else return false;
	}
	
	@Override
	protected void onDeathUpdate(){
		worldObj.spawnParticle("hugeexplosion",posX+(rand.nextFloat()*width*2F)-width,posY+(rand.nextFloat()*height),posZ+(rand.nextFloat()*width*2F)-width,0D,0D,0D);

		if (worldObj.isRemote)return;
		
		if (++deathTime > 99){
			for(int exp = getExperiencePoints(attackingPlayer); exp > 0;){
				int xpSplit = EntityXPOrb.getXPSplit(exp);
				exp -= xpSplit;
				worldObj.spawnEntityInWorld(new EntityXPOrb(worldObj,posX,posY,posZ,xpSplit));
			}
			
			setDead();
		}
		
		if (deathTime < 81 && deathTime%10 == 0){
			EntityWeatherEffect bolt = new EntityWeatherLightningBoltDemon(worldObj,posX,posY,posZ,this,false);
			worldObj.weatherEffects.add(bolt);
			PacketPipeline.sendToAllAround(bolt,512D,new C05CustomWeather(bolt,(byte)0));
		}
	}
	
	@Override public void knockBack(Entity entity, float damage, double xPower, double zPower){}
	@Override public void addVelocity(double xVelocity, double yVelocity, double zVelocity){}
	
	@Override
	public IEntityLivingData onSpawnWithEgg(IEntityLivingData data){
		setDead();
		motionY = 7D;
		return super.onSpawnWithEgg(data);
	}

	public boolean isDoingLightningAttack(){
		return lightningTarget != null;
	}

	@Override
	protected String getHurtSound(){
		return "hardcoreenderexpansion:mob.enderdemon.hurt";
	}

	@Override
	protected String getDeathSound(){
		return "hardcoreenderexpansion:mob.enderdemon.death";
	}
	
	@Override
	protected float getSoundVolume(){
		return 5.5F;
	}
	
	@Override
	public String getCommandSenderName(){
		return StatCollector.translateToLocal("entity.enderDemon.name");
	}
	
	@Override
	protected void despawnEntity(){}
}
