package chylex.hee.mechanics.energy;

public interface IEnergyBlock{
	boolean isDraining();
	void onWork();
	
	int getEnergyDrained();
	byte getDrainTimer();
}
