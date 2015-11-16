package chylex.hee.system.util;

public class FastRandom{
	private long last;
	
	public FastRandom(){
		this.last = System.currentTimeMillis();
	}
	
	public FastRandom(long seed){
		this.last = seed == 0 ? 1L : seed;
	}
	
	public long nextLong(){
	    last ^= (last << 21);
	    last ^= (last >>> 35);
	    last ^= (last << 4);
	    return last-1; // allows 0 to be returned
	}
	
	public int nextInt(int max){
		return Math.abs((int)nextLong()%max);
	}
	
	public float nextFloat(){
		return (float)Math.abs((double)nextLong()/Long.MAX_VALUE);
	}
	
	public double nextDouble(){
		return Math.abs((double)nextLong()/Long.MAX_VALUE);
	}
	
	// TODO add a test
}
