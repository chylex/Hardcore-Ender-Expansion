package chylex.hee.world.structure.island.biome.feature.island;
import java.util.Random;
import chylex.hee.world.structure.island.biome.feature.AbstractIslandStructure;
import chylex.hee.world.structure.island.biome.feature.island.laboratory.LaboratoryTerrainAnalyser;

public class StructureLaboratory extends AbstractIslandStructure{
	@Override
	protected boolean generate(Random rand){
		LaboratoryTerrainAnalyser terrain = new LaboratoryTerrainAnalyser(world);
		
		return false;
	}
}
