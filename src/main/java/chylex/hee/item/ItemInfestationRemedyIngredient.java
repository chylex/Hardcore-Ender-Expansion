package chylex.hee.item;
/*import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import chylex.hee.entity.item.EntityItemInfestationCauldron;

public class ItemInfestationRemedyIngredient extends Item{
	public ItemInfestationRemedyIngredient(){
		super();
	}
	
	@Override
	public boolean hasCustomEntity(ItemStack is){
		return true;
	}

	@Override
	public Entity createEntity(World world, Entity originalEntity, ItemStack is){
		EntityItemInfestationCauldron newEntity = new EntityItemInfestationCauldron(world,originalEntity.posX,originalEntity.posY,originalEntity.posZ,is);
		newEntity.delayBeforeCanPickup = 10;
		
		newEntity.copyLocationAndAnglesFrom(originalEntity);
		newEntity.motionX = newEntity.motionY = newEntity.motionZ = 0D;
		newEntity.addVelocity(originalEntity.motionX,originalEntity.motionY,originalEntity.motionZ);
		return newEntity;
	}
}
*/