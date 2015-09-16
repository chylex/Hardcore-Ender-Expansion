package chylex.hee.mechanics.energy;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import chylex.hee.block.BlockCorruptedEnergy;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.util.MathUtil;
import chylex.hee.tileentity.TileEntityEnergyCluster;

public final class EnergyClusterData{
	private float energyLevel, maxEnergyLevel;
	private EnergyClusterHealth health = EnergyClusterHealth.HEALTHY;
	
	private float regenAmount;
	private byte regenTimeLimit;
	
	private byte regenTimer;
	private boolean resync;
	
	public EnergyClusterData(){}
	
	EnergyClusterData(float energyLevel, float maxEnergyLevel, EnergyClusterHealth health){
		setData(energyLevel,maxEnergyLevel,health);
	}
	
	private void setData(float energyLevel, float maxEnergyLevel, EnergyClusterHealth health){
		this.energyLevel = energyLevel;
		this.maxEnergyLevel = maxEnergyLevel;
		this.health = health;
		
		this.regenAmount = (float)(Math.pow(1F+maxEnergyLevel,0.1D)-1F)*health.regenAmountMp;
		this.regenTimeLimit = (byte)(20F/health.regenSpeedMp);
	}
	
	public void update(TileEntityEnergyCluster cluster){
		World world = cluster.getWorldObj();
		Random rand = world.rand;
		
		if (++regenTimer >= regenTimeLimit){
			if ((energyLevel += regenAmount) > maxEnergyLevel)energyLevel = maxEnergyLevel;
			regenTimer = 0;
			cluster.synchronize();
		}
		
		if (health.leakChance != 0F && rand.nextFloat() < health.leakChance){ // direct comparison is fine here
			float leak = Math.min(energyLevel,0.5F+rand.nextFloat()*0.5F)*(float)(Math.pow(maxEnergyLevel,0.12F)+Math.pow(energyLevel,0.05F)-2);
			energyLevel -= leak;
			cluster.synchronize();
			
			for(int attempt = 0; attempt < 10; attempt++){
				Pos testPos = Pos.at(cluster).offset(rand.nextInt(5)-2,rand.nextInt(5)-2,rand.nextInt(5)-2);
				
				if (testPos.isAir(world)){
					testPos.setBlock(world,BlockCorruptedEnergy.getCorruptedEnergy(2+MathUtil.floor(leak*9F)));
					break;
				}
			}
		}
		
		if (resync){
			cluster.synchronize();
			resync = false;
		}
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
			resync = true;
			return true;
		}
		else return false;
	}
	
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
