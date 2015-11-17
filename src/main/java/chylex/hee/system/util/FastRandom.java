package chylex.hee.system.util;
import java.util.Random;
import chylex.hee.game.commands.HeeDebugCommand.HeeTest;
import chylex.hee.system.logging.Stopwatch;

/**
 * Fast pseudorandom number generator, which uses the XORShift algorithm.
 */
public class FastRandom{
	private long last;
	
	public FastRandom(){
		this.last = System.currentTimeMillis();
	}
	
	public FastRandom(long seed){
		this.last = seed == 0 ? 1L : seed;
	}
	
	/**
	 * Magic.
	 */
	protected long next(){
		last ^= (last << 21);
	    last ^= (last >>> 35);
	    last ^= (last << 4);
	    return last-1; // allows 0 to be returned
	}
	
	/**
	 * Generates a number between 0 (inclusive) and fuckknowswhat using magic. Guessing it's Long.MAX_VALUE,
	 * because I'm using it for {@link #nextFloat()} and {@link #nextDouble()} and it hasn't broken yet.
	 */
	public long nextLong(){
	    return Math.abs(next());
	}
	
	/**
	 * Generates a number between 0 (inclusive) and {@code max} (exclusive) using magic.
	 */
	public int nextInt(int max){
		return Math.abs((int)next()%max);
	}
	
	/**
	 * Generates a number between 0 (inclusive) and 1 (exclusive) using magic.
	 */
	public float nextFloat(){
		return (float)Math.abs((double)next()/Long.MAX_VALUE);
	}
	
	/**
	 * Generates a number between 0 (inclusive) and 1 (exclusive) using magic.
	 */
	public double nextDouble(){
		return Math.abs((double)next()/Long.MAX_VALUE);
	}
	
	/**
	 * Performance testing. Can be ran multiple times using a command, so I didn't bother making it better.
	 */
	public static final HeeTest $debugTest = new HeeTest(){
		@Override
		public void run(String...args){
			final int cycles = 5000000;
			
			FastRandom fast = new FastRandom();
			Random slow = new Random();
			
			Stopwatch.time("nextInt - fast");
			for(int a = 0; a < cycles; a++)fast.nextInt(Integer.MAX_VALUE);
			Stopwatch.finish("nextInt - fast");
			Stopwatch.time("nextInt - slow");
			for(int a = 0; a < cycles; a++)slow.nextInt(Integer.MAX_VALUE);
			Stopwatch.finish("nextInt - slow");
			
			Stopwatch.time("nextFloat - fast");
			for(int a = 0; a < cycles; a++)fast.nextFloat();
			Stopwatch.finish("nextFloat - fast");
			Stopwatch.time("nextFloat - slow");
			for(int a = 0; a < cycles; a++)slow.nextFloat();
			Stopwatch.finish("nextFloat - slow");
			
			Stopwatch.time("nextDouble - fast");
			for(int a = 0; a < cycles; a++)fast.nextDouble();
			Stopwatch.finish("nextDouble - fast");
			Stopwatch.time("nextDouble - slow");
			for(int a = 0; a < cycles; a++)slow.nextDouble();
			Stopwatch.finish("nextDouble - slow");
			
			Stopwatch.time("nextLong - fast");
			for(int a = 0; a < cycles; a++)fast.nextLong();
			Stopwatch.finish("nextLong - fast");
			Stopwatch.time("nextLong - slow");
			for(int a = 0; a < cycles; a++)slow.nextLong();
			Stopwatch.finish("nextLong - slow");
		}
	};
}
