package chylex.hee.mechanics.enhancements._old.types;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import chylex.hee.init.ItemList;
import chylex.hee.mechanics.enhancements._old.EnhancementEnumHelper;
import chylex.hee.mechanics.enhancements._old.IEnhancementEnum;
import chylex.hee.system.util.IItemSelector.IRepresentativeItemSelector;
import chylex.hee.system.util.IItemSelector.IRepresentativeItemSelector.SimpleItemSelector;

public enum TNTEnhancements implements IEnhancementEnum{
	NO_BLOCK_DAMAGE(new SimpleItemSelector(Items.feather)),
	NO_ENTITY_DAMAGE(new SimpleItemSelector(Items.slime_ball)),
	EXTRA_POWER(new SimpleItemSelector(Items.gunpowder)),
	TRAP(new SimpleItemSelector(Items.redstone)),
	NOCLIP(new SimpleItemSelector(Items.ender_pearl)),
	FIRE(new SimpleItemSelector(ItemList.igneous_rock)),
	NO_FUSE(new SimpleItemSelector(Blocks.redstone_torch));
	
	private final String name;
	private final IRepresentativeItemSelector itemSelector;
	
	private TNTEnhancements(IRepresentativeItemSelector itemSelector){
		this.name = EnhancementEnumHelper.getName(this,EnumChatFormatting.GOLD);
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
