package chylex.hee.world.structure.island.biome.feature.island.laboratory;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.block.BlockList;
import chylex.hee.world.structure.util.pregen.LargeStructureWorld;

public class LaboratoryElementPlacer{
	public static final byte hallStairsLength = 4;
	
	public static void generateHall(LargeStructureWorld world, Random rand, int x1, int z1, int x2, int z2, int y){
		if (x1 == x2){
			for(int z = Math.min(z1,z2), zMax = Math.max(z1,z2); z <= zMax; z++){
				for(int a = 0; a < 3; a++){
					world.setBlock(x1-1+a,y,z,BlockList.obsidian_special);
					world.setBlock(x1-1+a,y+4,z,Blocks.obsidian);
				}
				
				for(int py = 0; py < 4; py++){
					world.setBlock(x1-2,y+py,z,py == 2 && z != z1 && z != z2 ? BlockList.laboratory_glass : Blocks.obsidian);
					world.setBlock(x1+2,y+py,z,py == 2 && z != z1 && z != z2 ? BlockList.laboratory_glass : Blocks.obsidian);
				}
			}
			
			--x1;
			++x2;
		}
		else if (z1 == z2){
			for(int x = Math.min(x1,x2), xMax = Math.max(x1,x2); x <= xMax; x++){
				for(int a = 0; a < 3; a++){
					world.setBlock(x,y,z1-1+a,BlockList.obsidian_special);
					world.setBlock(x,y+4,z1-1+a,Blocks.obsidian);
				}
				
				for(int py = 0; py < 4; py++){
					world.setBlock(x,y+py,z1-2,py == 2 && x != x1 && x != x2 ? BlockList.laboratory_glass : Blocks.obsidian);
					world.setBlock(x,y+py,z1+2,py == 2 && x != x1 && x != x2 ? BlockList.laboratory_glass : Blocks.obsidian);
				}
			}
			
			--z1;
			++z1;
		}
		else throw new IllegalArgumentException("Hall coords need to be equal on one axis!");
		
		for(int x = Math.min(x1,x2), xMax = Math.max(x1,x2); x <= xMax; x++){
			for(int z = Math.min(z1,z2), zMax = Math.max(z1,z2); z <= zMax; z++){
				for(int py = -1; py > -5; py--){
					if (world.isAir(x,y+py,z) && !world.isAir(x,y+py+1,z)){
						world.setBlock(x,y+py,z,Blocks.obsidian);
					}
					else break;
				}
			}
		}
	}
	
