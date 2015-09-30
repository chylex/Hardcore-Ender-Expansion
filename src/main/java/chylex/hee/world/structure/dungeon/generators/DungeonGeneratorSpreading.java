package chylex.hee.world.structure.dungeon.generators;
import gnu.trove.impl.Constants;
import gnu.trove.map.hash.TObjectIntHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.tuple.Pair;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.system.collections.CollectionUtil;
import chylex.hee.system.collections.weight.WeightedList;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeon;
import chylex.hee.world.structure.dungeon.StructureDungeonGenerator;
import chylex.hee.world.structure.dungeon.StructureDungeonPiece;
import chylex.hee.world.structure.dungeon.StructureDungeonPiece.Connection;
import chylex.hee.world.structure.dungeon.StructureDungeonPiece.IPieceType;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceArray;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.structure.util.BoundingBox;
import chylex.hee.world.structure.util.Range;
import chylex.hee.world.structure.util.Size;

/**
 * Generates a dungeon by choosing a room piece and then creating a path using corridor pieces. This generator cannot create dead ends.
 * Total piece range controls controls how many rooms will be generated, and the generator has to be setup with range of pieces that determines length of path between rooms.
 * Piece range of room pieces controls how many of each room will be chosen, and range of other/corridor pieces determines how many can spawn in one path.
 */
public class DungeonGeneratorSpreading extends StructureDungeonGenerator{
	public static interface ISpreadingGeneratorPieceType extends IPieceType{
		boolean isRoom();
	}
	
	protected static boolean isRoom(StructureDungeonPieceArray array){
		return ((ISpreadingGeneratorPieceType)array.type).isRoom();
	}
	
	protected final WeightedList<StructureDungeonPieceArray> rooms;
	protected final WeightedList<StructureDungeonPieceArray> corridors;
	protected final TObjectIntHashMap<StructureDungeonPieceArray> generatedRoomCount;
	protected Range piecesBetweenRooms = new Range(0,0);
	
	public DungeonGeneratorSpreading(StructureDungeon<?> dungeon){
		super(dungeon);
		
		WeightedList<StructureDungeonPieceArray> pieces = dungeon.pieces;
		if (!pieces.stream().allMatch(array -> array.type instanceof ISpreadingGeneratorPieceType))throw new IllegalArgumentException("Dungeon contains pieces that don't extend ISpreadingGeneratorPieceType!");
		
		this.rooms = new WeightedList<>(pieces.stream().filter(array -> isRoom(array)).toArray(StructureDungeonPieceArray[]::new));
		this.corridors = new WeightedList<>(pieces.stream().filter(array -> !isRoom(array)).toArray(StructureDungeonPieceArray[]::new));
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
				List<StructureDungeonPieceInst> nextPieces = generateCorridorList(startingPoint,connection,nextArray,rand);
				
				if (nextPieces != null && !nextPieces.isEmpty()){
					startingPoint.useConnection(connection);
					nextPieces.stream().forEach(this::addGeneratedPiece);
					break;
				}
			}
		}
		
		if (generated.stream().filter(inst -> ((ISpreadingGeneratorPieceType)inst.piece.type).isRoom()).count() != targetAmount)return false;
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
	 * If the list has a different size than the selected corridor amount, it returns null instead.
	 */
	private List<StructureDungeonPieceInst> generateCorridorList(StructureDungeonPieceInst startPiece, Connection startConnection, StructureDungeonPieceArray nextArray, Random rand){
		int corridorAmount = piecesBetweenRooms.random(rand);
		
		final List<StructureDungeonPieceInst> pieces = new ArrayList<>(corridorAmount);
		final WeightedList<StructureDungeonPieceArray> corridorsAvailable = new WeightedList<>(corridors);
		final TObjectIntHashMap<StructureDungeonPieceArray> corridorCount = new TObjectIntHashMap<>(corridorAmount);
		
		for(int index = 0; index < corridorAmount && corridorsAvailable.getTotalWeight() > 0; index++){
			final StructureDungeonPieceInst targetPieceInst = index == 0 ? startPiece : pieces.get(index-1);
			final Connection targetConnection = index == 0 ? startConnection : CollectionUtil.randomOrNull(targetPieceInst.findConnections(),rand);
			
			if (targetConnection == null)break;
			
			for(int attempt = 0; attempt < 201; attempt++){
				Pair<StructureDungeonPiece,Connection> nextPiece = findSuitablePiece(corridorsAvailable.getRandomItem(rand),targetConnection.facing,targetPieceInst.piece.type,rand);
				
				if (nextPiece != null && attempt < 200){
					final Pos aligned = alignConnections(targetPieceInst,targetConnection,nextPiece.getRight());
					final Size pieceSize = nextPiece.getLeft().size;
					
					if (canPlaceAreaExtended(aligned,aligned.offset(pieceSize.sizeX-1,pieceSize.sizeY-1,pieceSize.sizeZ-1),pieces)){
						StructureDungeonPieceInst inst = new StructureDungeonPieceInst(nextPiece.getLeft(),aligned);
						inst.useConnection(nextPiece.getRight());
						if (index > 0)targetPieceInst.useConnection(targetConnection);
						
						pieces.add(inst);
						break;
					}
				}
				
				if (attempt == 200)return null;
			}
		}
		
		if (pieces.size() == corridorAmount){
			final StructureDungeonPieceInst targetPieceInst = pieces.isEmpty() ? startPiece : pieces.get(pieces.size()-1);
			final Connection targetConnection = pieces.isEmpty() ? startConnection : CollectionUtil.randomOrNull(targetPieceInst.findConnections(),rand);
			final Pair<StructureDungeonPiece,Connection> finalRoom = findSuitablePiece(nextArray,targetConnection.facing,targetPieceInst.piece.type,rand);
			
			if (finalRoom != null){
				final Pos aligned = alignConnections(targetPieceInst,targetConnection,finalRoom.getRight());
				final Size pieceSize = finalRoom.getLeft().size;
				
				if (canPlaceAreaExtended(aligned,aligned.offset(pieceSize.sizeX-1,pieceSize.sizeY-1,pieceSize.sizeZ-1),pieces)){
					StructureDungeonPieceInst inst = new StructureDungeonPieceInst(finalRoom.getLeft(),aligned);
					inst.useConnection(finalRoom.getRight());
					pieces.get(pieces.size()-1).useConnection(targetConnection);
					pieces.add(inst);
				}
				
				return pieces;
			}
		}
		
		return null;
	}
	
	/**
	 * Tries to find a piece with suitable connection in a piece array. Returns the piece and one of the suitable connections, or null if no piece has a valid connection.
	 */
	private Pair<StructureDungeonPiece,Connection> findSuitablePiece(StructureDungeonPieceArray array, Facing4 targetFacing, IPieceType targetType, Random rand){
		for(StructureDungeonPiece piece:CollectionUtil.shuffled(array.toList(),rand)){
			List<Connection> possibleConnections = piece.findConnections(targetFacing,targetType);
			if (!possibleConnections.isEmpty())return Pair.of(piece,possibleConnections.get(rand.nextInt(possibleConnections.size())));
		}
		
		return null;
	}
	
	/**
	 * Checks whether the area between two points is inside the structure and does not intersect any existing pieces, or pieces in the provided list.
	 */
	private boolean canPlaceAreaExtended(Pos pos1, Pos pos2, List<StructureDungeonPieceInst> extendedCheck){
		final BoundingBox box = new BoundingBox(pos1,pos2);
		return canPlaceArea(pos1,pos2) && extendedCheck.stream().allMatch(inst -> !inst.boundingBox.intersects(box));
	}
}
