package chylex.hee.mechanics.enhancements.types;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
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
