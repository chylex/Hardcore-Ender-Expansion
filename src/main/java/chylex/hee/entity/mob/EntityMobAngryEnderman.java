package chylex.hee.entity.mob;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import chylex.hee.entity.GlobalMobData.IIgnoreEnderGoo;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.mob.util.IEndermanRenderer;
import chylex.hee.mechanics.causatum.CausatumMeters;
import chylex.hee.mechanics.causatum.CausatumUtils;
import chylex.hee.mechanics.misc.Baconizer;
import chylex.hee.proxy.ModCommonProxy;

public class EntityMobAngryEnderman extends EntityMob implements IEndermanRenderer, IIgnoreEnderGoo{
	private static final UUID aggroSpeedBoostID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
	private static final AttributeModifier aggroSpeedBoost = new AttributeModifier(aggroSpeedBoostID,"Attacking speed boost",7.4D,0).setSaved(false); // 6.2 -> 7.4
	
	private Entity lastEntityToAttack;
	private int teleportDelay = 0;

	public EntityMobAngryEnderman(World world){
		super(world);
		setSize(0.6F,2.9F);
		stepHeight = 1F;
	}

	public EntityMobAngryEnderman(World world, double x, double y, double z){
		this(world);
		setPositionAndUpdate(x,y,z);
	}

	@Override
	protected void entityInit(){
		super.entityInit();
		dataWatcher.addObject(18,Byte.valueOf((byte)1));
		dataWatcher.addObject(19,Byte.valueOf((byte)0));
	}
	
