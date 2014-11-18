package chylex.hee.world.structure.island.biome.feature.island;
import java.util.Random;
import chylex.hee.world.structure.island.biome.feature.AbstractIslandStructure;
import chylex.hee.world.structure.island.biome.feature.island.laboratory.LaboratoryGenerator;
import chylex.hee.world.structure.island.biome.feature.island.laboratory.LaboratoryPlan;

public class StructureLaboratory extends AbstractIslandStructure{
	@Override
	protected boolean generate(Random rand){
		LaboratoryPlan plan = new LaboratoryPlan(), bestPlan = null;
		int score = -1, attempts = 0;
		
		do{
			plan.generate(world,rand);
			score = plan.getScore();
			if (bestPlan == null || score > bestPlan.getScore())bestPlan = plan.copy();
		}while(++attempts < 200 && score < 30);
		
		new LaboratoryGenerator(bestPlan).generateInWorld(world,rand);
		return true;
	}
}