	public static void generateHallStairs(LargeStructureWorld world, Random rand, int x, int y, int z, int xAdd, int yAdd, int zAdd){
		int x1 = x, x2 = x, z1 = z, z2 = z;
		
		if (yAdd == 1){
			x += xAdd*hallStairsLength;
			x1 = x2 = x;
			xAdd *= -1;
			z += zAdd*hallStairsLength;
			z1 = z2 = z;
			zAdd *= -1;
			yAdd = -1;
		}
		
		if (xAdd != 0){
			for(int a = 0; a <= hallStairsLength; a++){
				for(int py = 0; py < 5; py++){
					for(int b = 0; b < 2; b++){
						world.setBlock(x,y+py,z-5+b*10,(py == 3 && a < 3) || (py == 2 && a > 2) ? BlockList.laboratory_glass : Blocks.obsidian);
					}
					
					for(int b = 0; b < 3; b++){
						if (a == 0 && py == 1)world.setBlock(x,y+py,z-1+b,BlockList.obsidian_stairs,xAdd == 1 ? 0 : 1); // TODO whatever the correct metadata is
						else world.setBlock(x,y+py,z-1+b,py == 0 ? BlockList.obsidian_special : py == 4 ? Blocks.obsidian : Blocks.air);
					}
					
					if (a == 0 && py == 4)continue;
					else if (a == 1)y += yAdd;
				}
				
				x += xAdd;
			}
			
			z1 -= 2;
			z2 += 2;
			x2 += xAdd*hallStairsLength;
		}
		else if (zAdd != 0){
			for(int a = 0; a <= hallStairsLength; a++){
				for(int py = 0; py < 5; py++){
					for(int b = 0; b < 2; b++){
						world.setBlock(x-5+b*10,y+py,z,(py == 3 && a < 3) || (py == 2 && a > 2) ? BlockList.laboratory_glass : Blocks.obsidian);
					}
					
					for(int b = 0; b < 3; b++){
						if (a == 0 && py == 1)world.setBlock(x-1+b,y+py,z,BlockList.obsidian_stairs,zAdd == 1 ? 2 : 3); // TODO whatever the correct metadata is
						else world.setBlock(x-1+b,y+py,z,py == 0 ? BlockList.obsidian_special : py == 4 ? Blocks.obsidian : Blocks.air);
					}
					
					if (a == 0 && py == 4)continue;
					else if (a == 1)y += yAdd;
				}
				
				z += zAdd;
			}
			
			x1 -= 2;
			x2 += 2;
			z2 += zAdd*hallStairsLength;
		}
		
		for(int xx = Math.min(x1,x2), xMax = Math.max(x1,x2); xx <= xMax; xx++){
			for(int zz = Math.min(z1,z2), zMax = Math.max(z1,z2); zz <= zMax; zz++){
				for(int py = -1; py > -5; py--){
					if (world.isAir(xx,y+py,zz) && !world.isAir(xx,y+py+1,zz)){
						world.setBlock(xx,y+py,zz,Blocks.obsidian);
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
				world.setBlock(x-3+px,y,z-2+a,BlockList.obsidian_special);
				world.setBlock(x-3+px,y+4,z-2+a,Blocks.obsidian);
			}
			
			for(int pz = 0; pz < 2; pz++){
				world.setBlock(x-2+a,y,z-3+pz*6,BlockList.obsidian_special);
				world.setBlock(x-2+a,y+4,z-3+pz*6,Blocks.obsidian);
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
					world.setBlock(x-3+b*6,y+py,z-2+a,py == 2 && a >= 1 && a <= 3 ? BlockList.laboratory_glass : Blocks.obsidian);
					world.setBlock(x-2+a,y+py,z-3+b*6,py == 2 && a >= 1 && a <= 3 ? BlockList.laboratory_glass : Blocks.obsidian);
				}
			}
		}
		
		// fill in the bottom with obsidian and inside with air
		
		for(int px = 0; px < 7; px++){
			for(int pz = 0; pz < 7; pz++){
				for(int py = -1; py > -5; py--){
					if (world.isAir(x-4+px,y+py,z-4+pz) && !world.isAir(x-4+px,y+py+1,z-4+pz)){
						world.setBlock(x-4+px,y+py,z-4+pz,Blocks.obsidian);
					}
					else break;
				}
				
				if (px > 0 && px < 6 && pz > 0 && pz < 6){
					for(int py = 1; py < 4; py++){
						if (world.getBlock(x-3+px,y+py,z-3+pz) != Blocks.obsidian){
							world.setBlock(x-3+px,y+py,z-3+pz,Blocks.air);
						}
					}
				}
			}
		}
	}
	
	public static void generateLargeRoom(LargeStructureWorld world, Random rand, int x, int y, int z){
		// floor and ceiling
		
		for(int px = 0; px < 8; px++){
			for(int pz = 0; pz < 8; pz++){
				world.setBlock(x-3+pz,y,z-3+px,BlockList.obsidian_special);
				world.setBlock(x-3+pz,y+4,z-3+px,Blocks.obsidian);
			}
		}
		
		for(int a = 0; a < 5; a++){
			for(int b = 0; b < 2; b++){
				world.setBlock(x-4+8*b,y,z-2+a,BlockList.obsidian_special);
				world.setBlock(x-4+8*b,y+4,z-2+a,Blocks.obsidian);
				world.setBlock(x-2+a,y,z-4+8*b,BlockList.obsidian_special);
				world.setBlock(x-2+a,y+4,z-4+8*b,Blocks.obsidian);
			}
		}
		
		// walls
		
		for(int py = 0; py < 4; py++){
			for(int a = 0; a < 5; a++){
				for(int b = 0; b < 2; b++){
					world.setBlock(x-5+10*b,y+py,z-2+a,py == 2 && a >= 1 && a <= 3 ? BlockList.laboratory_glass : Blocks.obsidian);
					world.setBlock(x-2+a,y+py,z-5+10*b,py == 2 && a >= 1 && a <= 3 ? BlockList.laboratory_glass : Blocks.obsidian);
				}
			}
			
			for(int a = 0; a < 2; a++){
				for(int b = 0; b < 2; b++){
					world.setBlock(x-4+8*a,y+py,z-3+6*b,Blocks.obsidian);
					world.setBlock(x-3+6*a,y+py,z-4+8*b,Blocks.obsidian);
				}
			}
		}
		
		// fill in the bottom with obsidian and inside with air
		
		for(int px = 0; px < 10; px++){
			for(int pz = 0; pz < 10; pz++){
				for(int py = -1; py > -5; py--){
					if (world.isAir(x-5+px,y+py,z-5+pz) && !world.isAir(x-5+px,y+py+1,z-5+pz)){
						world.setBlock(x-5+px,y+py,z-5+pz,Blocks.obsidian);
					}
					else break;
				}
				
				if (px > 0 && px < 9 && pz > 0 && pz < 9){
					for(int py = 1; py < 4; py++){
						if (world.getBlock(x-4+px,y+py,z-4+pz) != Blocks.obsidian){
							world.setBlock(x-4+px,y+py,z-4+pz,Blocks.air);
						}
					}
				}
			}
		}
	}
}
