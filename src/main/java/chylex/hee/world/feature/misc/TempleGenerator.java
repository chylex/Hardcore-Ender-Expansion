package chylex.hee.world.feature.misc;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.BlockQuartz.EnumType;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStairs.EnumHalf;
import net.minecraft.block.BlockStoneSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import chylex.hee.block.BlockList;
import chylex.hee.system.util.BlockPosM;

public class TempleGenerator{
	private final World world;
	private final BlockPosM pos = new BlockPosM();
	
	public TempleGenerator(World world){
		this.world = world;
	}

	public void preloadAndClearArea(int x, int y, int z){
		BlockPosM pos = new BlockPosM();
		world.getChunkFromBlockCoords(pos.moveTo(x,0,z));
		world.getChunkFromBlockCoords(pos.moveTo(x+19,0,z));
		
		for(int xx = x; xx <= x+19; xx++){
			for(int zz = z; zz <= z+13; zz++){
				for(int yy = y+7; yy >= y; yy--)pos.moveTo(xx,yy,zz).setToAir(world);
			}
		}
	}
	
	public void spawnTemple(int x, int y, int z){
		preloadAndClearArea(x,y,z);
		
		// LAYER 1
		rect(x+1,z,x+18,z+12,y,Blocks.quartz_block);
		block(x,z+6,y,Blocks.quartz_block);
		block(x,z+5,y,getQuartzStairs(EnumFacing.SOUTH,true));
		block(x,z+7,y,getQuartzStairs(EnumFacing.WEST,true));
		
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
		linez(z+5,z+7,x+5,y,Blocks.stone_slab.getDefaultState().withProperty(BlockStoneSlab.VARIANT,BlockStoneSlab.EnumType.QUARTZ));
		for(int a = 0; a < 2; a++)linex(x+11,x+13,z+4+a*4,y,getQuartz(EnumType.CHISELED));
		linez(z+5,z+7,x+14,y,getQuartz(EnumType.CHISELED));
		for(int a = 0; a < 2; a++){
			for(int b = 0; b < 2; b++)block(x+10+4*b,z+4*(a+1),y,getQuartz(EnumType.LINES_Y));
		}
		
		for(int a = 0; a < 2; a++){
			rect(x+1,z+8*a,x+2,z+4+8*a,y,Blocks.quartz_block);
			block(x+1,z+4*(a+1),y,getQuartz(EnumType.CHISELED));
			linex(x+3,x+6,z+3+6*a,y,Blocks.quartz_block);
			block(x+6,z+2+8*a,y,Blocks.quartz_block);
			rect(x+17,z+11*a,x+18,z+1+11*a,y,Blocks.quartz_block);
			for(int b = 0; b < 13; b++)linez(z+11*a,z+1+11*a,x+4+b,y,getQuartz(b%2 == 0 ? EnumType.LINES_Y : EnumType.DEFAULT));
			linez(z+11*a,z+1+11*a,x+3,y,Blocks.quartz_block);
			linex(x+3,x+5,z+2+8*a,y,Blocks.water);
		}
		for(int a = 0; a < 9; a++)linex(x+17,x+18,z+2+a,y,getQuartz(a%2 == 1 ? EnumType.DEFAULT : EnumType.LINES_Y));
		
		for(int a = 0; a < 2; a++)block(x,z+5+2*a,y,Blocks.quartz_block);
		block(x,z+6,y,getQuartz(EnumType.LINES_Y));
		block(x,z+4,y,getQuartzStairs(EnumFacing.SOUTH,true));
		block(x,z+8,y,getQuartzStairs(EnumFacing.WEST,true));
		
		rect(x+1,z+5,x+4,z+7,y,Blocks.carpet.getDefaultState().withProperty(BlockCarpet.COLOR,EnumDyeColor.RED));
		linex(x+7,x+15,z+2,y,Blocks.carpet.getDefaultState().withProperty(BlockCarpet.COLOR,EnumDyeColor.RED));
		linex(x+7,x+15,z+10,y,Blocks.carpet.getDefaultState().withProperty(BlockCarpet.COLOR,EnumDyeColor.RED));
		linez(z+2,z+10,x+16,y,Blocks.carpet.getDefaultState().withProperty(BlockCarpet.COLOR,EnumDyeColor.RED));
		++y;
		
		// LAYER 3
		rect(x+8,z+5,x+10,z+7,y,Blocks.quartz_block);
		block(x+9,z+6,y,Blocks.coal_block);
		linez(z+5,z+7,x+7,y,Blocks.stone_slab.getDefaultState().withProperty(BlockStoneSlab.VARIANT,BlockStoneSlab.EnumType.QUARTZ));
		for(int a = 0; a < 2; a++)linex(x+11,x+13,z+4+a*4,y,Blocks.quartz_block);
		linez(z+5,z+7,x+14,y,Blocks.quartz_block);
		rect(x+11,z+5,x+13,z+7,y,BlockList.temple_end_portal);
		
		for(int a = 0; a < 2; a++){
			for(int b = 0; b < 2; b++)block(x+10+4*b,z+4*(a+1),y,getQuartz(EnumType.LINES_Y));
		}
		
		for(int a = 0; a < 2; a++){
			rect(x+1,z+8*a,x+2,z+4+8*a,y,Blocks.quartz_block);
			rect(x+17,z+11*a,x+18,z+1+11*a,y,Blocks.quartz_block);
			for(int b = 0; b < 2; b++)block(x+2,z+2+6*a+2*b,y,getQuartz(EnumType.CHISELED));
			for(int b = 0; b < 13; b++)linez(z+11*a,z+1+11*a,x+4+b,y,getQuartz(b%2 == 0 ? EnumType.LINES_Y : EnumType.DEFAULT));
			linez(z+11*a,z+1+11*a,x+3,y,Blocks.quartz_block);
		}
		for(int a = 0; a < 9; a++)linex(x+17,x+18,z+2+a,y,getQuartz(a%2 == 1 ? EnumType.DEFAULT : EnumType.LINES_Y));
		
		for(int a = 0; a < 5; a++)block(x,z+4+a,y,getQuartz(a == 2 ? EnumType.CHISELED : (a == 1 || a == 3 ? EnumType.LINES_Z : EnumType.DEFAULT)));
		++y;
		
		// LAYER 4
		for(int a = 0; a < 2; a++){
			for(int b = 0; b < 2; b++)block(x+10+4*a,z+4+4*b,y,Blocks.stone_slab.getDefaultState().withProperty(BlockStoneSlab.VARIANT,BlockStoneSlab.EnumType.QUARTZ));
		}
		
		for(int a = 0; a < 2; a++)block(x,z+5+2*a,y,Blocks.quartz_block);
		block(x,z+6,y,getQuartz(EnumType.LINES_Y));
		block(x,z+4,y,getQuartzStairs(EnumFacing.SOUTH,false));
		block(x,z+8,y,getQuartzStairs(EnumFacing.WEST,false));
		
		// LAYER 4 + 5
		for(int i = 0; i < 2; i++){
			for(int a = 0; a < 2; a++){
				rect(x+1,z+8*a,x+2,z+4+8*a,y,Blocks.quartz_block);
				rect(x+17,z+11*a,x+18,z+1+11*a,y,Blocks.quartz_block);
				for(int b = 0; b < 2; b++){
					if (i == 0){
						block(x+2,z+3+6*a,y,getQuartz(EnumType.CHISELED));
						block(x+1,z+4+4*a,y,getQuartz(EnumType.CHISELED));
					}
					else block(x+2,z+2+6*a+2*b,y,getQuartz(EnumType.CHISELED));
				}
				for(int b = 0; b < 13; b++){
					if (b%2 == 0)linez(z+11*a,z+1+11*a,x+4+b,y,getQuartz(EnumType.LINES_Y));
					else{
						block(x+4+b,z+11*a,y,a == 0 ? Blocks.glowstone : Blocks.iron_bars);
						block(x+4+b,z+1+11*a,y,a == 1 ? Blocks.glowstone : Blocks.iron_bars);
					}
				}
				linez(z+11*a,z+1+11*a,x+3,y,Blocks.quartz_block);
			}
			for(int a = 0; a < 9; a++){
				if (a%2 == 0)linex(x+17,x+18,z+2+a,y,getQuartz(EnumType.LINES_Y));
				else{
					block(x+17,z+2+a,y,Blocks.iron_bars);
					block(x+18,z+2+a,y,Blocks.glowstone);
				}
			}
			if (i == 0)++y;
		}
		
		// LAYER 5
		block(x,z+6,y,Blocks.quartz_block);
		block(x,z+5,y,getQuartzStairs(EnumFacing.SOUTH,false));
		block(x,z+7,y,getQuartzStairs(EnumFacing.WEST,false));

		for(int a = 0; a < 9; a++)block(x+2,z+2+a,y,getQuartz(a%2 == 0 ? EnumType.CHISELED : EnumType.DEFAULT));
		for(int a = 0; a < 3; a++)block(x+1,z+5+a,y,getQuartz(a%2 == 0 ? EnumType.CHISELED : EnumType.DEFAULT));
		++y;
		
		// LAYER 6
		for(int a = 0; a < 2; a++){
			linex(x+1,x+18,z+12*a,y,Blocks.quartz_block);
			linez(z+1,z+11,x+1+17*a,y,Blocks.quartz_block);
			for(int b = 0; b < 13; b++)block(x+4+b,z+1+10*a,y,getQuartz(b%2 == 0 ? EnumType.LINES_Y : EnumType.DEFAULT));
			linez(z+11*a,z+1+11*a,x+3,y,Blocks.quartz_block);
			linex(x+3,x+16,z+12*a,y,Blocks.quartz_block);
		}
		for(int a = 0; a < 9; a++){
			block(x+17,z+2+a,y,getQuartz(a%2 == 1 ? EnumType.DEFAULT : EnumType.LINES_Y));
			block(x+2,z+2+a,y,getQuartz(a%2 == 1 ? EnumType.CHISELED : EnumType.DEFAULT));
		}
		linez(z+2,z+10,x+18,y,Blocks.quartz_block);
		++y;
		
		// LAYER 7 + 8 (ROOF)
		for(int a = 0; a < 2; a++){
			linez(z+2,z+10,x+2+15*a,y,getQuartzStairs(getFacing(a),false));
			linex(x+2,x+17,z+1+10*a,y,getQuartzStairs(getFacing(2+a),false));
			linez(z+3,z+9,x+3+13*a,y,Blocks.quartz_block);
			linex(x+3,x+16,z+2+8*a,y,Blocks.quartz_block);
			linez(z+4,z+8,x+4+11*a,y,getQuartzStairs(getFacing(1+a),true));
			linex(x+4,x+15,z+3+6*a,y,getQuartzStairs(getFacing(3+a),true));
		}
		++y;
		for(int a = 0; a < 2; a++){
			linez(z+3,z+9,x+3+13*a,y,getQuartzStairs(getFacing(a),false));
			linex(x+3,x+16,z+2+8*a,y,getQuartzStairs(getFacing(2+a),false));
		}
		rect(x+4,z+3,x+15,z+9,y,Blocks.quartz_block);
		world.setBlockToAir(pos.moveTo(x+9,y,z+6));
	}
	
