package chylex.hee.entity.technical;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import chylex.hee.system.logging.Log;

public class EntityTechnicalTrigger extends EntityTechnicalBase{
	private TriggerBase trigger;
	
	public EntityTechnicalTrigger(World world){
		super(world);
	}
	
	public EntityTechnicalTrigger(World world, double x, double y, double z, TriggerBase trigger){
		super(world);
		setPosition(x,y,z);
		this.trigger = trigger;
	}
	
	@Override
	protected void entityInit(){}
	
	@Override
	public void onUpdate(){
		super.onUpdate();
		
		if (trigger == null)setDead();
		else trigger.update(this,worldObj,rand);
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt){
		if (trigger != null){
			NBTTagCompound tag = new NBTTagCompound();
			trigger.save(tag);
			nbt.setTag("triggerData",tag);
			nbt.setString("triggerCls",trigger.getClass().getName());
		}
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt){
		try{
			Class<? extends TriggerBase> trigger = (Class<? extends TriggerBase>)Class.forName(nbt.getString("triggerCls"));
			this.trigger = trigger.newInstance();
			this.trigger.load(nbt.getCompoundTag("triggerData"));
		}catch(Throwable t){
			Log.throwable(t,"Unable to load a trigger entity: $0",nbt.getString("triggerCls"));
			setDead();
		}
	}
	
	public static abstract class TriggerBase{
		protected abstract void update(EntityTechnicalTrigger entity, World world, Random rand);
		protected void save(NBTTagCompound nbt){}
		protected void load(NBTTagCompound nbt){}
	}
}
