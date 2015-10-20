package chylex.hee.world.util;
import java.util.Random;

public final class RangeGenerator{
	private final Range range;
	private final RandomAmount distribution;
	
	public RangeGenerator(int min, int max, RandomAmount distribution){
		this.range = new Range(min,max);
		this.distribution = distribution;
	}
	
	public RangeGenerator(Range range, RandomAmount distribution){
		this.range = range;
		this.distribution = distribution;
	}
	
	public int next(Random rand){
		return distribution.generate(rand,range.min,range.max);
	}
}
