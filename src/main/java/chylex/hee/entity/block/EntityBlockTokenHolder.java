package chylex.hee.entity.block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import chylex.hee.system.abstractions.entity.EntityDataWatcher;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.end.EndTerritory;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityBlockTokenHolder extends Entity{
	private enum Data{ CHARGE_PROGRESS, IS_RARE }
	
	protected EntityDataWatcher entityData;
	
	public float rotation, prevRotation, prevCharge;
	private boolean isRestoring;
	private short restoreTimer;
	
	public EntityBlockTokenHolder(World world){
		super(world);
		setSize(0.75F,1.05F);
		preventEntitySpawning = true;
		rotation = prevRotation = rand.nextFloat()*2F*(float)Math.PI;
	}

	@Override
	protected void entityInit(){
		entityData = new EntityDataWatcher(this);
		entityData.addFloat(Data.CHARGE_PROGRESS);
		entityData.addBoolean(Data.IS_RARE);
	}
	
	@Override
	public void onUpdate(){
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		
		if (worldObj.isRemote){
			prevRotation = rotation;
			prevCharge = getChargeProgress();
			rotation += 4F;
		}
		else{
			if (EndTerritory.fromPosition(posX) == EndTerritory.THE_HUB && ++restoreTimer >= 600){
				restoreTimer = 0;
				isRestoring = true;
			}
			
			if (isRestoring && getChargeProgress() < 1F){
				float newValue = Math.min(1F,getChargeProgress()+0.05F);
				if (MathUtil.floatEquals(newValue,1F))isRestoring = false;
				setChargeProgress(newValue);
			}
		}
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount){
		if (isEntityInvulnerable() || !(source.getSourceOfDamage() instanceof EntityPlayer))return false; // no indirect player damage
		
		if (source.getSourceOfDamage().isSneaking() && ((EntityPlayer)source.getSourceOfDamage()).capabilities.isCreativeMode){
			if (worldObj.isRemote)worldObj.playSound(posX,posY,posZ,"dig.glass",1F,rand.nextFloat()*0.1F+0.92F,false);
			setDead();
			return true;
		}
		
		if (!isDead && MathUtil.floatEquals(getChargeProgress(),1F)){
			if (EndTerritory.fromPosition(posX) == EndTerritory.THE_HUB){
				setChargeProgress(0F);
				isRestoring = false;
			}
			else setDead();
			
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
	
	public void setChargeProgress(float progress){
		entityData.setFloat(Data.CHARGE_PROGRESS,progress);
		restoreTimer = 0;
	}
	
	public float getChargeProgress(){
		return entityData.getFloat(Data.CHARGE_PROGRESS);
	}
	
	public void setRare(boolean isRare){
		entityData.setBoolean(Data.IS_RARE,isRare);
	}
	
	public boolean isRare(){
		return entityData.getBoolean(Data.IS_RARE);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt){
		nbt.setFloat("charge",getChargeProgress());
		nbt.setBoolean("isRare",isRare());
		if (restoreTimer > 0)nbt.setShort("restoreTim",restoreTimer);
		if (isRestoring)nbt.setBoolean("isRestoring",isRestoring);
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt){
		setChargeProgress(nbt.getFloat("charge"));
		setRare(nbt.getBoolean("isRare"));
		restoreTimer = nbt.getShort("restoreTim");
		isRestoring = nbt.getBoolean("isRestoring");
	}
	
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
