package chylex.hee.mechanics.enhancements.types;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.Constants;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.enhancements.EnhancementEnumHelper;
import chylex.hee.mechanics.enhancements.EnhancementHandler;
import chylex.hee.mechanics.enhancements.IEnhancementEnum;
import chylex.hee.system.achievements.AchievementManager;
import chylex.hee.system.util.IItemSelector.IRepresentativeItemSelector;
import chylex.hee.system.util.IItemSelector.IRepresentativeItemSelector.SimpleItemSelector;

public enum EnderPearlEnhancements implements IEnhancementEnum{
	NO_FALL_DAMAGE("No fall damage", new SimpleItemSelector(Blocks.wool)),
	NO_GRAVITY("No gravity", new SimpleItemSelector(Items.feather)),
	INCREASED_RANGE("Increased range", new SimpleItemSelector(Items.string)),
	DOUBLE_SPEED("Double speed", new SimpleItemSelector(Items.gold_nugget)),
	EXPLOSIVE("Explosive", new SimpleItemSelector(Items.gunpowder)),
	FREEZE("Freeze", new SimpleItemSelector(Items.snowball)),
	RIDING("Riding", new SimpleItemSelector(Items.lead));
	
	public static List<EnderPearlEnhancements> getOldEnhancements(ItemStack is){
		List<EnderPearlEnhancements> list = new ArrayList<>();
		if (is.getItem() == Items.ender_pearl && is.getItem() != ItemList.enhanced_ender_pearl)return list;
		
		NBTTagCompound tag = is.stackTagCompound;
		if (tag == null || !tag.hasKey("display"))return list;

		NBTTagList lore = tag.getCompoundTag("display").getTagList("Lore",Constants.NBT.TAG_STRING);
		if (lore == null)return list;
		
		for(int a = 0; a < lore.tagCount(); a++){
			String line = lore.getStringTagAt(a);
			for(EnderPearlEnhancements pearlType:values()){
				if (line.endsWith(pearlType.oldLore)){
					list.add(pearlType);
					break;
				}
			}
		}
		
		return list;
	}
	
	private final String name;
	private String oldLore;
	private IRepresentativeItemSelector itemSelector;
	
	private EnderPearlEnhancements(String lore, IRepresentativeItemSelector itemSelector){
		this.oldLore = EnumChatFormatting.RESET.toString()+EnumChatFormatting.RED.toString()+lore;
		this.name = EnhancementEnumHelper.getName(this,EnumChatFormatting.RED);
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
	public void onEnhanced(ItemStack is, EntityPlayer player){
		player.addStat(AchievementManager.ENHANCED_ENDER_PEARL,1);
		
		if (EnhancementHandler.getEnhancements(is).size() == EnhancementHandler.getEnhancementsForItem(is.getItem()).size()){
			player.addStat(AchievementManager.ENHANCED_ENDER_PEARL_FULL,1);
		}
	}
}
