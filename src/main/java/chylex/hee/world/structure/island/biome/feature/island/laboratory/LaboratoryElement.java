package chylex.hee.world.structure.island.biome.feature.island.laboratory;

public enum LaboratoryElement{
	NONE(-1), PATH(5), SMALL_ROOM(9), LARGE_ROOM(11);
	
	public final byte size;
	
	LaboratoryElement(int size){
		this.size = (byte)size;
	}
}
