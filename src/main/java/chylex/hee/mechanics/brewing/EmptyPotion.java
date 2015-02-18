package chylex.hee.mechanics.brewing;
import net.minecraft.potion.Potion;

public class EmptyPotion extends AbstractPotionData{
	public EmptyPotion(Potion potion, int requiredDamageValue, int damageValue){
		super(potion,requiredDamageValue,damageValue,1,1);
	}
}
