package chylex.hee.api;
import net.minecraft.nbt.NBTTagCompound;

public class HeeMessage{
	public final String modid;
	public final String key;
	public final String data;
	public final NBTTagCompound nbt;
	
	HeeMessage(String modid, String key, String data, NBTTagCompound nbt){
		this.modid = modid;
		this.key = key;
		this.data = data;
		this.nbt = nbt;
	}
}
