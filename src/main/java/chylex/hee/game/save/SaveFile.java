package chylex.hee.game.save;
import java.io.File;
import net.minecraft.nbt.NBTTagCompound;

public abstract class SaveFile{
	protected final String filename;
	private boolean wasModified = false;
	
	public SaveFile(String filename){
		this.filename = filename;
	}
	
	public File getFile(File root){
		return new File(root,filename);
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
