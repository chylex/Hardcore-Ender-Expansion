package chylex.hee.world.feature.stronghold;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.system.abstractions.BlockInfo;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.world.structure.IBlockPicker;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.structure.util.Facing4;
import chylex.hee.world.structure.util.Size;

public class StrongholdPieceEndPortal extends StrongholdPiece{
	public StrongholdPieceEndPortal(){
		super(Type.ROOM,new Size(17,13,17));
		addConnection(Facing4.NORTH_NEGZ,8,0,0);
		addConnection(Facing4.SOUTH_POSZ,8,0,16);
		addConnection(Facing4.EAST_POSX,16,0,8);
		addConnection(Facing4.WEST_NEGX,0,0,8);
		addConnection(Facing4.NORTH_NEGZ,8,6,0);
		addConnection(Facing4.SOUTH_POSZ,8,6,16);
		addConnection(Facing4.EAST_POSX,16,6,8);
		addConnection(Facing4.WEST_NEGX,0,6,8);
	}

	@Override
	public void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, final int x, final int y, final int z){
		PosMutable mpos = new PosMutable();
		
		// box
		placeCube(world,rand,placeStoneBrick,x,y+maxY,z,x+maxX,y+maxY,z+maxZ); // ceiling
		placeWalls(world,rand,placeStoneBrick,x,y,z,x+maxX,y+maxY-1,z+maxZ); // outer wall layer
		placeWalls(world,rand,placeStoneBrick,x+1,y,z+1,x+maxX-1,y+5,z+maxZ-1); // inner wall layer
		
		// first floor
		placeOutline(world,rand,placeStoneBrick,x+2,y,z+2,x+maxX-2,y,z+maxZ-2,1); // floor outline
		
		IBlockPicker placeSlabBottom = IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneBrickBottom);
		
		for(int cornerX = 0; cornerX < 2; cornerX++){
			for(int cornerZ = 0; cornerZ < 2; cornerZ++){
				// floor full block corners
				placeBlock(world,rand,placeStoneBrick,x+3+10*cornerX,y,z+3+10*cornerZ);
				placeBlock(world,rand,placeStoneBrick,x+4+8*cornerX,y,z+3+10*cornerZ);
				placeBlock(world,rand,placeStoneBrick,x+3+10*cornerX,y,z+4+8*cornerZ);
				// slabs in portal corners
				placeBlock(world,rand,placeSlabBottom,x+6+4*cornerX,y,z+6+4*cornerZ);
			}
		}
		
		placeOutline(world,rand,placeSlabBottom,x+5,y,z+5,x+maxX-5,y,z+maxZ-5,1); // slabs around portal
		
		for(Facing4 facing:Facing4.list){
			Facing4 perpendicular = facing.rotateRight();
			int perX = perpendicular.getX(), perZ = perpendicular.getZ();
			
			mpos.set(x+maxX/2,y,z+maxZ/2).move(facing,4);
			placeLine(world,rand,placeSlabBottom,mpos.x-perX*2,y,mpos.z-perZ*2,mpos.x+perX*2,y,mpos.z+perZ*2); // outer layer of slabs around portal
			
			// stairs around portal
			mpos.move(facing,1);
			placeLine(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(facing,false)),mpos.x-perX*2,y,mpos.z-perZ*2,mpos.x+perX*2,y,mpos.z+perZ*2);
			placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(facing.rotateLeft(),false)),mpos.x-perX*3,y,mpos.z-perZ*3);
			placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(facing.rotateRight(),false)),mpos.x+perX*3,y,mpos.z+perZ*3);
			mpos.move(facing,-1);
			placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(facing,false)),mpos.x-perX*3,y,mpos.z-perZ*3);
			placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(facing,false)),mpos.x+perX*3,y,mpos.z+perZ*3);
			placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(facing.rotateLeft(),false)),mpos.x-perX*4,y,mpos.z-perZ*4);
			placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(facing.rotateRight(),false)),mpos.x+perX*4,y,mpos.z+perZ*4);
		}
		
		// second floor
		placeOutline(world,rand,placeStoneBrick,x+1,y+6,z+1,x+maxX-1,y+6,z+maxZ-1,3); // floor outline
		placeOutline(world,rand,IBlockPicker.basic(Blocks.fence),x+3,y+7,z+3,x+maxX-3,y+7,z+maxZ-3,1); // fence on the edge
		
		// general		
		for(Facing4 facing:Facing4.list){
			Facing4 perpendicular = facing.rotateRight();
			int perX = perpendicular.getX(), perZ = perpendicular.getZ();
			
			mpos.set(x+maxX/2,y+1,z+maxZ/2).move(facing,8);
			placeCube(world,rand,placeAir,mpos.x-perX,y+1,mpos.z-perZ,mpos.x+perX,y+3,mpos.z+perZ); // bottom side doors
			placeCube(world,rand,placeAir,mpos.x-perX,y+7,mpos.z-perZ,mpos.x+perX,y+9,mpos.z+perZ); // top side doors
			
			mpos.move(facing,-1);
			placeCube(world,rand,placeAir,mpos.x-perX,y+1,mpos.z-perZ,mpos.x+perX,y+3,mpos.z+perZ); // bottom side doors (inner)
			mpos.move(facing,1);
			
			mpos.move(perpendicular,4);
			placeCube(world,rand,placeAir,mpos.x-perX,y+1,mpos.z-perZ,mpos.x+perX,y+3,mpos.z+perZ); // bottom windows
			mpos.move(facing,-1);
			placeBlock(world,rand,random -> new BlockInfo(Blocks.iron_bars),mpos.x,y+2,mpos.z); // iron bar
			mpos.move(facing,1);
			
			mpos.move(perpendicular,-8);
			placeCube(world,rand,placeAir,mpos.x-perX,y+1,mpos.z-perZ,mpos.x+perX,y+3,mpos.z+perZ); // bottom windows
			mpos.move(facing,-1);
			placeBlock(world,rand,random -> new BlockInfo(Blocks.iron_bars),mpos.x,y+2,mpos.z); // iron bar
			mpos.move(facing,1);
		}
	}
}
