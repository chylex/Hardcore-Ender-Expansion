package chylex.hee.entity.mob;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import chylex.hee.mechanics.misc.Baconizer;
import chylex.hee.system.abstractions.entity.EntityDataWatcher;

public class EntityMobInfestedBat extends EntityBat{
	private enum Data{ SCALE }
	
	private EntityDataWatcher entityData;
	
	public EntityMobInfestedBat(World world){
		super(world);
	}
	
	@Override
	protected void entityInit(){
		super.entityInit();
		entityData = new EntityDataWatcher(this);
		entityData.addFloat(Data.SCALE,0.35F+rand.nextFloat()*0.35F);
	}
	
	public float getScale(){
		return entityData.getFloat(Data.SCALE);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt){
		super.writeEntityToNBT(nbt);
		nbt.setFloat("scale",getScale());
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		
		float scale = nbt.getFloat("scale");
		if (scale != 0F)entityData.setFloat(Data.SCALE,scale);
	}

	@Override
	public boolean getCanSpawnHere(){
		return worldObj.checkNoEntityCollision(boundingBox) && worldObj.getCollidingBoundingBoxes(this,boundingBox).isEmpty() && !worldObj.isAnyLiquid(boundingBox);
	}
	
	@Override
	public String getCommandSenderName(){
		return hasCustomNameTag() ? getCustomNameTag() : StatCollector.translateToLocal(Baconizer.mobName("entity.infestedBat.name"));
	}
}
