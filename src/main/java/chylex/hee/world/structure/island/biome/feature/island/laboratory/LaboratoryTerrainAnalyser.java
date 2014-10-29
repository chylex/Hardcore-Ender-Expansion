package chylex.hee.world.structure.island.biome.feature.island.laboratory;
import java.util.Random;
import chylex.hee.world.structure.island.ComponentIsland;
import chylex.hee.world.structure.util.pregen.LargeStructureWorld;

public final class LaboratoryTerrainAnalyser{
	private final LargeStructureWorld world;
	
	public LaboratoryTerrainAnalyser(LargeStructureWorld world){
		this.world = world;
	}
	
	public LaboratoryTerrainMap generateBestMap(Random rand, int attempts){
		LaboratoryTerrainMap bestMap = new LaboratoryTerrainMap(world,0,0);
		
		for(int attempt = 0, dist = ComponentIsland.size*3/4, hdist = dist>>1; attempt < attempts; attempt++){
			LaboratoryTerrainMap map = new LaboratoryTerrainMap(world,rand.nextInt(dist)-hdist,rand.nextInt(dist)-hdist);
			// compare and update
		}
		
		return bestMap;
	}
}
