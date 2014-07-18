package chylex.hee.system.savedata;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public abstract class Savefile{
	private final WorldData worldData;
	protected final String filename;
	protected NBTTagCompound nbt;
	
	public Savefile(WorldData worldData, String filename){
		this.worldData = worldData;
		this.filename = filename;
		load();
	}
	
	public final boolean load(){
		if ((nbt = worldData.readFile(filename)) != null){
			onLoad();
			return true;
		}
		else return false;		
	}
	
	public final void save(){
		if (nbt != null)worldData.saveFile(filename,nbt);
	}
	
	protected void onLoad(){}
	
	public boolean isWorldEqual(World world){
		return worldData.equals(WorldData.get(world));
	}
}
