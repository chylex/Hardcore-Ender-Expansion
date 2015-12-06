package chylex.hee.world.feature.plants;
import java.util.Random;
import chylex.hee.world.structure.StructureWorld;

public interface IPlantGenerator{
	void generate(GeneratePlants gen, StructureWorld world, Random rand, int x, int y, int z);
	
	
}
