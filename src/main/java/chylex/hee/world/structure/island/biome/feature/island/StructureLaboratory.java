package chylex.hee.world.structure.island.biome.feature.island;
import java.util.Random;
import chylex.hee.world.structure.island.biome.feature.AbstractIslandStructure;
import chylex.hee.world.structure.island.biome.feature.island.laboratoryold.LaboratoryTerrainAnalyser;
import chylex.hee.world.structure.island.biome.feature.island.laboratoryold.LaboratoryTerrainMap;

public class StructureLaboratory extends AbstractIslandStructure{
	@Override
	protected boolean generate(Random rand){
		LaboratoryTerrainMap map = new LaboratoryTerrainAnalyser(world).generateBestMap(rand,169);
		
		
		return false;
	}
}
