package chylex.hee.mechanics.essence;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemUseCache{
	public final Item item;
	public final int damage;
	private long time;
	
	public ItemUseCache(ItemStack is){
		this.item = is.getItem();
		this.damage = is.getItemDamage();
		renewTime();
	}
	
	public void renewTime(){
		time = System.nanoTime();
	}
	
	public long getElapsedTime(){
		return System.nanoTime()-time;
	}
}
