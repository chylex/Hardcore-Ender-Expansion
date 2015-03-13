package chylex.hee.system.util;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.ArrayUtils;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;

public class ItemPattern{
	private String prefix = "";
	private String name = "";
	private boolean nameWildcard;
	private short[] damageValues = ArrayUtils.EMPTY_SHORT_ARRAY;
	private NBTTagCompound nbt;
	
	public ItemPattern setItemName(String prefix, String name){
		this.prefix = prefix;
		this.name = name;
		if (name.equals("*"))nameWildcard = true;
		return this;
	}
	
	public ItemPattern setDamageValues(int[] values){
		if (values != null){
			damageValues = new short[values.length];
			for(int a = 0; a < values.length; a++)damageValues[a] = (short)values[a];
		}
		
		return this;
	}
	
	public ItemPattern setNBT(NBTTagCompound nbt){
		if (!nbt.hasNoTags())this.nbt = nbt;
		return this;
	}
	
	public boolean matches(ItemStack is){
		if (!(nameWildcard && prefix.isEmpty())){
			UniqueIdentifier id = GameRegistry.findUniqueIdentifierFor(is.getItem());
			if (!(prefix.equals(id.modId) && (nameWildcard || name.equals(id.name))))return false;
		}
		
		if (!(damageValues.length == 0 || ArrayUtils.contains(damageValues,(short)is.getItemDamage())))return false;
		if (!(nbt == null || (is.hasTagCompound() && nbt.equals(is.getTagCompound()))))return false;
		
		return true;
	}
}
