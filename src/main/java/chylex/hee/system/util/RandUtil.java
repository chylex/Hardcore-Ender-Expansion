package chylex.hee.system.util;
import java.util.Random;

public final class RandUtil{
	public static int percentOf(Random rand, int value, float minPercentage, float maxPercentage){
		return MathUtil.floor(value*(minPercentage+rand.nextFloat()*(maxPercentage-minPercentage)));
	}
	
	public static <T> T anyOf(Random rand, T[] array){
		return array.length == 0 ? null : array[rand.nextInt(array.length)];
	}
	
	public static int anyOf(Random rand, int[] array){
		return array.length == 0 ? null : array[rand.nextInt(array.length)];
	}
	
	public static short anyOf(Random rand, short[] array){
		return array.length == 0 ? null : array[rand.nextInt(array.length)];
	}
	
	private RandUtil(){}
}
