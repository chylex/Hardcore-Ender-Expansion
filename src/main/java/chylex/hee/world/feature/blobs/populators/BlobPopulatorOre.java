package chylex.hee.world.feature.blobs.populators;
import java.util.Random;
import chylex.hee.world.feature.blobs.StructureWorldBlob;
import chylex.hee.world.feature.ores.GenerateOres;

public class BlobPopulatorOre extends BlobPopulator{
	private GenerateOres oreGenerator;
	
	public BlobPopulatorOre(int weight){
		super(weight);
	}
	
	public BlobPopulatorOre setGenerator(GenerateOres oreGenerator){
		this.oreGenerator = oreGenerator;
		return this;
	}
	
	@Override
	public void populate(StructureWorldBlob world, Random rand){
		oreGenerator.generateFull(world,rand,4);
	}
}
