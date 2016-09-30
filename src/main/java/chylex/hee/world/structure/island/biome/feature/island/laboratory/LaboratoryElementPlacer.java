/*package chylex.hee.world.structure.island.biome.feature.island.laboratory;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import chylex.hee.init.BlockList;
import chylex.hee.world.structure.util.pregen.LargeStructureWorld;

public class LaboratoryElementPlacer{
	public static final Block blockLab = BlockList.laboratory_obsidian;
	public static final Block blockFloor = BlockList.laboratory_floor;
	
	public static void generateRoomEntrance(LargeStructureWorld world, Random rand, int x, int y, int z, boolean isX){
		int x1 = x, x2 = x, z1 = z, z2 = z;
		
		if (isX){
			--z1;
			++z2;
		}
		else{
			--x1;
			++x2;
		}
		
		for(x = Math.min(x1, x2); x <= Math.max(x1, x2); x++){
			for(z = Math.min(z1, z2); z <= Math.max(z1, z2); z++){
				for(int py = 0; py < 5; py++){
					world.setBlock(x, y+py, z, py == 0 ? blockFloor : py == 4 ? blockLab : Blocks.air);
				}
			}
		}
	}
	
	public static void generateHall(LargeStructureWorld world, Random rand, int x1, int z1, int x2, int z2, int y, boolean isX){
		if (isX){
			for(int xMin = Math.min(x1, x2), xMax = Math.max(x1, x2), x = xMin; x <= xMax; x++){
				for(int a = 0; a < 3; a++){
					world.setBlock(x, y, z1-1+a, blockFloor);
					world.setBlock(x, y+4, z1-1+a, blockLab);
				}
				
				for(int py = 0; py < 4; py++){
					world.setBlock(x, y+py, z1-2, py == 2 ? BlockList.laboratory_glass : blockLab);
					world.setBlock(x, y+py, z1+2, py == 2 ? BlockList.laboratory_glass : blockLab);
				}
			}
			
			--z1;
			++z2;
		}
		else{
			for(int zMin = Math.min(z1, z2), zMax = Math.max(z1, z2), z = zMin; z <= zMax; z++){
				for(int a = 0; a < 3; a++){
					world.setBlock(x1-1+a, y, z, blockFloor);
					world.setBlock(x1-1+a, y+4, z, blockLab);
				}
				
				for(int py = 0; py < 4; py++){
					world.setBlock(x1-2, y+py, z, py == 2 ? BlockList.laboratory_glass : blockLab);
					world.setBlock(x1+2, y+py, z, py == 2 ? BlockList.laboratory_glass : blockLab);
				}
			}
			
			--x1;
			++x2;
		}
		
		// fill in the bottom with obsidian and inside with air
		
		for(int x = Math.min(x1, x2), xMax = Math.max(x1, x2); x <= xMax; x++){
			for(int z = Math.min(z1, z2), zMax = Math.max(z1, z2); z <= zMax; z++){
				for(int py = 1; py < 4; py++)world.setBlock(x, y+py, z, Blocks.air);
			}
		}
		
		if (isX){
			--z1;
			++z2;
		}
		else{
			--x1;
			++x2;
		}
		
		for(int x = Math.min(x1, x2), xMax = Math.max(x1, x2); x <= xMax; x++){
			for(int z = Math.min(z1, z2), zMax = Math.max(z1, z2); z <= zMax; z++){
				for(int py = -1; py > -5; py--){
					if (world.isAir(x, y+py, z) && isValidFloor(world.getBlock(x, y+py+1, z))){
						world.setBlock(x, y+py, z, blockLab);
					}
					else break;
				}
			}
		}
	}
	
	public static void generateHallStairs(LargeStructureWorld world, Random rand, int x, int y, int z, int xAdd, int yAdd, int zAdd){
		int x1 = x, x2 = x, z1 = z, z2 = z;
		
		if (yAdd == -1)--y;
		else{
			xAdd *= -1;
			zAdd *= -1;
		}
		
		if (xAdd != 0){
			for(int py = 0; py <= 5; py++){
				if (py < 5){
					for(int b = 0; b < 2; b++){
						world.setBlock(x, y+py, z-2+b*4, blockLab);
					}
				}
				
				for(int b = 0; b < 3; b++){
					if (py == 1)world.setBlock(x, y+py, z-1+b, BlockList.laboratory_stairs, xAdd == 1 ? 1 : 0);
					else world.setBlock(x, y+py, z-1+b, py == 0 ? blockFloor : py == 5 ? blockLab : Blocks.air);
				}
			}
			
			int space = 0;
			
			for(; space < 3; space++){
				if (world.isAir(x+(space+1)*xAdd, y+2, z-2))break;
			}
			
			if (space == 1 || space == 2){
				for(int side = 0; side < 2; side++)world.setBlock(x+xAdd, y+2, z-2+side*4, blockLab);
			}
			else if (space == 3){
				for(int side = 0; side < 2; side++){
					world.setBlock(x+xAdd, y+4, z-2+side*4, blockLab);
					world.setBlock(x, y+3, z-2+side*4, BlockList.laboratory_glass);
					world.setBlock(x+xAdd, y+3, z-2+side*4, BlockList.laboratory_glass);
				}
			}
			
			for(int b = 0; b <= Math.min(space, 2); b++){
				for(int side = 0; side < 2; side++)world.setBlock(x+xAdd*b, y+4, z-2+side*4, blockLab);
				
				for(int a = 0; a < 3; a++){
					world.setBlock(x+xAdd*b, y+4, z-1+a, Blocks.air);
					world.setBlock(x+xAdd*b, y+5, z-1+a, blockLab);
				}
			}

			z1 -= 2;
			z2 += 2;
		}
		else if (zAdd != 0){
			for(int py = 0; py <= 5; py++){
				if (py < 5){
					for(int b = 0; b < 2; b++){
						world.setBlock(x-2+b*4, y+py, z, blockLab);
					}
				}
				
				for(int b = 0; b < 3; b++){
					if (py == 1)world.setBlock(x-1+b, y+py, z, BlockList.laboratory_stairs, zAdd == 1 ? 3 : 2);
					else world.setBlock(x-1+b, y+py, z, py == 0 ? blockFloor : py == 5 ? blockLab : Blocks.air);
				}
			}
			
			int space = 0;
			
			for(; space < 3; space++){
				if (world.isAir(x-2, y+2, z+(space+1)*zAdd))break;
			}
			
			if (space == 1 || space == 2){
				for(int side = 0; side < 2; side++)world.setBlock(x-2+side*4, y+2, z+zAdd, blockLab);
			}
			else if (space == 3){
				for(int side = 0; side < 2; side++){
					world.setBlock(x-2+side*4, y+4, z+zAdd, blockLab);
					world.setBlock(x-2+side*4, y+3, z, BlockList.laboratory_glass);
					world.setBlock(x-2+side*4, y+3, z+zAdd, BlockList.laboratory_glass);
				}
			}
			
			for(int b = 0; b <= Math.min(space, 2); b++){
				for(int side = 0; side < 2; side++)world.setBlock(x-2+side*4, y+4, z+zAdd*b, blockLab);
				
				for(int a = 0; a < 3; a++){
					world.setBlock(x-1+a, y+4, z+zAdd*b, Blocks.air);
					world.setBlock(x-1+a, y+5, z+zAdd*b, blockLab);
				}
			}
			
			x1 -= 2;
			x2 += 2;
		}
		
		for(int xx = Math.min(x1, x2), xMax = Math.max(x1, x2); xx <= xMax; xx++){
			for(int zz = Math.min(z1, z2), zMax = Math.max(z1, z2); zz <= zMax; zz++){
				for(int py = -1; py > -5; py--){
					if (world.isAir(xx, y+py, zz) && isValidFloor(world.getBlock(xx, y+py+1, zz))){
						world.setBlock(xx, y+py, zz, blockLab);
					}
					else break;
				}
			}
		}
	}
	
	public static void generateSmallRoom(LargeStructureWorld world, Random rand, int x, int y, int z){
		// floor and ceiling
		
		for(int a = 0; a < 5; a++){
			for(int px = 0; px < 7; px++){
				world.setBlock(x-3+px, y, z-2+a, blockFloor);
				world.setBlock(x-3+px, y+4, z-2+a, blockLab);
			}
			
			for(int pz = 0; pz < 2; pz++){
				world.setBlock(x-2+a, y, z-3+pz*6, blockFloor);
				world.setBlock(x-2+a, y+4, z-3+pz*6, blockLab);
			}
		}
		
		// walls
		
		for(int py = 0; py < 4; py++){
			for(int px = 0; px < 2; px++){
				for(int pz = 0; pz < 2; pz++){
					world.setBlock(x-3+px*6, y+py, z-3+pz*6, blockLab);
				}
			}
			
			for(int a = 0; a < 5; a++){
				for(int b = 0; b < 2; b++){
					world.setBlock(x-4+b*8, y+py, z-2+a, py == 2 && a >= 1 && a <= 3 ? BlockList.laboratory_glass : blockLab);
					world.setBlock(x-2+a, y+py, z-4+b*8, py == 2 && a >= 1 && a <= 3 ? BlockList.laboratory_glass : blockLab);
				}
			}
		}
		
		// fill in the bottom with obsidian and inside with air
		
		for(int px = 0; px < 9; px++){
			for(int pz = 0; pz < 9; pz++){
				for(int py = -1; py > -5; py--){
					if (world.isAir(x-4+px, y+py, z-4+pz) && isValidFloor(world.getBlock(x-4+px, y+py+1, z-4+pz))){
						world.setBlock(x-4+px, y+py, z-4+pz, blockLab);
					}
					else break;
				}
			}
		}
		
		for(int py = 1; py < 4; py++){
			for(int px = 0; px < 7; px++){
				for(int pz = 0; pz < 7; pz++){
					if (world.getBlock(x-3+px, y+py, z-3+pz) != blockLab)world.setBlock(x-3+px, y+py, z-3+pz, Blocks.air);
				}
			}
		}
	}
	
	public static void generateLargeRoom(LargeStructureWorld world, Random rand, int x, int y, int z){
		// floor and ceiling
		
		for(int px = 0; px < 7; px++){
			for(int pz = 0; pz < 7; pz++){
				world.setBlock(x-3+pz, y, z-3+px, blockFloor);
				world.setBlock(x-3+pz, y+4, z-3+px, blockLab);
			}
		}
		
		for(int a = 0; a < 5; a++){
			for(int b = 0; b < 2; b++){
				world.setBlock(x-4+8*b, y, z-2+a, blockFloor);
				world.setBlock(x-4+8*b, y+4, z-2+a, blockLab);
				world.setBlock(x-2+a, y, z-4+8*b, blockFloor);
				world.setBlock(x-2+a, y+4, z-4+8*b, blockLab);
			}
		}
		
		// walls
		
		for(int py = 0; py < 4; py++){
			for(int a = 0; a < 5; a++){
				for(int b = 0; b < 2; b++){
					world.setBlock(x-5+10*b, y+py, z-2+a, py == 2 && a >= 1 && a <= 3 ? BlockList.laboratory_glass : blockLab);
					world.setBlock(x-2+a, y+py, z-5+10*b, py == 2 && a >= 1 && a <= 3 ? BlockList.laboratory_glass : blockLab);
				}
			}
			
			for(int a = 0; a < 2; a++){
				for(int b = 0; b < 2; b++){
					world.setBlock(x-4+8*a, y+py, z-3+6*b, blockLab);
					world.setBlock(x-3+6*a, y+py, z-4+8*b, blockLab);
				}
			}
		}
		
		// fill in the bottom with obsidian and inside with air
		
		for(int px = 0; px < 11; px++){
			for(int pz = 0; pz < 11; pz++){
				for(int py = -1; py > -5; py--){
					if (world.isAir(x-5+px, y+py, z-5+pz) && isValidFloor(world.getBlock(x-5+px, y+py+1, z-5+pz))){
						world.setBlock(x-5+px, y+py, z-5+pz, blockLab);
					}
					else break;
				}
			}
		}
		
		for(int py = 1; py < 4; py++){
			for(int px = 0; px < 7; px++){
				for(int pz = 0; pz < 7; pz++){
					if (world.getBlock(x-3+px, y+py, z-3+pz) != blockLab)world.setBlock(x-3+px, y+py, z-3+pz, Blocks.air);
				}
			}
			
			for(int a = 0; a < 5; a++){
				for(int b = 0; b < 2; b++){
					if (world.getBlock(x-2+a, y+py, z-4+b*8) != blockLab)world.setBlock(x-2+a, y+py, z-4+b*8, Blocks.air);
					if (world.getBlock(x-4+b*8, y+py, z-2+a) != blockLab)world.setBlock(x-4+b*8, y+py, z-2+a, Blocks.air);
				}
			}
		}
	}
	
	private static boolean isValidFloor(Block block){
		return block == blockLab || block == blockFloor;
	}
}
*/