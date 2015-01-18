package chylex.hee.item;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import chylex.hee.entity.item.EntityItemIgneousRock;
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.common.IFuelHandler;

public class ItemIgneousRock extends ItemAbstractCustomEntity implements IFuelHandler{
	@Override
	public void onUpdate(ItemStack is, World world, Entity entity, int slot, boolean isHeld){
		if (world.isRemote || !isHeld || entity.isInWater() || itemRand.nextInt(200) != 0)return;
		float mp = world.provider.getDimensionId() == 1 ? 0.4F : world.provider.getDimensionId() == -1 ? 3F : 1F;
		entity.setFire(MathUtil.ceil(mp*(itemRand.nextInt(4)+4+Math.max(1,is.stackSize/10))));
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
