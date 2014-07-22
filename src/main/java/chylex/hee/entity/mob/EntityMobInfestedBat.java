package chylex.hee.entity.mob;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import chylex.hee.mechanics.knowledge.KnowledgeRegistrations;
import chylex.hee.mechanics.knowledge.util.ObservationUtil;

public class EntityMobInfestedBat extends EntityBat{
	public EntityMobInfestedBat(World world){
		super(world);
	}
	
	@Override
	protected void entityInit(){
		super.entityInit();
		dataWatcher.addObject(17,0.35F+rand.nextFloat()*0.35F);
	}
	
	@Override
	protected void dropFewItems(boolean recentlyHit, int looting){
		for(EntityPlayer observer:ObservationUtil.getAllObservers(this,8D)){
			if (KnowledgeRegistrations.INFESTED_BAT.tryUnlockFragment(observer,0.3F,new byte[]{ 0,1 }).stopTrying)continue;
			if (rand.nextInt(3) == 0)KnowledgeRegistrations.ENHANCED_BREWING_STAND.tryUnlockFragment(observer,0.3F,new byte[]{ 4 });
			else KnowledgeRegistrations.INFESTATION_REMEDY.tryUnlockFragment(observer,0.4F);
		}
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
		return StatCollector.translateToLocal("entity.infestedBat.name");
	}
}
