package chylex.hee.world.end;
import java.util.Random;
import java.util.function.ToIntFunction;

final class TerritorySpawnInfo{
	private final int height;
	private final ToIntFunction<Random> bottomYGenerator;
	
	TerritorySpawnInfo(int height, ToIntFunction<Random> bottomYGenerator){
		this.height = height;
		this.bottomYGenerator = bottomYGenerator;
	}
	
	TerritorySpawnInfo(int height, int bottomY){
		this.height = height;
		this.bottomYGenerator = rand -> bottomY;
	}
	
	int getHeight(){
		return height;
	}
	
	int getBottomY(Random rand){
		return bottomYGenerator.applyAsInt(rand);
	}
}
