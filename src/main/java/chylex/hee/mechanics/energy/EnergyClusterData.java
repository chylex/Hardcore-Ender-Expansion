package chylex.hee.mechanics.energy;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import chylex.hee.tileentity.TileEntityEnergyCluster;

public final class EnergyClusterData{
	private EnergyClusterHealth healthStatus;
	private float energyLevel, maxEnergyLevel;
	
	public EnergyClusterData(){}
	
	public void generate(Random rand, int blockX, int blockZ){
		
	}
	
	public void update(TileEntityEnergyCluster cluster){
		
	}
	
	/*public boolean removeEnergyUnit(){
		if (energyAmount >= 100){
			energyAmount -= 100;
			return true;
		}
		else return false;
	}*/
	
	public EnergyClusterHealth getHealthStatus(){
		return healthStatus;
	}
	
	public float getEnergyLevel(){
		return energyLevel;
	}
	
	public float getMaxEnergyLevel(){
		return maxEnergyLevel;
	}
	
	public boolean drainEnergyUnit(){
		return drainEnergyUnits(1);
	}
	
	public boolean drainEnergyUnits(int units){
		if (energyLevel >= 1F){
			energyLevel -= 1F;
			return true;
		}
		else return false;
	}
	
	public void healCluster(){
		if (healthStatus.ordinal() > 0)healthStatus = EnergyClusterHealth.values[healthStatus.ordinal()-1];
	}
	
	public void weakenCluster(){
		if (healthStatus.ordinal() < EnergyClusterHealth.values.length)healthStatus = EnergyClusterHealth.values[healthStatus.ordinal()+1];
	}
	
	public void writeToNBT(NBTTagCompound nbt){
		nbt.setByte("status",(byte)healthStatus.ordinal());
		nbt.setFloat("lvl",energyLevel);
		nbt.setFloat("max",maxEnergyLevel);
	}
	
	public void readFromNBT(NBTTagCompound nbt){
		byte status = nbt.getByte("status");
		healthStatus = status >= 0 && status < EnergyClusterHealth.values.length ? EnergyClusterHealth.values[status] : EnergyClusterHealth.HEALTHY;
		
		energyLevel = nbt.getFloat("lvl");
		maxEnergyLevel = nbt.getFloat("max");
	}
}
