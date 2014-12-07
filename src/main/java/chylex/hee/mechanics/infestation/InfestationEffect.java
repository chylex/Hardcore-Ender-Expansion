package chylex.hee.mechanics.infestation;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import chylex.hee.system.util.MathUtil;
import chylex.hee.system.weight.IWeightProvider;

class InfestationEffect implements IWeightProvider{
	private final byte potionEffectId;
	private final float durationMp;
	private final byte weight;
	
	InfestationEffect(Potion potionEffect, float durationMp, int weight){
		this.potionEffectId = (byte)potionEffect.id;
		this.durationMp = durationMp;
		this.weight = (byte)weight;
	}
	
	PotionEffect createPotionEffect(int amplifier, int durationTicks){
		PotionEffect eff = new PotionEffect(potionEffectId,MathUtil.ceil(durationTicks*durationMp),amplifier,true);
		setCurativeItems(eff);
		return eff;
	}
	
	byte getPotionId(){
		return potionEffectId;
	}
	
	@Override
	public int getWeight(){
		return weight;
	}
	
	static void setCurativeItems(PotionEffect eff){
		eff.getCurativeItems().clear();
	}
}
