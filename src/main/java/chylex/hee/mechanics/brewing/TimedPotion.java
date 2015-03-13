package chylex.hee.mechanics.brewing;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class TimedPotion extends AbstractPotionData{
	protected int startDuration,maxDuration,durationStep;
	
	public TimedPotion(Potion potion, int requiredDamageValue, int damageValue, int maxLevel, int maxLevelEnhanced, int startDuration, int maxDuration){
		this(potion,requiredDamageValue,damageValue,maxLevel,maxLevelEnhanced,startDuration,maxDuration,45);
	}
	
	public TimedPotion(Potion potion, int requiredDamageValue, int damageValue, int maxLevel, int maxLevelEnhanced, int startDuration, int maxDuration, int durationStep){
		super(potion,requiredDamageValue,damageValue,maxLevel,maxLevelEnhanced);
		this.startDuration = startDuration*20;
		this.maxDuration = maxDuration*20;
		this.durationStep = durationStep*20;
	}

	public int getMaxDuration(){
		return maxDuration;
	}
	
	public int getDurationStep(){
		return durationStep;
	}
	
	public int getDurationLevel(int duration){
		return (duration-startDuration)/durationStep;
	}
	
	@Override
	public void onFirstBrewingFinished(ItemStack is){
		super.onFirstBrewingFinished(is);
		PotionEffect eff = PotionTypes.getEffectIfValid(is);
		if (eff != null)PotionTypes.setCustomPotionEffect(is,new PotionEffect(eff.getPotionID(),startDuration,eff.getAmplifier(),eff.getIsAmbient()));
	}
	
	public boolean canIncreaseDuration(ItemStack is){
		PotionEffect effect = PotionTypes.getEffectIfValid(is);
		return effect != null && effect.getPotionID() == potion.id && effect.getDuration() < maxDuration;
	}
}
