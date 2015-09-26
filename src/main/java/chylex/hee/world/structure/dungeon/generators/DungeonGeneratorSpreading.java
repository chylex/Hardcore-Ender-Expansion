package chylex.hee.world.structure.dungeon.generators;
import java.util.Random;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.collections.weight.WeightedList;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeon;
import chylex.hee.world.structure.dungeon.StructureDungeonGenerator;
import chylex.hee.world.structure.dungeon.StructureDungeonPiece;
import chylex.hee.world.structure.dungeon.StructureDungeonPiece.IPieceType;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceArray;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;

/**
 * Generates a dungeon by choosing a room piece and then creating a path using corridor pieces. This generator cannot create dead ends.
 * Piece range of room pieces controls how many will be chosen, and range of other/corridor pieces determines how many can spawn in a single path between rooms.
 */
public class DungeonGeneratorSpreading extends StructureDungeonGenerator{
	public static interface ISpreadingGeneratorPieceType extends IPieceType{
		boolean isRoom();
	}
	
	protected final WeightedList<StructureDungeonPieceInst> generated;
	
	public DungeonGeneratorSpreading(StructureDungeon dungeon){
		super(dungeon);
		this.generated = new WeightedList<StructureDungeonPieceInst>(dungeon.getPieceAmountRange().max);
	}

	@Override
	public boolean generate(StructureWorld world, Random rand){
		return false;
	}
	
	@Override
	protected StructureDungeonPieceArray selectNextPiece(Random rand){
		return null;
	}

	@Override
	protected StructureDungeonPieceInst addPiece(StructureDungeonPiece piece, Pos position){
		return null;
	}
}
