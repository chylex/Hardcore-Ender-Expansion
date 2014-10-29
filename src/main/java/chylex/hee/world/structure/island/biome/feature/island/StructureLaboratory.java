package chylex.hee.world.structure.island.biome.feature.island;
import java.util.Random;
import chylex.hee.world.structure.island.biome.feature.AbstractIslandStructure;
import chylex.hee.world.structure.island.biome.feature.island.laboratory.LaboratoryTerrainAnalyser;
import chylex.hee.world.structure.island.biome.feature.island.laboratory.LaboratoryTerrainMap;

public class StructureLaboratory extends AbstractIslandStructure{
	@Override
	protected boolean generate(Random rand){
		LaboratoryTerrainMap map = new LaboratoryTerrainAnalyser(world).generateBestMap(rand,100);
		
		
		return false;
	}
}
