package chylex.hee.world.feature.stronghold.rooms;
import javax.annotation.Nullable;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.world.feature.stronghold.StrongholdPieceGeneric;
import chylex.hee.world.util.Size;

public abstract class StrongholdRoom extends StrongholdPieceGeneric{
	public StrongholdRoom(Size size){
		super(Type.ROOM,size,fromRoom);
	}
	
	public StrongholdRoom(Size size, @Nullable Facing4[] connectWith){
		super(Type.ROOM,size,connectWith,fromRoom);
	}
}
