package chylex.hee.mechanics.energy;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import chylex.hee.block.BlockList;
import chylex.hee.system.savedata.WorldDataHandler;
import chylex.hee.system.savedata.types.EnergySavefile;
import chylex.hee.tileentity.TileEntityEnergyCluster;

public final class EnergyClusterData{
	private EnergyClusterHealth healthStatus = EnergyClusterHealth.HEALTHY;
	private float energyLevel, maxEnergyLevel;
	private byte regenTimer, drainTimer;
	
	public void generate(Random rand, int blockX, int blockZ){
		int chunkX = blockX>>4, chunkZ = blockZ>>4;
		EnergySavefile file = WorldDataHandler.get(EnergySavefile.class);
		
		float average = file.getFromChunkCoords(chunkX,chunkZ,false).getEnergyLevel();
		for(int a = 0; a < 4; a++)average += file.getFromChunkCoords(chunkX+Direction.offsetX[a]*EnergySavefile.sectionSize,chunkZ+Direction.offsetZ[a]*EnergySavefile.sectionSize,false).getEnergyLevel();
		
		average *= 0.2F;
		
		maxEnergyLevel = (0.25F+rand.nextFloat())*average;
		energyLevel = (0.1F+rand.nextFloat()*0.9F)*maxEnergyLevel;
		healthStatus = EnergyClusterHealth.spawnWeightedList.getRandomItem(rand);
	}
	
	public void update(TileEntityEnergyCluster cluster){
		World world = cluster.getWorldObj();
		Random rand = world.rand;
		
		if (energyLevel > 0.1F && energyLevel/maxEnergyLevel > 0.75F+rand.nextFloat()*0.5F && rand.nextInt(5) == 0){
			float leak = energyLevel*(0.05F+rand.nextFloat()*rand.nextFloat()*0.1F);
			energyLevel -= leak;
			
			for(int attempt = 0, placed = 0, xx, yy, zz; attempt < 8 && placed < 4; attempt++){
				xx = cluster.xCoord+rand.nextInt(5)-2;
				yy = cluster.yCoord+rand.nextInt(5)-2;
				zz = cluster.zCoord+rand.nextInt(5)-2;
				
				if (world.isAirBlock(xx,yy,zz)){
					world.setBlock(xx,yy,zz,BlockList.corrupted_energy_low,3+(int)Math.floor(leak*5F),3);
					++placed;
				}
			}
		}
		
		if (healthStatus.regenTimer != -1 && ++regenTimer > healthStatus.regenTimer){
			energyLevel += Math.min((float)Math.sqrt(maxEnergyLevel)*0.12F,maxEnergyLevel-energyLevel);
			regenTimer = 0;
		}
		
		if (world.provider.dimensionId == 1 && rand.nextInt(healthStatus.ordinal()+1) == 0 && ++drainTimer > 10+rand.nextInt(70)){
			drainTimer = 0;
			
			EnergyChunkData environment = WorldDataHandler.<EnergySavefile>get(EnergySavefile.class).getFromBlockCoords(cluster.xCoord,cluster.zCoord,true);
			float envLevel = environment.getEnergyLevel();
			
			if (envLevel > 0){
				float drain = Math.min(maxEnergyLevel-energyLevel,Math.min(envLevel*0.1F,maxEnergyLevel*0.2F));
				drain = drain*0.75F+rand.nextFloat()*0.25F*drain;
				drain = environment.drainEnergy(drain);
				energyLevel += drain;
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
