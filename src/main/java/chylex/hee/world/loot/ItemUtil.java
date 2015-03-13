package chylex.hee.world.loot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;

public final class ItemUtil{
	public static void addLore(ItemStack is, String lore){
		NBTTagCompound tag = is.stackTagCompound;
		if (tag == null)tag = new NBTTagCompound();
		if (!tag.hasKey("display"))tag.setTag("display",new NBTTagCompound());
		
		NBTTagList loreTag = tag.getCompoundTag("display").getTagList("Lore",Constants.NBT.TAG_STRING);
		if (lore == null)loreTag = new NBTTagList();
		loreTag.appendTag(new NBTTagString(lore));
		
		tag.getCompoundTag("display").setTag("Lore",loreTag);
		is.setTagCompound(tag);
	}
	
	public static void setArmorColor(ItemStack is, int color){
		NBTTagCompound tag = is.stackTagCompound;
		if (tag == null)tag = new NBTTagCompound();
		if (!tag.hasKey("display"))tag.setTag("display",new NBTTagCompound());
		
		tag.getCompoundTag("display").setInteger("color",color);
		is.setTagCompound(tag);
	}
}