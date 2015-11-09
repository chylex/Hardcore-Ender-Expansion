package chylex.hee.world.end;
import java.util.stream.IntStream;
import net.minecraft.world.ChunkCoordIntPair;

public enum EndTerritory{
	THE_HUB(24), // 384 blocks
	;
	
	public final int chunkSize;
	
	private EndTerritory(int chunkSize){
		this.chunkSize = chunkSize;
	}
	
	public static final int chunksBetween = 64; // 1024 blocks
	public static final int chunkOffset = -THE_HUB.chunkSize/2;
	public static final EndTerritory[] values = values();
	
	/**
	 * Finds the top left chunk for a territory using magic. They are generated in this manner:<br>
	 * {@code 7|5} <br>
	 * {@code 3|1} <br>
	 * {@code 2|0} <br>
	 * {@code 6|4} <br>
	 * This was tested using modified values that cannot be used in production. Do not touch this. Ever.
	 */
	public static ChunkCoordIntPair getStartPoint(EndTerritory territory, int index){
		int chunkOffX = 0;
		
		if ((index/2)%2 == 0)chunkOffX = IntStream.range(0,territory.ordinal()).map(level -> chunksBetween+values[level].chunkSize).sum(); // positive x
		else chunkOffX = -IntStream.rangeClosed(1,territory.ordinal()).map(level -> chunksBetween+values[level].chunkSize).sum(); // negative x
		
		int distZ = index/4;
		if (index%2 != 0)distZ = -distZ-1; // moves odd indexes to negative z
		
		int chunkOffZ = distZ*(chunksBetween+territory.chunkSize);
		
		return new ChunkCoordIntPair(chunkOffX+chunkOffset,chunkOffZ+chunkOffset);
	}
}
