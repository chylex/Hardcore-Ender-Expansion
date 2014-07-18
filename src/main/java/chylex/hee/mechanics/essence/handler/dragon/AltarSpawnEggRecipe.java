package chylex.hee.mechanics.essence.handler.dragon;
/*import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import chylex.hee.system.util.DragonUtil;

public class AltarSpawnEggRecipe extends AltarItemRecipe{
	private static Map<Class<? extends Entity>,Integer> classToId = new HashMap<>();
	
	@SuppressWarnings("unchecked")
	private static ItemStack getSpawnEgg(Class<? extends Entity> entity){
		if (classToId.size() == 0){
			int a = 0;
			
			for(Field field:EntityList.class.getDeclaredFields()){
				if (++a < 5)continue;
				
				try{
					field.setAccessible(true);
					for(Entry<?,?> entry:((Map<?,?>)field.get(null)).entrySet()){
						if (entry.getKey() instanceof Class<?> && entry.getValue() instanceof Integer)classToId.put((Class<? extends Entity>)entry.getKey(),(Integer)entry.getValue());
					}
				}catch(Exception e){
					e.printStackTrace();
					DragonUtil.severe("Exception in SpawnEggRecipe - reflection failed!");
				}
				
				break;
			}
		}
		
		return classToId.containsKey(entity)?new ItemStack(Items.spawn_egg,1,classToId.get(entity)):null;
	}
	
	public AltarSpawnEggRecipe(Item item, Class<? extends Entity> entity, int price){
		super(new ItemStack(item,1,0),getSpawnEgg(entity),price);
	}
}
*/