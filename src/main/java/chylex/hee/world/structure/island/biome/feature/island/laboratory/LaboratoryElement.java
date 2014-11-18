package chylex.hee.world.structure.island.biome.feature.island.laboratory;
import java.util.Arrays;

public final class LaboratoryElement{
	public final LaboratoryElementType type;
	public final int x, y, z;
	public final boolean[] connected = new boolean[4];
	
	public LaboratoryElement(LaboratoryElementType type, int x, int y, int z){
		this.type = type;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public String toString(){
		return type.name()+" ("+x+","+y+","+z+") ["+Arrays.toString(connected)+"]";
	}
}
