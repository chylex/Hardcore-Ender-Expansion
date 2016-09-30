package chylex.hee.world.structure;
import java.util.Random;
import java.util.function.Consumer;
import net.minecraft.world.World;
import chylex.hee.world.structure.util.IStructureGenerator;

public abstract class StructureBase<T extends IStructureGenerator>{
	protected final int radX, sizeY, radZ;
	protected Consumer<T> generatorSetup;
	
	public StructureBase(int radX, int sizeY, int radZ){
		this.radX = radX;
		this.sizeY = sizeY;
		this.radZ = radZ;
	}
	
	public void setGeneratorSetupFunc(Consumer<T> generatorSetup){
		this.generatorSetup = generatorSetup;
	}
	
	public boolean tryGenerateInWorld(World world, Random rand, int centerX, int bottomY, int centerZ, int maxAttempts){
		for(int attempt = 0; attempt < maxAttempts; attempt++){
			if (generateInWorld(world, rand, centerX, bottomY, centerZ))return true;
		}
		
		return false;
	}
	
	protected boolean generateInWorld(World world, Random rand, int centerX, int bottomY, int centerZ){
		StructureWorld structureWorld = new StructureWorld(world, radX, sizeY, radZ);
		
		T generator = createGenerator();
		if (generatorSetup != null)generatorSetup.accept(generator);
		
		if (!generator.generate(structureWorld, rand))return false;
		structureWorld.generateInWorld(world, rand, centerX, bottomY, centerZ);
		return true;
	}
	
	protected abstract T createGenerator();
}
