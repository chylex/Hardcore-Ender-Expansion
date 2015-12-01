package chylex.hee.world.feature.territory;
import java.util.Random;
import chylex.hee.world.end.EndTerritory;
import chylex.hee.world.structure.StructureWorld;

public interface ITerritoryFeature{
	boolean generate(EndTerritory territory, StructureWorld world, Random rand);
}
