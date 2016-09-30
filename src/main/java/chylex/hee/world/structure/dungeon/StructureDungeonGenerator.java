package chylex.hee.world.structure.dungeon;
import java.util.Random;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.collections.weight.WeightedList;
import chylex.hee.world.structure.dungeon.StructureDungeonPiece.Connection;
import chylex.hee.world.structure.util.IStructureGenerator;
import chylex.hee.world.util.BoundingBox;

public abstract class StructureDungeonGenerator implements IStructureGenerator{
	@FunctionalInterface
	public static interface Constructor<T extends StructureDungeonGenerator>{
		T construct(StructureDungeon dungeon);
	}
	
	protected final StructureDungeon<?> dungeon;
	protected final WeightedList<StructureDungeonPieceInst> generated;
	
	public StructureDungeonGenerator(StructureDungeon<?> dungeon){
		this.dungeon = dungeon;
		this.generated = new WeightedList<>(dungeon.getPieceAmountRange().max);
	}

	/**
	 * Checks whether the area between two points is inside the structure and does not intersect any existing pieces.
	 */
	protected boolean canPlaceArea(Pos pos1, Pos pos2){
		final BoundingBox box = new BoundingBox(pos1, pos2);
		return box.isInside(dungeon.boundingBox) && !generated.stream().anyMatch(inst -> inst.boundingBox.intersects(box));
	}
	
	/**
	 * Aligns two pieces by their connections.
	 */
	protected Pos alignConnections(StructureDungeonPieceInst targetPiece, Connection targetConnection, Connection sourceConnection){
		Pos pos = targetPiece.boundingBox.getTopLeft();
		pos = pos.offset(targetConnection.offsetX, targetConnection.offsetY, targetConnection.offsetZ);
		pos = pos.offset(targetConnection.facing, 1);
		pos = pos.offset(-sourceConnection.offsetX, -sourceConnection.offsetY, -sourceConnection.offsetZ);
		return pos;
	}
	
	/**
	 * Tries to connect two pieces together. If it can be done, it adds the piece to the structure, uses up both connections and returns true.
	 */
	protected boolean tryConnectPieces(StructureDungeonPiece sourcePiece, Connection sourceConnection, StructureDungeonPieceInst targetPiece, Connection targetConnection){
		Pos aligned = alignConnections(targetPiece, targetConnection, sourceConnection);
		
		if (canPlaceArea(aligned, aligned.offset(targetPiece.piece.size.sizeX-1, targetPiece.piece.size.sizeY-1, targetPiece.piece.size.sizeZ-1))){
			targetPiece.useConnection(targetConnection);
			addPiece(sourcePiece, aligned).useConnection(sourceConnection);
			return true;
		}
		else return false;
	}
	
	/**
	 * Generates the starting piece and adds it to the structure. If the dungeon does not one, a random piece is selected.
	 */
	protected StructureDungeonPieceInst generateStartPiece(Random rand){
		StructureDungeonPiece startPiece = dungeon.getStartingPiece().orElseGet(() -> selectNextPiece(rand)).getRandomPiece(rand);
		return addPiece(startPiece, Pos.at(-startPiece.size.sizeX/2, dungeon.boundingBox.y2/2-startPiece.size.sizeY/2, -startPiece.size.sizeZ));
	}
	
	/**
	 * Adds a new piece to the structure.
	 */
	protected StructureDungeonPieceInst addPiece(StructureDungeonPiece piece, Pos position){
		StructureDungeonPieceInst inst = new StructureDungeonPieceInst(piece, position);
		generated.add(inst);
		return inst;
	}
	
	/**
	 * Returns a random piece out of the available piece list, or null if there are no pieces available.
	 */
	protected abstract StructureDungeonPieceArray selectNextPiece(Random rand);
}
