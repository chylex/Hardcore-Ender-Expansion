package chylex.hee.mechanics.compendium.objects;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import chylex.hee.item.ItemList;
import chylex.hee.item.ItemSpawnEggs;

public class ObjectMob implements IKnowledgeObjectInstance<Class<? extends EntityLiving>>{
	private final Class<? extends EntityLiving> mobClass;
	
	public ObjectMob(Class<? extends EntityLiving> mobClass){
		this.mobClass = mobClass;
	}

	@Override
	public Class<? extends EntityLiving> getUnderlyingObject(){
		return mobClass;
	}
	
	@Override
	public ItemStack createItemStackToRender(){
		ItemStack is = null;
		String name = "Unknown Mob";
		int spawnEggDamage = ItemSpawnEggs.getDamageForMob(mobClass);
		
		if (spawnEggDamage != -1){
			is = new ItemStack(ItemList.spawn_eggs,1,spawnEggDamage);
			name = ItemSpawnEggs.getMobName(mobClass);
		}
		else{
			for(int a = 0; a < 256; a++){
				if (EntityList.getClassFromID(a) == mobClass){
					is = new ItemStack(Items.spawn_egg,1,a);
					name = (String)EntityList.classToStringMapping.get(mobClass);
					break;
				}
			}
		}
		
		if (is == null)is = new ItemStack(Blocks.bedrock);
		
		NBTTagCompound displayTag = new NBTTagCompound();
		displayTag.setString("Name",name);
		
		is.stackTagCompound = new NBTTagCompound();
		is.stackTagCompound.setTag("display",displayTag);
		
		return is;
	}

	@Override
	public boolean checkEquality(Object obj){
		return obj == mobClass || obj.getClass() == mobClass;
	}
}
