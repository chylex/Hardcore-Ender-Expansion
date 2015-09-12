package chylex.hee.world.structure.dungeon.generators;
import gnu.trove.impl.Constants;
import gnu.trove.map.hash.TObjectIntHashMap;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.system.collections.WeightedList;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeon;
import chylex.hee.world.structure.dungeon.StructureDungeonGenerator;
import chylex.hee.world.structure.dungeon.StructureDungeonPiece;
import chylex.hee.world.structure.dungeon.StructureDungeonPiece.Connection;
import chylex.hee.world.structure.dungeon.StructureDungeonPiece.IType;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceArray;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.structure.util.BoundingBox;

/**
 * Generates a dungeon by choosing a random existing piece and then trying to attach another random piece to it.
 */
public class DungeonGeneratorAttaching extends StructureDungeonGenerator{
	private WeightedList<StructureDungeonPieceInst> generated = new WeightedList<>();
	private TObjectIntHashMap<StructureDungeonPieceArray> generatedCount;
	
	public DungeonGeneratorAttaching(StructureDungeon dungeon){
		super(dungeon);
		this.generatedCount = new TObjectIntHashMap<>(dungeon.pieces.size(),Constants.DEFAULT_LOAD_FACTOR,0);
	}

	/**
	 * Checks whether the area is inside the structure and does not intersect any existing pieces.
	 */
	protected boolean canPlaceArea(Pos pos1, Pos pos2){
		BoundingBox box = new BoundingBox(pos1,pos2);
		if (!box.isInside(dungeon.boundingBox))return false;
		
		for(StructureDungeonPieceInst inst:generated){
			if (inst.boundingBox.intersects(box))return false;
		}
		
		return true;
	}
	
	/**
	 * Adds a new piece to the structure.
	 */
	protected StructureDungeonPieceInst addPiece(StructureDungeonPiece piece, Pos position){
		StructureDungeonPieceInst inst = new StructureDungeonPieceInst(piece,position);
		generated.add(inst);
		generatedCount.adjustOrPutValue(piece.getParentArray(),1,1);
		return inst;
	}
	
	/**
	 * Returns a random piece or null if the search fails.
	 */
	protected StructureDungeonPiece selectNextPiece(Random rand){
		StructureDungeonPieceArray nextPieceArray = dungeon.pieces.getRandomItem(rand);
		return nextPieceArray != null && generatedCount.get(nextPieceArray) >= nextPieceArray.amount.max ? null : nextPieceArray.getRandomPiece(rand);
	}
	
	/**
	 * Cycles through available connections for specified facing and piece type in random order. Return true from the function to use the connection.
	 */
	protected boolean cycleConnections(StructureDungeonPieceInst inst, Facing4 facing, IType type, Random rand, Function<Connection,Boolean> func){
		List<Connection> list = inst.findConnections(facing,type);
		if (list.isEmpty())return false;
		
		for(int index = list.size(); index > 0; index--){
			Connection connection = list.remove(rand.nextInt(index));
			
			if (func.apply(connection)){
				inst.useConnection(connection);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Aligns two pieces by their connections.
	 */
	private Pos alignConnections(StructureDungeonPieceInst targetPiece, Connection targetConnection, Connection sourceConnection){
		Pos pos = targetPiece.boundingBox.getTopLeft();
		pos = pos.offset(targetConnection.offsetX,targetConnection.offsetY,targetConnection.offsetZ);
		pos = pos.offset(targetConnection.facing,1);
		pos = pos.offset(-sourceConnection.offsetX,-sourceConnection.offsetY,-sourceConnection.offsetZ);
		return pos;
	}
	
	/**
	 * Generates the dungeon.
	 */
	@Override
	public boolean generate(StructureWorld world, Random rand){
		int cycleAttempts = 1000;
		int placeAttempts = 20;
		
		int targetAmount = dungeon.getPieceAmountRange().random(rand);
		
		StructureDungeonPiece startPiece = dungeon.getStartingPiece().orElseGet(() -> dungeon.pieces.getRandomItem(rand).getRandomPiece(rand));
		StructureDungeonPieceInst startPieceInst = addPiece(startPiece,Pos.at(-startPiece.size.sizeX/2,dungeon.boundingBox.y2/2-startPiece.size.sizeY/2,-startPiece.size.sizeZ));
		
		if (generated.size() < targetAmount && generated.getTotalWeight() > 0){
			for(int cycleAttempt = 0, count; cycleAttempt < cycleAttempts; cycleAttempt++){
				StructureDungeonPiece nextPiece = selectNextPiece(rand); // TODO cycle through all pieces in the array maybe?
				if (nextPiece == null)continue;
				
				Connection nextPieceConnection = nextPiece.getRandomConnection(rand);
				
				for(int placeAttempt = 0; placeAttempt < placeAttempts; placeAttempt++){
					StructureDungeonPieceInst connected = generated.tryGetRandomItem(rand).orElse(startPieceInst);
					
					if (cycleConnections(connected,nextPieceConnection.facing,nextPiece.type,rand,connection -> {
						Pos aligned = alignConnections(connected,connection,nextPieceConnection);
						
						if (canPlaceArea(aligned,aligned.offset(nextPiece.size.sizeX-1,nextPiece.size.sizeY-1,nextPiece.size.sizeZ-1))){
							StructureDungeonPieceInst newInst = addPiece(nextPiece,aligned);
							newInst.useConnection(nextPieceConnection);
							return true;
						}
						else return false;
					})){
						if (generated.size() >= targetAmount)cycleAttempt = Integer.MAX_VALUE-1;
						break;
					}
				}
			}
		}
		
		if (!dungeon.getPieceAmountRange().in(generated.size()))return false;
		
		for(StructureDungeonPieceArray piece:dungeon.pieces){
			if (!piece.amount.in(generatedCount.get(piece)))return false;
		}
		
		for(StructureDungeonPieceInst pieceInst:generated){
			pieceInst.clearArea(world,rand);
			pieceInst.generatePiece(world,rand);
		}
		
		return true;
	}
}
