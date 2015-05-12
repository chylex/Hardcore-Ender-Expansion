package chylex.hee.mechanics.energy;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import chylex.hee.init.BlockList;
import chylex.hee.system.savedata.WorldDataHandler;
import chylex.hee.system.savedata.types.EnergySavefile;
import chylex.hee.system.util.BlockPosM;
import chylex.hee.system.util.MathUtil;
import chylex.hee.tileentity.TileEntityEnergyCluster;
import chylex.hee.world.util.Direction;

public final class EnergyClusterData{
	private EnergyClusterHealth healthStatus = EnergyClusterHealth.HEALTHY;
	private float energyLevel, maxEnergyLevel;
	private byte regenTimer, drainTimer;
	
	public void generate(World world, int blockX, int blockZ){
		if (world.provider.dimensionId == 1){
			int chunkX = blockX>>4, chunkZ = blockZ>>4;
			EnergySavefile file = WorldDataHandler.get(EnergySavefile.class);
			
			float average = file.getFromChunkCoords(world,chunkX,chunkZ,false).getEnergyLevel();
			for(int a = 0; a < 4; a++)average += file.getFromChunkCoords(world,chunkX+Direction.offsetX[a]*EnergySavefile.sectionSize,chunkZ+Direction.offsetZ[a]*EnergySavefile.sectionSize,false).getEnergyLevel();
			
			maxEnergyLevel = (0.25F+world.rand.nextFloat())*average*0.2F;
		}
		else maxEnergyLevel = (0.25F+world.rand.nextFloat())*5F;
		
		energyLevel = (0.1F+world.rand.nextFloat()*0.9F)*maxEnergyLevel;
		healthStatus = EnergyClusterHealth.spawnWeightedList.getRandomItem(world.rand);
	}
	
	public void update(TileEntityEnergyCluster cluster){
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
		
		if (healthStatus.regenTimer != -1 && ++regenTimer > Math.min((int)(healthStatus.regenTimer*regenTimMp),125)){
			energyLevel += Math.min((EnergyChunkData.energyDrainUnit*0.08F+(float)Math.sqrt(maxEnergyLevel)*0.005F)*healthStatus.regenMultiplier,maxEnergyLevel-energyLevel);
			cluster.synchronize();
			regenTimer = 0;
		}
		
		if (world.provider.dimensionId == 1 && rand.nextInt(healthStatus.ordinal()+1) == 0 && ++drainTimer > 10+rand.nextInt(70)){
			drainTimer = 0;
			
			EnergyChunkData environment = WorldDataHandler.<EnergySavefile>get(EnergySavefile.class).getFromBlockCoords(world,cluster.xCoord,cluster.zCoord,true);
			float envLevel = environment.getEnergyLevel();
			
			if (envLevel > EnergyChunkData.minSignificantEnergy && maxEnergyLevel < energyLevel){
				float drain = Math.min(maxEnergyLevel-energyLevel,Math.min(envLevel*0.1F,maxEnergyLevel*0.2F));
				drain = drain*0.75F+rand.nextFloat()*0.25F*drain;
				drain = environment.drainEnergy(drain);
				energyLevel += drain;
				cluster.synchronize();
			}
		}
	}
	
	public EnergyClusterHealth getHealthStatus(){
		return healthStatus;
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
		if (energyLevel >= EnergyChunkData.energyDrainUnit*units){
			energyLevel -= EnergyChunkData.energyDrainUnit*units;
			regenTimer = -18;
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
		nbt.setByte("regen",regenTimer);
		nbt.setByte("drain",drainTimer);
	}
	
	public void readFromNBT(NBTTagCompound nbt){
		byte status = nbt.getByte("status");
		healthStatus = status >= 0 && status < EnergyClusterHealth.values.length ? EnergyClusterHealth.values[status] : EnergyClusterHealth.HEALTHY;
		
		energyLevel = nbt.getFloat("lvl");
		maxEnergyLevel = nbt.getFloat("max");
		regenTimer = nbt.getByte("regen");
		drainTimer = nbt.getByte("drain");
	}
}
