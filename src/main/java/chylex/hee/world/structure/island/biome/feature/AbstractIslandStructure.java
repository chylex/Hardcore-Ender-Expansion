/*package chylex.hee.world.structure.island.biome.feature;
import java.util.Random;
import net.minecraft.block.Block;
import chylex.hee.world.structure.island.ComponentIsland;
import chylex.hee.world.structure.island.biome.IslandBiomeBase;
import chylex.hee.world.structure.island.biome.data.IslandBiomeData;
import chylex.hee.world.structure.util.pregen.LargeStructureWorld;

public abstract class AbstractIslandStructure{
	protected LargeStructureWorld world;
	protected IslandBiomeData biomeData;
	
	public final boolean generateInWorld(LargeStructureWorld world, Random rand, IslandBiomeBase biome){
		this.world = world;
		this.biomeData = biome.getData();
		return generate(rand);
	}
	
	protected abstract boolean generate(Random rand);
	
	// helper methods
	
	protected final int getRandomXZ(Random rand, int distFromEdges){
		return rand.nextInt(ComponentIsland.size-distFromEdges*2)+distFromEdges;
	}
	
	protected final Block surface(){
		return IslandBiomeBase.getTopBlock();
	}
}
*/