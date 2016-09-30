package chylex.hee.world.feature;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import chylex.hee.init.BlockList;
import chylex.hee.mechanics.energy.EnergyClusterGenerator;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.tileentity.TileEntityEnergyCluster;
import cpw.mods.fml.common.IWorldGenerator;

public class WorldGenDispersedCluster implements IWorldGenerator{
	@Override
	public void generate(Random rand, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider){
		if (world.provider.dimensionId == 0 && chunkX%2 == 0 && chunkZ%2 == 0 && rand.nextInt(40) == 0){
			int blockX = chunkX*16, blockZ = chunkZ*16;
			int height = world.getActualHeight()-8;
			
			while(height >= 60){
				Pos pos = Pos.at(blockX+8+rand.nextInt(16), 4+rand.nextInt(height), blockZ+8+rand.nextInt(16));
				
				if (pos.isAir(world)){
					PosMutable testPos = new PosMutable();
					
					for(int surroundingsCheck = 0; surroundingsCheck < 12; surroundingsCheck++){
						if (!testPos.set(pos).move(rand.nextInt(7)-3, rand.nextInt(7)-3, rand.nextInt(7)-3).isAir(world)){
							testPos.setBlock(world, BlockList.energy_cluster);
							testPos.<TileEntityEnergyCluster>tryGetTileEntity(world).ifPresent(tile -> tile.generate(EnergyClusterGenerator.overworld, rand));
							return;
						}
					}
				}
				
				height -= 4;
			}
		}
	}
}
