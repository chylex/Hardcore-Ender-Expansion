package chylex.hee.entity.item;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.init.ItemList;
import chylex.hee.system.achievements.AchievementManager;

public class EntityItemAltar extends EntityItem{
	public byte pedestalUpdate;
	public byte essenceType;
	
	public EntityItemAltar(World world){
		super(world);
	}
	
	public EntityItemAltar(World world, double x, double y, double z, EntityItem originalItem, byte essenceType){
		super(world,x,y,z);
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
		dataWatcher.addObject(11,Byte.valueOf((byte)0));
	}

	@Override
	public void onUpdate(){
		if (!worldObj.isRemote && ++pedestalUpdate > 10){
			EntityItem item = new EntityItem(worldObj,posX,posY,posZ,getEntityItem());
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
		
		if (worldObj.isRemote && (ticksExisted&3) == 1 && dataWatcher.getWatchableObjectByte(11) == 1){
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
				if (is.getItem() == ItemList.enhanced_brewing_stand)player.addStat(AchievementManager.ENHANCED_BREWING_STAND,1);
			}
		}
	}
	
	public void setSparkling(){
		dataWatcher.updateObject(11,Byte.valueOf((byte)1));
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt){
		super.writeEntityToNBT(nbt);
		nbt.setBoolean("sparkling",dataWatcher.getWatchableObjectByte(11) == 1);
		nbt.setByte("essenceType",essenceType);
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		dataWatcher.updateObject(11,(byte)(nbt.getBoolean("sparkling") ? 1 : 0));
		essenceType = nbt.getByte("essenceType");
	}
}
