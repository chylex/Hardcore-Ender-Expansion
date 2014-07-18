package chylex.hee.world.structure.island.feature;
import java.util.Random;

public class StructureForestRuinStructure extends AbstractIslandStructure{
	public static enum RuinStructureType{
		WALL(6), PLATEAU(3);
		
		final byte rad;
		
		RuinStructureType(int rad){
			this.rad = (byte)rad;
		}
	}
	
	private RuinStructureType structureType;
	
	public StructureForestRuinStructure setStructureType(RuinStructureType structureType){
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
