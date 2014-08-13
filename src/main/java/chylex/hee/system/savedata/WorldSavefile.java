package chylex.hee.system.savedata;
import net.minecraft.nbt.NBTTagCompound;

public abstract class WorldSavefile{
	public final String filename;
	private boolean wasModified = false;
	
	public WorldSavefile(String filename){
		this.filename = filename;
	}
	
	protected void setModified(){
		wasModified = true;
	}
	
	public boolean wasModified(){
		return wasModified;
	}
	
	public final void saveToNBT(NBTTagCompound nbt){
		wasModified = false;
		onSave(nbt);
	}
	
	public final void loadFromNBT(NBTTagCompound nbt){
		wasModified = false;
		onLoad(nbt);
	}
	
	protected abstract void onSave(NBTTagCompound nbt);
	protected abstract void onLoad(NBTTagCompound nbt);
}
