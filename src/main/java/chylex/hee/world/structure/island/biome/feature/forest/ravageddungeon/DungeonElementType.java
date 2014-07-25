package chylex.hee.world.structure.island.biome.feature.forest.ravageddungeon;
import java.util.Random;

public enum DungeonElementType{
	EMPTY('#'), ENTRANCE('E'), HALLWAY(' '), ROOM('+'), DESCEND('V'), DESCENDBOTTOM('T'), END('X');
	
	public final char c;
	
	DungeonElementType(char c){
		this.c = c;
	}
	
	static enum RoomCombo{
		LONG, TURN, BLOCK;
		
		public static RoomCombo random(Random rand, int layer){
			int n = rand.nextInt(6);
			
			if (n == 0)return TURN;
			else if (n == 1 || n == 2)return BLOCK;
			else return LONG;
		}
	}
}
