package chylex.hee.mechanics.energy;
import java.util.Random;
import net.minecraft.world.World;

public class EnergyChunkDataOverworld extends EnergyChunkData{
	public EnergyChunkDataOverworld(){
		super(0,0,new Random());
	}
	
	@Override
	public void onUpdate(World world, Random rand){}
	@Override
	public void onAdjacentInteract(Random rand, EnergyChunkData data){}
	
	@Override
	public float addEnergy(float amount){
		return amount;
	}
	
	@Override
	public boolean drainEnergyUnit(){
		return false;
	}
	
	@Override
	public float drainEnergy(float amount){
		return amount;
	}
	
	@Override
	public float getEnergyLevel(){
		return 0F;
	}
	
	@Override
	public boolean equals(Object obj){
		return obj == this; // expected only single instance
	}
}
