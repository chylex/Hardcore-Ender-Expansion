package chylex.hee.mechanics.enhancements.types;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import chylex.hee.mechanics.enhancements.EnhancementEnumHelper;
import chylex.hee.mechanics.enhancements.IEnhancementEnum;
import chylex.hee.system.util.IItemSelector.IRepresentativeItemSelector;
import chylex.hee.system.util.IItemSelector.IRepresentativeItemSelector.SimpleItemSelector;

public enum EssenceAltarEnhancements implements IEnhancementEnum{
	RANGE(new SimpleItemSelector(Blocks.lapis_block)),
	SPEED(new SimpleItemSelector(Blocks.redstone_block)),
	EFFICIENCY(new SimpleItemSelector(Blocks.emerald_block));
	
	private final String name;
	private IRepresentativeItemSelector itemSelector;
	
	EssenceAltarEnhancements(IRepresentativeItemSelector itemSelector){
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
