package chylex.hee.entity.item;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.block.BlockEnderGoo;
import chylex.hee.init.ItemList;
import chylex.hee.system.abstractions.entity.EntitySelector;
import com.google.common.collect.Sets;

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
			if (!isInsideOfMaterial(BlockEnderGoo.enderGoo))return;
			
			Map<Set<Item>,ItemStack> ingredientList = new HashMap<>();
			ingredientList.put(Sets.newHashSet(Items.emerald,Items.string),new ItemStack(ItemList.ghost_amulet));
			ingredientList.put(Sets.newHashSet(Items.gold_ingot,ItemList.ectoplasm,Items.string),new ItemStack(ItemList.curse_amulet));
			
			List<EntityItem> nearbyItems = EntitySelector.type(worldObj,EntityItem.class,boundingBox.expand(1.8D,1.8D,1.8D));
			
			for(Entry<Set<Item>,ItemStack> entry:ingredientList.entrySet()){
				Set<Item> ingredients = entry.getKey();
				List<EntityItem> foundIngredients = new ArrayList<>(ingredients.size());
				
				for(EntityItem nearbyItem:nearbyItems){
					Item item = nearbyItem.getEntityItem().getItem();
					
					if (ingredients.remove(item)){
						foundIngredients.add(nearbyItem);
						if (ingredients.isEmpty())break;
					}
				}
				
				if (ingredients.isEmpty()){
					if (++progress > 8){
						progress = 0;
						for(int a = 0; a < 20; a++)HardcoreEnderExpansion.fx.global("portalbig",posX,posY+0.5D,posZ,(rand.nextDouble()-0.5D)*0.2D,0.15D+rand.nextDouble()*0.15D,(rand.nextDouble()-0.5D)*0.2D);
						
						if (!worldObj.isRemote){
							ItemStack is = getEntityItem();
							if (--is.stackSize == 0)setDead();
							else setEntityItemStack(is);
							
							for(EntityItem ingredient:foundIngredients){
								if (--(is = ingredient.getEntityItem()).stackSize == 0)ingredient.setDead();
								else ingredient.setEntityItemStack(is);
							}
							
							EntityItem amulet = new EntityItem(worldObj,posX,posY,posZ,entry.getValue());
							amulet.addVelocity((rand.nextDouble()-rand.nextDouble())*0.1D,0.45D,(rand.nextDouble()-rand.nextDouble())*0.1D);
							worldObj.spawnEntityInWorld(amulet);
						}
					}
					
					break;
				}
			}
		}
	}
}
