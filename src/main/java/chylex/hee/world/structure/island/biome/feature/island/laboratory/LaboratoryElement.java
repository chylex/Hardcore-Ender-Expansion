package chylex.hee.world.structure.island.biome.feature.island.laboratory;

public final class LaboratoryElement{
	public final LaboratoryElementType type;
	public final int x, y, z;
	
	public LaboratoryElement(LaboratoryElementType type, int x, int y, int z){
		this.type = type;
		this.x = x;
		this.y = y;
		this.z = z;
	}
}
