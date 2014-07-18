package chylex.hee.world.structure.util;

public class Offsets{
	public final int x,y,z;
	public final boolean isInsideBB;
	
	public Offsets(int x, int y, int z, boolean isInsideBB){
		this.x = x;
		this.y = y;
		this.z = z;
		this.isInsideBB = isInsideBB;
	}
}
