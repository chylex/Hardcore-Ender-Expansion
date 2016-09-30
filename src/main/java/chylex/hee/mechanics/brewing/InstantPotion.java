package chylex.hee.mechanics.brewing;
import net.minecraft.potion.Potion;

public class InstantPotion extends AbstractPotionData{
	public InstantPotion(Potion potion, int requiredDamageValue, int damageValue, int maxLevel, int maxLevelEnhanced){
		super(potion, requiredDamageValue, damageValue, maxLevel, maxLevelEnhanced);
	}
}
