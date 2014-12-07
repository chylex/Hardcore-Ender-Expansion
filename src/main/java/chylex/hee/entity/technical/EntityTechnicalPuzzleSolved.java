package chylex.hee.entity.technical;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityTechnicalPuzzleSolved extends Entity{
	public EntityTechnicalPuzzleSolved(World world){
		super(world);
	}
	
	public EntityTechnicalPuzzleSolved(World world, int x, int y, int z){
		super(world);
		setPosition(x+0.5D,y+0.5D,z+0.5D);
		
	}

	@Override
	protected void entityInit(){}
	
	@Override
	public void onUpdate(){
		// TODO
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt){
		
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt){
		
	}
}
