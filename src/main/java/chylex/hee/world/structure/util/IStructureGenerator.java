package chylex.hee.world.structure.util;
import java.util.Random;
import chylex.hee.world.structure.StructureWorld;

public interface IStructureGenerator{
	boolean generate(StructureWorld world, Random rand);
}
