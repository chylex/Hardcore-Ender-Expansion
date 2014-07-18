package chylex.hee.item;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import chylex.hee.entity.item.EntityItemInstabilityOrb;

public class ItemInstabilityOrb extends ItemAbstractCustomEntity{
	@Override
	protected EntityItem createEntityItem(World world, double x, double y, double z, ItemStack is){
		return new EntityItemInstabilityOrb(world,x,y,z,is);
	}
}
