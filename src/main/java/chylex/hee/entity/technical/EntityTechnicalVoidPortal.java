package chylex.hee.entity.technical;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import chylex.hee.system.abstractions.entity.EntityDataWatcher;

public class EntityTechnicalVoidPortal extends EntityTechnicalBase{
	private enum Data{ TOKEN }
	
	private EntityDataWatcher entityData;
	
	private int timer;
	private float speed = 1F;
	public float renderAlpha, prevRenderAlpha;
	public float renderTranslation, prevRenderTranslation;
	
	public EntityTechnicalVoidPortal(World world){
		super(world);
	}
	
	public void activate(ItemStack tokenIS){
		entityData.setItemStack(Data.TOKEN,tokenIS.copy());
		timer = 280;
	}

	@Override
	protected void entityInit(){
		entityData = new EntityDataWatcher(this);
		entityData.addItemStack(Data.TOKEN);
	}
	
	@Override
	public void onUpdate(){
		super.onUpdate();
		
		prevRenderTranslation = renderTranslation;
		prevRenderAlpha = renderAlpha;
		
		if (timer > 0){
			--timer;
			
			if (timer >= 240)renderAlpha = Math.min(1F,renderAlpha+0.025F);
			else if (timer <= 80)speed = Math.max(0F,speed-0.025F);
			else if (timer <= 40)renderAlpha = Math.max(0F,renderAlpha-0.025F);
			
			if (timer == 0)speed = 1F;
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt){}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt){}
}
