package chylex.hee.mechanics.essence.handler.dragon;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import chylex.hee.entity.item.EntityItemAltar;

public class AltarItemRecipe{
	public final ItemStack input, output;
	public final short cost;
	
	public AltarItemRecipe(ItemStack input, ItemStack output, int cost){
		this.input = input;
		this.output = output;
		this.cost = (short)cost;
	}
	
	public boolean isApplicable(ItemStack is){
		return input.isItemEqual(is); // TODO check nbt, remove altar data from items
	}
	
	public void doTransaction(EntityItem item){
		ItemStack is = output.copy();
		is.stackSize = 1;
		item.setEntityItemStack(is);
		if (item instanceof EntityItemAltar)((EntityItemAltar)item).setSparkling();
	}
}
