package chylex.hee.world.feature.stronghold.rooms.general;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.init.BlockList;
import chylex.hee.system.abstractions.BlockInfo;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.world.feature.stronghold.StrongholdPiece;
import chylex.hee.world.structure.IBlockPicker;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.structure.util.Size;

public class StrongholdRoomEndPortal extends StrongholdPiece{ // TODO maybe add stairs to upper level
	public StrongholdRoomEndPortal(){
		super(Type.ROOM,new Size(19,13,19));
		
		addConnection(Facing4.NORTH_NEGZ,maxX/2,0,0,fromRoom);
		addConnection(Facing4.SOUTH_POSZ,maxX/2,0,maxZ,fromRoom);
		addConnection(Facing4.EAST_POSX,maxX,0,maxZ/2,fromRoom);
		addConnection(Facing4.WEST_NEGX,0,0,maxZ/2,fromRoom);
		addConnection(Facing4.NORTH_NEGZ,maxX/2,6,0,fromRoom);
		addConnection(Facing4.SOUTH_POSZ,maxX/2,6,maxZ,fromRoom);
		addConnection(Facing4.EAST_POSX,maxX,6,maxZ/2,fromRoom);
		addConnection(Facing4.WEST_NEGX,0,6,maxZ/2,fromRoom);
	}

