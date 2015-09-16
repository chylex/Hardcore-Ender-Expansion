package chylex.hee.mechanics.enhancements.types;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import chylex.hee.init.ItemList;
import chylex.hee.mechanics.enhancements.EnhancementEnumHelper;
import chylex.hee.mechanics.enhancements.IEnhancementEnum;
import chylex.hee.system.util.IItemSelector.IRepresentativeItemSelector;
import chylex.hee.system.util.IItemSelector.IRepresentativeItemSelector.SimpleItemSelector;

public enum SacredWandEnhancements implements IEnhancementEnum{
	CAPACITY(new SimpleItemSelector(ItemList.endium_ingot)),
	EFFICIENCY(null),
	CAPABILITY(null),
	SPEED(null),
	RANGE(null),
	HOMING(null);
	
	private final String name;
	public final IRepresentativeItemSelector itemSelector;
	
	private SacredWandEnhancements(IRepresentativeItemSelector itemSelector){
		this.name = EnhancementEnumHelper.getName(this,EnumChatFormatting.YELLOW);
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
		// TODO if (this == CAPACITY)ItemAbstractEnergyAcceptor.enhanceCapacity(is);
	}
}
