package chylex.hee.world.structure.island.biome.feature.forest.ravageddungeon;
import java.util.Random;
import chylex.hee.system.weight.IWeightProvider;
import chylex.hee.system.weight.WeightedList;

public enum DungeonElementType{
	EMPTY('#'), ENTRANCE('E'), HALLWAY(' '), ROOM('+'), DESCEND('V'), DESCENDBOTTOM('T'), END('X');
	
	public final char c;
	
	DungeonElementType(char c){
		this.c = c;
	}
	
	static enum RoomCombo implements IWeightProvider{
		SINGLE(10), LONG(5), TURN(3), BLOCK(1);
		
		private byte weight;
		
		RoomCombo(int weight){
			this.weight = (byte)weight;
		}

		@Override
		public int getWeight(){
			return weight;
		}
		
		private static WeightedList<RoomCombo> weights = new WeightedList<>(RoomCombo.values());
		
		public static void setRoomWeights(int singleRoom, int longRoom, int turnRoom, int blockRoom){
			SINGLE.weight = (byte)singleRoom;
			LONG.weight = (byte)longRoom;
			TURN.weight = (byte)turnRoom;
			BLOCK.weight = (byte)blockRoom;
			weights.recalculateWeight();
		}
		
		public static RoomCombo random(Random rand){
			return weights.getRandomItem(rand);
		}
	}
}
