package chylex.hee.world.feature.stronghold;
import java.util.Random;
import javax.annotation.Nullable;
import org.apache.commons.lang3.ArrayUtils;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.util.Size;

public abstract class StrongholdPieceGeneric extends StrongholdPiece{
	public StrongholdPieceGeneric(Type type, Size size, IConnectWith connection){
		super(type, size);
		
		addConnection(Facing4.NORTH_NEGZ, maxX/2, 0, 0, connection);
		addConnection(Facing4.SOUTH_POSZ, maxX/2, 0, maxZ, connection);
		addConnection(Facing4.EAST_POSX, maxX, 0, maxZ/2, connection);
		addConnection(Facing4.WEST_NEGX, 0, 0, maxZ/2, connection);
	}
	
	public StrongholdPieceGeneric(Type type, Size size, @Nullable Facing4[] connectWith, @Nullable IConnectWith connection){
		super(type, size);
		
		if (ArrayUtils.contains(connectWith, Facing4.NORTH_NEGZ))addConnection(Facing4.NORTH_NEGZ, maxX/2, 0, 0, connection);
		if (ArrayUtils.contains(connectWith, Facing4.SOUTH_POSZ))addConnection(Facing4.SOUTH_POSZ, maxX/2, 0, maxZ, connection);
		if (ArrayUtils.contains(connectWith, Facing4.EAST_POSX))addConnection(Facing4.EAST_POSX, maxX, 0, maxZ/2, connection);
		if (ArrayUtils.contains(connectWith, Facing4.WEST_NEGX))addConnection(Facing4.WEST_NEGX, 0, 0, maxZ/2, connection);
	}
	
	@Override
	public void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		// box
		placeCube(world, rand, placeStoneBrick, x, y, z, x+maxX, y, z+maxZ);
		placeCube(world, rand, placeStoneBrick, x, y+maxY, z, x+maxX, y+maxY, z+maxZ);
		placeWalls(world, rand, placeStoneBrick, x, y+1, z, x+maxX, y+maxY-1, z+maxZ);
		
		// connections
		PosMutable mpos = new PosMutable();
		
		for(Connection connection:connections){
			if (!inst.isConnectionFree(connection)){
				int perX = connection.facing.perpendicular().getX(), perZ = connection.facing.perpendicular().getZ();
				
				mpos.set(x+connection.offsetX, y+connection.offsetY, z+connection.offsetZ);
				placeCube(world, rand, placeAir, mpos.x-perX, mpos.y+1, mpos.z-perZ, mpos.x+perX, mpos.y+3, mpos.z+perZ);
			}
		}
	}
}