	@Override
	public void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, final int x, final int y, final int z){
		final int centerX = x+maxX/2, centerZ = z+maxZ/2;
		PosMutable mpos = new PosMutable();
		
		// box
		placeCube(world,rand,placeStoneBrick,x,y+maxY,z,x+maxX,y+maxY,z+maxZ); // ceiling
		placeWalls(world,rand,placeStoneBrick,x,y,z,x+maxX,y+maxY-1,z+maxZ); // outer wall layer
		placeWalls(world,rand,placeStoneBrick,x+1,y,z+1,x+maxX-1,y+5,z+maxZ-1); // inner wall layer
		
		// second floor
		placeOutline(world,rand,placeStoneBrick,x+1,y+6,z+1,x+maxX-1,y+6,z+maxZ-1,4); // floor outline
		placeOutline(world,rand,IBlockPicker.basic(Blocks.fence),x+4,y+7,z+4,x+maxX-4,y+7,z+maxZ-4,1); // fence on the edge
		
		// first floor
		placeOutline(world,rand,placeStoneBrick,x+2,y,z+2,x+maxX-2,y,z+maxZ-2,2); // floor outline
		
		IBlockPicker placeSlabBottom = IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneBrickBottom);
		
		for(int cornerX = 0; cornerX < 2; cornerX++){
			for(int cornerZ = 0; cornerZ < 2; cornerZ++){
				// floor full block corners
				placeBlock(world,rand,placeStoneBrick,x+4+10*cornerX,y,z+4+10*cornerZ);
				placeBlock(world,rand,placeStoneBrick,x+5+8*cornerX,y,z+4+10*cornerZ);
				placeBlock(world,rand,placeStoneBrick,x+4+10*cornerX,y,z+5+8*cornerZ);
				// slabs in portal corners
				placeBlock(world,rand,placeSlabBottom,x+7+4*cornerX,y,z+7+4*cornerZ);
				// walls in corners
				placeLine(world,rand,IBlockPicker.basic(BlockList.stone_brick_wall),x+3+12*cornerX,y+1,z+3+12*cornerZ,x+3+12*cornerX,y+5,z+3+12*cornerZ);
			}
		}
		
		placeOutline(world,rand,placeSlabBottom,x+6,y,z+6,x+maxX-6,y,z+maxZ-6,1); // slabs around portal
		placeCube(world,rand,IBlockPicker.basic(Blocks.end_portal,Meta.endPortalDisabled),centerX-1,y,centerZ-1,centerX+1,y,centerZ+1); // portal
		
		for(Facing4 facing:Facing4.list){
			int perX = facing.perpendicular().getX(), perZ = facing.perpendicular().getZ();
			int leftX = facing.rotateLeft().getX(), leftZ = facing.rotateLeft().getZ();
			int rightX = facing.rotateRight().getX(), rightZ = facing.rotateRight().getZ();
			
			mpos.set(centerX,y,centerZ).move(facing,4);
			
			// outer layer of slabs around portal
			placeLine(world,rand,placeSlabBottom,mpos.x-perX*2,y,mpos.z-perZ*2,mpos.x+perX*2,y,mpos.z+perZ*2);
			
			// stairs around portal
			mpos.move(facing,1);
			placeLine(world,rand,placeStoneBrickStairs(facing,false),mpos.x-perX*2,y,mpos.z-perZ*2,mpos.x+perX*2,y,mpos.z+perZ*2);
			placeBlock(world,rand,placeStoneBrickStairs(facing.rotateLeft(),false),mpos.x+leftX*3,y,mpos.z+leftZ*3);
			placeBlock(world,rand,placeStoneBrickStairs(facing.rotateRight(),false),mpos.x+rightX*3,y,mpos.z+rightZ*3);
			mpos.move(facing,-1);
			placeBlock(world,rand,placeStoneBrickStairs(facing,false),mpos.x-perX*3,y,mpos.z-perZ*3);
			placeBlock(world,rand,placeStoneBrickStairs(facing,false),mpos.x+perX*3,y,mpos.z+perZ*3);
			placeBlock(world,rand,placeStoneBrickStairs(facing.rotateLeft(),false),mpos.x+leftX*4,y,mpos.z+leftZ*4);
			placeBlock(world,rand,placeStoneBrickStairs(facing.rotateRight(),false),mpos.x+rightX*4,y,mpos.z+rightZ*4);
			
			// vines in corners
			mpos.set(centerX,y,centerZ).move(facing,7).move(facing = facing.rotateRight(),7);
			placeLine(world,rand,IBlockPicker.basic(BlockList.dry_vine,Meta.getVine(facing,facing.rotateLeft())),mpos.x,y+1,mpos.z,mpos.x,y+6,mpos.z);
			
			// portal frame
			mpos.set(centerX,y,centerZ).move(facing,2);
			placeBlock(world,rand,IBlockPicker.basic(BlockList.end_portal_frame,Meta.endPortalFrameAcceptor),mpos.x,y,mpos.z);
			mpos.move(facing.rotateLeft());
			placeBlock(world,rand,IBlockPicker.basic(BlockList.end_portal_frame,Meta.endPortalFramePlain),mpos.x,y,mpos.z);
			mpos.move(facing.rotateRight(),2);
			placeBlock(world,rand,IBlockPicker.basic(BlockList.end_portal_frame,Meta.endPortalFramePlain),mpos.x,y,mpos.z);
		}
		
		// general		
		for(Facing4 facing:Facing4.list){
			Facing4 perpendicular = facing.perpendicular();
			int perX = perpendicular.getX(), perZ = perpendicular.getZ();
			
			boolean bottomUnused = inst.isConnectionFree(facing,connection -> connection.offsetY == 0);
			boolean topUnused = inst.isConnectionFree(facing,connection -> connection.offsetY == 6);
			
			mpos.set(centerX,y+1,centerZ).move(facing,9);
			if (!bottomUnused)placeCube(world,rand,placeAir,mpos.x-perX,y+1,mpos.z-perZ,mpos.x+perX,y+3,mpos.z+perZ); // bottom side doors
			if (!topUnused)placeCube(world,rand,placeAir,mpos.x-perX,y+7,mpos.z-perZ,mpos.x+perX,y+9,mpos.z+perZ); // top side doors
			
			if (!bottomUnused){
				mpos.move(facing,-1);
				placeCube(world,rand,placeAir,mpos.x-perX,y+1,mpos.z-perZ,mpos.x+perX,y+3,mpos.z+perZ); // bottom side doors (inner)
				mpos.move(facing,1);
			}
			
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
