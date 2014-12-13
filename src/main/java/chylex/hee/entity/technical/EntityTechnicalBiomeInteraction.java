package chylex.hee.entity.technical;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import chylex.hee.world.structure.island.biome.data.AbstractBiomeInteraction;
import chylex.hee.world.structure.island.biome.data.AbstractBiomeInteraction.BiomeInteraction;

public class EntityTechnicalBiomeInteraction extends EntityTechnicalBase{
	private AbstractBiomeInteraction interaction;
	
	public EntityTechnicalBiomeInteraction(World world){
		super(world);
	}
	
	public EntityTechnicalBiomeInteraction(World world, double x, double y, double z, AbstractBiomeInteraction interaction){
		super(world);
		setPosition(x,y,z);
		this.interaction = interaction;
		this.interaction.init(this);
		this.interaction.init();
	}

	@Override
	protected void entityInit(){}
	
	@Override
	public void onUpdate(){
		if (!worldObj.isRemote)interaction.update();
	}
	
	public AbstractBiomeInteraction getInteraction(){
		return interaction;
	}
	
	public Class<? extends AbstractBiomeInteraction> getInteractionType(){
		return interaction != null ? interaction.getClass() : null;
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt){
		if (!worldObj.isRemote){
			NBTTagCompound tag = new NBTTagCompound();
			interaction.saveToNBT(tag);
			nbt.setTag("interactionData",tag);
			nbt.setString("interactionId",interaction.getIdentifier());
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt){
		if (!worldObj.isRemote){
			interaction = BiomeInteraction.createByIdentifier(nbt.getString("interactionId"));
			
			if (interaction != null){
				interaction.init(this);
				interaction.loadFromNBT(nbt.getCompoundTag("interactionData"));
			}
			else setDead();
		}
	}
}
