package chylex.hee.world.structure.dungeon;
import gnu.trove.impl.Constants;
import gnu.trove.map.hash.TObjectIntHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import chylex.hee.system.abstractions.BlockInfo;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.collections.WeightedList;
import chylex.hee.world.structure.IStructureGenerator;
import chylex.hee.world.structure.StructureBase;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPiece.Connection;
import chylex.hee.world.structure.dungeon.StructureDungeonPiece.IType;
import chylex.hee.world.structure.util.BoundingBox;
import chylex.hee.world.structure.util.Facing4;
import chylex.hee.world.structure.util.Range;
import com.google.common.base.Objects;

/**
 * Basic dungeon consisting of multiple cuboids connected to each other.
 */
public class StructureDungeon extends StructureBase{
	private final BoundingBox dungeonBoundingBox;
	private final WeightedList<StructureDungeonPieceArray> pieces = new WeightedList<>();
	private StructureDungeonPiece startingPiece;
	private Range pieceAmount;
	
	public StructureDungeon(int radX, int sizeY, int radZ){
		super(radX,sizeY,radZ);
		dungeonBoundingBox = new BoundingBox(Pos.at(-radX,0,-radZ),Pos.at(radX,sizeY,radZ));
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
	
	public void setPieceAmount(int min, int max){
		this.pieceAmount = new Range(min,max);
	}
	
	private class Generator implements IStructureGenerator{
		private List<StructureDungeonPieceInst> generated = new ArrayList<>();
		private WeightedList<StructureDungeonPieceInst> weightedInstances = new WeightedList<>();
		private TObjectIntHashMap<StructureDungeonPieceArray> pieceCount = new TObjectIntHashMap<>(pieces.size(),Constants.DEFAULT_LOAD_FACTOR,0);
		
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
			pieceCount.adjustOrPutValue(piece.getParentArray(),1,1);
			return inst;
		}
		
		/**
		 * Returns a random piece or null if the search fails.
		 */
		private StructureDungeonPiece selectNextPiece(Random rand){
			StructureDungeonPieceArray nextPieceArray = pieces.getRandomItem(rand);
			return nextPieceArray != null && pieceCount.get(nextPieceArray) >= nextPieceArray.amount.max ? null : nextPieceArray.getRandomPiece(rand);
		}
		
		/**
		 * Cycles through available connections for specified facing and piece type in random order. Return true from the function to use the connection.
		 */
		private boolean cycleConnections(StructureDungeonPieceInst inst, Facing4 facing, IType type, Random rand, Function<Connection,Boolean> func){
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
			
			int targetAmount = pieceAmount.random(rand);
			
			StructureDungeonPiece startPiece = startingPiece == null ? pieces.getRandomItem(rand).getRandomPiece(rand) : startingPiece;
			StructureDungeonPieceInst startPieceInst = addPiece(startPiece,Pos.at(-startPiece.size.sizeX/2,sizeY/2-startPiece.size.sizeY/2,-startPiece.size.sizeZ));
			if (startPieceInst.getWeight() != 0)weightedInstances.add(startPieceInst);
			
			if (generated.size() < targetAmount){
				for(int cycleAttempt = 0, count; cycleAttempt < cycleAttempts; cycleAttempt++){
					StructureDungeonPiece nextPiece = selectNextPiece(rand); // TODO cycle through all pieces in the array maybe?
					if (nextPiece == null)continue;
					
					Connection nextPieceConnection = nextPiece.getRandomConnection(rand);
					
					for(int placeAttempt = 0; placeAttempt < placeAttempts; placeAttempt++){
						StructureDungeonPieceInst connected = Objects.firstNonNull(weightedInstances.getRandomItem(rand),startPieceInst);
						
						if (cycleConnections(connected,nextPieceConnection.facing,nextPiece.type,rand,connection -> {
							Pos aligned = alignConnections(connected,connection,nextPieceConnection);
							
							if (canPlaceArea(aligned,aligned.offset(nextPiece.size.sizeX-1,nextPiece.size.sizeY-1,nextPiece.size.sizeZ-1))){
								StructureDungeonPieceInst newInst = addPiece(nextPiece,aligned);
								newInst.useConnection(nextPieceConnection);
								if (newInst.getWeight() != 0)weightedInstances.add(newInst);
								return true;
							}
							else return false;
						})){
							if (connected.getWeight() == 0)weightedInstances.remove(connected);
							if (generated.size() >= targetAmount)cycleAttempt = Integer.MAX_VALUE-1;
							break;
						}
					}
				}
			}
			
			if (!pieceAmount.in(generated.size()))return false;
			
			for(StructureDungeonPieceArray piece:pieces){
				if (!piece.amount.in(pieceCount.get(piece)))return false;
			}
			
			for(StructureDungeonPieceInst pieceInst:generated){
				StructureDungeonPiece.placeCube(world,rand,random -> BlockInfo.air,pieceInst.boundingBox.x1,pieceInst.boundingBox.y1,pieceInst.boundingBox.z1,pieceInst.boundingBox.x2,pieceInst.boundingBox.y2,pieceInst.boundingBox.z2);
				pieceInst.piece.generate(pieceInst,world,rand,pieceInst.boundingBox.x1,pieceInst.boundingBox.y1,pieceInst.boundingBox.z1);
			}
			
			return true;
		}
	}
	
	@Override
	protected IStructureGenerator createGenerator(){
		return new Generator();
	}
}
