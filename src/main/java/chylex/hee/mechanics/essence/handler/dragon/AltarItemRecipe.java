package chylex.hee.mechanics.essence.handler.dragon;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import chylex.hee.entity.item.EntityItemAltar;

public class AltarItemRecipe{
	private static final NBTTagCompound emptyTag = new NBTTagCompound();
	
	public final ItemStack input, output;
	public final short cost;
	
	public AltarItemRecipe(ItemStack input, ItemStack output, int cost){
		this.input = input;
		this.output = output;
		this.cost = (short)cost;
		
		if (input.stackTagCompound == null)input.stackTagCompound = new NBTTagCompound();
	}
	
	/**
	 * Checks if the ItemStacks have the same item, damage and NBT (except for Altar status NBT).
	 */
	public boolean isApplicable(ItemStack is){ // TODO test
		if (input.getItem() == is.getItem() && input.getItemDamage() == is.getItemDamage()){
			NBTTagCompound nbt = is.stackTagCompound == null ? emptyTag : (NBTTagCompound)is.stackTagCompound.copy();
			
			nbt.removeTag("HEE_transform");
			nbt.removeTag("HEE_enchant");
			nbt.removeTag("HEE_repair");
			
			return nbt.equals(emptyTag);
		}
		else return false;
	}
	
	public void doTransaction(EntityItem item){
		ItemStack is = output.copy();
		is.stackSize = 1;
		item.setEntityItemStack(is);
		if (item instanceof EntityItemAltar)((EntityItemAltar)item).setSparkling();
	}
	
	@Override
	public String toString(){
		return "{ input: "+input.toString()+", output: "+output.toString()+", cost: "+cost+" }";
	}
}
