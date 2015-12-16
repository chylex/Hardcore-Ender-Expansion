package chylex.hee.system.abstractions;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import chylex.hee.system.util.MathUtil;

public class Vec{
	public static Vec xyz(double x, double y, double z){
		return new Vec(x,y,z);
	}

	public static Vec xz(double x, double z){
		return new Vec(x,0D,z);
	}
	
	public static Vec zero(){
		return new Vec(0D,0D,0D);
	}
	
	public static Vec from(Vec3 vec3){
		return new Vec(vec3.xCoord,vec3.yCoord,vec3.zCoord);
	}
	
	public static Vec xzRandom(Random rand){
		return new Vec(rand.nextDouble()-0.5D,0D,rand.nextDouble()-0.5D).normalized();
	}
	
	public static Vec xyzRandom(Random rand){
		return new Vec(rand.nextDouble()-0.5D,rand.nextDouble()-0.5D,rand.nextDouble()-0.5D).normalized();
	}
	
	public static Vec between(Entity source, Entity target){
		return new Vec(target.posX-source.posX,target.posY-source.posY,target.posZ-source.posZ);
	}
	
	public static Vec pos(Entity entity){
		return new Vec(entity.posX,entity.posY,entity.posZ);
	}
	
	public static Vec look(EntityLivingBase entity){
		return from(entity.getLookVec());
	}
	
	public double x, y, z;
	
	private Vec(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	// Mutable
	
	public void moveBy(Vec vec){
		this.x += vec.x;
		this.y += vec.y;
		this.z += vec.z;
	}
	
	// Immutable
	
	public Vec copy(){
		return new Vec(x,y,z);
	}
	
	public Vec normalized(){
		double len = length();
		return MathUtil.floatEquals((float)len,0F) ? Vec.zero() : Vec.xyz(x/len,y/len,z/len);
	}
	
	public Vec offset(double offX, double offY, double offZ){
		return Vec.xyz(x+offX,y+offY,z+offZ);
	}
	
	public Vec offset(Vec byVec){
		return Vec.xyz(x+byVec.x,y+byVec.y,z+byVec.z);
	}
	
	public Vec offset(Vec byVec, double factor){
		return Vec.xyz(x+byVec.x*factor,y+byVec.y*factor,z+byVec.z*factor);
	}
	
	public Vec interpolated(Vec toVec, double progress){
		return Vec.xyz(x+(toVec.x-x)*progress,y+(toVec.y-y)*progress,z+(toVec.z-z)*progress);
	}
	
	public Vec multiplied(double factor){
		return Vec.xyz(x*factor,y*factor,z*factor);
	}
	
	// Calculations
	
	public double length(){
		return Math.sqrt(x*x+y*y+z*z);
	}
	
	public double distance(Vec vec){
		return MathUtil.distance(vec.x-x,vec.y-y,vec.z-z);
	}
	
	public double dotProduct(Vec vec){
		return vec.x*x+vec.y*y+vec.z*z;
	}
	
	// Conversions
	
	public Pos toPos(){
		return Pos.at(x,y,z);
	}
	
	public Vec3 toVec3(){
		return Vec3.createVectorHelper(x,y,z);
	}
	
	public AxisAlignedBB toAABB(){
		return AxisAlignedBB.getBoundingBox(x,y,z,x,y,z);
	}
	
	@Override
	public String toString(){
		return "{ "+x+", "+y+", "+z+" }";
	}
	
	// Equality
	
	@Override
	public boolean equals(Object obj){
		if (obj == this)return true;
		
		if (obj instanceof Vec){
			Vec vec = (Vec)obj;
			return vec.x == x && vec.y == y && vec.z == z;
		}
		
		return false;
	}
	
	@Override
	public int hashCode(){
		return (Double.hashCode(y)+Double.hashCode(z)*31)*31+Double.hashCode(x);
	}
}
