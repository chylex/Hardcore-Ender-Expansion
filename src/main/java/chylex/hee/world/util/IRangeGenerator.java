package chylex.hee.world.util;
import java.util.Random;

public interface IRangeGenerator{
	int next(Random rand);
	
	public static final class RangeGenerator implements IRangeGenerator{
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
		
		@Override
		public int next(Random rand){
			return distribution.generate(rand,range.min,range.max);
		}
	}
}
