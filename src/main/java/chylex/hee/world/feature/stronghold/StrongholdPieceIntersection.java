package chylex.hee.world.feature.stronghold;
import java.util.Random;
import org.apache.commons.lang3.ArrayUtils;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.util.Facing4;
import chylex.hee.world.structure.util.Size;

public class StrongholdPieceIntersection extends StrongholdPiece{
	public static StrongholdPieceIntersection[] generateCorners(){
		return new StrongholdPieceIntersection[]{
			new StrongholdPieceIntersection(Facing4.NORTH_NEGZ,Facing4.EAST_POSX),
			new StrongholdPieceIntersection(Facing4.EAST_POSX,Facing4.SOUTH_POSZ),
			new StrongholdPieceIntersection(Facing4.SOUTH_POSZ,Facing4.WEST_NEGX),
			new StrongholdPieceIntersection(Facing4.WEST_NEGX,Facing4.NORTH_NEGZ)
		};
	}
	
	public static StrongholdPieceIntersection[] generateThreeWay(){
		return new StrongholdPieceIntersection[]{
			new StrongholdPieceIntersection(Facing4.NORTH_NEGZ,Facing4.EAST_POSX,Facing4.SOUTH_POSZ),
			new StrongholdPieceIntersection(Facing4.NORTH_NEGZ,Facing4.WEST_NEGX,Facing4.SOUTH_POSZ),
			new StrongholdPieceIntersection(Facing4.EAST_POSX,Facing4.NORTH_NEGZ,Facing4.WEST_NEGX),
			new StrongholdPieceIntersection(Facing4.EAST_POSX,Facing4.SOUTH_POSZ,Facing4.WEST_NEGX)
		};
	}
	
	public static StrongholdPieceIntersection[] generateFourWay(){
		return new StrongholdPieceIntersection[]{
			new StrongholdPieceIntersection(Facing4.list)
		};
	}
	
	private final Facing4[] facings;
	
	/**
	 * Facings array determines the access facing, so the hole generation is then inverted.
	 */
	private StrongholdPieceIntersection(Facing4...facings){
		super(Type.CORRIDOR,new Size(5,5,5));
		this.facings = facings;
		
		if (ArrayUtils.contains(facings,Facing4.NORTH_NEGZ))addConnection(Facing4.NORTH_NEGZ,2,0,4);
		if (ArrayUtils.contains(facings,Facing4.EAST_POSX))addConnection(Facing4.EAST_POSX,0,0,2);
		if (ArrayUtils.contains(facings,Facing4.SOUTH_POSZ))addConnection(Facing4.SOUTH_POSZ,2,0,0);
		if (ArrayUtils.contains(facings,Facing4.WEST_NEGX))addConnection(Facing4.WEST_NEGX,4,0,2);
	}
	
	@Override
	public void generate(StructureWorld world, Random rand, int x, int y, int z){
		placeCube(world,rand,placeStoneBrick,x,y,z,x+maxX,y,z+maxZ);
		placeCube(world,rand,placeStoneBrick,x,y+maxY,z,x+maxX,y+maxY,z+maxZ);
		placeWalls(world,rand,placeStoneBrick,x,y+1,z,x+maxX,y+maxY-1,z+maxZ);
		
		PosMutable hole = new PosMutable();
		
		for(Facing4 facing:facings){
			facing = facing.opposite();
			
			Facing4 perpendicular = facing.rotateRight();
			hole.set(x+2,y,z+2).move(facing,2);
			placeCube(world,rand,placeAir,hole.x-perpendicular.getX(),y+1,hole.z-perpendicular.getZ(),hole.x+perpendicular.getX(),y+3,hole.z+perpendicular.getZ());
		}
	}
}
