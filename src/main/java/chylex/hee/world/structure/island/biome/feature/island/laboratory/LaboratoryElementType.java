package chylex.hee.world.structure.island.biome.feature.island.laboratory;

public enum LaboratoryElementType{
	NONE(-1), PATH(2), SMALL_ROOM(4), LARGE_ROOM(5);
	
	public final byte halfSize;
	
	LaboratoryElementType(int halfSize){
		this.halfSize = (byte)halfSize;
	}
}
