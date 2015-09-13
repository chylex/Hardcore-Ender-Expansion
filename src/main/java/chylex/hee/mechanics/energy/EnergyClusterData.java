package chylex.hee.mechanics.energy;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import chylex.hee.system.util.MathUtil;
import chylex.hee.tileentity.TileEntityEnergyCluster;

public final class EnergyClusterData{
	private float energyLevel, maxEnergyLevel;
	private EnergyClusterHealth health = EnergyClusterHealth.HEALTHY;
	
	private float regenAmount;
	private byte regenTimeLimit;
	
	private byte regenTimer;
	
	public EnergyClusterData(){}
	
	EnergyClusterData(float energyLevel, float maxEnergyLevel, EnergyClusterHealth health){
		setData(energyLevel,maxEnergyLevel,health);
	}
	
	private void setData(float energyLevel, float maxEnergyLevel, EnergyClusterHealth health){
		this.energyLevel = energyLevel;
		this.maxEnergyLevel = maxEnergyLevel;
		this.health = health;
		
		this.regenAmount = (float)(Math.pow(maxEnergyLevel,0.1D)-1F)*health.regenAmountMp;
		this.regenTimeLimit = (byte)(20F/health.regenSpeedMp);
	}
	
	/**
	 * Updates the cluster and returns true if it should resynchronize with clients.
	 */
	public boolean update(TileEntityEnergyCluster cluster){
		boolean resync = false;
		World world = cluster.getWorldObj();
		Random rand = world.rand;
		
		if (++regenTimer >= regenTimeLimit){
			if ((energyLevel += regenAmount) > maxEnergyLevel)energyLevel = maxEnergyLevel;
			regenTimer = 0;
		}
		
		if (health.leakChance != 0F && rand.nextFloat() < health.leakChance){ // direct comparison is fine here
			// TODO
		}
		
		return resync;
	}
	
	public float getEnergyLevel(){
		return energyLevel;
	}
	
	public float getMaxLevel(){
		return maxEnergyLevel;
	}
	
	public EnergyClusterHealth getHealth(){
		return health;
	}
	
	public boolean drainUnit(){
		return drainUnits(1);
	}
	
	public boolean drainUnits(int units){
		if (energyLevel >= EnergyValues.unit*units){
			energyLevel -= EnergyValues.unit*units;
			regenTimer = (byte)-(40F/health.regenSpeedMp);
			return true;
		}
		else return false;
	}
	
	/*public void update(TileEntityEnergyCluster cluster){
		World world = cluster.getWorldObj();
		Random rand = world.rand;
		
		if (energyLevel > 0.1F && energyLevel/maxEnergyLevel > 0.85F+rand.nextFloat()*2.5F && rand.nextInt(75) == 0){
			float leak = energyLevel*(0.05F+rand.nextFloat()*rand.nextFloat()*0.15F);
			energyLevel -= leak;
			cluster.synchronize();
			
			BlockPosM tmpPos = BlockPosM.tmp();
			
			for(int attempt = 0, placed = 0; attempt < 8 && placed < 4; attempt++){
				tmpPos.set(cluster.xCoord+rand.nextInt(7)-3,cluster.yCoord+rand.nextInt(7)-3,cluster.zCoord+rand.nextInt(7)-3);
				
				if (tmpPos.isAir(world)){
					tmpPos.setBlock(world,BlockList.corrupted_energy_low,3+MathUtil.floor(leak*4.5F));
					++placed;
				}
			}
		}
		
		float regenTimMp = energyLevel < maxEnergyLevel*0.5F ? 1F+(1F-(energyLevel/(maxEnergyLevel*0.5F)))*2F : 1F; // slow down regen if below half max energy
		
		if (health.regenTimer != -1 && ++regenTimer > Math.min((int)(health.regenTimer*regenTimMp),125)){
			energyLevel += Math.min((EnergyValues.unit*0.08F+(float)Math.sqrt(maxEnergyLevel)*0.005F)*health.regenMultiplier,maxEnergyLevel-energyLevel);
			cluster.synchronize();
			regenTimer = 0;
		}
	}
	
	public EnergyClusterHealth getHealthStatus(){
		return health;
	}
	
	public float getEnergyLevel(){
		return energyLevel;
	}
	
	public float getMaxEnergyLevel(){
		return maxEnergyLevel;
	}
	
	public void setEnergyLevel(float newLevel){
		this.energyLevel = newLevel;
	}
	
	public float addEnergy(float amount){
		if (energyLevel+amount <= maxEnergyLevel){
			energyLevel += amount;
			return 0F;
		}
		else{
			amount -= maxEnergyLevel-energyLevel;
			energyLevel = maxEnergyLevel;
			return amount;
		}
	}
	
	public float drainEnergy(float amount){
		if (energyLevel >= amount){
			energyLevel -= amount;
			return 0F;
		}
		else{
			float diff = amount-energyLevel;
			energyLevel = 0;
			return diff;
		}
	}
	
	public boolean drainEnergyUnit(){
		return drainEnergyUnits(1);
	}
	
	public boolean drainEnergyUnits(int units){
		if (energyLevel >= EnergyValues.unit*units){
			energyLevel -= EnergyValues.unit*units;
			regenTimer = -18;
			return true;
		}
		else return false;
	}*//*
	
	public void healCluster(){
		if (health.ordinal() > 0)health = EnergyClusterHealth.values[health.ordinal()-1];
	}
	
	public void weakenCluster(){
		if (health.ordinal() < EnergyClusterHealth.values.length)health = EnergyClusterHealth.values[health.ordinal()+1];
	}*/
	
	public void writeToNBT(NBTTagCompound nbt){
		nbt.setByte("status",(byte)health.ordinal());
		nbt.setFloat("lvl",energyLevel);
		nbt.setFloat("max",maxEnergyLevel);
		nbt.setByte("tim",regenTimer);
	}
	
	public void readFromNBT(NBTTagCompound nbt){
		setData(nbt.getFloat("lvl"),nbt.getFloat("max"),EnergyClusterHealth.values[MathUtil.clamp(nbt.getByte("status"),0,EnergyClusterHealth.values.length-1)]);
		regenTimer = nbt.getByte("tim");
	}
}
