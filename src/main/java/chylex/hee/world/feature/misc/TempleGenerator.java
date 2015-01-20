package chylex.hee.world.feature.misc;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import chylex.hee.block.BlockList;

public class TempleGenerator{
	private final World world;
	
	public TempleGenerator(World world){
		this.world = world;
	}

	public void preloadAndClearArea(int x, int y, int z){
		world.getChunkFromBlockCoords(x,z);
		world.getChunkFromBlockCoords(x+19,z);
		
		for(int xx = x; xx <= x+19; xx++){
			for(int zz = z; zz <= z+13; zz++){
				for(int yy = y+7; yy >= y; yy--)world.setBlockToAir(xx,yy,zz);
			}
		}
	}
	
	public void spawnTemple(int x, int y, int z){
		preloadAndClearArea(x,y,z);
		
		// LAYER 1
		rect(x+1,z,x+18,z+12,y,Blocks.quartz_block);
		block(x,z+6,y,Blocks.quartz_block);
		block(x,z+5,y,Blocks.quartz_stairs,6);
		block(x,z+7,y,Blocks.quartz_stairs,7);
		
		block(x+3,z+6,y,Blocks.glowstone);
		rect(x+11,z+5,x+13,z+7,y,Blocks.glowstone);
		for(int a = 0; a < 7; a++){
			block(x+4+2*a,z+2,y,Blocks.glowstone);
			block(x+4+2*a,z+10,y,Blocks.glowstone);
		}
		for(int a = 0; a < 3; a++)block(x+16,z+4+2*a,y,Blocks.glowstone);
		++y;
		
		// LAYER 2
		rect(x+11,z+5,x+13,z+7,y,Blocks.quartz_block);
		rect(x+6,z+5,x+10,z+7,y,Blocks.quartz_block);
		linez(z+5,z+7,x+5,y,Blocks.stone_slab,7);
		for(int a = 0; a < 2; a++)linex(x+11,x+13,z+4+a*4,y,Blocks.quartz_block,1);
		linez(z+5,z+7,x+14,y,Blocks.quartz_block,1);
		for(int a = 0; a < 2; a++){
			for(int b = 0; b < 2; b++)block(x+10+4*b,z+4*(a+1),y,Blocks.quartz_block,2);
		}
		
		for(int a = 0; a < 2; a++){
			rect(x+1,z+8*a,x+2,z+4+8*a,y,Blocks.quartz_block);
			block(x+1,z+4*(a+1),y,Blocks.quartz_block,1);
			linex(x+3,x+6,z+3+6*a,y,Blocks.quartz_block);
			block(x+6,z+2+8*a,y,Blocks.quartz_block);
			rect(x+17,z+11*a,x+18,z+1+11*a,y,Blocks.quartz_block);
			for(int b = 0; b < 13; b++)linez(z+11*a,z+1+11*a,x+4+b,y,Blocks.quartz_block,b%2 == 0 ? 2 : 0);
			linez(z+11*a,z+1+11*a,x+3,y,Blocks.quartz_block);
			linex(x+3,x+5,z+2+8*a,y,Blocks.water);
		}
		for(int a = 0; a < 9; a++)linex(x+17,x+18,z+2+a,y,Blocks.quartz_block,a%2 == 1 ? 0 : 2);
		
		for(int a = 0; a < 2; a++)block(x,z+5+2*a,y,Blocks.quartz_block);
		block(x,z+6,y,Blocks.quartz_block,2);
		block(x,z+4,y,Blocks.quartz_stairs,6);
		block(x,z+8,y,Blocks.quartz_stairs,7);
		
		rect(x+1,z+5,x+4,z+7,y,Blocks.carpet,14);
		linex(x+7,x+15,z+2,y,Blocks.carpet,14);
		linex(x+7,x+15,z+10,y,Blocks.carpet,14);
		linez(z+2,z+10,x+16,y,Blocks.carpet,14);
		++y;
		
		// LAYER 3
		rect(x+8,z+5,x+10,z+7,y,Blocks.quartz_block);
		block(x+9,z+6,y,Blocks.coal_block);
		linez(z+5,z+7,x+7,y,Blocks.stone_slab,7);
		for(int a = 0; a < 2; a++)linex(x+11,x+13,z+4+a*4,y,Blocks.quartz_block);
		linez(z+5,z+7,x+14,y,Blocks.quartz_block);
		rect(x+11,z+5,x+13,z+7,y,BlockList.temple_end_portal);
		
		for(int a = 0; a < 2; a++){
			for(int b = 0; b < 2; b++)block(x+10+4*b,z+4*(a+1),y,Blocks.quartz_block,2);
		}
		
		for(int a = 0; a < 2; a++){
			rect(x+1,z+8*a,x+2,z+4+8*a,y,Blocks.quartz_block);
			rect(x+17,z+11*a,x+18,z+1+11*a,y,Blocks.quartz_block);
			for(int b = 0; b < 2; b++)block(x+2,z+2+6*a+2*b,y,Blocks.quartz_block,1);
			for(int b = 0; b < 13; b++)linez(z+11*a,z+1+11*a,x+4+b,y,Blocks.quartz_block,b%2 == 0?2:0);
			linez(z+11*a,z+1+11*a,x+3,y,Blocks.quartz_block);
		}
		for(int a = 0; a < 9; a++)linex(x+17,x+18,z+2+a,y,Blocks.quartz_block,a%2 == 1?0:2);
		
		for(int a = 0; a < 5; a++)block(x,z+4+a,y,Blocks.quartz_block,a == 2?1:(a == 1 || a == 3?4:0));
		++y;
		
		// LAYER 4
		for(int a = 0; a < 2; a++){
			for(int b = 0; b < 2; b++)block(x+10+4*a,z+4+4*b,y,Blocks.stone_slab,7);
		}
		
		for(int a = 0; a < 2; a++)block(x,z+5+2*a,y,Blocks.quartz_block);
		block(x,z+6,y,Blocks.quartz_block,2);
		block(x,z+4,y,Blocks.quartz_stairs,2);
		block(x,z+8,y,Blocks.quartz_stairs,3);
		
		// LAYER 4 + 5
		for(int i = 0; i < 2; i++){
			for(int a = 0; a < 2; a++){
				rect(x+1,z+8*a,x+2,z+4+8*a,y,Blocks.quartz_block);
				rect(x+17,z+11*a,x+18,z+1+11*a,y,Blocks.quartz_block);
				for(int b = 0; b < 2; b++){
					if (i == 0){
						block(x+2,z+3+6*a,y,Blocks.quartz_block,1);
						block(x+1,z+4+4*a,y,Blocks.quartz_block,1);
					}
					else block(x+2,z+2+6*a+2*b,y,Blocks.quartz_block,1);
				}
				for(int b = 0; b < 13; b++){
					if (b%2 == 0)linez(z+11*a,z+1+11*a,x+4+b,y,Blocks.quartz_block,2);
					else{
						block(x+4+b,z+11*a,y,a == 0?Blocks.glowstone:Blocks.iron_bars);
						block(x+4+b,z+1+11*a,y,a == 1?Blocks.glowstone:Blocks.iron_bars);
					}
				}
				linez(z+11*a,z+1+11*a,x+3,y,Blocks.quartz_block);
			}
			for(int a = 0; a < 9; a++){
				if (a%2 == 0)linex(x+17,x+18,z+2+a,y,Blocks.quartz_block,2);
				else{
					block(x+17,z+2+a,y,Blocks.iron_bars);
					block(x+18,z+2+a,y,Blocks.glowstone);
				}
			}
			if (i == 0)++y;
		}
		
		// LAYER 5
		block(x,z+6,y,Blocks.quartz_block,0);
		block(x,z+5,y,Blocks.quartz_stairs,2);
		block(x,z+7,y,Blocks.quartz_stairs,3);

		for(int a = 0; a < 9; a++)block(x+2,z+2+a,y,Blocks.quartz_block,a%2 == 0?1:0);
		for(int a = 0; a < 3; a++)block(x+1,z+5+a,y,Blocks.quartz_block,a%2 == 0?1:0);
		++y;
		
		// LAYER 6
		for(int a = 0; a < 2; a++){
			linex(x+1,x+18,z+12*a,y,Blocks.quartz_block);
			linez(z+1,z+11,x+1+17*a,y,Blocks.quartz_block);
			for(int b = 0; b < 13; b++)block(x+4+b,z+1+10*a,y,Blocks.quartz_block,b%2 == 0?2:0);
			linez(z+11*a,z+1+11*a,x+3,y,Blocks.quartz_block);
			linex(x+3,x+16,z+12*a,y,Blocks.quartz_block);
		}
		for(int a = 0; a < 9; a++){
			block(x+17,z+2+a,y,Blocks.quartz_block,a%2 == 1?0:2);
			block(x+2,z+2+a,y,Blocks.quartz_block,a%2 == 1?1:0);
		}
		linez(z+2,z+10,x+18,y,Blocks.quartz_block);
		++y;
		
		// LAYER 7 + 8 (ROOF)
		for(int a = 0; a < 2; a++){
			linez(z+2,z+10,x+2+15*a,y,Blocks.quartz_stairs,0+a);
			linex(x+2,x+17,z+1+10*a,y,Blocks.quartz_stairs,2+a);
			linez(z+3,z+9,x+3+13*a,y,Blocks.quartz_block);
			linex(x+3,x+16,z+2+8*a,y,Blocks.quartz_block);
			linez(z+4,z+8,x+4+11*a,y,Blocks.quartz_stairs,5-a);
			linex(x+4,x+15,z+3+6*a,y,Blocks.quartz_stairs,7-a);
		}
		++y;
		for(int a = 0; a < 2; a++){
			linez(z+3,z+9,x+3+13*a,y,Blocks.quartz_stairs,0+a);
			linex(x+3,x+16,z+2+8*a,y,Blocks.quartz_stairs,2+a);
		}
		rect(x+4,z+3,x+15,z+9,y,Blocks.quartz_block);
		world.setBlockToAir(x+9,y,z+6);
	}
	
	private void block(int x, int z, int y, Block block){
		block(x,z,y,block,0);
	}
	
	private void block(int x, int z, int y, Block block, int data){
		world.setBlock(x,y,z,block,data,2);
	}
	
	private void linex(int x1, int x2, int z, int y, Block block){
		linex(x1,x2,z,y,block,0);
	}
	
	private void linex(int x1, int x2, int z, int y, Block block, int data){
		for(int x = x1; x <= x2; x++)world.setBlock(x,y,z,block,data,2);
	}
	
	private void linez(int z1, int z2, int x, int y, Block block){
		linez(z1,z2,x,y,block,0);
	}
	
	private void linez(int z1, int z2, int x, int y, Block block, int data){
		for(int z = z1; z <= z2; z++)world.setBlock(x,y,z,block,data,2);
	}
	
	private void rect(int x1, int z1, int x2, int z2, int y, Block block){
		rect(x1,z1,x2,z2,y,block,0);
	}
	
	private void rect(int x1, int z1, int x2, int z2, int y, Block block, int data){
		for(int x = x1; x <= x2; x++){
			for(int z = z1; z <= z2; z++)world.setBlock(x,y,z,block,data,2);
		}
	}
}
