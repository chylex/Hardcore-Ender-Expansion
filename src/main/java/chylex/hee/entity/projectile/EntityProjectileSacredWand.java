package chylex.hee.entity.projectile;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import chylex.hee.item.ItemSacredWand;

public class EntityProjectileSacredWand extends EntityThrowable{
	private ItemStack wand;
	
	public EntityProjectileSacredWand(World world){
		super(world);
	}
	
	public EntityProjectileSacredWand(World world, EntityLivingBase owner, ItemStack wand){
		super(world, owner);
		this.wand = wand.copy();
		motionX *= 2D;
		motionY *= 2D;
		motionZ *= 2D;
	}
	
	@Override
	protected void entityInit(){}
	
	@Override
	protected void onImpact(MovingObjectPosition mop){
		if (mop.entityHit instanceof EntityLivingBase && getThrower() instanceof EntityPlayer){
			ItemSacredWand.attackEntity(wand, (EntityPlayer)getThrower(), (EntityLivingBase)mop.entityHit, this);
		}
		
		setDead();
	}
	
	@Override
	protected float getGravityVelocity(){
		return 0F;
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt){
		super.writeEntityToNBT(nbt);
		nbt.setTag("wandIS", wand.writeToNBT(new NBTTagCompound()));
		nbt.removeTag("inTile");
		nbt.removeTag("shake");
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		wand = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("wandIS"));
	}
}
