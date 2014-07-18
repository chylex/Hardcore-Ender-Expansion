package chylex.hee.mechanics.enhancements.types;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.Constants;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.brewing.PotionTypes;
import chylex.hee.mechanics.enhancements.EnhancementEnumHelper;
import chylex.hee.mechanics.enhancements.IEnhancementEnum;
import chylex.hee.system.util.IItemSelector.IRepresentativeItemSelector;
import chylex.hee.system.util.IItemSelector.IRepresentativeItemSelector.SimpleItemSelector;

public enum TransferenceGemEnhancements implements IEnhancementEnum{
	HEAL("Heal",new IRepresentativeItemSelector(){
		@Override
		public boolean isValid(ItemStack is){
			if (is.getItem() != Items.potionitem)return false;
			
			PotionEffect eff = PotionTypes.getEffectIfValid(is);
			return eff != null && eff.getPotionID() == Potion.heal.id && eff.getAmplifier() == 3;
		}
		
		@Override
		public ItemStack getRepresentativeItem(){
			return PotionTypes.setCustomPotionEffect(new ItemStack(Items.potionitem,1,8197),new PotionEffect(Potion.heal.id,0,3));
		}
	}),
	TOUCH("Touch", new SimpleItemSelector(Items.item_frame)),
	MOB("Mob", new SimpleItemSelector(Items.lead));
	
	public static List<TransferenceGemEnhancements> getOldEnhancements(ItemStack is){
		List<TransferenceGemEnhancements> list = new ArrayList<>();
		if (is.getItem() != ItemList.transference_gem)return list;
		
		NBTTagCompound tag = is.stackTagCompound;
		if (tag == null)return list;
		if (!tag.hasKey("display"))return list;

		NBTTagList lore = tag.getCompoundTag("display").getTagList("Lore",Constants.NBT.TAG_STRING);
		if (lore == null)return list;
		
		for(int a = 0; a < lore.tagCount(); a++){
			String line = lore.getStringTagAt(a);
			for(TransferenceGemEnhancements enhancement:values()){
				if (line.endsWith(enhancement.effectName)){
					list.add(enhancement);
					break;
				}
			}
		}
		
		return list;
	}
	
	private final String name;
	private final String effectName;
	public final IRepresentativeItemSelector itemSelector;
	
	private TransferenceGemEnhancements(String effectName, IRepresentativeItemSelector itemSelector){
		this.name = EnhancementEnumHelper.getName(this,EnumChatFormatting.GRAY);
		this.effectName = EnumChatFormatting.GRAY+effectName;
		this.itemSelector = itemSelector;
	}

	@Override
	public String getName(){
		return name;
	}

	@Override
	public IRepresentativeItemSelector getItemSelector(){
		return itemSelector;
	}
	
	@Override
	public void onEnhanced(ItemStack is, EntityPlayer player){}
}
