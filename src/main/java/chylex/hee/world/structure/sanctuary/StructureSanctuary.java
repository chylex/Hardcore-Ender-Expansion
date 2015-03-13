package chylex.hee.world.structure.sanctuary;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureStart;

public class StructureSanctuary extends StructureStart{
	/**
	 * Required for reflection.
	 */
	public StructureSanctuary(){}
	
	public StructureSanctuary(World world, Random rand, int x, int z){
		super(x,z);
		components.add(new ComponentSanctuary(rand,x*16,z*16));
		updateBoundingBox();
	}
}
