package chylex.hee.item;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import chylex.hee.entity.item.EntityItemIgneousRock;
import cpw.mods.fml.common.IFuelHandler;

public class ItemIgneousRock extends ItemAbstractCustomEntity implements IFuelHandler{
	@Override
	public void onUpdate(ItemStack is, World world, Entity entity, int slot, boolean isHeld){
		if (world.isRemote || !isHeld || entity.isInWater() || itemRand.nextInt(20) != 0)return;
		float mp = world.provider.dimensionId == 1 ? 0.2F : world.provider.dimensionId == -1 ? 3F : 1F;

		if (is != null && is.getItem() == ItemList.igneous_rock){
			entity.setFire((int)Math.ceil(mp*(itemRand.nextInt(4)+3+Math.max(1,is.stackSize/10))));
		}
	}

	@Override
	public EntityItem createEntityItem(World world, double x, double y, double z, ItemStack is){
		return new EntityItemIgneousRock(world,x,y,z,is);
	}

	@Override
	public int getBurnTime(ItemStack fuel){
		return fuel.getItem() == this ? 8000 : 0;
	}
}
