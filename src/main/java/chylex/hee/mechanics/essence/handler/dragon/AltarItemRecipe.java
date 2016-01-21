package chylex.hee.mechanics.essence.handler.dragon;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import chylex.hee.entity.item.EntityItemAltar;
import chylex.hee.system.abstractions.nbt.NBT;

public class AltarItemRecipe{
	private static final NBTTagCompound emptyTag = new NBTTagCompound();
	
	public final ItemStack input, output;
	public final short cost;
	
	public AltarItemRecipe(ItemStack input, ItemStack output, int cost){
		this.input = input;
		this.output = output;
		this.cost = (short)cost;
		
		NBT.item(input,true);
	}
	
	/**
	 * Checks if the ItemStacks have the same item, damage and NBT (except for Altar status NBT).
	 */
	public boolean isApplicable(ItemStack is){
		if (input.getItem() == is.getItem() && input.getItemDamage() == is.getItemDamage()){
			NBTTagCompound nbt = is.hasTagCompound() ? (NBTTagCompound)is.getTagCompound().copy() : emptyTag;
			
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
