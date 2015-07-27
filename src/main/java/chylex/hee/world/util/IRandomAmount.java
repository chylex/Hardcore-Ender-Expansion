package chylex.hee.world.util;
import gnu.trove.map.hash.TIntIntHashMap;
import java.util.Random;
import chylex.hee.system.commands.HeeDebugCommand.HeeTest;
import chylex.hee.system.logging.Log;
import chylex.hee.system.util.MathUtil;

@FunctionalInterface
public interface IRandomAmount{
	int generate(Random rand, int minAmount, int maxAmount);
	
	public static final IRandomAmount
	
	exact = (rand, min, max) -> {
		return min;
	},
	
	linear = (rand, min, max) -> {
		return min+rand.nextInt(max-min+1);
	},
	
	preferSmaller = (rand, min, max) -> {
		return min+MathUtil.floor(rand.nextDouble()*rand.nextDouble()*(1+max-min));
	},
	
	aroundCenter = (rand, min, max) -> {
		return MathUtil.clamp((int)Math.round(min+(max-min)*0.5D+(rand.nextDouble()-0.5D)*rand.nextDouble()*(1+max-min)),min,max);
	},
	
	gaussian = (rand, min, max) -> {
		return min+(int)Math.round(MathUtil.clamp(rand.nextGaussian()*0.5D,0D,1D)*(max-min));
	};
	
	public static final HeeTest $debugTest = new HeeTest(){
		@Override
		public void run(String...args){
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
