package chylex.hee.world.feature.stronghold;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.world.structure.IBlockPicker;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.util.Facing4;
import chylex.hee.world.structure.util.Size;

public class StrongholdPieceEndPortal extends StrongholdPiece{
	public StrongholdPieceEndPortal(){
		super(Type.ROOM,new Size(17,13,17));
		addConnection(Facing4.NORTH_NEGZ,8,0,0);
		addConnection(Facing4.SOUTH_POSZ,8,0,16);
		addConnection(Facing4.EAST_POSX,0,0,8);
		addConnection(Facing4.WEST_NEGX,16,0,8);
	}

	@Override
	public void generate(StructureWorld world, Random rand, final int x, final int y, final int z){
		// box
		placeCube(world,rand,placeStoneBrick,x,y+maxY,z,x+maxX,y+maxY,z+maxZ); // ceiling
		placeWalls(world,rand,placeStoneBrick,x,y,z,x+maxX,y+maxY-1,z+maxZ); // outer wall layer
		placeWalls(world,rand,placeStoneBrick,x+1,y,z+1,x+maxX-1,y+5,z+maxZ-1); // inner wall layer
		
		// first floor
		placeOutline(world,rand,placeStoneBrick,x+2,y,z+2,x+maxX-2,y,z+maxZ-2,2); // floor outline
		
		// second floor
		placeOutline(world,rand,placeStoneBrick,x+1,y+6,z+1,x+maxX-1,y+6,z+maxZ-1,3); // floor outline
		placeOutline(world,rand,IBlockPicker.basic(Blocks.fence),x+3,y+7,z+3,x+maxX-3,y+7,z+maxZ-3,1); // fence on the edge
		
		// general
		PosMutable door = new PosMutable();
		
		for(Facing4 facing:Facing4.list){
			Facing4 perpendicular = facing.rotateRight();
			door.set(x+maxX/2,y+1,z+maxZ/2).move(facing.toEnumFacing(),8);
			
			placeCube(world,rand,placeAir,door.x-perpendicular.getX(),y+1,door.z-perpendicular.getZ(),door.x+perpendicular.getX(),y+3,door.z+perpendicular.getZ());
			placeCube(world,rand,placeAir,door.x-perpendicular.getX(),y+7,door.z-perpendicular.getZ(),door.x+perpendicular.getX(),y+9,door.z+perpendicular.getZ());
			
			door.move(facing.toEnumFacing(),-1);
			placeCube(world,rand,placeAir,door.x-perpendicular.getX(),y+1,door.z-perpendicular.getZ(),door.x+perpendicular.getX(),y+3,door.z+perpendicular.getZ());
		}
	}
}
