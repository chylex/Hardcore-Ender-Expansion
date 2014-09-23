package chylex.hee.entity.item;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import chylex.hee.system.savedata.WorldDataHandler;
import chylex.hee.system.savedata.types.DragonSavefile;

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
		if (overrideDeath){
			ChunkCoordinates coords = WorldDataHandler.<DragonSavefile>get(DragonSavefile.class).getPortalEggLocation();
			DimensionManager.getWorld(1).setBlock(coords.posX,coords.posY,coords.posZ,Blocks.dragon_egg);
			// TODO particle effect
		}
		
		super.setDead();
	}
}
