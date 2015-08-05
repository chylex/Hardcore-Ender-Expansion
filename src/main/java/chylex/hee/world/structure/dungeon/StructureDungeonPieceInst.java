package chylex.hee.world.structure.dungeon;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.collections.weight.IWeightProvider;
import chylex.hee.world.structure.dungeon.StructureDungeonPiece.Connection;
import chylex.hee.world.structure.dungeon.StructureDungeonPiece.Type;
import chylex.hee.world.structure.util.BoundingBox;
import chylex.hee.world.structure.util.Facing4;

public class StructureDungeonPieceInst implements IWeightProvider{
	public final StructureDungeonPiece piece;
	public final BoundingBox boundingBox;
	private final List<Connection> availableConnections = new ArrayList<>();
	
	public StructureDungeonPieceInst(StructureDungeonPiece piece, Pos position){
		this.piece = piece;
		this.boundingBox = new BoundingBox(position,position.offset(piece.size.sizeX-1,piece.size.sizeY-1,piece.size.sizeZ-1));
		this.availableConnections.addAll(piece.getConnections());
	}
	
	public List<Connection> findConnections(Facing4 facing, Type pieceType){
		return availableConnections.stream().filter(connection -> connection.facing == facing.opposite() && connection.canConnectWith(pieceType)).collect(Collectors.toList());
	}
	
	public void useConnection(Connection connection){
		availableConnections.remove(connection);
	}

	@Override
	public int getWeight(){
		return availableConnections.size();
	}
}
