package chylex.hee.world.structure.island.feature;
/*import java.util.EnumSet;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.block.BlockEndstoneTerrain;
import chylex.hee.block.BlockList;
import chylex.hee.entity.item.EntityItemEndermanRelic;
import chylex.hee.item.ItemEndermanRelicShattered;
import chylex.hee.world.structure.island.ComponentScatteredFeatureIsland;

public class StructureIslandRelic extends AbstractStructure{
	@Override
	protected boolean generate(Random rand, int centerX, int centerZ){
		int sz = (int)Math.floor(ComponentScatteredFeatureIsland.islandSize*0.3D),halfsz = sz>>1;
		x = x+rand.nextInt(sz)-halfsz;
		z = z+rand.nextInt(sz)-halfsz;
		
		int y = getHighestY(x,z),hy;
		if (getBlock(x,y-1,z) != BlockList.end_terrain)return false;
		
		boolean canGenerate = true;
		for(int testX = x-5; testX <= x+5; testX++){
			for(int testZ = z-5; testZ <= z+5; testZ++){
				if (Math.abs((hy = getHighestY(testX,testZ))-y) > 1)return false;
				if (hy > y)y = hy;
			}
		}
		
		// Clean area, setup floor
		
		for(int xx = x-5,a,b; xx <= x+5; xx++){
			for(int zz = z-5; zz <= z+5; zz++){
				a = (xx == x-5 || xx == x+5?5:xx == x-4 || xx == x+4?4:0);
				b = (zz == z-5 || zz == z+5?5:zz == z-4 || zz == z+4?4:0);
				
				if ((a == 5 && b == 4) || (a == 4 && b == 5) || (a == 5 && b == 5))continue;
				
				for(int yy = y; yy < y+2; yy++)placeBlock(null,0,xx,yy,zz);
				placeBlock(BlockList.end_terrain,BlockEndstoneTerrain.metaEnchanted,xx,y-1,zz);
			}
		}
		
		// Walls and bookshelves
		
		for(int a = 0; a < 2; a++){
			for(int b = 0; b < 2; b++){
				for(int yy = 0; yy < 5; yy++){					
					placeBlock(yy == 4?Blocks.obsidian:BlockList.obsidian_special,yy == 3?1:0,x-4+8*a,y+yy,z-4+8*b);
				}
			}
			
			for(int b = 0; b < 7; b++){
				for(int yy = 0; yy < 5; yy++){
					placeBlock(yy == 4?Blocks.obsidian:BlockList.obsidian_special,yy == 3?1:(b == 2 || b == 4) && yy < 3?2:0,x-3+b,y+yy,z-5+10*a);
					placeBlock(yy == 4?Blocks.obsidian:BlockList.obsidian_special,yy == 3?1:(b == 2 || b == 4) && yy < 3?2:0,x-5+10*a,y+yy,z-3+b);
				}
			}
			
			for(int b = 0,n; b < 7; b++){
				n = Math.min(b == 0 || b == 6?5:4,(int)Math.floor(Math.abs((b == 0 || b == 6?1.8D:1D)*rand.nextGaussian()+rand.nextGaussian()*1.2D+rand.nextFloat()*0.3D)));
				for(int yy = 0; yy < n; yy++){
					placeBlock(Blocks.bookshelf,0,x-4+8*a,y+yy,z-3+b);
				}
				
				n = Math.min(b == 0 || b == 6?5:4,(int)Math.floor(Math.abs((b == 0 || b == 6?1.8D:1D)*rand.nextGaussian()+rand.nextGaussian()*1.2D+rand.nextFloat()*0.3D)));
				for(int yy = 0; yy < n; yy++){
					placeBlock(Blocks.bookshelf,0,x-3+b,y+yy,z-4+8*a);
				}
			}
		}
		
		// Doors
		
		int add = rand.nextBoolean()?-5:5;
		byte[] mp = rand.nextBoolean()?new byte[]{ 0,1 }:new byte[]{ 1,0 };		
		
		for(int yy = 0; yy < 3; yy++){
			for(int a = -1; a <= 1; a++){
				placeBlock(null,0,x+mp[0]*add+a*mp[1],y+yy,z+mp[1]*add+a*mp[0]);
			}
			
			for(int a = 0; a < 2; a++){
				placeBlock(BlockList.obsidian_special,yy == 1?1:2,x+mp[0]*add+(a == 0?-2:2)*mp[1],y+yy,z+mp[1]*add+(a == 0?-2:2)*mp[0]);
			}
		}
		
		add = add == -5?-4:4;
		for(int yy = 0; yy < 5; yy++){
			for(int a = -1; a <= 1; a++){
				placeBlock(null,0,x+mp[0]*add+a*mp[1],y+yy,z+mp[1]*add+a*mp[0]);
			}
		}
		
		// Ceiling and stairs
		
		for(int a = 0; a < 2; a++){
			for(int b = 0; b < 3; b++){
				placeBlock(BlockList.obsidian_stairs,4+(a == 0?1:0),x-4+8*a,y+4,z-1+b);
				placeBlock(BlockList.obsidian_stairs,4+(a == 0?3:2),x-1+b,y+4,z-4+8*a);
			}
			
			for(int b = 0,metasame,metadiff; b < 2; b++){
				metasame = 4+(a == 0?1:0);
				metadiff = 4+(b == 0?3:2);
				
				placeBlock(BlockList.obsidian_stairs,metasame,x-4+8*a,y+4,z-2+4*b);
				placeBlock(BlockList.obsidian_stairs,metasame,x-2+4*a,y+4,z-4+8*b);
				placeBlock(BlockList.obsidian_stairs,metasame,x-3+6*a,y+4,z-3+6*b);
				
				placeBlock(BlockList.obsidian_stairs,metadiff,x-2+4*a,y+4,z-3+6*b);
				placeBlock(BlockList.obsidian_stairs,metadiff,x-3+6*a,y+4,z-2+4*b);
				
				placeBlock(Blocks.obsidian,0,x-4+8*a,y+4,z-3+6*b);
				placeBlock(Blocks.obsidian,0,x-3+6*a,y+4,z-4+8*b);
			}
		}
		
		// Center
		
		placeBlock(BlockList.obsidian_special_glow,1,x,y,z);
		getWorld().spawnEntityInWorld(new EntityItemEndermanRelic(getWorld(),x+0.5D,y+1,z+0.5D,ItemEndermanRelicShattered.metaShattered1));
		
		return true;
	}
	
	@Override
	protected EnumSet<GenerationType> getGenerationType(){
		return EnumSet.of(GenerationType.WORLD);
	}
}
*/