package chylex.hee.system.util;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class Vec3M{
	public double x, y, z;
	
	public Vec3M(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vec3M(Vec3 vec){
		this.x = vec.xCoord;
		this.y = vec.yCoord;
		this.z = vec.zCoord;
	}
	
	public Vec3M(BlockPos pos){
		this.x = pos.getX();
		this.y = pos.getY();
		this.z = pos.getZ();
	}
	
	public Vec3M copy(){
		return new Vec3M(x,y,z);
	}
	
	public Vec3 toVec3(){
		return new Vec3(x,y,z);
	}
	
	public Vec3M add(double x, double y, double z){
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}
	
	public Vec3M normalize(){
		double len = MathUtil.distance(x,y,z);
		
		if (len == 0D)x = y = z = 0D;
		else{
			x /= len;
			y /= len;
			z /= len;
		}
		
		return this;
	}
	
	public double dotProduct(Vec3M vec){
		return x*vec.x+y*vec.y+z*vec.z;
	}
	
	public Vec3M crossProduct(Vec3M vec){
		return new Vec3M(y*vec.z-z*vec.y,z*vec.x-x*vec.z,x*vec.y-y*vec.x);
	}
}
