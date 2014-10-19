package chylex.hee.entity.item;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import chylex.hee.block.BlockDragonEggCustom;

public class EntityItemDragonEgg extends EntityItem{
	private boolean overrideDeath = false;
	
	public EntityItemDragonEgg(World world){
		super(world);
	}

	public EntityItemDragonEgg(World world, double x, double y, double z, ItemStack is){
		super(world,x,y,z,is);
	}
	
	@Override
	public void onUpdate(){
		super.onUpdate();
		lifespan = Integer.MAX_VALUE;
		overrideDeath = false;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount){
		overrideDeath = true;
		return super.attackEntityFrom(source,amount);
	}
	
	@Override
	public void setDead(){
		if (overrideDeath)BlockDragonEggCustom.teleportEntityToPortal(this);
		super.setDead();
	}
}
