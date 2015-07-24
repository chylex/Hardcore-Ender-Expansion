package chylex.hee.entity.fx;
import java.util.Random;
import net.minecraft.entity.Entity;
import chylex.hee.HardcoreEnderExpansion;

public class FXHelper{
	public static FXHelper create(String type){
		return new FXHelper(type);
	}
	
	private final String type;
	private double posX, posY, posZ, posRand;
	private double motionX, motionY, motionZ, motionRand;
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
		this.posRand = fluctuation;
		return pos(x,y,z);
	}
	
	public FXHelper pos(Entity entity){
		return pos(entity.posX,entity.posY,entity.posZ);
	}

	public FXHelper pos(Entity entity, double fluctuation){
		return pos(entity.posX,entity.posY,entity.posZ,fluctuation);
	}
	
	public FXHelper motion(double x, double y, double z){
		this.motionX = x;
		this.motionY = y;
		this.motionZ = z;
		return this;
	}
	
	public FXHelper motion(double x, double y, double z, double fluctuation){
		this.motionRand = fluctuation;
		return this.motion(x,y,z);
	}
	
	public FXHelper motionRand(double fluctuation){
		this.motionRand = fluctuation;
		return this;
	}
	
	public FXHelper param(float param){
		this.param = param;
		return this;
	}
	
	public void spawn(Random rand, int amount){
		this.tmpRand = rand;
		
		for(int a = 0; a < amount; a++){
			if (param == Float.MIN_VALUE)HardcoreEnderExpansion.fx.global(type,posX+rnd(posRand),posY+rnd(posRand),posZ+rnd(posRand),motionX+rnd(motionRand),motionY+rnd(motionRand),motionZ+rnd(motionRand));
			else HardcoreEnderExpansion.fx.global(type,posX+rnd(posRand),posY+rnd(posRand),posZ+rnd(posRand),motionX+rnd(motionRand),motionY+rnd(motionRand),motionZ+rnd(motionRand),param);
		}
		
		this.tmpRand = null;
	}
	
	private double rnd(double max){
		return (tmpRand.nextDouble()-0.5D)*2D*max;
	}
}
