package chylex.hee.entity.projectile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import chylex.hee.item.ItemAbstractPotion;
import chylex.hee.system.abstractions.entity.EntityDataWatcher;
import chylex.hee.system.abstractions.entity.EntitySelector;

public class EntityProjectilePotion extends EntityPotion{
	private enum Data{ ITEM_ID }
	
	private EntityDataWatcher entityData;
	private Item potionItem;
	
	public EntityProjectilePotion(World world){
		super(world);
	}

	public EntityProjectilePotion(World world, EntityLivingBase thrower, ItemAbstractPotion potion){
		super(world,thrower,new ItemStack(potion,1,1));
		this.potionItem = potion;
	}
	
	public Item getPotionItem(){
		if (potionItem == null || potionItem == Items.potionitem){
			if ((potionItem = Item.getItemById(entityData.getShort(Data.ITEM_ID))) == null)potionItem = Items.potionitem;
		}
		
		return potionItem;
	}
	
	@Override
	protected void entityInit(){
		super.entityInit();
		entityData = new EntityDataWatcher(this);
		entityData.addShort(Data.ITEM_ID);
	}
	
	@Override
	public void onUpdate(){
		super.onUpdate();
		if (ticksExisted == 1)entityData.setShort(Data.ITEM_ID,Item.getIdFromItem(potionItem));
	}
	
	@Override
	protected void onImpact(MovingObjectPosition mop){
		if (!worldObj.isRemote && potionItem instanceof ItemAbstractPotion){
			for(Entity entity:EntitySelector.any(worldObj,boundingBox.expand(4D,2D,4D))){
				double dist = getDistanceSqToEntity(entity);
				if (dist < 16D)((ItemAbstractPotion)potionItem).applyEffectThrown(entity,mop.entityHit == entity ? Double.MAX_VALUE : dist);
			}

			worldObj.playAuxSFX(2002,(int)Math.round(posX),(int)Math.round(posY),(int)Math.round(posZ),0);
			setDead();
		}
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt){
		super.writeEntityToNBT(nbt);
		nbt.setShort("potionItemID",(short)Item.getIdFromItem(potionItem));
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		potionItem = Item.getItemById(nbt.getShort("potionItemID"));
	}
}
