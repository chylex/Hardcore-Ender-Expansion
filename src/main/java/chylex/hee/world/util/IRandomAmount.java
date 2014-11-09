package chylex.hee.world.util;
import gnu.trove.map.hash.TIntIntHashMap;
import java.util.Random;
import chylex.hee.system.commands.HeeDebugCommand.HeeTest;
import chylex.hee.system.logging.Log;

public interface IRandomAmount{
	int generate(Random rand, int minAmount, int maxAmount);
	
	public static final IRandomAmount
	
	exact = new IRandomAmount(){
		@Override
		public int generate(Random rand, int minAmount, int maxAmount){
			return minAmount;
		}
	},
	
	linear = new IRandomAmount(){
		@Override
		public int generate(Random rand, int minAmount, int maxAmount){
			return minAmount+rand.nextInt(maxAmount-minAmount+1);
		}
	},
	
	preferSmaller = new IRandomAmount(){
		@Override
		public int generate(Random rand, int minAmount, int maxAmount){
			return minAmount+(int)Math.floor(rand.nextDouble()*rand.nextDouble()*(1+maxAmount-minAmount));
		}
	},
	
	aroundCenter = new IRandomAmount(){
		@Override
		public int generate(Random rand, int minAmount, int maxAmount){
			return Math.min(maxAmount,Math.max(minAmount,(int)Math.round(minAmount+(maxAmount-minAmount)*0.5D+(rand.nextDouble()-0.5D)*rand.nextDouble()*(1+maxAmount-minAmount))));
		}
	},
	
	gaussian = new IRandomAmount(){
		@Override
		public int generate(Random rand, int minAmount, int maxAmount){
			return minAmount+(int)Math.round(Math.min(1D,Math.max(0D,rand.nextGaussian()*0.5D))*(maxAmount-minAmount));
		}
	};
	
	public static final HeeTest $debugTest = new HeeTest(){
		@Override
		public void run(){
			Random rand = new Random();
			int index = 0;
			
			IRandomAmount[] algos = new IRandomAmount[]{
				linear, preferSmaller, aroundCenter, gaussian
			};
			
			String[] algoNames = new String[]{
				"linear", "preferSmaller", "aroundCenter", "gaussian"
			};
			
			TIntIntHashMap map = new TIntIntHashMap();
			
			for(IRandomAmount algo:algos){
				map.clear();
				
				for(int a = 0; a < 10000; a++){
					int val = algo.generate(rand,1,10);
					map.adjustOrPutValue(val,1,1);
				}
				
				Log.debug("== Algorithm - "+algoNames[index]+" ==");
				for(int a = -1; a < 11; a++)Log.debug("$0 ... $1",a+1,map.get(a+1));
				
				++index;
			}
		}
	};
}
