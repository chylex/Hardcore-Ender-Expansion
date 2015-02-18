package chylex.hee.mechanics.enhancements.types;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import chylex.hee.mechanics.enhancements.EnhancementEnumHelper;
import chylex.hee.mechanics.enhancements.IEnhancementEnum;
import chylex.hee.system.util.IItemSelector.IRepresentativeItemSelector;
import chylex.hee.system.util.IItemSelector.IRepresentativeItemSelector.SimpleItemSelector;

public enum EnhancedBrewingStandEnhancements implements IEnhancementEnum{
	TIER(new SimpleItemSelector(Blocks.bedrock)), // TODO items
	SPEED(new SimpleItemSelector(Items.quartz)),
	COST(new SimpleItemSelector(Items.emerald));
	
	private final String name;
	private IRepresentativeItemSelector itemSelector;
	
	EnhancedBrewingStandEnhancements(IRepresentativeItemSelector itemSelector){
		this.name = EnhancementEnumHelper.getName(this,EnumChatFormatting.GREEN);
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
