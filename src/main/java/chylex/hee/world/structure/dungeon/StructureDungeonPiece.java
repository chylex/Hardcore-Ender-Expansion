package chylex.hee.world.structure.dungeon;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.util.IBlockPicker;
import chylex.hee.world.util.Size;
import com.google.common.collect.ImmutableList;

public abstract class StructureDungeonPiece{
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
		connections.add(new Connection(facing,offsetX,offsetY,offsetZ,canConnect));
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
	
	public abstract void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, final int x, final int y, final int z);
	
	public final class Connection{
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
	
	protected static final void placeBlock(StructureWorld world, Random rand, IBlockPicker picker, int x, int y, int z){
		world.setBlock(x,y,z,picker.pick(rand));
	}
	
	protected static final void placeCube(StructureWorld world, Random rand, IBlockPicker picker, int x1, int y1, int z1, int x2, int y2, int z2){
		int xMin = Math.min(x1,x2), xMax = Math.max(x1,x2);
		int yMin = Math.min(y1,y2), yMax = Math.max(y1,y2);
		int zMin = Math.min(z1,z2), zMax = Math.max(z1,z2);
		
		for(int x = xMin; x <= xMax; x++){
			for(int y = yMin; y <= yMax; y++){
				for(int z = zMin; z <= zMax; z++){
					world.setBlock(x,y,z,picker.pick(rand));
				}
			}
		}
	}
	
	protected static final void placeOutline(StructureWorld world, Random rand, IBlockPicker picker, int x1, int y1, int z1, int x2, int y2, int z2, int insideThickness){
		int xMin = Math.min(x1,x2), xMax = Math.max(x1,x2);
		int zMin = Math.min(z1,z2), zMax = Math.max(z1,z2);
		
		for(int y = Math.min(y1,y2), yMax = Math.max(y1,y2); y <= yMax; y++){
			for(int level = 0; level < insideThickness; level++){
				for(int x = xMin+level; x <= xMax-level; x++){
					world.setBlock(x,y,zMin+level,picker.pick(rand));
					world.setBlock(x,y,zMax-level,picker.pick(rand));
				}
				
				for(int z = zMin+1+level; z <= zMax-1-level; z++){
					world.setBlock(xMin+level,y,z,picker.pick(rand));
					world.setBlock(xMax-level,y,z,picker.pick(rand));
				}
			}
		}
	}
	
	protected static final void placeLine(StructureWorld world, Random rand, IBlockPicker picker, int x1, int y1, int z1, int x2, int y2, int z2){
		if (x1 == x2 || z1 == z2){
			for(int y = Math.min(y1,y2), yMax = Math.max(y1,y2); y <= yMax; y++){
				if (x1 == x2){
					for(int z = Math.min(z1,z2), zMax = Math.max(z1,z2); z <= zMax; z++)world.setBlock(x1,y,z,picker.pick(rand));
				}
				else{
					for(int x = Math.min(x1,x2), xMax = Math.max(x1,x2); x <= xMax; x++)world.setBlock(x,y,z1,picker.pick(rand));
				}
			}
		}
		else throw new IllegalArgumentException("Lines can only be generated on one axis: "+x1+","+z1+" - "+x2+","+z2);
	}
	
	protected static final void placeWalls(StructureWorld world, Random rand, IBlockPicker picker, int x1, int y1, int z1, int x2, int y2, int z2){
		for(int y = Math.min(y1,y2), yMax = Math.max(y1,y2); y <= yMax; y++){
			for(int x = Math.min(x1,x2), xMax = Math.max(x1,x2); x <= xMax; x++){
				world.setBlock(x,y,z1,picker.pick(rand));
				world.setBlock(x,y,z2,picker.pick(rand));
			}
			
			for(int z = Math.min(z1,z2)+1, zMax = Math.max(z1,z2)-1; z <= zMax; z++){
				world.setBlock(x1,y,z,picker.pick(rand));
				world.setBlock(x2,y,z,picker.pick(rand));
			}
		}
	}
}
