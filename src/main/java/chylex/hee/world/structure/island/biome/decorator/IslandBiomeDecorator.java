/*package chylex.hee.world.structure.island.biome.decorator;
import java.util.Random;
import net.minecraft.block.Block;
import chylex.hee.world.structure.island.ComponentIsland;
import chylex.hee.world.structure.island.biome.IslandBiomeBase;
import chylex.hee.world.structure.island.biome.data.IslandBiomeData;
import chylex.hee.world.structure.island.biome.feature.AbstractIslandStructure;
import chylex.hee.world.structure.util.pregen.LargeStructureWorld;

public abstract class IslandBiomeDecorator{
	protected static final Block topBlock = IslandBiomeBase.getTopBlock();
	
	protected LargeStructureWorld world;
	protected Random rand;
	protected IslandBiomeData data;
	protected int centerX, centerZ;
	
	public void begin(LargeStructureWorld world, Random rand, int centerX, int centerZ, IslandBiomeData data){
		this.world = world;
		this.rand = rand;
		this.centerX = centerX;
		this.centerZ = centerZ;
		this.data = data;
	}
	
	public void end(){
		this.world = null;
	}
	
	protected final boolean generateStructure(AbstractIslandStructure structure){
		return structure.generateInWorld(world, rand, getBiome());
	}
	
	protected final int getRandomXZ(Random rand, int distFromEdges){
		return rand.nextInt(ComponentIsland.size-distFromEdges*2)+distFromEdges;
	}
	
	protected abstract IslandBiomeBase getBiome();
}
*/