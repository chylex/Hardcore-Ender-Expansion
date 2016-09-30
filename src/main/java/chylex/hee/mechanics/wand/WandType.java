package chylex.hee.mechanics.wand;
import net.minecraft.item.ItemStack;

public enum WandType{
	BASIC(2, 13),
	ADVANCED(3, 17),
	EPIC(4, 25);
	
	public static WandType[] values = values();
	
	public final byte slots;
	public final byte baseDamage;
	
	WandType(int slots, int baseDamage){
		this.slots = (byte)slots;
		this.baseDamage = (byte)baseDamage;
	}
	
	public static WandType fromItemStack(ItemStack is){
		int dmg = is.getItemDamage();
		return dmg >= 0 && dmg < values.length ? values[dmg] : BASIC;
	}
}
