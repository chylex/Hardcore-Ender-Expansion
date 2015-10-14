package chylex.hee.world.feature.stronghold.corridors;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.world.feature.stronghold.StrongholdPieceGeneric;
import chylex.hee.world.util.Size;

public class StrongholdCorridorIntersection extends StrongholdPieceGeneric{
	public static StrongholdCorridorIntersection[] generateCorners(){
		return new StrongholdCorridorIntersection[]{
			new StrongholdCorridorIntersection(Facing4.NORTH_NEGZ,Facing4.EAST_POSX),
			new StrongholdCorridorIntersection(Facing4.EAST_POSX,Facing4.SOUTH_POSZ),
			new StrongholdCorridorIntersection(Facing4.SOUTH_POSZ,Facing4.WEST_NEGX),
			new StrongholdCorridorIntersection(Facing4.WEST_NEGX,Facing4.NORTH_NEGZ)
		};
	}
	
	public static StrongholdCorridorIntersection[] generateThreeWay(){
		return new StrongholdCorridorIntersection[]{
			new StrongholdCorridorIntersection(Facing4.NORTH_NEGZ,Facing4.EAST_POSX,Facing4.SOUTH_POSZ),
			new StrongholdCorridorIntersection(Facing4.NORTH_NEGZ,Facing4.WEST_NEGX,Facing4.SOUTH_POSZ),
			new StrongholdCorridorIntersection(Facing4.EAST_POSX,Facing4.NORTH_NEGZ,Facing4.WEST_NEGX),
			new StrongholdCorridorIntersection(Facing4.EAST_POSX,Facing4.SOUTH_POSZ,Facing4.WEST_NEGX)
		};
	}
	
	public static StrongholdCorridorIntersection[] generateFourWay(){
		return new StrongholdCorridorIntersection[]{
			new StrongholdCorridorIntersection(Facing4.list)
		};
	}
	
	private StrongholdCorridorIntersection(Facing4...facings){
		super(Type.CORRIDOR,new Size(5,5,5),facings,withAnything);
	}
}
