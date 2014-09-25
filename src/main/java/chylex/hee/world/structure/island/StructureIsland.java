package chylex.hee.world.structure.island;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureStart;

public class StructureIsland extends StructureStart{
	/**
	 * Required for reflection.
	 */
	public StructureIsland(){}
	
	@SuppressWarnings("unchecked")
	public StructureIsland(World world, Random rand, int x, int z){
		super(x,z);
		components.add(new ComponentIsland(rand,x*16,z*16));
		updateBoundingBox();
	}
}
