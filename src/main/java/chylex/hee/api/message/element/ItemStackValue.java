package chylex.hee.api.message.element;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.tuple.Pair;
import chylex.hee.api.message.MessageRunner;
import chylex.hee.api.message.element.base.Optional;
import chylex.hee.api.message.element.base.PreconditionComposite;
import cpw.mods.fml.common.registry.GameRegistry;

public class ItemStackValue extends PreconditionComposite<ItemStack>{
	public static final ItemStackValue any(){
		return new ItemStackValue();
	}
	
	public static final StringValue itemString = StringValue.function(input -> getItemFromString(input) != null);
	
	public static final Pair<String,String> parseItemName(String fullName){
		String modid = "minecraft", name = "";
		int colon = fullName.indexOf(':');
		
		if (colon == -1)name = fullName;
		else{
			modid = fullName.substring(0,colon);
			name = fullName.substring(colon+1);
			
			if (modid.equals("~hee"))modid = "HardcoreEnderExpansion";
		}
		
		return Pair.of(modid,name);
	}
	
	public static final Item getItemFromString(String input){
		Block block = null;
		Item item = null;
		Pair<String,String> name = parseItemName(input);
		
		if ((item = GameRegistry.findItem(name.getLeft(),name.getRight())) != null)return item;
		else if ((block = GameRegistry.findBlock(name.getLeft(),name.getRight())) != null)return Item.getItemFromBlock(block);
		else return null;
	}
	
	private ItemStackValue(){
		addCondition("id",itemString);
		addCondition("damage",Optional.of(IntValue.range(0,Short.MAX_VALUE),0));
		addCondition("count",Optional.of(IntValue.range(1,64),1));
		addCondition("tag",Optional.of(NbtValue.any(),new NBTTagCompound()));
	}
	
	@Override
	public ItemStack getValue(MessageRunner runner){
		ItemStack is = new ItemStack(getItemFromString(runner.getString("id")));
		if (is.getItem() == null)throw new IllegalStateException("Failed constructing an ItemStack from IMC, item is null.");
		
		is.setItemDamage(runner.getInt("damage"));
		is.stackSize = runner.getInt("count");
		
		NBTTagCompound tag = runner.<NBTTagCompound>getValue("tag");
		if (!tag.hasNoTags())is.setTagCompound(tag);
		
		return is;
	}
}
