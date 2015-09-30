package chylex.hee.world.util;
import net.minecraft.util.AxisAlignedBB;
import chylex.hee.system.abstractions.Pos;

/**
 * Integer bounding box which includes blocks between two points, including the full block on the edge.<br>
 * <br>
 * [ 0,0,0 -> 0,0,0 ] contains a single block, and the decimal bounding box includes coords from 0 to 1<br>
 * [ 0,0,0 -> 1,1,1 ] contains 8 blocks in the 2x2x2 area<br>
 * <br>
 * [ 0,0,0 -> 1,1,1 ] and [ 0,1,0 -> 1,2,1 ] intersect<br>
 * [ 0,0,0 -> 1,1,1 ] and [ 0,2,0 -> 1,3,1 ] do not intersect
 */
public class BoundingBox{
	public final int x1, y1, z1, x2, y2, z2;
	
	public BoundingBox(Pos loc1, Pos loc2){
		x1 = Math.min(loc1.getX(),loc2.getX());
		y1 = Math.min(loc1.getY(),loc2.getY());
		z1 = Math.min(loc1.getZ(),loc2.getZ());
		x2 = Math.max(loc1.getX(),loc2.getX());
		y2 = Math.max(loc1.getY(),loc2.getY());
		z2 = Math.max(loc1.getZ(),loc2.getZ());
	}
	
	public boolean intersects(BoundingBox bb){
		if (bb.x2 < x1 || bb.y2 < y1 || bb.z2 < z1)return false;
		if (bb.x1 > x2 || bb.y1 > y2 || bb.z1 > z2)return false;
		return true;
	}
	
	public boolean isInside(BoundingBox bb){
		return x1 >= bb.x1 && y1 >= bb.y1 && z1 >= bb.z1 && x2 <= bb.x2 && y2 <= bb.y2 && z2 <= bb.z2;
	}
	
	public Pos getTopLeft(){
		return Pos.at(x1,y1,z1);
	}
	
	public Pos getBottomRight(){
		return Pos.at(x2,y2,z2);
	}
	
	public AxisAlignedBB toAABB(){
		return AxisAlignedBB.getBoundingBox(x1,y1,z1,x2+1D,y2+1D,z2+1D);
	}
	
	@Override
	public String toString(){
		return "["+x1+","+y1+","+z1+" -> "+x2+","+y2+","+z2+"]";
	}
}
