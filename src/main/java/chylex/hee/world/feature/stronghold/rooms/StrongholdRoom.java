package chylex.hee.world.feature.stronghold.rooms;
import java.util.Random;
import javax.annotation.Nullable;
import org.apache.commons.lang3.ArrayUtils;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.world.feature.stronghold.StrongholdPiece;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.structure.util.Facing4;
import chylex.hee.world.structure.util.Size;

public abstract class StrongholdRoom extends StrongholdPiece{
	public StrongholdRoom(Size size){
		super(Type.ROOM,size);
		
		addConnection(Facing4.NORTH_NEGZ,maxX/2,0,0,fromRoom);
		addConnection(Facing4.SOUTH_POSZ,maxX/2,0,maxZ,fromRoom);
		addConnection(Facing4.EAST_POSX,maxX,0,maxZ/2,fromRoom);
		addConnection(Facing4.WEST_NEGX,0,0,maxZ/2,fromRoom);
	}
	
	public StrongholdRoom(Size size, @Nullable Facing4[] connectWith){
		super(Type.ROOM,size);
		
		if (ArrayUtils.contains(connectWith,Facing4.NORTH_NEGZ))addConnection(Facing4.NORTH_NEGZ,maxX/2,0,0,fromRoom);
		if (ArrayUtils.contains(connectWith,Facing4.SOUTH_POSZ))addConnection(Facing4.SOUTH_POSZ,maxX/2,0,maxZ,fromRoom);
		if (ArrayUtils.contains(connectWith,Facing4.EAST_POSX))addConnection(Facing4.EAST_POSX,maxX,0,maxZ/2,fromRoom);
		if (ArrayUtils.contains(connectWith,Facing4.WEST_NEGX))addConnection(Facing4.WEST_NEGX,0,0,maxZ/2,fromRoom);
	}
	
	@Override
	public void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		// box
		placeCube(world,rand,placeStoneBrick,x,y,z,x+maxX,y,z+maxZ);
		placeCube(world,rand,placeStoneBrick,x,y+maxY,z,x+maxX,y+maxY,z+maxZ);
		placeWalls(world,rand,placeStoneBrick,x,y+1,z,x+maxX,y+maxY-1,z+maxZ);
		
		// connections
		PosMutable mpos = new PosMutable();
		
		for(Connection connection:connections){
			if (!inst.isConnectionFree(connection)){
				int perX = connection.facing.perpendicular().getX(), perZ = connection.facing.perpendicular().getZ();
				
				mpos.set(x+connection.offsetX,y+connection.offsetY+1,z+connection.offsetZ);
				placeCube(world,rand,placeAir,mpos.x-perX,y+1,mpos.z-perZ,mpos.x+perX,y+3,mpos.z+perZ);
			}
		}
	}
}
