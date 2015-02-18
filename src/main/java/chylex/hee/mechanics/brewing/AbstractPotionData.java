package chylex.hee.mechanics.brewing;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public abstract class AbstractPotionData{
	protected Potion potion;
	protected short damageValue, requiredDamageValue;
	private int maxLevel, maxLevelEnhanced;
	
	public AbstractPotionData(Potion potion, int requiredDamageValue, int damageValue, int maxLevel, int maxLevelEnhanced){
		this.potion = potion;
		this.requiredDamageValue = (short)requiredDamageValue;
		this.damageValue = (short)damageValue;
		this.maxLevel = maxLevel;
		this.maxLevel = maxLevelEnhanced;
	}
	
	public Potion getPotionType(){
		return potion;
	}
	
	public int getMaxLevel(boolean enhanced){
		return enhanced ? maxLevelEnhanced : maxLevel;
	}
	
	public void onFirstBrewingFinished(ItemStack is){
		is.setItemDamage(damageValue);
	}
	
	public boolean canIncreaseLevel(ItemStack is, boolean enhanced){
		PotionEffect effect = PotionTypes.getEffectIfValid(is);
		return effect != null && effect.getPotionID() == potion.id && effect.getAmplifier() < getMaxLevel(enhanced)-1;
	}
}
