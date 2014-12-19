package chylex.hee.entity.mob;
import java.util.List;
import chylex.hee.mechanics.misc.Baconizer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class EntityMobParalyzedEnderman extends EntityEnderman{
	private byte newTargetTimer = 0;
	private short paralyzationEnd = 0;
	
	public EntityMobParalyzedEnderman(World world){
		super(world);
	}
	
	@Override
	protected Entity findPlayerToAttack(){
		return null;
	}

	@Override
	public void onLivingUpdate(){
		if (entityToAttack == null && ++newTargetTimer > 60){
			newTargetTimer = 0;
			
			if (rand.nextInt(10) == 0){
				List<EntityLiving> nearby = worldObj.getEntitiesWithinAABB(EntityLiving.class,boundingBox.expand(8D,4D,8D));
				
				if (!nearby.isEmpty()){
					entityToAttack = nearby.get(rand.nextInt(nearby.size()));
					if (!canEntityBeSeen(entityToAttack))entityToAttack = null;
				}
			}
		}
		
		if (entityToAttack != null && (entityToAttack instanceof EntityPlayer || rand.nextInt(30) == 0))entityToAttack = null;
		
		if ((paralyzationEnd += rand.nextBoolean()?2:1) > 500){
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
	public String getCommandSenderName(){
		return StatCollector.translateToLocal(Baconizer.mobName("entity.brainlessEnderman.name"));
	}
}
