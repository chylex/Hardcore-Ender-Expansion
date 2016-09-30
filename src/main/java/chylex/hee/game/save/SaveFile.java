package chylex.hee.game.save;
import java.io.File;
import chylex.hee.system.abstractions.nbt.NBTCompound;

public abstract class SaveFile{
	protected final String filename;
	private boolean wasModified = false;
	
	public SaveFile(String filename){
		this.filename = filename;
	}
	
	protected File getFile(File root){
		return new File(root, filename);
	}
	
	protected void setModified(){
		wasModified = true;
	}
	
	public boolean wasModified(){
		return wasModified;
	}
	
	public final void saveToNBT(File root){
		wasModified = false;
		
		NBTCompound nbt = new NBTCompound();
		onSave(nbt);
		SaveData.saveFile(getFile(root), nbt);
	}
	
	public final void loadFromNBT(File root){
		wasModified = false;
		
		onLoad(SaveData.readFile(getFile(root)));
	}
	
	protected abstract void onSave(NBTCompound nbt);
	protected abstract void onLoad(NBTCompound nbt);
}
