package chylex.hee.world.structure.island.feature.ravageddungeon;
import java.util.Random;

public enum DungeonElementType{
	EMPTY('#'), ENTRANCE('E'), HALLWAY(' '), ROOM('+'), DESCEND('V'), END('X');
	
	public final char c;
	
	DungeonElementType(char c){
		this.c = c;
	}
	
	static enum RoomShape{
		LONG, TURN, BLOCK;
		
		public static RoomShape random(Random rand){
			int n = rand.nextInt(6);
			
			if (n == 0)return TURN;
			else if (n == 1 || n == 2)return BLOCK;
			else return LONG;
		}
	}
}
