package chylex.hee.system.savedata;
import net.minecraft.nbt.NBTTagCompound;

public abstract class WorldSavefile{
	public abstract void onSave(NBTTagCompound nbt);
	public abstract void onLoad(NBTTagCompound nbt);
}
