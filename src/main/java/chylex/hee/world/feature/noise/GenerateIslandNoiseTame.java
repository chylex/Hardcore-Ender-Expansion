package chylex.hee.world.feature.noise;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.StructureWorldPart;
import chylex.hee.world.util.BoundingBox;

/**
 * A special version of island noise generation which uses {@link StructureWorldPart} and allows
 * conditional spawning with multiple attempts to have more control over the generator.
 */
public class GenerateIslandNoiseTame{
	private final GenerateIslandNoise generator;
	
	public GenerateIslandNoiseTame(GenerateIslandNoise generator){
		this.generator = generator;
	}
	
	public void generate(StructureWorld world, int offsetX, int offsetY, int offsetZ){
		BoundingBox area = world.getArea();
		StructureWorldPart part = new StructureWorldPart(world.getParentWorld(),area.x2,area.y2,area.z2);
		generator.generate(part);
		
		// TODO condition handling
		part.insertInto(world,offsetX,offsetY,offsetZ);
	}
}
