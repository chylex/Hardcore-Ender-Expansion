package chylex.hee.mechanics.enhancements.types;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import chylex.hee.mechanics.brewing.PotionTypes;
import chylex.hee.mechanics.enhancements.EnhancementEnumHelper;
import chylex.hee.mechanics.enhancements.IEnhancementEnum;
import chylex.hee.system.util.IItemSelector.IRepresentativeItemSelector;
import chylex.hee.system.util.IItemSelector.IRepresentativeItemSelector.SimpleItemSelector;

public enum TransferenceGemEnhancements implements IEnhancementEnum{
	HEAL(new IRepresentativeItemSelector(){
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
	TOUCH(new SimpleItemSelector(Items.item_frame)),
	MOB(new SimpleItemSelector(Items.lead));
	
	private final String name;
	public final IRepresentativeItemSelector itemSelector;
	
	private TransferenceGemEnhancements(IRepresentativeItemSelector itemSelector){
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
