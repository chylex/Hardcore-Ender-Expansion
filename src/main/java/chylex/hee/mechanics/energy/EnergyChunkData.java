package chylex.hee.mechanics.energy;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;

public class EnergyChunkData{
	private int x, z;
	private float energyLevel = 0F, maxEnergyLevel = 0F, regenLimiter = 0F;
	
	private EnergyChunkData(){}
	
	public EnergyChunkData(int x, int z, Random rand){
		this.x = x;
		this.z = z;
		
		// TODO generate
	}
	
	public void onUpdate(Random rand){
		
	}
	
	public void onAdjacentInteract(Random rand, EnergyChunkData data){
		if (data.energyLevel < energyLevel){
			
		}
	}
	
	public float tryRegenerate(float amount){
		
	}
	
	@Override
	public boolean equals(Object obj){
		if (obj instanceof EnergyChunkData){
			EnergyChunkData data = (EnergyChunkData)obj;
			return data.x == x && data.z == z;
		}
		else return false;
	}
	
	@Override
	public int hashCode(){ // copied hashing from ChunkCoordIntPair
		return (1664525*x+1013904223)^(1664525*(z^-559038737)+1013904223);
	}
	
	public NBTTagCompound saveToNBT(){
		NBTTagCompound tag = new NBTTagCompound();
		tag.setFloat("lvl",energyLevel);
		return tag;
	}
	
	public static EnergyChunkData readFromNBT(NBTTagCompound nbt){
		EnergyChunkData data = new EnergyChunkData();
		data.x = nbt.getInteger("x");
		data.z = nbt.getInteger("z");
		data.energyLevel = nbt.getFloat("lvl");
		return data;
	}
}
