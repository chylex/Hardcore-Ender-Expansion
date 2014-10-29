package chylex.hee.world.structure.island.biome.feature.island.laboratory;

public final class LaboratoryTerrainNode{
	private LaboratoryElement largestElement = LaboratoryElement.NONE;
	private int mostFrequentY;
	private boolean unusable;
	
	LaboratoryTerrainNode(){
		
	}
	
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
}
