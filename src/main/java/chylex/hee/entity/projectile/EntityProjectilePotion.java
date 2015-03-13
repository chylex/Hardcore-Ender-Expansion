package chylex.hee.entity.projectile;
import java.util.List;
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

public class EntityProjectilePotion extends EntityPotion{
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
			if ((potionItem = Item.getItemById(dataWatcher.getWatchableObjectShort(16))) == null)potionItem = Items.potionitem;
		}
		
		return potionItem;
	}
	
	@Override
	protected void entityInit(){
		dataWatcher.addObject(16,Short.valueOf((short)0));
	}
	
	@Override
	public void onUpdate(){
		super.onUpdate();
		if (ticksExisted == 1)dataWatcher.updateObject(16,(short)Item.getIdFromItem(potionItem));
	}
	
	@Override
	protected void onImpact(MovingObjectPosition mop){
		if (!worldObj.isRemote && potionItem instanceof ItemAbstractPotion){
			for(Entity entity:(List<Entity>)worldObj.getEntitiesWithinAABB(Entity.class,boundingBox.expand(4D,2D,4D))){
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
