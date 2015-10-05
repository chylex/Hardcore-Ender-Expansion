package chylex.hee.mechanics.enhancements._old;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants.NBT;
import org.apache.commons.lang3.EnumUtils;
import chylex.hee.mechanics.enhancements.EnhancementData;
import chylex.hee.system.collections.CollectionUtil;
import chylex.hee.system.util.ItemUtil;
import chylex.hee.system.util.NBTUtil;

public final class EnhancementHandler{
	private static final IdentityHashMap<Item, EnhancementData> itemMap = new IdentityHashMap<>(8);
	
	public static List<IEnhancementEnum> getAllEnhancements(){
		List<IEnhancementEnum> list = new ArrayList<>();
		
		for(EnhancementData data:itemMap.values()){
			for(IEnhancementEnum enhancement:data.valuesInterface)list.add(enhancement);
		}
		
		return list;
	}
	
	public static List<IEnhancementEnum> getEnhancementsForItem(Item item){
		return canEnhanceItem(item) ? CollectionUtil.newList(itemMap.get(item).valuesInterface) : new ArrayList<>();
	}
	
	public static List<Enum> getEnhancements(ItemStack is){
		List<Enum> enhancements = new ArrayList<>();
		
		if (!is.hasTagCompound() || !canEnhanceItem(is.getItem()))return enhancements;
		
		NBTUtil.readStringList(is.getTagCompound(),"HEE_enhancements").forEach(name -> {
			Enum ele = EnumUtils.getEnum(itemMap.get(is.getItem()).clsEnum,name);
			if (ele != null)enhancements.add(ele);
		});
		
		return enhancements;
	}
	
	public static boolean hasEnhancement(ItemStack is, Enum enhancement){
		return getEnhancements(is).contains(enhancement);
	}

	public static ItemStack addEnhancement(ItemStack is, Object enhancement){
		if (!(enhancement instanceof IEnhancementEnum))throw new IllegalArgumentException("Tried adding foreign object "+enhancement+" as an enhancement!");
		return addEnhancement(is,(Enum)enhancement);
	}

	public static ItemStack addEnhancement(ItemStack is, Enum enhancement){
		if (getEnhancements(is).contains(enhancement) || !canEnhanceItem(is.getItem()))return is;
		
		is = is.copy();
		is.func_150996_a(itemMap.get(is.getItem()).newItem);
		addEnhancementToItemStack(is,enhancement);
		
		return is;
	}
	
	public static ItemStack addEnhancements(ItemStack is, Collection<Enum> enhancements){
		if (!canEnhanceItem(is.getItem()) || enhancements.isEmpty())return is;
		
		is = is.copy();
		is.func_150996_a(itemMap.get(is.getItem()).newItem);
		List<Enum> current = getEnhancements(is);
		
		for(Enum enhancement:enhancements){
			if (!current.contains(enhancement))addEnhancementToItemStack(is,enhancement);
		}
		
		return is;
	}
	
	public static void addEnhancementToItemStack(ItemStack is, Enum enhancement){
		NBTTagCompound nbt = ItemUtil.getTagRoot(is,true);
		NBTTagList list = nbt.getTagList("HEE_enhancements",NBT.TAG_STRING);
		list.appendTag(new NBTTagString(enhancement.name()));
		nbt.setTag("HEE_enhancements",list);
	}
	
	/* TODO public static void appendEnhancementNames(ItemStack is, List list){
		for(Enum e:getEnhancements(is))list.add(((IEnhancementEnum)e).getName());
	}*/
	
	private EnhancementHandler(){}
}
