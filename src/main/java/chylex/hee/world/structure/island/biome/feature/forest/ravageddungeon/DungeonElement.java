package chylex.hee.world.structure.island.biome.feature.forest.ravageddungeon;

public final class DungeonElement{
	public final byte x, y, size;
	public final DungeonElementType type;
	private final boolean[] connections;
	
	public DungeonElement(int x, int y, DungeonElementType type){
		this.x = (byte)x;
		this.y = (byte)y;
		this.size = (byte)(type == DungeonElementType.ROOM ? 3 : 1);
		this.type = type;
		this.connections = new boolean[4];
	}
	
	public DungeonElement connect(DungeonDir dir){
		connections[dir.ordinal()] = true;
		return this;
	}
	
	public boolean checkConnection(DungeonDir dir){
		return connections[dir.ordinal()];
	}
	
	public boolean hasConnection(){
		for(int a = 0; a < 4; a++){
			if (connections[a])return true;
		}
		
		return false;
	}
	
	@Override
	public boolean equals(Object o){
		if (o instanceof DungeonElement){
			DungeonElement other = (DungeonElement)o;
			return x == other.x && y == other.y && type == other.type;
		}
		else return false;
	}
	
	@Override
	public int hashCode(){
		return x*117+y;
	}
}
