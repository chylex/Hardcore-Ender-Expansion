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
			if (plan.generate(world,rand) && (bestPlan == null || plan.getScore() > bestPlan.getScore()))bestPlan = plan.copy();
		}while(++attempts < 400 && bestPlan.getScore() < 600);
		
		System.out.println("SCORE "+bestPlan.getScore());
		
		new LaboratoryGenerator(bestPlan).generateInWorld(world,rand);
		return true;
	}
}
