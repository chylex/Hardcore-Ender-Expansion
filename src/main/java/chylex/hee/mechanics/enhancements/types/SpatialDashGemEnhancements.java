package chylex.hee.mechanics.enhancements.types;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.enhancements.EnhancementEnumHelper;
import chylex.hee.mechanics.enhancements.IEnhancementEnum;
import chylex.hee.system.util.IItemSelector.IRepresentativeItemSelector;
import chylex.hee.system.util.IItemSelector.IRepresentativeItemSelector.ItemStackSelector;
import chylex.hee.system.util.IItemSelector.IRepresentativeItemSelector.SimpleItemSelector;

public enum SpatialDashGemEnhancements implements IEnhancementEnum{
	CAPACITY(new SimpleItemSelector(ItemList.endium_ingot)),
	RANGE(new ItemStackSelector(new ItemStack(Items.dye,1,4))),
	INSTANT(new SimpleItemSelector(ItemList.instability_orb));
	
	private final String name;
	public final IRepresentativeItemSelector itemSelector;
	
	private SpatialDashGemEnhancements(IRepresentativeItemSelector itemSelector){
		this.name = EnhancementEnumHelper.getName(this,EnumChatFormatting.GRAY);
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
