package chylex.hee.entity.mob;
import java.util.List;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import chylex.hee.mechanics.misc.Baconizer;

public class EntityMobParalyzedEnderman extends EntityEnderman{
	private byte newTargetTimer = 0;
	private short paralyzationEnd = 0;
	
	public EntityMobParalyzedEnderman(World world){
		super(world);
		targetTasks.taskEntries.clear();
	}

	@Override
	public void onLivingUpdate(){
		if (getAttackTarget() == null && ++newTargetTimer > 60){
			newTargetTimer = 0;
			
			if (rand.nextInt(10) == 0){
				List<EntityLiving> nearby = worldObj.getEntitiesWithinAABB(EntityLiving.class,boundingBox.expand(8D,4D,8D));
				
				if (!nearby.isEmpty()){
					EntityLiving target = nearby.get(rand.nextInt(nearby.size()));
					setAttackTarget(canEntityBeSeen(target) ? target : null);
				}
			}
		}
		
		EntityLivingBase entityToAttack = getAttackTarget();
		if (entityToAttack != null && (entityToAttack instanceof EntityPlayer || rand.nextInt(30) == 0))setAttackTarget(entityToAttack = null);
		
		if ((paralyzationEnd += rand.nextBoolean() ? 2 : 1) > 500){
			if (rand.nextBoolean())setHealth(0);
			else{
				EntityEnderman enderman = new EntityEnderman(worldObj);
				enderman.copyLocationAndAnglesFrom(this);
				enderman.setHealth(getHealth());
				worldObj.spawnEntityInWorld(enderman);
				setDead();
			}
		}

		super.onLivingUpdate();
	}
	
	@Override
	protected void dropFewItems(boolean recentlyHit, int looting){
		if (rand.nextInt(3) == 0)dropItem(getDropItem(),1);
	}
	
	@Override
	protected boolean teleportTo(double x, double y, double z){
		if (rand.nextInt(10) == 0)return super.teleportTo(x,y,z);
		return true;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt){
		super.writeEntityToNBT(nbt);
		nbt.setShort("paralyzationEnd",paralyzationEnd);
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		paralyzationEnd = nbt.getShort("paralyzationEnd");
	}
	
	@Override
	protected String getLivingSound(){
		return Baconizer.soundNormal(super.getLivingSound());
	}
	
	@Override
	protected String getHurtSound(){
		return Baconizer.soundNormal(super.getHurtSound());
	}
	
	@Override
	protected String getDeathSound(){
		return Baconizer.soundDeath(super.getDeathSound());
	}
	
	@Override
	public String getName(){
		return hasCustomName() ? getCustomNameTag() : StatCollector.translateToLocal(Baconizer.mobName("entity.brainlessEnderman.name"));
	}
}
