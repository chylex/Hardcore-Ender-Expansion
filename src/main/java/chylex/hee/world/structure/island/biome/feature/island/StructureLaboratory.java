package chylex.hee.world.structure.island.biome.feature.island;
import java.util.Random;
import chylex.hee.system.logging.Stopwatch;
import chylex.hee.world.structure.island.biome.feature.AbstractIslandStructure;
import chylex.hee.world.structure.island.biome.feature.island.laboratory.LaboratoryGenerator;
import chylex.hee.world.structure.island.biome.feature.island.laboratory.LaboratoryPlan;

public class StructureLaboratory extends AbstractIslandStructure{
	@Override
	protected boolean generate(Random rand){
		Stopwatch.time("StructureLaboratory - generate");
		
		LaboratoryPlan plan = new LaboratoryPlan(), bestPlan = null;
		int attemptsGeneral = 500, attemptsSuccess = 120;
		
		do{
			if (plan.generate(world,rand)){
				--attemptsSuccess;
				if ((bestPlan == null || plan.getScore() > bestPlan.getScore()))bestPlan = plan.copy();
			}
		}while(--attemptsGeneral > 0 && attemptsSuccess > 0 && (bestPlan == null || bestPlan.getScore() < 400));
		
		if (bestPlan == null)return false;

		new LaboratoryGenerator(bestPlan).generateInWorld(world,rand);
		Stopwatch.finish("StructureLaboratory - generate");
		return true;
	}
}
