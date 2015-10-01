package chylex.hee.world.structure.dungeon;
import java.util.Optional;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.collections.weight.WeightedList;
import chylex.hee.world.structure.StructureBase;
import chylex.hee.world.util.BoundingBox;
import chylex.hee.world.util.Range;

/**
 * Basic dungeon consisting of multiple cuboids connected to each other.
 */
public class StructureDungeon<T extends StructureDungeonGenerator> extends StructureBase<T>{
	private final StructureDungeonGenerator.Constructor<T> generatorConstructor;
	
	public final BoundingBox boundingBox;
	public final WeightedList<StructureDungeonPieceArray> pieces = new WeightedList<>();
	private StructureDungeonPieceArray startingPiece;
	private Range pieceAmount;
	
	public StructureDungeon(int radX, int sizeY, int radZ, StructureDungeonGenerator.Constructor<T> generatorConstructor){
		super(radX,sizeY,radZ);
		this.generatorConstructor = generatorConstructor;
		boundingBox = new BoundingBox(Pos.at(-radX,0,-radZ),Pos.at(radX,sizeY-1,radZ));
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
		this.startingPiece = new StructureDungeonPieceArray(0,new Range(1,1),new StructureDungeonPiece[]{ piece });
	}
	
	public void setStartingPiece(StructureDungeonPiece[] possiblePieces){
		this.startingPiece = new StructureDungeonPieceArray(0,new Range(1,1),possiblePieces);
	}
	
	public Optional<StructureDungeonPieceArray> getStartingPiece(){
		return Optional.ofNullable(startingPiece);
	}
	
	public void setPieceAmount(int min, int max){
		this.pieceAmount = new Range(min,max);
	}
	
	public Range getPieceAmountRange(){
		return pieceAmount;
	}
	
	@Override
	protected T createGenerator(){
		return generatorConstructor.construct(this);
	}
}
