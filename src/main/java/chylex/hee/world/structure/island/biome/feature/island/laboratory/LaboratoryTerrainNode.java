package chylex.hee.world.structure.island.biome.feature.island.laboratory;


public final class LaboratoryTerrainNode{
	private LaboratoryElement largestElement = LaboratoryElement.NONE;
	private int mostFrequentY;
	private boolean unusable;
	private boolean[] connections = new boolean[4];
	
	LaboratoryTerrainNode(){}
	
	public LaboratoryTerrainNode setUnusable(){
		this.unusable = true;
		return this;
	}
	
	public boolean isUnusable(){
		return unusable;
	}
	
	public LaboratoryTerrainNode setLargestElement(LaboratoryElement element){
		this.largestElement = element;
		return this;
	}
	
	public LaboratoryElement getLargestElement(){
		return largestElement;
	}
	
	public LaboratoryTerrainNode setMostFrequentY(int y){
		this.mostFrequentY = y;
		return this;
	}
	
	public int getMostFrequentY(){
		return mostFrequentY;
	}
	
	public LaboratoryTerrainNode setConnectionAvailable(int direction, boolean available){
		connections[direction] = available;
		return this;
	}
	
	public boolean hasNoAvailableConnections(){
		return !connections[0] && !connections[1] && !connections[2] && !connections[3];
	}
}
