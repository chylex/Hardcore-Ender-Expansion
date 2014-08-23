package chylex.hee.mechanics.compendium.content.objects;
import chylex.hee.item.ItemList;
import chylex.hee.item.ItemSpawnEggs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class ObjectMob implements IKnowledgeObjectInstance<Class<? extends EntityLivingBase>>{
	private final Class<? extends EntityLivingBase> mobClass;
	
	public ObjectMob(Class<? extends EntityLivingBase> mobClass){
		this.mobClass = mobClass;
	}
	
	@Override
	public ItemStack createItemStackToRender(){
		int spawnEggDamage = ItemSpawnEggs.getDamageForMob(mobClass);
		return spawnEggDamage == -1 ? new ItemStack(Blocks.bedrock) : new ItemStack(ItemList.spawn_eggs,1,spawnEggDamage);
	}

	@Override
	public boolean areObjectsEqual(Class<? extends EntityLivingBase> obj1, Class<? extends EntityLivingBase> obj2){
		return obj1 == obj2;
	}
}
