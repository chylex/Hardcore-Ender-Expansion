package chylex.hee.world.structure.dungeon;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.collections.WeightedList;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.structure.IStructureGenerator;
import chylex.hee.world.structure.StructureBase;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPiece.Connection;
import chylex.hee.world.structure.util.BoundingBox;
import chylex.hee.world.structure.util.Facing4;
import gnu.trove.impl.Constants;
import gnu.trove.map.hash.TObjectIntHashMap;

/**
 * Basic dungeon consisting of multiple cuboids connected to each other.
 */
public class StructureDungeon extends StructureBase{
	private final BoundingBox dungeonBoundingBox;
	private final WeightedList<StructureDungeonPiece> pieces = new WeightedList<>();
	private StructureDungeonPiece startingPiece;
	private int minPieces = 0, maxPieces = Integer.MAX_VALUE;
	
	public StructureDungeon(int radX, int sizeY, int radZ){
		super(radX,sizeY,radZ);
		dungeonBoundingBox = new BoundingBox(Pos.at(-radX,0,-radZ),Pos.at(radX,sizeY,radZ));
	}
	
	public void addPiece(StructureDungeonPiece piece){
		pieces.add(piece);
	}
	
	public void setStartingPiece(StructureDungeonPiece piece){
		this.startingPiece = piece;
	}
	
	public void setPieceAmount(int min, int max){
		this.minPieces = min;
		this.maxPieces = max;
	}
	
	private class Generator implements IStructureGenerator{
		private List<StructureDungeonPieceInst> generated = new ArrayList<>();
		private WeightedList<StructureDungeonPieceInst> weightedInstances = new WeightedList<>();
		private TObjectIntHashMap<StructureDungeonPiece> pieceCount = new TObjectIntHashMap<>(pieces.size(),Constants.DEFAULT_LOAD_FACTOR,0);
		
		/**
		 * Checks whether the area is inside the structure and does not intersect any existing pieces.
		 */
		private boolean canPlaceArea(Pos pos1, Pos pos2){
			BoundingBox box = new BoundingBox(pos1,pos2);
			if (!box.isInside(dungeonBoundingBox))return false;
			
			for(StructureDungeonPieceInst inst:generated){
				if (inst.boundingBox.intersects(box))return false;
			}
			
			return true;
		}
		
		/**
		 * Adds a new piece to the structure.
		 */
		private StructureDungeonPieceInst addPiece(StructureDungeonPiece piece, Pos position){
			StructureDungeonPieceInst inst = new StructureDungeonPieceInst(piece,position);
			generated.add(inst);
			if (inst.getWeight() != 0)weightedInstances.add(inst);
			return inst;
		}
		
		/**
		 * Returns a random piece or null if the search fails.
		 */
		private StructureDungeonPiece selectNextPiece(Random rand){
			StructureDungeonPiece nextPiece = pieces.getRandomItem(rand);
			return pieceCount.get(nextPiece) >= nextPiece.maxAmount ? null : nextPiece;
		}
		
		/**
		 * Cycles through available connections for specified facing in random order. Return true from the function to use the connection.
		 */
		private boolean cycleConnections(StructureDungeonPieceInst inst, Facing4 facing, Random rand, Function<Connection,Boolean> func){
			List<Connection> list = inst.findConnections(facing);
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
		
		private Pos alignConnections(StructureDungeonPieceInst targetPiece, Connection targetConnection, Connection sourceConnection){
			Pos pos = targetPiece.boundingBox.getTopLeft();
			pos = pos.offset(targetConnection.offsetX,targetConnection.offsetY,targetConnection.offsetZ);
			pos = pos.offset(targetConnection.facing.toEnumFacing(),1);
			pos = pos.offset(-sourceConnection.offsetX,sourceConnection.offsetY,-sourceConnection.offsetZ);
			return pos;
		}
		
		/**
		 * Generates the dungeon.
		 */
		@Override
		public boolean generate(StructureWorld world, Random rand){
			int targetAmount = minPieces+rand.nextInt(maxPieces-minPieces+1);
			
			StructureDungeonPiece startPiece = startingPiece == null ? pieces.getRandomItem(rand) : startingPiece;
			addPiece(startPiece,Pos.at(-startPiece.size.sizeX/2,sizeY/2-startPiece.size.sizeY/2,-startPiece.size.sizeZ));
			
			for(int cycleAttempt = 0, count; cycleAttempt < 1000; cycleAttempt++){
				StructureDungeonPiece nextPiece = selectNextPiece(rand);
				if (nextPiece == null)continue;
				
				Connection nextPieceConnection = nextPiece.getRandomConnection(rand);
				
				for(int placeAttempt = 0; placeAttempt < 10; placeAttempt++){
					StructureDungeonPieceInst connected = weightedInstances.getRandomItem(rand);
					
					if (cycleConnections(connected,nextPieceConnection.facing,rand,connection -> {
						Pos aligned = alignConnections(connected,connection,nextPieceConnection);
						
						if (canPlaceArea(aligned,aligned.offset(nextPiece.size.sizeX,nextPiece.size.sizeY,nextPiece.size.sizeZ))){
							addPiece(nextPiece,aligned);
							return true;
						}
						else return false;
					})){
						if (connected.getWeight() == 0)weightedInstances.remove(connected);
						if (generated.size() >= targetAmount)cycleAttempt = Integer.MAX_VALUE;
						break;
					}
				}
			}
			
			return MathUtil.inRangeIncl(generated.size(),minPieces,maxPieces);
		}
	}
	
	@Override
	protected IStructureGenerator createGenerator(){
		return new Generator();
	}
}
