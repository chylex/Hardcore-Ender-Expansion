package chylex.hee.world.structure.dungeon.generators;
import gnu.trove.impl.Constants;
import gnu.trove.map.hash.TObjectIntHashMap;
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
import chylex.hee.world.structure.util.Range;

/**
 * Generates a dungeon by choosing a room piece and then creating a path using corridor pieces. This generator cannot create dead ends.
 * Piece range of room pieces controls how many will be chosen, and range of other/corridor pieces determines how many can spawn in a single path between rooms.
 */
public class DungeonGeneratorSpreading extends StructureDungeonGenerator{
	public static interface ISpreadingGeneratorPieceType extends IPieceType{
		boolean isRoom();
	}
	
	protected static boolean isRoom(StructureDungeonPieceArray array){
		return ((ISpreadingGeneratorPieceType)array.type).isRoom();
	}
	
	protected final WeightedList<StructureDungeonPieceArray> rooms;
	protected final WeightedList<StructureDungeonPieceArray> connections;
	protected final TObjectIntHashMap<StructureDungeonPieceArray> generatedRoomCount;
	protected Range piecesBetweenRooms = new Range(0,0);
	
	public DungeonGeneratorSpreading(StructureDungeon<?> dungeon){
		super(dungeon);
		
		WeightedList<StructureDungeonPieceArray> pieces = dungeon.pieces;
		if (!pieces.stream().allMatch(array -> array.type instanceof ISpreadingGeneratorPieceType))throw new IllegalArgumentException("Dungeon contains pieces that don't extend ISpreadingGeneratorPieceType!");
		
		this.rooms = new WeightedList<>(pieces.stream().filter(array -> isRoom(array)).toArray(StructureDungeonPieceArray[]::new));
		this.connections = new WeightedList<>(pieces.stream().filter(array -> !isRoom(array)).toArray(StructureDungeonPieceArray[]::new));
		this.generatedRoomCount = new TObjectIntHashMap<>(rooms.size(),Constants.DEFAULT_LOAD_FACTOR,0);
	}
	
	public void setPiecesBetweenRooms(int min, int max){
		this.piecesBetweenRooms = new Range(min,max);
	}

	@Override
	public boolean generate(StructureWorld world, Random rand){
		return false;
	}
	
	/**
	 * Returns a random piece out of the available piece list, or null if there are no pieces available.
	 */
	@Override
	protected StructureDungeonPieceArray selectNextPiece(Random rand){
		return rooms.getRandomItem(rand);
	}
	
	/**
	 * Adds a new piece to the structure.
	 */
	@Override
	protected StructureDungeonPieceInst addPiece(StructureDungeonPiece piece, Pos position){
		StructureDungeonPieceInst inst = new StructureDungeonPieceInst(piece,position);
		generated.add(inst);
		
		StructureDungeonPieceArray parentArray = piece.getParentArray();
		if (isRoom(parentArray) && generatedRoomCount.adjustOrPutValue(parentArray,1,1) >= parentArray.amount.max)rooms.remove(parentArray);
		return inst;
	}
}
