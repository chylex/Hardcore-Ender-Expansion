package chylex.hee.world.structure.dungeon;
import java.util.Random;
import chylex.hee.system.collections.weight.IWeightProvider;
import chylex.hee.world.structure.util.Range;

public class StructureDungeonPieceArray implements IWeightProvider{
	public final Range amount;
	private final int weight;
	private final StructureDungeonPiece[] pieces;
	
	public StructureDungeonPieceArray(int weight, Range amount, StructureDungeonPiece[] pieces){
		this.amount = amount;
		this.weight = weight;
		this.pieces = pieces;
		for(StructureDungeonPiece piece:pieces)piece.setParentArray(this);
	}
	
	public StructureDungeonPiece getRandomPiece(Random rand){
		return pieces[rand.nextInt(pieces.length)];
	}
	
	@Override
	public int getWeight(){
		return weight;
	}
}
