package chylex.hee.world.structure;
import java.util.Random;
import net.minecraft.world.World;

public abstract class StructureBase{
	protected final int radX, sizeY, radZ;
	
	public StructureBase(int radX, int sizeY, int radZ){
		this.radX = radX;
		this.sizeY = sizeY;
		this.radZ = radZ;
	}
	
	public boolean generateInWorld(World world, Random rand, int centerX, int bottomY, int centerZ){
		StructureWorld structureWorld = new StructureWorld(radX,sizeY,radZ);
		
		if (!createGenerator().generate(structureWorld,rand))return false;
		structureWorld.generateInWorld(world,rand,centerX,bottomY,centerZ);
		return true;
	}
	
	protected abstract IStructureGenerator createGenerator();
}
