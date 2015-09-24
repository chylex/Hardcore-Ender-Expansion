package chylex.hee.entity.projectile;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import chylex.hee.entity.fx.FXHelper;
import chylex.hee.system.util.MathUtil;

public class EntityProjectileEyeOfEnder extends Entity{
	public int timer;
	
	public EntityProjectileEyeOfEnder(World world){
		super(world);
		setSize(0.25F,0.25F);
	}
	
	public EntityProjectileEyeOfEnder(World world, Entity thrower){
		this(world);
		setPosition(thrower.posX+MathUtil.lendirx(1D,-thrower.rotationYaw),thrower.posY+1.6D+MathUtil.lendirx(1D,-thrower.rotationPitch),thrower.posZ+MathUtil.lendiry(1D,-thrower.rotationYaw));
	}

	@Override
	protected void entityInit(){}
	
	@Override
	public void onUpdate(){
		super.onUpdate();
		++timer;
		
		if (timer > 80){
			// TODO posX += 0.1D;
		}
		
		if (!worldObj.isRemote){
			if (timer > 400 && timer > 400+rand.nextInt(100)){
				setDead();
			}
		}
		else{
			if (timer == 1)FXHelper.create("smoke").pos(this).fluctuatePos(0.15D).fluctuateMotion(0.1D).spawn(rand,8);
			else if (timer > 70){
				FXHelper.create("glitter")
				.pos(posX,posY+height*0.5F+(float)Math.sin(timer*0.15D)*0.25F,posZ)
				.fluctuatePos(0.2D)
				.motion(0D,-0.025D,0)
				.fluctuateMotion(0.02D)
				.paramColor(0.15F+rand.nextFloat()*0.3F,0.25F+rand.nextFloat()*0.4F,0.3F+rand.nextFloat()*0.05F)
				.spawn(rand,3);
			}
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt){
		nbt.setShort("eoetim",(short)timer);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt){
		timer = nbt.getShort("eoetim");
	}
}
