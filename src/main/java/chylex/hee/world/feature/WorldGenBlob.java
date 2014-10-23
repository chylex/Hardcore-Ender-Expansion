package chylex.hee.world.feature;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenBlob extends WorldGenerator{
	@Override
	public boolean generate(World world, Random rand, int x, int y, int z){
		return false;
	}
}
