package chylex.hee.world.util;
import java.util.Random;

public class Range{
	public final int min;
	public final int max;
	
	public Range(int min, int max){
		this.min = min;
		this.max = max;
		if (min > max)throw new IllegalArgumentException("Min cannot be larger than Max!");
	}
	
	public boolean in(int value){
		return value >= min && value <= max;
	}
	
	public int random(Random rand){
		return min+rand.nextInt(max-min+1);
	}
}
