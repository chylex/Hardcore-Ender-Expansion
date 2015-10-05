package chylex.hee.mechanics.enhancements._old.types;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import chylex.hee.game.achievements.AchievementManager;
import chylex.hee.mechanics.enhancements._old.EnhancementEnumHelper;
import chylex.hee.mechanics.enhancements._old.EnhancementHandler;
import chylex.hee.mechanics.enhancements._old.IEnhancementEnum;
import chylex.hee.system.util.IItemSelector.IRepresentativeItemSelector;
import chylex.hee.system.util.IItemSelector.IRepresentativeItemSelector.SimpleItemSelector;

public enum EnderPearlEnhancements implements IEnhancementEnum{
	NO_FALL_DAMAGE(new SimpleItemSelector(Blocks.wool)),
	NO_GRAVITY(new SimpleItemSelector(Items.feather)),
	INCREASED_RANGE(new SimpleItemSelector(Items.string)),
	DOUBLE_SPEED(new SimpleItemSelector(Items.gold_nugget)),
	EXPLOSIVE(new SimpleItemSelector(Items.gunpowder)),
	FREEZE(new SimpleItemSelector(Items.snowball)),
	RIDING(new SimpleItemSelector(Items.lead));
	
	private final String name;
	private IRepresentativeItemSelector itemSelector;
	
	private EnderPearlEnhancements(IRepresentativeItemSelector itemSelector){
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