	private void block(int x, int z, int y, Block block){
		block(x,z,y,block.getDefaultState());
	}
	
	private void block(int x, int z, int y, IBlockState state){
		world.setBlockState(pos.moveTo(x,y,z),state,2);
	}
	
	private void linex(int x1, int x2, int z, int y, Block block){
		linex(x1,x2,z,y,block.getDefaultState());
	}
	
	private void linex(int x1, int x2, int z, int y, IBlockState state){
		for(int x = x1; x <= x2; x++)world.setBlockState(pos.moveTo(x,y,z),state,2);
	}
	
	private void linez(int z1, int z2, int x, int y, Block block){
		linez(z1,z2,x,y,block.getDefaultState());
	}
	
	private void linez(int z1, int z2, int x, int y, IBlockState state){
		for(int z = z1; z <= z2; z++)world.setBlockState(pos.moveTo(x,y,z),state,2);
	}
	
	private void rect(int x1, int z1, int x2, int z2, int y, Block block){
		rect(x1,z1,x2,z2,y,block.getDefaultState());
	}
	
	private void rect(int x1, int z1, int x2, int z2, int y, IBlockState state){
		for(int x = x1; x <= x2; x++){
			for(int z = z1; z <= z2; z++)world.setBlockState(pos.moveTo(x,y,z),state,2);
		}
	}
	
	private IBlockState getQuartz(EnumType type){
		return Blocks.quartz_block.getDefaultState().withProperty(BlockQuartz.VARIANT,type);
	}
	
	private IBlockState getQuartzStairs(EnumFacing facing, boolean upsideDown){
		return Blocks.quartz_stairs.getDefaultState().withProperty(BlockStairs.FACING,facing).withProperty(BlockStairs.HALF,upsideDown ? EnumHalf.TOP : EnumHalf.BOTTOM);
	}
	
	private EnumFacing getFacing(int facing){
		return EnumFacing.getHorizontal(facing);
	}
}
