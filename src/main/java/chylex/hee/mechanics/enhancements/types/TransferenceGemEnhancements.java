package chylex.hee.mechanics.enhancements.types;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import chylex.hee.init.ItemList;
import chylex.hee.item.ItemAbstractEnergyAcceptor;
import chylex.hee.mechanics.enhancements.EnhancementEnumHelper;
import chylex.hee.mechanics.enhancements.IEnhancementEnum;
import chylex.hee.system.util.IItemSelector.IRepresentativeItemSelector;
import chylex.hee.system.util.IItemSelector.IRepresentativeItemSelector.SimpleItemSelector;

public enum TransferenceGemEnhancements implements IEnhancementEnum{
	CAPACITY(new SimpleItemSelector(ItemList.endium_ingot)),
	TOUCH(new SimpleItemSelector(Items.leather)),
	BEAST(new SimpleItemSelector(ItemList.ectoplasm));
	
	private final String name;
	public final IRepresentativeItemSelector itemSelector;
	
	private TransferenceGemEnhancements(IRepresentativeItemSelector itemSelector){
		this.name = EnhancementEnumHelper.getName(this,EnumChatFormatting.BLUE);
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
		if (this == CAPACITY)ItemAbstractEnergyAcceptor.enhanceCapacity(is);
	}
}
