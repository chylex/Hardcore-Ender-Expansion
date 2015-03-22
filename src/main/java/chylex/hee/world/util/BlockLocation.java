package chylex.hee.world.util;
import net.minecraft.util.AxisAlignedBB;

public class BlockLocation{
	public static AxisAlignedBB getBoundingBox(BlockLocation loc1, BlockLocation loc2){
		return AxisAlignedBB.getBoundingBox(Math.min(loc1.x,loc2.x),loc1.y,Math.min(loc1.z,loc2.z),Math.max(loc1.x,loc2.x),loc2.y,Math.max(loc1.z,loc2.z));
	}
	
	public final int x, y, z;
	
	public BlockLocation(int x, int y, int z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public boolean equals(Object o){
		if (o instanceof BlockLocation){
			BlockLocation loc = (BlockLocation)o;
			return x == loc.x && y == loc.y && z == loc.z;
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		return y+31*x+961*z;
	}
	
	@Override
	public String toString(){
		return new StringBuilder().append("{ ").append(x).append(", ").append(y).append(", ").append(z).append(" }").toString();
	}
}
