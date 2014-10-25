package chylex.hee.world.util;
import java.util.Random;

public interface IRandomAmount{
	int generate(Random rand, int minAmount, int maxAmount);
	
	public static final IRandomAmount
	
	linear = new IRandomAmount(){
		@Override
		public int generate(Random rand, int minAmount, int maxAmount){
			return minAmount+rand.nextInt(maxAmount-minAmount+1);
		}
	},
	
	preferSmaller = new IRandomAmount(){
		@Override
		public int generate(Random rand, int minAmount, int maxAmount){
			return minAmount+(int)Math.round(rand.nextDouble()*rand.nextDouble()*(maxAmount-minAmount));
		}
	},
	
	gaussian = new IRandomAmount(){
		@Override
		public int generate(Random rand, int minAmount, int maxAmount){
			return minAmount+(int)Math.round(Math.min(1D,Math.max(0D,rand.nextGaussian()))*(maxAmount-minAmount));
		}
	};
}
