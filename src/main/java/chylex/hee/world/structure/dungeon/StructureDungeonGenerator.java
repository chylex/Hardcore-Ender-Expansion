package chylex.hee.world.structure.dungeon;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.collections.weight.WeightedList;
import chylex.hee.world.structure.IStructureGenerator;
import chylex.hee.world.structure.dungeon.StructureDungeonPiece.Connection;
import chylex.hee.world.structure.util.BoundingBox;

public abstract class StructureDungeonGenerator implements IStructureGenerator{
	protected final StructureDungeon dungeon;
	protected final WeightedList<StructureDungeonPieceInst> generated;
	
	public StructureDungeonGenerator(StructureDungeon dungeon){
		this.dungeon = dungeon;
		this.generated = new WeightedList<StructureDungeonPieceInst>(dungeon.getPieceAmountRange().max);
	}

	/**
	 * Checks whether the area between two points is inside the structure and does not intersect any existing pieces.
	 */
	protected boolean canPlaceArea(Pos pos1, Pos pos2){
		final BoundingBox box = new BoundingBox(pos1,pos2);
		return box.isInside(dungeon.boundingBox) && !generated.stream().anyMatch(inst -> inst.boundingBox.intersects(box));
	}
	
	/**
	 * Aligns two pieces by their connections.
	 */
	protected Pos alignConnections(StructureDungeonPieceInst targetPiece, Connection targetConnection, Connection sourceConnection){
		Pos pos = targetPiece.boundingBox.getTopLeft();
		pos = pos.offset(targetConnection.offsetX,targetConnection.offsetY,targetConnection.offsetZ);
		pos = pos.offset(targetConnection.facing,1);
		pos = pos.offset(-sourceConnection.offsetX,-sourceConnection.offsetY,-sourceConnection.offsetZ);
		return pos;
	}
	
	@FunctionalInterface
	public static interface Constructor<T extends StructureDungeonGenerator>{
		T construct(StructureDungeon dungeon);
	}
}
