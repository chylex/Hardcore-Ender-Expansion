package chylex.hee.world.structure.island.biome.feature.island.laboratory;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.block.BlockList;
import chylex.hee.world.structure.util.pregen.LargeStructureWorld;

public class LaboratoryPlacer{
	public void generateSmallRoom(LargeStructureWorld world, Random rand, int x, int y, int z){
		// floor and ceiling
		
		for(int a = 0; a < 5; a++){
			for(int px = 0; px < 7; px++){
				world.setBlock(x-3+px,y,z-2+a,BlockList.obsidian_special);
				world.setBlock(x-3+px,y+4,z-2+a,Blocks.obsidian);
			}
			
			for(int pz = 0; pz < 2; pz++){
				world.setBlock(x-2+a,y,z-3+pz*2,BlockList.obsidian_special);
				world.setBlock(x-2+a,y+4,z-3+pz*2,Blocks.obsidian);
			}
		}
		
		// walls
		
		for(int py = 0; py < 4; py++){
			for(int px = 0; px < 2; px++){
				for(int pz = 0; pz < 2; pz++){
					world.setBlock(x-3+px*6,y+py,z-3+pz*6,Blocks.obsidian);
				}
			}
			
			for(int a = 0; a < 5; a++){
				for(int b = 0; b < 2; b++){
					world.setBlock(x-3+b*6,y+py,z-2+a,py == 1 && a >= 1 && a <= 3 ? BlockList.laboratory_glass : Blocks.obsidian);
					world.setBlock(x-2+a,y+py,z-3+b*6,py == 1 && a >= 1 && a <= 3 ? BlockList.laboratory_glass : Blocks.obsidian);
				}
			}
		}
		
		// fill in the bottom
		
		for(int px = 0; px < 7; px++){
			for(int pz = 0; pz < 7; pz++){
				for(int py = -1; py > -5; py--){
					if (world.isAir(x-3+px,y+py,z-3+pz) && !world.isAir(x-3+px,y+py+1,z-3+pz)){
						world.setBlock(x-3+px,y+py,z-3+pz,Blocks.obsidian);
					}
					else break;
				}
			}
		}
		
		// TODO fill the inside with air
	}
	
	public void generateLargeRoom(LargeStructureWorld world, Random rand, int x, int y, int z){
		
	}
}
