package chylex.hee.mechanics.energy;

public enum EnergyClusterHealth{
	HEALTHY(0x54ff54,90,"energy.status.healthy"), WEAKENED(0xffc454,40,"energy.status.weakened"), TIRED(0xff7a54,10,"energy.status.tired"), UNSTABLE(0xff5454,0,"energy.status.unstable");
	
	public static final EnergyClusterHealth[] values = values();
	
	public final int color;
	public final byte chanceToWeaken;
	public final String translationText;
	
	EnergyClusterHealth(int color, int chanceToWeaken, String translationText){
		this.color = color;
		this.chanceToWeaken = (byte)chanceToWeaken;
		this.translationText = translationText;
	}
}
