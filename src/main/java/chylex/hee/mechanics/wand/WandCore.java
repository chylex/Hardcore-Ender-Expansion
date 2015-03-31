package chylex.hee.mechanics.wand;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants.NBT;
import chylex.hee.system.logging.Log;
import chylex.hee.system.util.CollectionUtil;

public enum WandCore{
	INFESTATION, BLAZE, MAGIC, DEXTERITY, FORCE, REPULSION;
	
	public static WandCore[] values = values();
	
	/**
	 * Returns an array of cores sorted based on slots (there can be null elements).
	 */
	public static List<WandCore> getCores(ItemStack is){
		if (is.stackTagCompound == null || !is.stackTagCompound.hasKey("wandcores"))return CollectionUtil.newList();
		
		NBTTagList list = is.stackTagCompound.getTagList("wandcores",NBT.TAG_STRING);
		List<WandCore> cores = new ArrayList<>(list.tagCount());
		
		for(int a = 0; a < list.tagCount(); a++){
			String data = list.getStringTagAt(a);
			
			try{
				cores.add(data.isEmpty() ? null : Enum.valueOf(WandCore.class,data));
			}catch(IllegalArgumentException e){
				Log.error("Unknown wand core entry: "+data);
			}
		}
		
		return cores;
	}
	
	public static boolean hasCore(ItemStack is, WandCore core){
		if (is.stackTagCompound == null || !is.stackTagCompound.hasKey("wandcores"))return false;
		
		NBTTagList list = is.stackTagCompound.getTagList("wandcores",NBT.TAG_STRING);
		String name = core.name();
		
		for(int a = 0; a < list.tagCount(); a++){
			if (list.getStringTagAt(a).equals(name))return true;
		}
		
		return false;
	}
	
	public static void setCores(ItemStack is, WandCore[] cores){
		NBTTagCompound nbt = is.stackTagCompound;
		if (nbt == null)nbt = is.stackTagCompound = new NBTTagCompound();
		
		NBTTagList list = new NBTTagList();
		for(WandCore core:cores)list.appendTag(new NBTTagString(core == null ? "" : core.name()));
		nbt.setTag("wandcores",list);
	}
}
