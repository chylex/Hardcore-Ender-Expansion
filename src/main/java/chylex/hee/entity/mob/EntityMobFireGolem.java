package chylex.hee.entity.mob;
import java.util.UUID;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import chylex.hee.entity.projectile.EntityProjectileGolemFireball;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.essence.EssenceType;
import chylex.hee.mechanics.knowledge.KnowledgeRegistrations;
import chylex.hee.mechanics.knowledge.util.ObservationUtil;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C08PlaySound;
import chylex.hee.packets.client.C15ParticleFireGolemFlame;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.util.MathUtil;

public class EntityMobFireGolem extends EntityMob{
	private static final UUID cancelMovementModifierUUID = UUID.fromString("7107DE5E-7CE8-4030-940E-514C1F160890");
	private static final AttributeModifier cancelMovement = new AttributeModifier(cancelMovementModifierUUID,"Movement cancellation",-1,2).setSaved(false);
	
	private byte rangedStatus = -1, teleportCooldown = 0;
	
	public EntityMobFireGolem(World world){
		super(world);
		setSize(0.9F,1.4F);
		isImmuneToFire = true;
		experienceValue = 8;
	}
	
	@Override
	protected void applyEntityAttributes(){
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.55D);
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(ModCommonProxy.opMobs?42D:24D);
		getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(ModCommonProxy.opMobs?4D:2D);
	}
	
	@Override
	protected Entity findPlayerToAttack(){
		EntityPlayer player = worldObj.getClosestVulnerablePlayerToEntity(this,28D);
		return player != null && canEntityBeSeen(player)?player:null;
	}

	@Override
	public void onLivingUpdate(){
		super.onLivingUpdate();

		if (!worldObj.isRemote){
			getEntityAttribute(SharedMonsterAttributes.movementSpeed).removeModifier(cancelMovement);
			
			if (teleportCooldown > 0)--teleportCooldown;
			
			if (entityToAttack != null){
				double dist = MathUtil.distance(posX-entityToAttack.posX,posZ-entityToAttack.posZ);
				
				if (rangedStatus == -1 && dist > 4D && dist < 10D && canEntityBeSeen(entityToAttack)){
					rangedStatus = 0;
				}
				else if (rangedStatus >= 0){
					getEntityAttribute(SharedMonsterAttributes.movementSpeed).applyModifier(cancelMovement);
					
					PacketPipeline.sendToAllAround(this,64D,new C15ParticleFireGolemFlame(this,Byte.valueOf((byte)((int)Math.floor(70-rangedStatus)>>2))));
					
					if (++rangedStatus>(ModCommonProxy.opMobs?28:38)){
						rangedStatus = -1;
						Vec3 look = getLook(1F);
						
						worldObj.spawnEntityInWorld(new EntityProjectileGolemFireball(
							worldObj,this,posX+look.xCoord*0.8D,posY+1D,posZ+look.zCoord*0.8D,
							entityToAttack.posX-posX,entityToAttack.posY+0.2D-posY,entityToAttack.posZ-posZ)
						);
						
						PacketPipeline.sendToAllAround(this,64D,new C08PlaySound(C08PlaySound.SPAWN_FIREBALL,posX,posY,posZ,1.5F,0.85F+rand.nextFloat()*0.1F));
						
						for(EntityPlayer observer:ObservationUtil.getAllObservers(this,12D))KnowledgeRegistrations.FIRE_GOLEM.tryUnlockFragment(observer,0.09F,new byte[]{ 0,1,2,3 });
					}
				}
			}
		}
	}
	
	@Override
	public boolean attackEntityAsMob(Entity entity){
		if (entity.attackEntityFrom(DamageSource.causeMobDamage(this),(float)getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue())){
			if (rand.nextInt(5) <= 1)entity.setFire(rand.nextInt(4)+2);

			if (entity instanceof EntityLivingBase){
				EnchantmentHelper.func_151384_a((EntityLivingBase)entity,this); // OBFUSCATED handle thorns
			}
			
			return true;
		}
		return false;
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float damage){
		if (!worldObj.isRemote && source.isExplosion() && teleportCooldown == 0){
			teleportCooldown = 45;

			Vec3 look = getLook(3F);
			double xx,yy,zz;
			
			for(int attempt = 0,ix,iy,iz; attempt < 300; attempt++){
				xx = posX+look.xCoord+rand.nextDouble()*18D-9D;
				yy = posY+rand.nextDouble()*8D-4D;
				zz = posZ+look.zCoord+rand.nextDouble()*18D-9D;
				
				if (Math.pow(xx-posX,2)+Math.pow(yy-posY,2)+Math.pow(zz-posZ,2) < 30)continue;
				
				ix = (int)Math.floor(xx);
				iy = (int)Math.floor(yy);
				iz = (int)Math.floor(zz);
				
				if (!worldObj.isAirBlock(ix,iy-1,iz) && worldObj.isAirBlock(ix,iy,iz) && worldObj.isAirBlock(ix,iy+1,iz)){
					setPosition(xx,yy,zz);
					if (entityToAttack != null)faceEntity(entityToAttack,360F,360F);
					playSound("mob.endermen.portal",1.0F,1.1F);
					return false;
				}
				
				if (source.getEntity() instanceof EntityPlayer){
					KnowledgeRegistrations.FIRE_GOLEM.tryUnlockFragment((EntityPlayer)source.getEntity(),0.08F,new byte[]{ 0,1,2,3 });
				}
			}
		}
		
		if (super.attackEntityFrom(source,damage)){
			if (entityToAttack instanceof IBossDisplayData)entityToAttack = null;
			return true;
		}
		return false;
	}
	
	@Override
	protected void dropFewItems(boolean recentlyHit, int looting){
		dropItem(Items.fire_charge,rand.nextInt(2));
		if (recentlyHit)entityDropItem(new ItemStack(ItemList.essence,rand.nextInt(4+looting)+1,EssenceType.FIERY.getItemDamage()),0F);
		
		for(EntityPlayer observer:ObservationUtil.getAllObservers(this,6D))KnowledgeRegistrations.FIRE_GOLEM.tryUnlockFragment(observer,0.25F,new byte[]{ 4,5 });
	}
	
	@Override
	public boolean isOnLadder(){
		return isCollidedHorizontally;
	}
	
	@Override
	protected String getLivingSound(){
		return "fire.fire";
	}

	@Override
	protected String getHurtSound(){
		return "hardcoreenderexpansion:mob.firegolem.hurt";
	}

	@Override
	protected String getDeathSound(){
		return "hardcoreenderexpansion:mob.firegolem.death";
	}
	
	@Override
	protected boolean isValidLightLevel(){
		return worldObj.provider.dimensionId == 1?true:super.isValidLightLevel();
	}
	
	@Override
	public String getCommandSenderName(){
		return StatCollector.translateToLocal("entity.fireGolem.name");
	}
}
