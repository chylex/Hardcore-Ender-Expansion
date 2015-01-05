package chylex.hee.world.util;
import java.util.Random;

public final class WorldGenChance{
	public static final WorldGenChance2
	
		linear2Incr = new WorldGenChance2(){
			@Override public double calculate(double dist, double startDist, double endDist){
				return dist < startDist ? 0D : dist > endDist ? 1D : (dist-startDist)/(endDist-startDist);
			}
		},
		
		linear2Decr = new WorldGenChance2(){
			@Override public double calculate(double dist, double startDist, double endDist){
				return dist < startDist || dist > endDist ? 0D : 1D-(dist-startDist)/(endDist-startDist);
			}
		},
		
		cubic2Incr = new WorldGenChance2(){
			@Override public double calculate(double dist, double startDist, double endDist){
				return dist < startDist ? 0D : dist > endDist ? 1D : Math.pow((dist-startDist)/(endDist-startDist),3);
			}
		},
		
		cubic2Decr = new WorldGenChance2(){
			@Override public double calculate(double dist, double startDist, double endDist){
				return dist < startDist || dist > endDist ? 0D : 1D-Math.pow((dist-startDist)/(endDist-startDist),3);
			}
		};
	
	public static final WorldGenChance3
	
		linear3IncrDecr = new WorldGenChance3(){
			@Override public double calculate(double dist, double startDist, double middleDist, double endDist){
				return dist < startDist || dist > endDist ? 0D : dist < middleDist ? linear2Incr.calculate(dist,startDist,middleDist) : linear2Decr.calculate(dist,middleDist,endDist);
			}
		};
	
	public static boolean checkChance(double chance, Random rand){
		return chance == 0D ? false : chance == 1D ? true : rand.nextDouble() < chance;
	}
	
	private WorldGenChance(){}
	
	public static abstract class WorldGenChance2{
		private WorldGenChance2(){}
		public abstract double calculate(double dist, double startDist, double endDist);
	}
	
	public static abstract class WorldGenChance3{
		private WorldGenChance3(){}
		public abstract double calculate(double dist, double startDist, double middleDist, double endDist);
	}
}
