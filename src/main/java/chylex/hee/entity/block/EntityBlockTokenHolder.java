package chylex.hee.entity.block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityBlockTokenHolder extends Entity{	
	public float rotation, prevRotation;
	
	public EntityBlockTokenHolder(World world){
		super(world);
		setSize(0.75F,1.05F);
		preventEntitySpawning = true;
		rotation = prevRotation = rand.nextFloat()*2F*(float)Math.PI;
	}

	@Override
	protected void entityInit(){}
	
	@Override
	public void onUpdate(){
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		
		if (worldObj.isRemote){
			prevRotation = rotation;
			rotation += 4F;
		}
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount){
		if (isEntityInvulnerable() || !(source.getSourceOfDamage() instanceof EntityPlayer))return false; // no indirect player damage
		
		if (!isDead){
			setDead();
			
			if (worldObj.isRemote){
				worldObj.playSound(posX,posY,posZ,"dig.glass",1F,rand.nextFloat()*0.1F+0.92F,false);
				for(int a = 0; a < 20; a++)worldObj.spawnParticle("largesmoke",posX+(rand.nextDouble()-0.5D)*0.8D,posY+0.05D+rand.nextDouble()*1D,posZ+(rand.nextDouble()-0.5D)*0.8D,0D,0D,0D);
			}
			else{
				// TODO spawn token
			}
		}
		
		return true;
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt){}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt){}
	
	@Override
	protected boolean canTriggerWalking(){
		return false;
	}
	
	@Override
	public boolean canBeCollidedWith(){
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public float getShadowSize(){
		return 0F;
	}
}
