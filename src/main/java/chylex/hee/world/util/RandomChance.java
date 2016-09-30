package chylex.hee.world.util;
import java.util.Random;

public final class RandomChance{
	public static final RandomChance2
	
		linear2Incr = new RandomChance2(){
			@Override public double calculate(double dist, double startDist, double endDist){
				return dist < startDist ? 0D : dist > endDist ? 1D : (dist-startDist)/(endDist-startDist);
			}
		},
		
		linear2Decr = new RandomChance2(){
			@Override public double calculate(double dist, double startDist, double endDist){
				return dist < startDist || dist > endDist ? 0D : 1D-(dist-startDist)/(endDist-startDist);
			}
		},
		
		cubic2Incr = new RandomChance2(){
			@Override public double calculate(double dist, double startDist, double endDist){
				return dist < startDist ? 0D : dist > endDist ? 1D : Math.pow((dist-startDist)/(endDist-startDist), 3);
			}
		},
		
		cubic2Decr = new RandomChance2(){
			@Override public double calculate(double dist, double startDist, double endDist){
				return dist < startDist || dist > endDist ? 0D : 1D-Math.pow((dist-startDist)/(endDist-startDist), 3);
			}
		};
	
	public static final RandomChance3
	
		linear3IncrDecr = new RandomChance3(){
			@Override public double calculate(double dist, double startDist, double middleDist, double endDist){
				return dist < startDist || dist > endDist ? 0D : dist < middleDist ? linear2Incr.calculate(dist, startDist, middleDist) : linear2Decr.calculate(dist, middleDist, endDist);
			}
		};
	
	public static boolean checkChance(double chance, Random rand){
		return chance == 0D ? false : chance == 1D ? true : rand.nextDouble() < chance;
	}
	
	private RandomChance(){}
	
	public static abstract class RandomChance2{
		private RandomChance2(){}
		public abstract double calculate(double dist, double startDist, double endDist);
	}
	
	public static abstract class RandomChance3{
		private RandomChance3(){}
		public abstract double calculate(double dist, double startDist, double middleDist, double endDist);
	}
}
