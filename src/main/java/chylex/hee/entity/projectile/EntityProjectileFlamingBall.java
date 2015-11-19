package chylex.hee.entity.projectile;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.Vec;
import chylex.hee.system.abstractions.facing.Facing6;

public class EntityProjectileFlamingBall extends EntityFireball{
	public EntityProjectileFlamingBall(World world){
		super(world);
		setSize(0.15F,0.15F);
	}
	
	public EntityProjectileFlamingBall(World world, EntityLivingBase shooter, double x, double y, double z, double xDiff, double yDiff, double zDiff){
		super(world,shooter,xDiff,yDiff,zDiff);
		setSize(0.15F,0.15F);
		setPosition(x,y,z);
	}
	
	@Override
	public void onUpdate(){
		super.onUpdate();
		
		if (worldObj.isRemote){
			HardcoreEnderExpansion.fx.flame(posX,posY+0.4D,posZ,5);
			if (ticksExisted == 1 && rand.nextInt(4) <= 1)worldObj.playSound(posX,posY,posZ,"mob.ghast.fireball",0.8F,rand.nextFloat()*0.1F+1.2F,false);
		}
		
		if (ticksExisted > 35)setDead();
	}

	@Override
	protected void onImpact(MovingObjectPosition mop){
		if (worldObj.isRemote)return;

		if (mop.entityHit != null && shootingEntity != null){
			if (mop.entityHit instanceof EntityProjectileFlamingBall)return;

			boolean isLiving = mop.entityHit instanceof EntityLivingBase;
			
			if (isLiving && rand.nextInt(ModCommonProxy.opMobs ? 3 : 4) == 0){
				Vec vec = Vec.between(mop.entityHit,shootingEntity).normalized();
				((EntityLivingBase)mop.entityHit).knockBack(shootingEntity,0.9F,vec.x*0.15D*(0.85D+0.2D*rand.nextDouble()),vec.z*0.15D*(0.85D+0.4D*rand.nextDouble()));
			}
			
			mop.entityHit.attackEntityFrom(DamageSource.onFire,3F);
			if (isLiving)mop.entityHit.attackEntityFrom(DamageSource.causeMobDamage(shootingEntity),ModCommonProxy.opMobs ? 8F : 5F);
			mop.entityHit.fire += 3+EnchantmentProtection.getFireTimeForEntity(mop.entityHit,25);
		}
		else if (rand.nextInt(3) == 0){
			Pos pos = Pos.at(mop);
			
			if (Facing6.fromSide(mop.sideHit).getY() == 0)pos = pos.offset(mop.sideHit);
			
			if (pos.isAir(worldObj))pos.setBlock(worldObj,Blocks.fire);
			else if (pos.getUp().isAir(worldObj))pos.getUp().setBlock(worldObj,Blocks.fire);
		}
		
		setDead();
	}
	
	@Override
	protected float getMotionFactor(){
		return 0.8F;
	}
	
	@Override
	public boolean isBurning(){
		return false;
	}
	
	@Override
	public boolean canBeCollidedWith(){
		return false;
	}
	
	@Override
	public boolean isEntityInvulnerable(){
		return true;
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		ticksExisted = 30;
	}
}
