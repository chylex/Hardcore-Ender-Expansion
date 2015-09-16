package chylex.hee.entity.fx;
import java.util.Random;
import net.minecraft.entity.Entity;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.proxy.FXCommonProxy;
import chylex.hee.system.abstractions.Pos;

public class FXHelper{ // TODO check what could be ported
	public static FXHelper create(String type){
		return new FXHelper(type);
	}
	
	private static final IFluctuation defaultFluctuation = (rand, axis) -> 0D;
	
	private final String type;
	private double posX, posY, posZ;
	private double motionX, motionY, motionZ;
	private IFluctuation posFluct = defaultFluctuation;
	private IFluctuation motFluct = defaultFluctuation;
	private float[] params;
	
	private FXHelper(String type){
		this.type = type;
	}
	
	/* === POSITION === */
	
	public FXHelper pos(double x, double y, double z){
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		return this;
	}
	
	public FXHelper pos(int x, int y, int z){
		return pos(x+0.5D,y+0.5D,z+0.5D);
	}
	
	public FXHelper pos(Pos pos){
		return pos(pos.getX()+0.5D,pos.getY()+0.5D,pos.getZ()+0.5D);
	}
	
	public FXHelper pos(Entity entity){
		return pos(entity.posX,entity.posY,entity.posZ);
	}
	
	public FXHelper fluctuatePos(final double maxAmount){
		this.posFluct = (rand, axis) -> (rand.nextDouble()-0.5D)*2D*maxAmount;
		return this;
	}
	
	public FXHelper fluctuatePos(IFluctuation fluctuation){
		this.posFluct = fluctuation;
		return this;
	}
	
	/* === MOTION === */
	
	public FXHelper motion(double x, double y, double z){
		this.motionX = x;
		this.motionY = y;
		this.motionZ = z;
		return this;
	}
	
	public FXHelper fluctuateMotion(final double maxAmount){
		this.motFluct = (rand, axis) -> (rand.nextDouble()-0.5D)*2D*maxAmount;
		return this;
	}
	
	public FXHelper fluctuateMotion(IFluctuation fluctuation){
		this.motFluct = fluctuation;
		return this;
	}
	
	/* === PARAMETERS === */
	
	public FXHelper paramSingle(float param){
		this.params = new float[]{ param };
		return this;
	}
	
	public FXHelper paramColor(float red, float green, float blue){
		this.params = new float[]{ red, green, blue };
		return this;
	}
	
	public void spawn(Random rand, int amount){
		FXCommonProxy fx = HardcoreEnderExpansion.fx;
		
		for(int a = 0; a < amount; a++){
			if (params == null){
				fx.global(
					type,
					posX+posFluct.fluctuate(rand,Axis.X),
					posY+posFluct.fluctuate(rand,Axis.Y),
					posZ+posFluct.fluctuate(rand,Axis.Z),
					motionX+motFluct.fluctuate(rand,Axis.X),
					motionY+motFluct.fluctuate(rand,Axis.Y),
					motionZ+motFluct.fluctuate(rand,Axis.Z)
				);
			}
			else if (params.length == 1){
				fx.global(
					type,
					posX+posFluct.fluctuate(rand,Axis.X),
					posY+posFluct.fluctuate(rand,Axis.Y),
					posZ+posFluct.fluctuate(rand,Axis.Z),
					motionX+motFluct.fluctuate(rand,Axis.X),
					motionY+motFluct.fluctuate(rand,Axis.Y),
					motionZ+motFluct.fluctuate(rand,Axis.Z),
					params[0]
				);
			}
			else if (params.length == 3){
				fx.global(
					type,
					posX+posFluct.fluctuate(rand,Axis.X),
					posY+posFluct.fluctuate(rand,Axis.Y),
					posZ+posFluct.fluctuate(rand,Axis.Z),
					motionX+motFluct.fluctuate(rand,Axis.X),
					motionY+motFluct.fluctuate(rand,Axis.Y),
					motionZ+motFluct.fluctuate(rand,Axis.Z),
					params[0],
					params[1],
					params[2]
				);
			}
		}
	}
	
	@FunctionalInterface
	public static interface IFluctuation{
		double fluctuate(Random rand, Axis axis);
	}
	
	public enum Axis{
		X, Y, Z
	}
}
