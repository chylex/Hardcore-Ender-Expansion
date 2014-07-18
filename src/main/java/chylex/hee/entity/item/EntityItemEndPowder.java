package chylex.hee.entity.item;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.item.ItemList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityItemEndPowder extends EntityItem{
	private byte checkTimer = 0;
	private byte progress = 0;
	
	public EntityItemEndPowder(World world){
		super(world);
	}

	public EntityItemEndPowder(World world, double x, double y, double z, ItemStack is){
		super(world,x,y,z,is);
	}
	
	@Override
	public void onUpdate(){
		super.onUpdate();
		
		if (++checkTimer > 10){
			checkTimer = 0;
			
			EntityItem emerald = null, string = null;
			
			for(Object o:worldObj.getEntitiesWithinAABB(EntityItem.class,boundingBox.expand(1.8D,1.8D,1.8D))){
				if (o == this)continue;
				
				EntityItem entity = (EntityItem)o;
				ItemStack is = entity.getEntityItem();
				Item item = is.getItem();
				
				if (item == Items.emerald && is.stackSize == 1)emerald = entity;
				else if (item == Items.string && is.stackSize == 1)string = entity;
			}
			
			if (emerald != null && string != null && ++progress > 8){
				for(int a = 0; a < 20; a++)HardcoreEnderExpansion.fx.portalBig(worldObj,posX,posY+0.5D,posZ,0D,0.2D+rand.nextDouble()*0.2D,0D);
				emerald.setDead();
				string.setDead();
				setDead();
				
				if (!worldObj.isRemote){
					EntityItem amulet = new EntityItem(worldObj,posX,posY,posZ,new ItemStack(ItemList.ghost_amulet));
					amulet.addVelocity((rand.nextDouble()-rand.nextDouble())*0.1D,0.45D,(rand.nextDouble()-rand.nextDouble())*0.1D);
					worldObj.spawnEntityInWorld(amulet);
				}
			}
		}
	}
}
