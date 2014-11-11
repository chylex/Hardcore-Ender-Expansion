package chylex.hee.mechanics.energy;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import chylex.hee.block.BlockList;
import chylex.hee.system.util.MathUtil;

public class EnergyChunkData{
	public static final float minSignificantEnergy = 0.0001F;
	public static final float energyDrainUnit = 0.05F;
	
	private int x, z;
	private float energyLevel, maxEnergyLevel;
	private byte regenTimer, releaseTimer;
	
	private EnergyChunkData(){}
	
	public EnergyChunkData(int x, int z, Random rand){
		this.x = x;
		this.z = z;
		
		float dist = (float)MathUtil.distance(x,z);
		float lowerBound = 4F+2F*(dist/6000F), upperBound = 10F+6F*(dist/6000F);
		
		maxEnergyLevel = lowerBound+rand.nextFloat()*(upperBound-lowerBound);
		energyLevel = maxEnergyLevel-maxEnergyLevel*rand.nextFloat()*rand.nextFloat();
	}
	
	public void onUpdate(World world, Random rand){
		if ((regenTimer == 0 || --regenTimer == 0) && energyLevel < maxEnergyLevel){
			if ((energyLevel += Math.sqrt(maxEnergyLevel)*0.004F) > maxEnergyLevel)energyLevel = maxEnergyLevel;
			regenTimer = (byte)(8+rand.nextInt(12+rand.nextInt(6))+(int)Math.floor((16F*energyLevel/maxEnergyLevel)+maxEnergyLevel*0.2F));
		}
		
		if ((energyLevel > maxEnergyLevel || (energyLevel > maxEnergyLevel*0.8F && rand.nextInt(10) == 0)) && (releaseTimer == 0 || --releaseTimer == 0)){
			float release = (0.2F+rand.nextFloat()*0.5F)*maxEnergyLevel*0.0625F;
			if (energyLevel < release)release = energyLevel; // precaution
			
			energyLevel -= release;
			releaseTimer = (byte)(4+rand.nextInt(7));
			
			world.setBlock(x+rand.nextInt(16),8+rand.nextInt(116),z+rand.nextInt(16),BlockList.corrupted_energy_low,Math.min(Math.max(2,(int)Math.floor(1F+release*12F)),8),3);
		}
	}
	
	public void onAdjacentInteract(Random rand, EnergyChunkData data){
		if (data.energyLevel < energyLevel && data.energyLevel < data.maxEnergyLevel && Math.abs(data.energyLevel-energyLevel) > 0.4F+rand.nextFloat()*2.5F){
			float amt = Math.min(energyLevel,Math.min(data.maxEnergyLevel-data.energyLevel,Math.max(0.01F,Math.min(0.2F,Math.abs(data.energyLevel-energyLevel)*0.02F*(0.8F+rand.nextFloat()*0.2F)))));
			
			if (amt > minSignificantEnergy){
				energyLevel -= amt;
				data.energyLevel += amt;
			}
		}
	}
	
	public void addEnergy(float amount){
		if (energyLevel+amount <= maxEnergyLevel*2)energyLevel += amount;
		else energyLevel = maxEnergyLevel*2;
	}
	
	public boolean drainEnergyUnit(){
		if (energyLevel >= energyDrainUnit){
			drainEnergy(energyDrainUnit);
			return true;
		}
		else return false;
	}
	
	public float drainEnergy(float amount){
		if (energyLevel-amount >= 0F){
			energyLevel -= amount;
			return 0F;
		}
		else{
			float diff = amount-energyLevel;
			energyLevel = 0F;
			return diff;
		}
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
		tag.setInteger("x",x);
		tag.setInteger("z",z);
		tag.setFloat("lvl",energyLevel);
		tag.setFloat("max",maxEnergyLevel);
		return tag;
	}
	
	public static EnergyChunkData readFromNBT(NBTTagCompound nbt){
		EnergyChunkData data = new EnergyChunkData();
		data.x = nbt.getInteger("x");
		data.z = nbt.getInteger("z");
		data.energyLevel = nbt.getFloat("lvl");
		if (MathUtil.floatEquals(data.maxEnergyLevel = nbt.getFloat("max"),0F))data.maxEnergyLevel = minSignificantEnergy;
		return data;
	}
}
