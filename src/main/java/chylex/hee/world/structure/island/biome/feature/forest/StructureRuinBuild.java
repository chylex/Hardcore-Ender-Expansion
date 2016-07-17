package chylex.hee.world.structure.island.biome.feature.forest;
import java.util.Random;
import chylex.hee.world.structure.island.biome.feature.AbstractIslandStructure;

public class StructureRuinBuild extends AbstractIslandStructure{
	public static enum RuinStructureType{
		WALL(6), PLATEAU(3);
		
		final byte rad;
		
		RuinStructureType(int rad){
			this.rad = (byte)rad;
		}
	}
	
	private RuinStructureType structureType;
	
	public StructureRuinBuild setStructureType(RuinStructureType structureType){
		this.structureType = structureType;
		return this;
	}
	
	@Override
	protected boolean generate(Random rand){
		//int x = getRandomXZ(rand,structureType.rad), z = getRandomXZ(rand,structureType.rad), y = world.getHighestY(x,z);
		
		if (structureType == RuinStructureType.WALL){
			
		}
		
		return false;
	}
}
