package chylex.hee.world.feature;
import java.util.Random;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import chylex.hee.system.util.BlockPosM;

public abstract class WorldGenBase extends WorldGenerator{
	public abstract boolean generate(World world, Random rand, BlockPosM pos);
	
	@Override
	public boolean generate(World world, Random rand, BlockPos pos){
		return generate(world,rand,new BlockPosM(pos));
	}
}
