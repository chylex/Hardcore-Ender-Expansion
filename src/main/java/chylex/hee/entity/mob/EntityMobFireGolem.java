package chylex.hee.entity.mob;
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
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.entity.projectile.EntityProjectileGolemFireball;
import chylex.hee.init.ItemList;
import chylex.hee.mechanics.essence.EssenceType;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C08PlaySound;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.abstractions.entity.EntityAttributes;
import chylex.hee.system.abstractions.entity.EntityAttributes.Operation;
import chylex.hee.system.abstractions.entity.EntityDataWatcher;
import chylex.hee.system.util.MathUtil;

public class EntityMobFireGolem extends EntityMob{
	private enum Data{ FLAME_PARTICLES }
	
	private static final AttributeModifier stopMovingModifier = EntityAttributes.createModifier("Cancel movement", Operation.MULTIPLY, 0D);
	
	private EntityDataWatcher entityData;
	private byte rangedStatus = -1, teleportCooldown = 0;
	
	public EntityMobFireGolem(World world){
		super(world);
		setSize(0.9F, 1.4F);
		isImmuneToFire = true;
		experienceValue = 8;
	}
	
	@Override
	protected void entityInit(){
		super.entityInit();
		entityData = new EntityDataWatcher(this);
		entityData.addByte(Data.FLAME_PARTICLES);
	}
	
	@Override
	protected void applyEntityAttributes(){
		super.applyEntityAttributes();
		EntityAttributes.setValue(this, EntityAttributes.maxHealth, ModCommonProxy.opMobs ? 42D : 24D);
		EntityAttributes.setValue(this, EntityAttributes.movementSpeed, 0.55D);
		EntityAttributes.setValue(this, EntityAttributes.attackDamage, ModCommonProxy.opMobs ? 4D : 2D);
	}
	
	@Override
	protected Entity findPlayerToAttack(){
		EntityPlayer player = worldObj.getClosestVulnerablePlayerToEntity(this, 28D);
		return player != null && canEntityBeSeen(player) ? player : null;
	}

	@Override
	public void onLivingUpdate(){
		super.onLivingUpdate();

		if (worldObj.isRemote){
			byte flameParticleAmount = entityData.getByte(Data.FLAME_PARTICLES);
			
			if (flameParticleAmount > 0){
				rotationYaw = renderYawOffset = rotationYawHead;
				Vec3 look = getLookVec();
				double xx = posX+look.xCoord*0.62D, zz = posZ+look.zCoord*0.62D;
				int amt = rand.nextInt(2+flameParticleAmount);
				
				for(int a = 0; a < amt; a++)HardcoreEnderExpansion.fx.flame(xx+(rand.nextDouble()-0.5D)*0.2D, posY+0.8D+rand.nextDouble()*0.1D, zz+(rand.nextDouble()-0.5D)*0.2D, 4);
			}
		}
		else{
			EntityAttributes.removeModifier(this, EntityAttributes.movementSpeed, stopMovingModifier);
			
			if (teleportCooldown > 0)--teleportCooldown;
			
			if (entityToAttack != null){
				double dist = MathUtil.distance(posX-entityToAttack.posX, posZ-entityToAttack.posZ);
				
				if (rangedStatus == -1 && dist > 4D && dist < 10D && canEntityBeSeen(entityToAttack)){
					rangedStatus = 0;
				}
				else if (rangedStatus >= 0){
					EntityAttributes.applyModifier(this, EntityAttributes.movementSpeed, stopMovingModifier);
					rotationYaw = rotationYawHead;
					
					byte flameParticleAmountNew = (byte)(MathUtil.floor(70-rangedStatus)>>2);
					
					if (++rangedStatus > (ModCommonProxy.opMobs ? 28 : 38)){
						flameParticleAmountNew = 0;
						rangedStatus = -1;
						Vec3 look = getLookVec();
						
						worldObj.spawnEntityInWorld(new EntityProjectileGolemFireball(
							worldObj, this, posX+look.xCoord*0.8D, posY+1D, posZ+look.zCoord*0.8D,
							entityToAttack.posX-posX, entityToAttack.posY+0.2D-posY, entityToAttack.posZ-posZ)
						);
						
						PacketPipeline.sendToAllAround(this, 64D, new C08PlaySound(C08PlaySound.SPAWN_FIREBALL, posX, posY, posZ, 1.5F, 0.85F+rand.nextFloat()*0.1F));
					}
					
					if (entityData.getByte(Data.FLAME_PARTICLES) != flameParticleAmountNew)entityData.setByte(Data.FLAME_PARTICLES, flameParticleAmountNew);
				}
			}
		}
	}
	
	@Override
	public boolean attackEntityAsMob(Entity entity){
		if (entity.attackEntityFrom(DamageSource.causeMobDamage(this), (float)getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue())){ // TODO
			if (rand.nextInt(5) <= 1)entity.setFire(rand.nextInt(4)+2);

			if (entity instanceof EntityLivingBase){
				EnchantmentHelper.func_151384_a((EntityLivingBase)entity, this); // OBFUSCATED handle thorns
			}
			
			return true;
		}
		return false;
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount){
		if (!worldObj.isRemote && source.isExplosion() && teleportCooldown == 0){
			teleportCooldown = 45;

			/* TODO convert to MobTeleporter
			Vec3 look = getLookVec();
			double xx, yy, zz;
			BlockPosM tmpPos = BlockPosM.tmp();
			
			for(int attempt = 0; attempt < 300; attempt++){
				xx = posX+look.xCoord*3F+rand.nextDouble()*18D-9D;
				yy = posY+rand.nextDouble()*8D-4D;
				zz = posZ+look.zCoord*3F+rand.nextDouble()*18D-9D;
				
				if (Math.pow(xx-posX, 2)+Math.pow(yy-posY, 2)+Math.pow(zz-posZ, 2) < 30)continue;
				
				tmpPos.set(xx, yy, zz).moveDown();
				
				if (!tmpPos.isAir(worldObj) && tmpPos.moveUp().isAir(worldObj) && tmpPos.moveUp().isAir(worldObj)){
					setPosition(xx, yy, zz);
					if (entityToAttack != null)faceEntity(entityToAttack, 360F, 360F);
					playSound("mob.endermen.portal", 1F, 1.1F);
					return false;
				}
			}*/
		}
		
		if (super.attackEntityFrom(source, amount)){
			if (entityToAttack instanceof IBossDisplayData)entityToAttack = null;
			// TODO CausatumUtils.increase(source, CausatumMeters.END_MOB_DAMAGE, amount*0.25F);
			return true;
		}
		else return false;
	}
	
	@Override
	protected void dropFewItems(boolean recentlyHit, int looting){
		dropItem(Items.fire_charge, rand.nextInt(2));
		if (recentlyHit)entityDropItem(new ItemStack(ItemList.essence, rand.nextInt(4+looting)+1, EssenceType.FIERY.getItemDamage()), 0F);
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
		return worldObj.provider.dimensionId == 1 || super.isValidLightLevel();
	}
	
	@Override
	public String getCommandSenderName(){
		return hasCustomNameTag() ? getCustomNameTag() : StatCollector.translateToLocal("entity.fireGolem.name");
	}
}
