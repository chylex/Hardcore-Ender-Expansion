package chylex.hee.entity.fx;
import java.util.Random;
import net.minecraft.entity.Entity;
import chylex.hee.HardcoreEnderExpansion;

public class FXHelper{
	public static FXHelper create(String type){
		return new FXHelper(type);
	}
	
	private final String type;
	private double posX, posY, posZ, posRandX, posRandY, posRandZ;
	private double motionX, motionY, motionZ, motionRandX, motionRandY, motionRandZ;
	private float param = Float.MIN_VALUE;
	
	private Random tmpRand;
	
	private FXHelper(String type){
		this.type = type;
	}
	
	public FXHelper pos(int x, int y, int z){
		return pos(x+0.5D,y+0.5D,z+0.5D);
	}
	
	public FXHelper pos(int x, int y, int z, double fluctuation){
		return pos(x+0.5D,y+0.5D,z+0.5D,fluctuation);
	}
	
	public FXHelper pos(double x, double y, double z){
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		return this;
	}
	
	public FXHelper pos(double x, double y, double z, double fluctuation){
		this.posRandX = this.posRandY = this.posRandZ = fluctuation;
		return pos(x,y,z);
	}
	
	public FXHelper pos(Entity entity){
		return pos(entity.posX,entity.posY,entity.posZ);
	}

	public FXHelper pos(Entity entity, double fluctuation){
		return pos(entity.posX,entity.posY,entity.posZ,fluctuation);
	}
	
	public FXHelper posRand(double fluctuationX, double fluctuationY, double fluctuationZ){
		this.posRandX = fluctuationX;
		this.posRandY = fluctuationY;
		this.posRandZ = fluctuationZ;
		return this;
	}
	
	public FXHelper motion(double x, double y, double z){
		this.motionX = x;
		this.motionY = y;
		this.motionZ = z;
		return this;
	}
	
	public FXHelper motion(double x, double y, double z, double fluctuation){
		this.motionRandX = this.motionRandY = this.motionRandZ = fluctuation;
		return this.motion(x,y,z);
	}
	
	public FXHelper motionRand(double fluctuation){
		this.motionRandX = this.motionRandY = this.motionRandZ = fluctuation;
		return this;
	}
	
	public FXHelper motionRand(double fluctuationX, double fluctuationY, double fluctuationZ){
		this.motionRandX = fluctuationX;
		this.motionRandY = fluctuationY;
		this.motionRandZ = fluctuationZ;
		return this;
	}
	
	public FXHelper param(float param){
		this.param = param;
		return this;
	}
	
	public void spawn(Random rand, int amount){
		this.tmpRand = rand;
		
		for(int a = 0; a < amount; a++){
			if (param == Float.MIN_VALUE)HardcoreEnderExpansion.fx.global(type,posX+rnd(posRandX),posY+rnd(posRandY),posZ+rnd(posRandZ),motionX+rnd(motionRandX),motionY+rnd(motionRandY),motionZ+rnd(motionRandZ));
			else HardcoreEnderExpansion.fx.global(type,posX+rnd(posRandX),posY+rnd(posRandY),posZ+rnd(posRandZ),motionX+rnd(motionRandX),motionY+rnd(motionRandY),motionZ+rnd(motionRandZ),param);
		}
		
		this.tmpRand = null;
	}
	
	private double rnd(double max){
		return max == 0D ? 0D : (tmpRand.nextDouble()-0.5D)*2D*max;
	}
}
