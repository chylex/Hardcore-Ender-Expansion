package chylex.hee.mechanics.energy;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import chylex.hee.tileentity.TileEntityEnergyCluster;

public final class EnergyClusterData{
	public static final byte MAX_BOOST = 24;
	
	private int energyAmount;
	private int maxEnergyAmount;
	private int maxRegenerativeEnergyAmount;
	private byte weaknessLevel;
	private byte healTimer;
	private byte regenBoost;
	
	public EnergyClusterData(Random rand){
		maxEnergyAmount = Math.min(1600,380+rand.nextInt(1300));
		maxRegenerativeEnergyAmount = maxEnergyAmount/5+rand.nextInt(maxEnergyAmount-maxEnergyAmount/3-maxEnergyAmount/8);
		energyAmount = 80+rand.nextInt(535);
		while(energyAmount > maxRegenerativeEnergyAmount)energyAmount -= 50+rand.nextInt(80);
		
		if (rand.nextInt(10) == 0)regenBoost = 1;
		else if (rand.nextInt(4) == 0)weaknessLevel = (byte)rand.nextInt(90);
	}
	
	public void update(TileEntityEnergyCluster cluster){
		if (energyAmount < getMaxRegenerativeEnergyAmount() && ++healTimer > 85+cluster.getWorldObj().rand.nextInt(40-(regenBoost>>2))-regenBoost){
			energyAmount += (int)Math.ceil((4+regenBoost*0.125F)*(weaknessLevel > 0 ? (50-(weaknessLevel>>1))*0.01F : 0.85F+cluster.getWorldObj().rand.nextFloat()*0.3F)*(regenBoost == 0 ? 0.24F : 1F));
			healTimer = (byte)(regenBoost == 0 ? -127 : -90);
			cluster.synchronize();
		}
	}
	
	public void addEnergy(int amount){
		energyAmount += amount;
	}
	
	public boolean removeEnergyUnit(){
		if (energyAmount >= 100){
			energyAmount -= 100;
			return true;
		}
		else return false;
	}
	
	public int getEnergyAmount(){
		return energyAmount;
	}
	
	public int getMaxEnergyAmount(){
		return weaknessLevel > 0 ? (maxEnergyAmount>>1) : maxEnergyAmount;
	}
	
	public int getMaxRegenerativeEnergyAmount(){
		return weaknessLevel > 0 ? (maxRegenerativeEnergyAmount>>1) : maxRegenerativeEnergyAmount;
	}
	
	public void boost(){
		regenBoost = (byte)Math.min(regenBoost+1,MAX_BOOST);
		if ((maxRegenerativeEnergyAmount += (maxEnergyAmount-maxRegenerativeEnergyAmount)/12)+(int)Math.floor(Math.random()*3D) > maxEnergyAmount)maxRegenerativeEnergyAmount = maxEnergyAmount;
		
	}
	
	public int getBoost(){
		return regenBoost;
	}
	
	public void weaken(){
		energyAmount /= 2;
		weaknessLevel = 100;
	}
	
	public void healWeakness(int amount){
		weaknessLevel = (byte)Math.max(0,weaknessLevel-amount);
		if (regenBoost == 0)regenBoost = 1;
	}
	
	public int getWeaknessLevel(){
		return weaknessLevel;
	}
	
	public void writeToNBT(NBTTagCompound nbt){
		nbt.setShort("energyAmt",(short)energyAmount);
		nbt.setShort("maxAmt",(short)maxEnergyAmount);
		nbt.setShort("maxRegen",(short)maxRegenerativeEnergyAmount);
		nbt.setByte("weakness",weaknessLevel);
		nbt.setByte("healTimer",healTimer);
		nbt.setByte("regenBoost",regenBoost);
	}
	
	public void readFromNBT(NBTTagCompound nbt){
		energyAmount = nbt.getShort("energyAmt");
		maxEnergyAmount = nbt.getShort("maxAmt");
		maxRegenerativeEnergyAmount = nbt.getShort("maxRegen");
		weaknessLevel = nbt.getByte("weakness");
		healTimer = nbt.getByte("healTimer");
		regenBoost = nbt.getByte("regenBoost");
	}
}
