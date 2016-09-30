package chylex.hee.world.structure.dungeon;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.world.structure.StructurePiece;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.util.BoundingBox;
import chylex.hee.world.util.Size;
import com.google.common.collect.ImmutableList;

public abstract class StructureDungeonPiece extends StructurePiece{
	public interface IPieceType{}
	
	@FunctionalInterface
	public interface IConnectWith{
		boolean check(IPieceType type);
	}
	
	public final IPieceType type;
	public final Size size;
	protected final List<Connection> connections = new ArrayList<>();
	private StructureDungeonPieceArray parentArray;
	
	/**
	 * Used as a shortcut for size.sizeXYZ-1 in worldgen.
	 */
	protected final int maxX, maxY, maxZ;
	
	public StructureDungeonPiece(IPieceType type, Size size){
		this.type = type;
		this.size = size;
		
		this.maxX = size.sizeX-1;
		this.maxY = size.sizeY-1;
		this.maxZ = size.sizeZ-1;
	}
	
	/**
	 * Adds connection facing outside of the piece bounding box.
	 */
	protected void addConnection(Facing4 facing, int offsetX, int offsetY, int offsetZ, IConnectWith canConnect){
		connections.add(new Connection(facing, offsetX, offsetY, offsetZ, canConnect));
	}
	
	public ImmutableList<Connection> getConnections(){
		return ImmutableList.copyOf(connections);
	}
	
	public int countConnections(){
		return connections.size();
	}
	
	public Connection getRandomConnection(Random rand){
		return connections.get(rand.nextInt(connections.size()));
	}
	
	public List<Connection> findConnections(Facing4 facing, IPieceType pieceType){
		return connections.stream().filter(connection -> connection.facing == facing.opposite() && connection.canConnectWith(pieceType)).collect(Collectors.toList());
	}
	
	public void setParentArray(StructureDungeonPieceArray array){
		if (this.parentArray == null)this.parentArray = array;
		else throw new IllegalArgumentException("Cannot set multiple parent arrays for structure dungeon piece!");
	}
	
	public StructureDungeonPieceArray getParentArray(){
		return parentArray;
	}
	
	/**
	 * Used by a piece instance to calculate the weight for choosing the next source piece. Must return 0 if there are no available connections. 
	 */
	public int calculateInstWeight(int availableConnections){
		return availableConnections;
	}
	
	public void clearArea(StructureWorld world, Random rand, final BoundingBox box){
		placeCube(world, rand, placeAir, box.x1, box.y1, box.z1, box.x2, box.y2, box.z2);
	}
	
	public abstract void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, final int x, final int y, final int z);
	
	public static final class Connection{
		public final Facing4 facing;
		public final byte offsetX, offsetY, offsetZ;
		private final IConnectWith canConnect;
		
		Connection(Facing4 facing, int offsetX, int offsetY, int offsetZ, IConnectWith canConnect){
			this.facing = facing;
			this.offsetX = (byte)offsetX;
			this.offsetY = (byte)offsetY;
			this.offsetZ = (byte)offsetZ;
			this.canConnect = canConnect;
		}
		
		public boolean canConnectWith(IPieceType type){
			return canConnect.check(type);
		}
	}
}
