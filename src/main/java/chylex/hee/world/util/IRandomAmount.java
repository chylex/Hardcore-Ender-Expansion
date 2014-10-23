package chylex.hee.world.util;
import java.util.Random;

public interface IRandomAmount{
	int generate(Random rand, int totalAmount);
	
	public static final IRandomAmount
	
	linear = new IRandomAmount(){
		@Override
		public int generate(Random rand, int totalAmount){
			return rand.nextInt(totalAmount+1);
		}
	},
	
	preferSmaller = new IRandomAmount(){
		@Override
		public int generate(Random rand, int totalAmount){
			return (int)Math.round(rand.nextDouble()*rand.nextDouble()*totalAmount);
		}
	},
	
	gaussian = new IRandomAmount(){
		@Override
		public int generate(Random rand, int totalAmount){
			return (int)Math.round(Math.min(1D,Math.max(0D,rand.nextGaussian()))*totalAmount);
		}
	};
}
