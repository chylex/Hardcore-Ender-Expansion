package chylex.hee.system.collections;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public final class BitStream{
	public static IntStream forInt(int bitSet){
		IntStream.Builder build = IntStream.builder();
		final int highest = Integer.bitCount(Integer.highestOneBit(bitSet)-1);
		
		for(int bit = 0; bit <= highest; bit++){
			if ((bitSet&(1<<bit)) != 0)build.accept(bit);
		}
		
		return build.build();
	}
	
	public static LongStream forLong(long bitSet){
		LongStream.Builder build = LongStream.builder();
		final int highest = Long.bitCount(Long.highestOneBit(bitSet)-1L);
		
		for(int bit = 0; bit <= highest; bit++){
			if ((bitSet&(1L<<bit)) != 0)build.accept(bit);
		}
		
		return build.build();
	}
}
