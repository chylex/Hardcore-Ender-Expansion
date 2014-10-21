package chylex.hee.mechanics.energy;
import java.util.Random;
import chylex.hee.system.util.MathUtil;
import net.minecraft.nbt.NBTTagCompound;

public class EnergyChunkData{
	public static final float minSignificantEnergy = 0.0001F;
	
	private int x, z;
	private float energyLevel, maxEnergyLevel, regenLimiter;
	private byte regenTimer;
	
	private EnergyChunkData(){}
	
	public EnergyChunkData(int x, int z, Random rand){
		this.x = x;
		this.z = z;
		
		float dist = (float)MathUtil.distance(x,z);
		float lowerBound = 4F+2F*(dist/6000F), upperBound = 10F+6F*(dist/6000F);
		
		maxEnergyLevel = lowerBound+rand.nextFloat()*(upperBound-lowerBound);
		energyLevel = maxEnergyLevel-maxEnergyLevel*rand.nextFloat()*rand.nextFloat();
	}
	
	public void onUpdate(Random rand){
		if (regenLimiter > 0F && (regenTimer == 0 || --regenTimer == 0)){
			regenLimiter = regenLimiter < 0.05F ? 0F : regenLimiter*0.85F;
			regenTimer = 3; // 0.6 seconds
		}
	}
	
	public void onAdjacentInteract(Random rand, EnergyChunkData data){
		if (data.energyLevel < energyLevel && data.energyLevel < data.maxEnergyLevel && Math.abs(data.energyLevel-energyLevel) > 0.4F+rand.nextFloat()*2.5F){
			float amt = Math.min(energyLevel,Math.min(data.maxEnergyLevel-data.energyLevel,Math.max(0.01F,Math.min(0.2F,Math.abs(data.energyLevel-energyLevel)*0.08F))));
			
			if (amt > minSignificantEnergy){
				energyLevel -= amt;
				data.energyLevel += amt;
			}
		}
	}
	
	public float tryRegenerate(float amount){
		float maxRegen = maxEnergyLevel*0.0625F;
		float regen = Math.min(amount,maxEnergyLevel-energyLevel);
		
		if (regenLimiter+regen > maxRegen)regen = maxRegen-regenLimiter;
		if (regen <= minSignificantEnergy)return amount;
		
		energyLevel += regen;
		regenLimiter += regen;
		regenTimer = 10; // 2 seconds
		
		return MathUtil.floatEquals(regen,amount) ? 0F : amount-regen;
	}
	
	public boolean drainEnergy(float amount){
		return false;
	}
	
	public float getEnergyLevel(){
		return energyLevel;
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
		tag.setFloat("max",maxEnergyLevel);
		tag.setFloat("lim",regenLimiter);
		return tag;
	}
	
	public static EnergyChunkData readFromNBT(NBTTagCompound nbt){
		EnergyChunkData data = new EnergyChunkData();
		data.x = nbt.getInteger("x");
		data.z = nbt.getInteger("z");
		data.energyLevel = nbt.getFloat("lvl");
		data.maxEnergyLevel = nbt.getFloat("max");
		data.regenLimiter = nbt.getFloat("lim");
		return data;
	}
}
