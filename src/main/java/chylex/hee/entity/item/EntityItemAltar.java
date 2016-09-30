package chylex.hee.entity.item;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.game.achievements.AchievementManager;
import chylex.hee.init.ItemList;
import chylex.hee.system.abstractions.entity.EntityDataWatcher;

public class EntityItemAltar extends EntityItem{
	private enum Data{ SPARKLING };
	
	private EntityDataWatcher entityData;
	public byte pedestalUpdate;
	public byte essenceType;
	
	public EntityItemAltar(World world){
		super(world);
	}
	
	public EntityItemAltar(World world, double x, double y, double z, EntityItem originalItem, byte essenceType){
		super(world, x, y, z);
		motionX = motionY = motionZ = 0D;
		delayBeforeCanPickup = 50;
		
		hoverStart = originalItem.hoverStart;
		rotationYaw = originalItem.rotationYaw;
		
		NBTTagCompound tag = new NBTTagCompound();
		originalItem.writeEntityToNBT(tag);
		readEntityFromNBT(tag);
		
		ItemStack is = getEntityItem();
		is.stackSize = 1;
		setEntityItemStack(is);
		
		is = originalItem.getEntityItem();
		if (--is.stackSize == 0)originalItem.setDead();
		else originalItem.setEntityItemStack(is);
		
		this.essenceType = essenceType;
	}
	
	@Override
	public void entityInit(){
		super.entityInit();
		entityData = new EntityDataWatcher(this);
		entityData.addBoolean(Data.SPARKLING);
	}

	@Override
	public void onUpdate(){
		if (!worldObj.isRemote && ++pedestalUpdate > 10){
			EntityItem item = new EntityItem(worldObj, posX, posY, posZ, getEntityItem());
			item.copyLocationAndAnglesFrom(this);
			worldObj.spawnEntityInWorld(item);
			setDead();
			return;
		}
		
		onEntityUpdate();

		if (delayBeforeCanPickup > 0)--delayBeforeCanPickup;
		
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		
		++age;
		
		if (worldObj.isRemote && (ticksExisted&3) == 1 && entityData.getBoolean(Data.SPARKLING)){
			HardcoreEnderExpansion.fx.altarAura(this);
		}

		ItemStack item = dataWatcher.getWatchableObjectItemStack(10);
		if (item != null && item.stackSize <= 0)setDead();
	}
	
	@Override
	public boolean combineItems(EntityItem item){
		return false;
	}
	
	@Override
	public void onCollideWithPlayer(EntityPlayer player){
		if (Math.abs(player.posX-posX) < 0.8001D && Math.abs(player.posZ-posZ) < 0.8001D){
			ItemStack is = getEntityItem().copy();
			super.onCollideWithPlayer(player);
			
			if (isDead){
				if (is.getItem() == ItemList.enhanced_brewing_stand)player.addStat(AchievementManager.ENHANCED_BREWING_STAND, 1);
			}
		}
	}
	
	public void setSparkling(){
		entityData.setBoolean(Data.SPARKLING, true);
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt){
		super.writeEntityToNBT(nbt);
		nbt.setBoolean("sparkling", entityData.getBoolean(Data.SPARKLING));
		nbt.setByte("essenceType", essenceType);
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		if (nbt.getBoolean("sparkling"))entityData.setBoolean(Data.SPARKLING, true);
		essenceType = nbt.getByte("essenceType");
	}
}
