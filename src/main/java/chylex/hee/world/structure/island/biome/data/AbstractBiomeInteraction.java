package chylex.hee.world.structure.island.biome.data;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import chylex.hee.entity.technical.EntityTechnicalBiomeInteraction;
import chylex.hee.system.logging.Log;
import chylex.hee.system.weight.IWeightProvider;

public abstract class AbstractBiomeInteraction{
	String identifier;
	protected EntityTechnicalBiomeInteraction entity;
	protected World world;
	protected Random rand;
	protected int centerX, centerY, centerZ;
	
	public AbstractBiomeInteraction(){}
	
	public final void init(EntityTechnicalBiomeInteraction entity){
		this.entity = entity;
		this.world = entity.worldObj;
		this.rand = entity.worldObj.rand;
		this.centerX = (int)Math.floor(entity.posX);
		this.centerY = (int)Math.floor(entity.posY);
		this.centerZ = (int)Math.floor(entity.posZ);
	}
	
	public abstract void init();
	public abstract void update();
	public abstract void saveToNBT(NBTTagCompound nbt);
	public abstract void loadFromNBT(NBTTagCompound nbt);
	
	public String getIdentifier(){
		return identifier;
	}
	
	public static final class BiomeInteraction implements IWeightProvider{
		private static final Map<String,BiomeInteraction> idLookup = new HashMap<>();
		
		public static AbstractBiomeInteraction createByIdentifier(String identifier){
			BiomeInteraction interaction = idLookup.get(identifier);
			return interaction == null ? null : interaction.create();
		}
		
		private final String identifier;
		private final Class<? extends AbstractBiomeInteraction> interactionClass;
		private final int weight;
		
		public BiomeInteraction(String identifier, Class<? extends AbstractBiomeInteraction> interactionClass, int weight){
			this.identifier = identifier;
			this.interactionClass = interactionClass;
			this.weight = weight;
			idLookup.put(identifier,this);
		}
		
		public AbstractBiomeInteraction create(){
			try{
				AbstractBiomeInteraction interaction = interactionClass.newInstance();
				interaction.identifier = identifier;
				return interaction;
			}catch(InstantiationException|IllegalAccessException e){
				Log.throwable(e,"Could not create Biome Island Interaction ($0).",interactionClass.getSimpleName());
				return null;
			}
		}
		
		public String getIdentifier(){
			return identifier;
		}
		
		@Override
		public int getWeight(){
			return weight;
		}
	}
}
