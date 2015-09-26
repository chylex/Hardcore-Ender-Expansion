package chylex.hee.world.structure.dungeon;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import chylex.hee.system.collections.weight.IWeightProvider;
import chylex.hee.world.structure.dungeon.StructureDungeonPiece.IPieceType;
import chylex.hee.world.structure.util.Range;

public class StructureDungeonPieceArray implements IWeightProvider{
	public final IPieceType type;
	public final Range amount;
	private final int weight;
	private final StructureDungeonPiece[] pieces;
	
	public StructureDungeonPieceArray(int weight, Range amount, StructureDungeonPiece[] pieces){
		if (pieces == null || pieces.length == 0)throw new IllegalArgumentException("Piece array cannot be null or empty!");
		
		this.type = pieces[0].type;
		if (!Arrays.stream(pieces).allMatch(piece -> piece.type == this.type))throw new IllegalArgumentException("Pieces in an array must be of the same type!");
		
		this.amount = amount;
		this.weight = weight;
		this.pieces = pieces;
		for(StructureDungeonPiece piece:pieces)piece.setParentArray(this);
	}
	
	public StructureDungeonPiece getRandomPiece(Random rand){
		return pieces[rand.nextInt(pieces.length)];
	}
	
	public List<StructureDungeonPiece> toList(){
		return Arrays.asList(pieces);
	}
	
	@Override
	public int getWeight(){
		return weight;
	}
}
