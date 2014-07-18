package chylex.hee.item;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class ItemAbstractCustomEntity extends Item{
	protected abstract EntityItem createEntityItem(World world, double x, double y, double z, ItemStack is);
	
	@Override
	public final boolean hasCustomEntity(ItemStack is){
		return true;
	}

	@Override
	public final Entity createEntity(World world, Entity originalEntity, ItemStack is){
		EntityItem newEntity = createEntityItem(world,originalEntity.posX,originalEntity.posY,originalEntity.posZ,is);
		newEntity.delayBeforeCanPickup = 10;
		
		newEntity.copyLocationAndAnglesFrom(originalEntity);
		newEntity.motionX = newEntity.motionY = newEntity.motionZ = 0D;
		newEntity.addVelocity(originalEntity.motionX,originalEntity.motionY,originalEntity.motionZ);
		return newEntity;
	}
}
