package chylex.hee.world.structure.dungeon;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.system.collections.weight.IWeightProvider;
import chylex.hee.world.structure.IBlockPicker;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.util.Facing4;
import chylex.hee.world.structure.util.Size;
import com.google.common.collect.ImmutableList;

public abstract class StructureDungeonPiece implements IWeightProvider{
	public enum Type{ CORRIDOR, ROOM }
	
	public final Type type;
	public final int weight, minAmount, maxAmount;
	public final Size size;
	private final List<Connection> connections = new ArrayList<>();
	
	public StructureDungeonPiece(Type type, Size size){
		this.type = type;
		this.weight = 0;
		this.minAmount = this.maxAmount = 1;
		this.size = size;
	}
	
	public StructureDungeonPiece(Type type, int weight, int minAmount, int maxAmount, Size size){
		this.type = type;
		this.weight = weight;
		this.minAmount = minAmount;
		this.maxAmount = maxAmount;
		this.size = size;
	}
	
	protected void addConnection(Facing4 facing, int offsetX, int offsetY, int offsetZ){
		connections.add(new Connection(facing,offsetX,offsetY,offsetZ));
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
	
	public abstract void generate(StructureWorld world, Random rand, int x, int y, int z);
	
	@Override
	public int getWeight(){
		return weight;
	}
	
	public final class Connection{
		public final Facing4 facing;
		public final byte offsetX, offsetY, offsetZ;
		
		Connection(Facing4 facing, int offsetX, int offsetY, int offsetZ){
			this.facing = facing;
			this.offsetX = (byte)offsetX;
			this.offsetY = (byte)offsetY;
			this.offsetZ = (byte)offsetZ;
		}
		
		public boolean canConnectWith(Type type){
			return StructureDungeonPiece.this.type == Type.ROOM ? type == Type.CORRIDOR : true;
		}
	}
	
	protected static final void placeCube(StructureWorld world, Random rand, IBlockPicker picker, int x1, int y1, int z1, int x2, int y2, int z2){
		int xMin = Math.min(x1,x2), xMax = Math.max(x1,x2);
		int yMin = Math.min(y1,y2), yMax = Math.max(y1,y2);
		int zMin = Math.min(z1,z2), zMax = Math.max(z1,z2);
		PosMutable mutablePos = new PosMutable();
		
		for(int x = xMin; x <= xMax; x++){
			for(int y = yMin; y <= yMax; y++){
				for(int z = zMin; z <= zMax; z++){
					world.setBlock(x,y,z,picker.pick(rand));
				}
			}
		}
	}
	
	protected static final void placeWalls(StructureWorld world, Random rand, IBlockPicker picker, int x1, int y1, int z1, int x2, int y2, int z2){
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
		else{
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
}
