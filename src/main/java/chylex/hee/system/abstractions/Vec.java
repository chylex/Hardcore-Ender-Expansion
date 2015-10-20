package chylex.hee.system.abstractions;
import java.util.Random;
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
	
	public static Vec xzRandom(Random rand){
		return new Vec(rand.nextDouble()-0.5D,0D,rand.nextDouble()-0.5D).normalized();
	}
	
	public static Vec xyzRandom(Random rand){
		return new Vec(rand.nextDouble()-0.5D,rand.nextDouble()-0.5D,rand.nextDouble()-0.5D).normalized();
	}
	
	public double x, y, z;
	
	private Vec(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public double length(){
		return Math.sqrt(x*x+y*y+z*z);
	}
	
	public Vec normalized(){
		double len = length();
		return MathUtil.floatEquals((float)len,0F) ? Vec.zero() : Vec.xyz(x/len,y/len,z/len);
	}
	
	public Vec offset(double offX, double offY, double offZ){
		return Vec.xyz(x+offX,y+offY,z+offZ);
	}
	
	public Pos toPos(){
		return Pos.at(x,y,z);
	}
}
