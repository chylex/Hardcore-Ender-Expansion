package chylex.hee.world.structure.island.biome.feature.island.laboratory;

public enum LaboratoryElementType{
	NONE(-1), HALL_X(2, 0), HALL_Z(0, 2), SMALL_ROOM(4), LARGE_ROOM(5);
	
	public final byte halfSizeX, halfSizeZ;
	final byte sizeX, sizeZ;
	final float oneOverArea;
	
	LaboratoryElementType(int halfSize){
		this(halfSize, halfSize);
	}
	
	LaboratoryElementType(int halfSizeX, int halfSizeZ){
		this.halfSizeX = (byte)halfSizeX;
		this.halfSizeZ = (byte)halfSizeZ;
		this.sizeX = (byte)(halfSizeX*2+1);
		this.sizeZ = (byte)(halfSizeZ*2+1);
		this.oneOverArea = 1F/(sizeX*sizeZ);
	}
	
	public boolean isRoom(){
		return this == SMALL_ROOM || this == LARGE_ROOM;
	}
	
	public boolean isHall(){
		return this == HALL_X || this == HALL_Z;
	}
}
