package chylex.hee.mechanics.gem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class GemData{
	private static final GemData cache = new GemData();
	
	/**
	 * @return Instance of GemData cache, do not store anywhere!!!
	 */
	public static GemData loadFromItemStack(ItemStack is){
		cache.set(is.stackTagCompound == null ? new NBTTagCompound() : is.stackTagCompound);
		return cache;
	}
	
	public static void updateItemStack(ItemStack is, int dimension, int x, int y, int z){
		cache.set(dimension,x,y,z);
		cache.saveToItemStack(is);
	}
	
	private int dim,x,y,z;
	
	private void set(NBTTagCompound nbt){
		dim = nbt.hasKey("HED_Gem_Dim") ? nbt.getInteger("HED_Gem_Dim") : -999;
		x = nbt.getInteger("HED_Gem_X");
		y = nbt.getInteger("HED_Gem_Y");
		z = nbt.getInteger("HED_Gem_Z");
	}
	
	private void set(int dimension, int x, int y, int z){
		this.dim = dimension;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public boolean isLinked(){
		return dim != -999;
	}
	
	public int getDimension(){
		return dim;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public int getZ(){
		return z;
	}
	
	public void saveToItemStack(ItemStack is){
		NBTTagCompound nbt = is.stackTagCompound;
		if (nbt == null)nbt = new NBTTagCompound();
		
		nbt.setInteger("HED_Gem_Dim",dim);
		nbt.setInteger("HED_Gem_X",x);
		nbt.setInteger("HED_Gem_Y",y);
		nbt.setInteger("HED_Gem_Z",z);
		is.stackTagCompound = nbt;
	}
}