	@Override
	protected void applyEntityAttributes(){
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(ModCommonProxy.opMobs ? 40D : 32D); // maxHealth 40 => 32
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3D);
		getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(ModCommonProxy.opMobs ? 9D : 5D); // attackDamage 7 => 5
	}

	@Override
	protected Entity findPlayerToAttack(){
		return entityToAttack;
	}

	@Override
	public void onLivingUpdate(){
		if (isWet()){
			attackEntityFrom(DamageSource.drown,1F);
		}

		if (lastEntityToAttack != entityToAttack){
			IAttributeInstance attributeinstance = getEntityAttribute(SharedMonsterAttributes.movementSpeed);
			attributeinstance.removeModifier(aggroSpeedBoost);
			if (entityToAttack != null)attributeinstance.applyModifier(aggroSpeedBoost);

		}
		lastEntityToAttack = entityToAttack;

		if (entityToAttack != null){
			faceEntity(entityToAttack,100F,100F);
		}
		
		for(int i = 0; i < 2; ++i){
			worldObj.spawnParticle("portal",posX+(rand.nextDouble()-0.5D)*width,posY+rand.nextDouble()*height-0.25D,posZ+(rand.nextDouble()-0.5D)*this.width,(this.rand.nextDouble()-0.5D)*2D,-this.rand.nextDouble(),(this.rand.nextDouble()-0.5D)*2D);
		}

		if (!worldObj.isRemote && isEntityAlive()){
			if (entityToAttack != null){
				if (entityToAttack.getDistanceSqToEntity(this) > 256D && teleportDelay++ >= 30 && teleportToEntity(entityToAttack)){
					teleportDelay = 0;
				}
			}
			else{
				teleportDelay = 0;

				if (rand.nextInt(30) == 0){
					for(EntityPlayer player:(List<EntityPlayer>)worldObj.getEntitiesWithinAABB(EntityPlayer.class,boundingBox.expand(6D,4D,6D))){
						if (!player.capabilities.isCreativeMode){
							entityToAttack = player;
							break;
						}
					}
				}
			}
		}
		
		isJumping = false;

		super.onLivingUpdate();
	}

	protected boolean teleportRandomly(){
		return teleportTo(posX+(rand.nextDouble()-0.5D)*64D,posY+(rand.nextInt(64)-32),posZ+(rand.nextDouble()-0.5D)*64D);
	}

	protected boolean teleportToEntity(Entity entity){
		Vec3 targVec = Vec3.createVectorHelper(posX-entity.posX,boundingBox.minY+(height/2F)-entity.posY+entity.getEyeHeight(),posZ-entity.posZ).normalize();
		double dist = 16D;
		return teleportTo(posX+(rand.nextDouble()-0.5D)*8D-targVec.xCoord*dist,posY+(rand.nextInt(16)-8)-targVec.yCoord*dist,posZ+(rand.nextDouble()-0.5D)*8D-targVec.zCoord*dist);
	}

	/**
	 * Teleport the enderman
	 */
	protected boolean teleportTo(double x, double y, double z){
		double origX = posX;
		double origY = posY;
		double origZ = posZ;
		posX = x;
		posY = y;
		posZ = z;
		
		boolean wasTeleported = false;
		int ix = MathHelper.floor_double(posX),iy = MathHelper.floor_double(posY),iz = MathHelper.floor_double(posZ);

		if (worldObj.blockExists(ix,iy,iz)){
			boolean found = false;

			while(!found && iy > 0){
				if (worldObj.getBlock(ix,iy-1,iz).getMaterial().blocksMovement())found = true;
				else{
					--posY;
					--iy;
				}
			}

			if (found){
				setPosition(posX,posY,posZ);

				if (worldObj.getCollidingBoundingBoxes(this,boundingBox).isEmpty() && !worldObj.isAnyLiquid(boundingBox)){
					wasTeleported = true;
				}
			}
		}

		if (!wasTeleported){
			setPosition(origX,origY,origZ);
			return false;
		}
		else{
			short dist = 128;

			for(int i = 0; i < dist; ++i){
				double prog = i/(dist-1D);
				
				worldObj.spawnParticle("portal",
					origX+(posX-origX)*prog+(rand.nextDouble()-0.5D)*width*2D,
					origY+(posY-origY)*prog+rand.nextDouble()*height,
					origZ+(posZ-origZ)*prog+(rand.nextDouble()-0.5D)*width*2D,
					(rand.nextFloat()-0.5F)*0.2F,(rand.nextFloat()-0.5F)*0.2F,(rand.nextFloat()-0.5F)*0.2F);
			}

			worldObj.playSoundEffect(origX,origY,origZ,"mob.endermen.portal",1F,1F);
			playSound("mob.endermen.portal",1F,1F);
			return true;
		}
	}

	@Override
	protected String getLivingSound(){
		return Baconizer.soundNormal(isScreaming() ? "mob.endermen.scream" : "mob.endermen.idle");
	}

	@Override
	protected String getHurtSound(){
		return Baconizer.soundNormal("mob.endermen.hit");
	}

	@Override
	protected String getDeathSound(){
		return Baconizer.soundDeath("mob.endermen.death");
	}

	@Override
	protected Item getDropItem(){
		return Items.ender_pearl;
	}

	@Override
	protected void dropFewItems(boolean recentlyHit, int looting){
		Item item = getDropItem();

		if (item != null){
			for(int a = 0, amount = rand.nextInt(2+looting); a < amount; a++)dropItem(item,1); 
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount){
		if (isEntityInvulnerable()){
			return false;
		}
		else{
			setScreaming(true);
			
			if (source instanceof EntityDamageSourceIndirect && entityToAttack == null){
				for(int attempt = 0; attempt < 64; ++attempt){
					if (teleportRandomly())return true;
				}

				return false;
			}
			else if (super.attackEntityFrom(source,amount)){
				CausatumUtils.increase(source,CausatumMeters.END_MOB_DAMAGE,amount*0.5F);
				return true;
			}
			else return false;
		}
	}
	
	@Override
	public void onDeath(DamageSource source){
		if (!worldObj.isRemote && worldObj.provider.dimensionId == 1 && worldObj.getTotalWorldTime()-EntityBossDragon.lastUpdate < 20 && source.getEntity() instanceof EntityPlayer){
			for(Object o:worldObj.loadedEntityList){
				if (o instanceof EntityBossDragon){
					((EntityBossDragon)o).achievements.onPlayerKilledEnderman((EntityPlayer)source.getEntity());
					break;
				}
			}
		}
		
		super.onDeath(source);
	}

	@Override
	public boolean isScreaming(){
		return dataWatcher.getWatchableObjectByte(18) > 0;
	}

	public void setScreaming(boolean isScreaming){
		dataWatcher.updateObject(18,Byte.valueOf((byte)(isScreaming ? 1 : 0)));
	}
	
	@Override
	protected boolean isValidLightLevel(){
		return worldObj.provider.dimensionId == 1 ? true : super.isValidLightLevel();
	}
	
	public void setCanDespawn(boolean canDespawn){
		dataWatcher.updateObject(19,Byte.valueOf((byte)(canDespawn ? 0 : 1)));
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt){
		super.writeEntityToNBT(nbt);
		nbt.setBoolean("canDespawn",dataWatcher.getWatchableObjectByte(19) == 0);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		setCanDespawn(nbt.getBoolean("canDespawn"));
	}
	
	@Override
	protected void despawnEntity(){
		if (dataWatcher.getWatchableObjectByte(19) == 0)super.despawnEntity();
	}

	@Override
	public boolean isCarrying(){
		return false;
	}
	
	@Override
	public ItemStack getCarrying(){
		return null;
	}
	
	@Override
	public String getCommandSenderName(){
		return hasCustomNameTag() ? getCustomNameTag() : StatCollector.translateToLocal(Baconizer.mobName("entity.angryEnderman.name"));
	}
}