package chylex.hee.entity.mob;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import chylex.hee.entity.mob.util.IEndermanRenderer;
import chylex.hee.mechanics.misc.HomelandEndermen;
import chylex.hee.mechanics.misc.HomelandEndermen.HomelandRole;
import chylex.hee.mechanics.misc.HomelandEndermen.OvertakeGroupRole;

public class EntityMobHomelandEnderman extends EntityMob implements IEndermanRenderer{
	private HomelandRole homelandRole;
	private long groupId = -1;
	private OvertakeGroupRole overtakeGroupRole;
	
	public EntityMobHomelandEnderman(World world){
		super(world);
		setSize(0.6F,2.9F);
        stepHeight = 1.0F;
	}
	
	@Override
	protected void entityInit(){
        super.entityInit();
        dataWatcher.addObject(16,Byte.valueOf((byte)0));
        dataWatcher.addObjectByDataType(17,5);
    }
	
	@Override
	protected void applyEntityAttributes(){
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(40D);
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3D);
        getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(7D);
		updateAttributes();
	}
	
	private void updateAttributes(){
		if (homelandRole == null)return;
		
		switch(homelandRole){
			case ISLAND_LEADERS:
				getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(70D);
				getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(12D);
				break;
				
			case GUARD:
				getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(15D);
				break;
				
			case BUSINESSMAN:
				getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.35D);
				break;
		}
	}
	
	@Override
	public void onLivingUpdate(){
		super.onLivingUpdate();
		
		// TODO AI
	}
	
	@Override
	protected Entity findPlayerToAttack(){
		if (groupId != -1 && !HomelandEndermen.isOvertakeHappening(this)){
			//teleportRandomly(); // TODO
			return null;
		}
		else{
			
		}
		
		return super.findPlayerToAttack();
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt){
		super.writeEntityToNBT(nbt);
		nbt.setByte("homelandRole",(byte)homelandRole.ordinal());
		nbt.setLong("groupId",groupId);
		if (groupId != -1)nbt.setByte("groupRole",(byte)overtakeGroupRole.ordinal());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		
		byte homelandRoleId = nbt.getByte("homelandRole");
		homelandRole = homelandRoleId >= 0 && homelandRoleId < HomelandRole.values.length ? HomelandRole.values[homelandRoleId] : null;
		
		if ((groupId = nbt.getLong("groupId")) != -1){
			byte groupRoleId = nbt.getByte("groupRole");
			overtakeGroupRole = groupRoleId >= 0 && groupRoleId < OvertakeGroupRole.values.length ? OvertakeGroupRole.values[groupRoleId] : null;
		}
		
		if (homelandRole == null || (groupId != -1 && overtakeGroupRole == null))setDead();
	}
	
	@Override
	protected void despawnEntity(){}
	
	// LOGIC HANDLING
	
	private boolean canTeleport(){
		return !HomelandEndermen.isOvertakeHappening(this);
	}
	
	// GETTERS AND SETTERS
	
	public void setHomelandRole(HomelandRole role){
		this.homelandRole = role;
		updateAttributes();
	}
	
	public HomelandRole getHomelandRole(){
		return homelandRole;
	}
	
	public long setNewGroupLeader(){
		this.overtakeGroupRole = OvertakeGroupRole.LEADER;
		return this.groupId = UUID.randomUUID().getLeastSignificantBits();
	}
	
	public void setGroupMember(long groupId, OvertakeGroupRole role){
		
	}
	
	public OvertakeGroupRole getGroupRole(){
		return overtakeGroupRole;
	}
	
	public boolean isInSameGroup(EntityMobHomelandEnderman enderman){
		return groupId == enderman.groupId;
	}
	
	public void setCarrying(ItemStack is){
		dataWatcher.updateObject(17,is);
	}

	@Override
	public boolean isCarrying(){
		return getCarrying() != null;
	}
	
	@Override
	public ItemStack getCarrying(){
		return dataWatcher.getWatchableObjectItemStack(17);
	}
	
	public void setScreaming(boolean isScreaming){
		dataWatcher.updateObject(16,Byte.valueOf((byte)(isScreaming ? 1 : 0)));
	}

	@Override
	public boolean isScreaming(){
		return dataWatcher.getWatchableObjectByte(16) == 1;
	}
	
	// ENDERMAN METHODS
	
	protected boolean teleportRandomly(){
		return teleportTo(posX+(rand.nextDouble()-0.5D)*64D,posY+(rand.nextInt(64)-32),posZ+(rand.nextDouble()-0.5D)*64D);
	}

	protected boolean teleportToEntity(Entity entity){
		Vec3 vec = Vec3.createVectorHelper(posX-entity.posX,boundingBox.minY+(height/2F)-entity.posY+entity.getEyeHeight(),posZ-entity.posZ).normalize();
		double newX = posX+(rand.nextDouble()-0.5D)*8D-vec.xCoord*16D;
		double newY = posY+(rand.nextInt(16)-8)-vec.yCoord*16D;
		double newZ = posZ+(rand.nextDouble()-0.5D)*8D-vec.zCoord*16D;
		return this.teleportTo(newX,newY,newZ);
	}

	protected boolean teleportTo(double x, double y, double z){
		if (!canTeleport())return false;
		
		double oldX = posX, oldY = posY, oldZ = posZ;
		posX = x;
		posY = y;
		posZ = y;
		
		boolean hasTeleported = false;
		int ix = MathHelper.floor_double(posX), iy = MathHelper.floor_double(posY), iz = MathHelper.floor_double(posZ);

		if (worldObj.blockExists(ix,iy,iz)){
			boolean foundTopBlock = false;

			while(!foundTopBlock && iy > 0){
				if (worldObj.getBlock(ix,iy-1,iz).getMaterial().blocksMovement())foundTopBlock = true;
				else{
					--posY;
					--iy;
				}
			}

			if (foundTopBlock){
				setPosition(posX,posY,posZ);

				if (worldObj.getCollidingBoundingBoxes(this,boundingBox).isEmpty() && !worldObj.isAnyLiquid(boundingBox)){
					hasTeleported = true;
				}
			}
		}

		if (!hasTeleported){
			setPosition(oldX,oldY,oldZ);
			return false;
		}
		else{
			for(int a = 0, particleAmt = 128; a < particleAmt; a++){
				double linePosition = a/(particleAmt-1D);
				double particleX = oldX+(posX-oldX)*linePosition+(rand.nextDouble()-0.5D)*width*2D;
				double particleY = oldY+(posY-oldY)*linePosition+rand.nextDouble()*height;
				double particleZ = oldZ+(posZ-oldZ)*linePosition+(rand.nextDouble()-0.5D)*width*2D;
				worldObj.spawnParticle("portal",particleX,particleY,particleZ,(rand.nextFloat()-0.5F)*0.2F,(rand.nextFloat()-0.5F)*0.2F,(rand.nextFloat()-0.5F)*0.2F);
			}

			worldObj.playSoundEffect(oldX,oldY,oldZ,"mob.endermen.portal",1F,1F);
			playSound("mob.endermen.portal",1F,1F);
			return true;
		}
	}
	
	// OVERRIDDEN METHODS
	
	@Override
	protected String getLivingSound(){
		return isScreaming() ? "mob.endermen.scream" : "mob.endermen.idle";
	}

	@Override
	protected String getHurtSound(){
		return "mob.endermen.hit";
	}

	@Override
	protected String getDeathSound(){
		return "mob.endermen.death";
	}

	@Override
	protected Item getDropItem(){
		return Items.ender_pearl;
	}

	@Override
	protected void dropFewItems(boolean recentlyHit, int looting){
		Item item = getDropItem();
		
		if (item != null){
			for(int a = 0, total = rand.nextInt(2+looting); a < total; a++)dropItem(item,1);
		}
	}
}
