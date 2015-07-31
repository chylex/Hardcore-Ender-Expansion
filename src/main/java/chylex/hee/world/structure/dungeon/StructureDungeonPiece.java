package chylex.hee.world.structure.dungeon;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.google.common.collect.ImmutableList;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.collections.weight.IWeightProvider;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.util.Facing4;
import chylex.hee.world.structure.util.Size;

public abstract class StructureDungeonPiece implements IWeightProvider{
	public enum Type{ CORRIDOR, ROOM }
	public enum ConnectsWith { ANY, CORRIDOR, ROOM }
	
	public final int weight, minAmount, maxAmount;
	public final Size size;
	private final List<Connection> connections = new ArrayList<>();
	
	public StructureDungeonPiece(Size size){
		this.weight = 0;
		this.minAmount = this.maxAmount = 1;
		this.size = size;
	}
	
	public StructureDungeonPiece(int weight, int minAmount, int maxAmount, Size size){
		this.weight = weight;
		this.minAmount = minAmount;
		this.maxAmount = maxAmount;
		this.size = size;
	}
	
	protected void addConnection(Connection connection){
		connections.add(connection);
	}
	
	public ImmutableList<Connection> getConnections(){
		return ImmutableList.copyOf(connections);
	}
	
	public Connection getRandomConnection(Random rand){
		return connections.get(rand.nextInt(connections.size()));
	}
	
	public abstract void generate(StructureWorld world, Random rand, Pos topLeft);
	
	@Override
	public int getWeight(){
		return weight;
	}
	
	public static final class Connection{
		public final Facing4 facing;
		public final byte offsetX, offsetY, offsetZ;
		
		Connection(Facing4 facing, int offsetX, int offsetY, int offsetZ){
			this.facing = facing;
			this.offsetX = (byte)offsetX;
			this.offsetY = (byte)offsetY;
			this.offsetZ = (byte)offsetZ;
		}
	}
}
