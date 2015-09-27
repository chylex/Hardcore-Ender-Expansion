package chylex.hee.world.structure.dungeon.generators;
import gnu.trove.impl.Constants;
import gnu.trove.map.hash.TObjectIntHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.collections.weight.WeightedList;
import chylex.hee.system.util.CollectionUtil;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeon;
import chylex.hee.world.structure.dungeon.StructureDungeonGenerator;
import chylex.hee.world.structure.dungeon.StructureDungeonPiece;
import chylex.hee.world.structure.dungeon.StructureDungeonPiece.Connection;
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
		int targetAmount = dungeon.getPieceAmountRange().random(rand);
		
		generateStartPiece(rand);
		
		for(int room = 1; room < targetAmount; room++){ // start piece counts as 1 room
			StructureDungeonPieceArray nextArray = selectNextPiece(rand);
			StructureDungeonPieceInst startingPoint = generated.getRandomItem(rand);
			if (nextArray == null || startingPoint == null)break;
			
			for(Connection connection:CollectionUtil.shuffled(startingPoint.findConnections(),rand)){
				List<StructureDungeonPieceInst> nextPieces = generateCorridorList(startingPoint,connection,nextArray,piecesBetweenRooms.random(rand));
				
				if (nextPieces != null && !nextPieces.isEmpty()){
					startingPoint.useConnection(connection);
					nextPieces.stream().forEach(this::addGeneratedPiece);
					break;
				}
			}
		}
		
		if (!rooms.stream().allMatch(array -> array.amount.in(generatedRoomCount.get(array))))return false;
		
		for(StructureDungeonPieceInst pieceInst:generated){
			pieceInst.clearArea(world,rand);
			pieceInst.generatePiece(world,rand);
		}
		
		return true;
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
		return addGeneratedPiece(new StructureDungeonPieceInst(piece,position));
	}
	
	/**
	 * Add a piece instance to the structure and returns itself.
	 */
	private StructureDungeonPieceInst addGeneratedPiece(StructureDungeonPieceInst inst){
		generated.add(inst);
		StructureDungeonPieceArray parentArray = inst.piece.getParentArray();
		if (isRoom(parentArray) && generatedRoomCount.adjustOrPutValue(parentArray,1,1) >= parentArray.amount.max)rooms.remove(parentArray);
		return inst;
	}
	
	/**
	 * Generates a list of corridor pieces and the next room piece. It only uses connections on the generated pieces, not on the starting piece.
	 * The new pieces are not added to the generated piece list, that is up to the calling method after validating the list.
	 * If the list has a different size than the specified corridor amount, it returns null instead.
	 */
	private List<StructureDungeonPieceInst> generateCorridorList(StructureDungeonPieceInst startPiece, Connection startConnection, StructureDungeonPieceArray nextArray, int length){
		final List<StructureDungeonPieceInst> pieces = new ArrayList<>(length);
		final TObjectIntHashMap<StructureDungeonPieceArray> corridors = new TObjectIntHashMap<>(length);
		
		// TODO
		
		return pieces.size() == length ? pieces : null;
	}
}
