package chylex.hee.world.structure;
import java.util.Random;

public interface IWorldPositionPredicate{
	boolean check(StructureWorld world, Random rand, int x, int y, int z);
}