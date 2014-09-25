package chylex.hee.world.structure.island.biome.data;
import net.minecraft.nbt.NBTTagCompound;
import chylex.hee.entity.technical.EntityTechnicalBiomeInteraction;
import chylex.hee.system.logging.Log;
import chylex.hee.system.weight.IWeightProvider;

public abstract class AbstractBiomeInteraction{
	protected EntityTechnicalBiomeInteraction entity;
	
	public AbstractBiomeInteraction(){}
	
	public void init(EntityTechnicalBiomeInteraction entity){
		this.entity = entity;
	}
	
	public abstract void update();
	public abstract void saveToNBT(NBTTagCompound nbt);
	public abstract void loadFromNBT(NBTTagCompound nbt);
	
	public static final class BiomeInteraction implements IWeightProvider{
		private final Class<? extends AbstractBiomeInteraction> interactionClass;
		private final int weight;
		
		public BiomeInteraction(Class<? extends AbstractBiomeInteraction> interactionClass, int weight){
			this.interactionClass = interactionClass;
			this.weight = weight;
		}
		
		public AbstractBiomeInteraction create(){
			try{
				return interactionClass.newInstance();
			}catch(InstantiationException|IllegalAccessException e){
				Log.throwable(e,"Could not create Biome Island Interaction ($0).",interactionClass.getSimpleName());
				return null;
			}
		}
		
		@Override
		public int getWeight(){
			return weight;
		}
	}
}
