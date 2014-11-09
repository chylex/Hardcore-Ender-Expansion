package chylex.hee.system.util;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemDamagePair{
	private final Item item;
	private final int damage;
	
	public ItemDamagePair(Item item, int damage){
		this.item = item;
		this.damage = damage;
	}
	
	public boolean check(ItemStack is){
		return item == is.getItem() && (damage == is.getItemDamage() || damage == -1); 
	}
	
	@Override
	public boolean equals(Object o){
		if (!(o instanceof ItemDamagePair))return false;
		ItemDamagePair pair = (ItemDamagePair)o;
		return pair.item == item && pair.damage == damage;
	}
	
	@Override
	public int hashCode(){
		return item.hashCode()*131+damage;
	}
}
