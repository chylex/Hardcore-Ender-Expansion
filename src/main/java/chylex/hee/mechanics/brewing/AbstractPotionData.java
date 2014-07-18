package chylex.hee.mechanics.brewing;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public abstract class AbstractPotionData{
	protected Potion potion;
	protected short damageValue,requiredDamageValue;
	protected int maxLevel;
	
	public AbstractPotionData(Potion potion, int requiredDamageValue, int damageValue, int maxLevel){
		this.potion = potion;
		this.requiredDamageValue = (short)requiredDamageValue;
		this.damageValue = (short)damageValue;
		this.maxLevel = maxLevel;
	}
	
	public Potion getPotionType(){
		return potion;
	}
	
	public int getMaxLevel(){
		return maxLevel;
	}
	
	public void onFirstBrewingFinished(ItemStack is){
		is.setItemDamage(damageValue);
	}
	
	public boolean canIncreaseLevel(ItemStack is){
		PotionEffect effect = PotionTypes.getEffectIfValid(is);
		return effect != null && effect.getPotionID() == potion.id && effect.getAmplifier() < maxLevel-1;
	}
}
