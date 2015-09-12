package chylex.hee.world.structure.dungeon;
import java.util.Optional;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.collections.WeightedList;
import chylex.hee.world.structure.IStructureGenerator;
import chylex.hee.world.structure.StructureBase;
import chylex.hee.world.structure.util.BoundingBox;
import chylex.hee.world.structure.util.Range;

/**
 * Basic dungeon consisting of multiple cuboids connected to each other.
 */
public class StructureDungeon extends StructureBase{
	private final StructureDungeonGenerator.Constructor generator;
	
	public final BoundingBox boundingBox;
	public final WeightedList<StructureDungeonPieceArray> pieces = new WeightedList<>();
	private StructureDungeonPiece startingPiece;
	private Range pieceAmount;
	
	public StructureDungeon(int radX, int sizeY, int radZ, StructureDungeonGenerator.Constructor generator){
		super(radX,sizeY,radZ);
		this.generator = generator;
		boundingBox = new BoundingBox(Pos.at(-radX,0,-radZ),Pos.at(radX,sizeY,radZ));
	}
	
	
	public void addPiece(int weight, Range amountRange, StructureDungeonPiece piece){
		if (piece.countConnections() == 0)throw new IllegalArgumentException("Invalid structure data, no connections found!");
		this.pieces.add(new StructureDungeonPieceArray(weight,amountRange,new StructureDungeonPiece[]{ piece }));
	}
	
	public void addPieces(int weight, Range amountRange, StructureDungeonPiece[] pieces){
		for(StructureDungeonPiece piece:pieces){
			if (piece.countConnections() == 0)throw new IllegalArgumentException("Invalid structure data, no connections found! Class: "+piece.getClass().getName());
		}
		
		this.pieces.add(new StructureDungeonPieceArray(weight,amountRange,pieces));
	}
	
	public void setStartingPiece(StructureDungeonPiece piece){
		this.startingPiece = piece;
	}
	
	public Optional<StructureDungeonPiece> getStartingPiece(){
		return Optional.ofNullable(startingPiece);
	}
	
	public void setPieceAmount(int min, int max){
		this.pieceAmount = new Range(min,max);
	}
	
	public Range getPieceAmountRange(){
		return pieceAmount;
	}
	
	@Override
	protected IStructureGenerator createGenerator(){
		return generator.construct(this);
	}
}
