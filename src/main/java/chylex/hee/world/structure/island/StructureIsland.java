package chylex.hee.world.structure.island;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureStart;

public class StructureIsland extends StructureStart{
	public StructureIsland(){}
	
	@SuppressWarnings("unchecked")
	public StructureIsland(World world, Random rand, int x, int z){
		super(x,z);
		components.add(new ComponentScatteredFeatureIsland(rand,x*16,z*16));
		updateBoundingBox();
	}
}
