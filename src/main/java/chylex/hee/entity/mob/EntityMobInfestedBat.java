package chylex.hee.entity.mob;
import chylex.hee.mechanics.misc.Baconizer;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class EntityMobInfestedBat extends EntityBat{
	public EntityMobInfestedBat(World world){
		super(world);
	}
	
	@Override
	protected void entityInit(){
		super.entityInit();
		dataWatcher.addObject(17,0.35F+rand.nextFloat()*0.35F);
	}
	
	public float getScale(){
		return dataWatcher.getWatchableObjectFloat(17);
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
		if (scale != 0F)dataWatcher.updateObject(17,scale);
	}

	@Override
	public boolean getCanSpawnHere(){
		return worldObj.checkNoEntityCollision(boundingBox) && worldObj.getCollidingBoundingBoxes(this,boundingBox).isEmpty() && !worldObj.isAnyLiquid(boundingBox);
	}
	
	@Override
	public String getCommandSenderName(){
		return StatCollector.translateToLocal(Baconizer.mobName("entity.infestedBat.name"));
	}
}
