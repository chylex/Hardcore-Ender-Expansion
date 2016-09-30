package chylex.hee.world.feature.ores;
import java.util.Random;
import chylex.hee.world.structure.StructureWorld;

public abstract class OreGeneratorConditioned implements IOreGenerator{
	private final IOreGenerator wrapped;
	
	public OreGeneratorConditioned(IOreGenerator wrapped){
		this.wrapped = wrapped;
	}
	
	@Override
	public void generate(GenerateOres gen, StructureWorld world, Random rand, int x, int y, int z, int ores){
		wrapped.generate(gen, world, rand, x, y, z, ores);
	}
	
	@Override
	public boolean canPlaceAt(GenerateOres gen, StructureWorld world, Random rand, int x, int y, int z){
		return wrapped.canPlaceAt(gen, world, rand, x, y, z);
	}
}
