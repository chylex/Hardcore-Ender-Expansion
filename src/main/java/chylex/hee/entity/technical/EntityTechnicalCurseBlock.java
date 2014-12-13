package chylex.hee.entity.technical;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityTechnicalCurseBlock extends EntityTechnicalBase{
	public EntityTechnicalCurseBlock(World world){
		super(world);
	}

	@Override
	protected void entityInit(){}
	
	@Override
	public void onUpdate(){
		
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt){}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt){}
}
