package chylex.hee.world.structure.island.biome.feature.forest;
import java.util.Random;
import chylex.hee.world.structure.island.biome.feature.AbstractIslandStructure;

public class StructureRuinStructure extends AbstractIslandStructure{
	public static enum RuinStructureType{
		WALL(6), PLATEAU(3);
		
		final byte rad;
		
		RuinStructureType(int rad){
			this.rad = (byte)rad;
		}
	}
	
	private RuinStructureType structureType;
	
	public StructureRuinStructure setStructureType(RuinStructureType structureType){
		this.structureType = structureType;
		return this;
	}
	
	@Override
	protected boolean generate(Random rand){
		int xx = getRandomXZ(rand,structureType.rad), zz = getRandomXZ(rand,structureType.rad), yy = world.getHighestY(xx,zz);
		
		if (structureType == RuinStructureType.WALL){
			
		}
		
		return false;
	}
}
