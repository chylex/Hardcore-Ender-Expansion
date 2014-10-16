package chylex.hee.mechanics.energy;
import net.minecraft.nbt.NBTTagCompound;

public class EnergyChunkData{
	public NBTTagCompound saveToNBT(){
		return new NBTTagCompound();
	}
	
	public static EnergyChunkData readFromNBT(NBTTagCompound nbt){
		return new EnergyChunkData();
	}
}
