package chylex.hee.entity.technical;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import chylex.hee.system.abstractions.entity.EntityDataWatcher;

public class EntityTechnicalVoidPortal extends EntityTechnicalBase{
	private enum Data{ TOKEN }
	
	private EntityDataWatcher entityData;
	
	private ItemStack prevTokenIS;
	private int timer;
	private float speed = 1F;
	
	public float renderAlpha, prevRenderAlpha;
	public float renderTranslation, prevRenderTranslation;
	
	public EntityTechnicalVoidPortal(World world){
		super(world);
	}
	
	public void activate(ItemStack tokenIS){
		entityData.setItemStack(Data.TOKEN,tokenIS.copy());
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
		
		ItemStack tokenIS = entityData.getItemStack(Data.TOKEN);
		
		if (tokenIS != null && (timer == 0 || prevTokenIS != tokenIS)){
			prevTokenIS = tokenIS;
			timer = 280;
			speed = 1F;
		}
		
		if (timer > 0){
			--timer;
			
			renderTranslation += speed*0.0015F;
			
			if (timer >= 240)renderAlpha = Math.min(1F,renderAlpha+0.05F);
			if (timer <= 80)speed = Math.max(0F,speed-0.025F);
			if (timer <= 45)renderAlpha = Math.max(0F,renderAlpha-0.025F);
			
			if (timer == 10)entityData.setItemStack(Data.TOKEN,null);
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt){}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt){}
}
