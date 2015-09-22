package chylex.hee.game.save.types;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.NBTTagCompound;
import chylex.hee.game.save.SaveFile;

public class QuickSaveFile extends SaveFile{
	private static final List<IQuickSavefile> handlers = new ArrayList<>();
	
	public static final void addHandler(IQuickSavefile handler){
		handlers.add(handler);
	}
	
	/**
	 * QuickSavefile handler interface for simplified data saving across worlds.
	 * Call WorldGenHandler.get to refresh the savefile before using the data.
	 */
	public static interface IQuickSavefile{
		void onSave(NBTTagCompound nbt);
		void onLoad(NBTTagCompound nbt);
	}
	
	public QuickSaveFile(){
		super("generic.nbt");
	}
	
	@Override
	public void setModified(){
		super.setModified();
	}

	@Override
	protected void onSave(NBTTagCompound nbt){
		for(IQuickSavefile handler:handlers){
			NBTTagCompound tag = new NBTTagCompound();
			handler.onSave(tag);
			nbt.setTag(handler.getClass().getSimpleName(),tag);
		}
	}

	@Override
	protected void onLoad(NBTTagCompound nbt){
		for(IQuickSavefile handler:handlers)handler.onLoad(nbt.getCompoundTag(handler.getClass().getSimpleName()));
	}
}
