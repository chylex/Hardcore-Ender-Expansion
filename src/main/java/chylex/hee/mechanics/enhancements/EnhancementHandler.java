package chylex.hee.mechanics.enhancements;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;
import chylex.hee.block.BlockList;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.enhancements.SlotList.SlotType;
import chylex.hee.mechanics.enhancements.types.EnderPearlEnhancements;
import chylex.hee.mechanics.enhancements.types.TNTEnhancements;
import chylex.hee.mechanics.enhancements.types.TransferenceGemEnhancements;
import chylex.hee.system.util.CollectionUtil;

public final class EnhancementHandler{
	private static final IdentityHashMap<Item, EnhancementData> itemMap = new IdentityHashMap<>(8);
	
	static{
		SlotType p = SlotType.POWDER, i = SlotType.INGREDIENT;
		
		itemMap.put(
			Items.ender_pearl,
			new EnhancementData(EnderPearlEnhancements.class, ItemList.enhanced_ender_pearl, p, i, p)
		);
		
		itemMap.put(
			ItemList.enhanced_ender_pearl,
			new EnhancementData(EnderPearlEnhancements.class, ItemList.enhanced_ender_pearl, p, i, p)
		);
		
		itemMap.put(
			ItemList.transference_gem,
			new EnhancementData(TransferenceGemEnhancements.class, ItemList.transference_gem, p, p, p, i, p, p, p)
		);
		
		itemMap.put(
			Item.getItemFromBlock(Blocks.tnt),
			new EnhancementData(TNTEnhancements.class, Item.getItemFromBlock(BlockList.enhanced_tnt), p, p, i, i, p, p)
		);
		
		itemMap.put(
			Item.getItemFromBlock(BlockList.enhanced_tnt),
			new EnhancementData(TNTEnhancements.class, Item.getItemFromBlock(BlockList.enhanced_tnt), p, p, i, i, p, p)
		);
	}
	
	public static boolean canEnhanceItem(Item item){
		return itemMap.containsKey(item);
	}
	
	public static List<IEnhancementEnum> getEnhancementsForItem(Item item){
		return canEnhanceItem(item) ? CollectionUtil.newList(itemMap.get(item).valuesInterface) : new ArrayList<IEnhancementEnum>();
	}
	
	public static SlotList getEnhancementSlotsForItem(Item item){
		return canEnhanceItem(item) ? itemMap.get(item).slots : new SlotList();
	}
	
	public static List<Enum> getEnhancements(ItemStack is){
		List<Enum> enhancements = new ArrayList<>();
		
		if (is.stackTagCompound == null || !canEnhanceItem(is.getItem()))return enhancements;
		NBTTagList list = is.stackTagCompound.getTagList("HEE_enhancements",Constants.NBT.TAG_STRING);
		EnhancementData enhancementData = itemMap.get(is.getItem());
		
		for(int a = 0; a < list.tagCount(); a++){
			String name = list.getStringTagAt(a);
			
			for(Enum e:enhancementData.valuesEnum){
				if (e.name().equals(name)){
					enhancements.add(e);
					break;
				}
			}
		}
		
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
	
	public static void addEnhancementToItemStack(ItemStack is, Enum enhancement){
		if (is.stackTagCompound == null)is.stackTagCompound = new NBTTagCompound();
		
		NBTTagList list = is.stackTagCompound.getTagList("HEE_enhancements",Constants.NBT.TAG_STRING);
		list.appendTag(new NBTTagString(enhancement.name()));
		is.stackTagCompound.setTag("HEE_enhancements",list);
	}
	
	public static void appendEnhancementNames(ItemStack is, List list){
		for(Enum e:getEnhancements(is))list.add(((IEnhancementEnum)e).getName());
	}
	
	private EnhancementHandler(){}
}
