package chylex.hee.mechanics.essence.handler.dragon;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import chylex.hee.entity.item.EntityItemAltar;

public class AltarItemRecipe{
	private final ItemStack source,result;
	private final byte cost;
	
	public AltarItemRecipe(Item source, Item result, int cost){
		this(new ItemStack(source,1,0),new ItemStack(result,1,0),cost);
	}
	
	public AltarItemRecipe(Item source, int sourceMeta, Item result, int resultMeta, int cost){
		this(new ItemStack(source,1,sourceMeta),new ItemStack(result,1,resultMeta),cost);
	}
	
	public AltarItemRecipe(ItemStack source, ItemStack result, int cost){
		this.source = source.copy();
		this.result = result.copy();
		this.cost = (byte)cost;
	}
	
	public boolean isApplicable(ItemStack is){
		return source.isItemEqual(is);
	}
	
	public void doTransaction(EntityItem item){
		ItemStack is = result.copy();
		is.stackSize = 1;
		item.setEntityItemStack(is);
		if (item instanceof EntityItemAltar)((EntityItemAltar)item).setSparkling();
	}
	
	public final int getCost(){
		return cost;
	}
}
