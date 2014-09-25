package chylex.hee.entity.technical;
import chylex.hee.world.structure.island.biome.data.AbstractBiomeInteraction;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityTechnicalBiomeInteraction extends Entity{
	private final AbstractBiomeInteraction interaction;
	
	public EntityTechnicalBiomeInteraction(World world){
		super(world);
		this.interaction = null;
	}
	
	public EntityTechnicalBiomeInteraction(World world, double x, double y, double z, AbstractBiomeInteraction interaction){
		super(world);
		setPosition(x,y,z);
		this.interaction = interaction;
	}

	@Override
	protected void entityInit(){
		if (interaction != null)interaction.init(this);
	}
	
	@Override
	public void onUpdate(){
		if (interaction != null)interaction.update();
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt){
		if (interaction != null){
			NBTTagCompound tag = new NBTTagCompound();
			interaction.saveToNBT(tag);
			nbt.setTag("interactionData",tag);
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt){
		if (interaction != null)interaction.loadFromNBT(nbt.getCompoundTag("interactionData"));
	}
}
