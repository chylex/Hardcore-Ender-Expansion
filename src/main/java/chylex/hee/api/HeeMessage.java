package chylex.hee.api;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.StringUtils;

public class HeeMessage{
	public final String modid;
	public final String key;
	public final String data;
	public final NBTTagCompound nbt;
	
	HeeMessage(String modid, String key, String data, NBTTagCompound nbt){
		this.modid = modid;
		this.key = StringUtils.removeStart(key, "HEE:");
		this.data = data;
		this.nbt = nbt;
	}
}
