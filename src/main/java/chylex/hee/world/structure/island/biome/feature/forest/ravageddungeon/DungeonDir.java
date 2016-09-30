package chylex.hee.world.structure.island.biome.feature.forest.ravageddungeon;
import java.util.Random;

public enum DungeonDir{
	UP(0, -1), DOWN(0, 1), LEFT(-1, 0), RIGHT(1, 0);
	
	public static final DungeonDir[] values = values();
	
	public static DungeonDir random(Random rand){
		return values[rand.nextInt(4)];
	}
	
	public final byte addX, addY;
	
	private DungeonDir(int addX, int addY){
		this.addX = (byte)addX;
		this.addY = (byte)addY;
	}
	
	public DungeonDir reversed(){
		switch(this){
			case UP: return DOWN;
			case DOWN: return UP;
			case LEFT: return RIGHT;
			case RIGHT: return LEFT;
			default: throw new IllegalStateException("Invalid Dir");
		}
	}
	
	public DungeonDir rotatedLeft(){
		switch(this){
			case UP: return LEFT;
			case LEFT: return DOWN;
			case DOWN: return RIGHT;
			case RIGHT: return UP;
			default: throw new IllegalStateException("Invalid Dir");
		}
	}
	
	public DungeonDir rotatedRight(){
		switch(this){
			case UP: return RIGHT;
			case RIGHT: return DOWN;
			case DOWN: return LEFT;
			case LEFT: return UP;
			default: throw new IllegalStateException("Invalid Dir");
		}
	}
}