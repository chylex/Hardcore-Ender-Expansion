package chylex.hee.mechanics.compendium;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.util.Constants.NBT;
import chylex.hee.mechanics.compendium.PlayerDiscoveryList.IObjectSerializer;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;

public class PlayerCompendiumData implements IExtendedEntityProperties{
	private int fragmentAmount;
	
	private PlayerDiscoveryList<Block> discoveredBlocks = new PlayerDiscoveryList<>(new DiscoveryBlockSerializer());
	private PlayerDiscoveryList<Item> discoveredItems = new PlayerDiscoveryList<>(new DiscoveryItemSerializer());
	private PlayerDiscoveryList<Class<? extends EntityLivingBase>> discoveryMobs = new PlayerDiscoveryList<>(new DiscoveryMobSerializer());
	
	@Override
	public void init(Entity entity, World world){}
	
	@Override
	public void saveNBTData(NBTTagCompound nbt){
		nbt.setInteger("kfs",fragmentAmount);
		nbt.setTag("discoveredBlocks",discoveredBlocks.saveToNBTList());
		nbt.setTag("discoveredItems",discoveredItems.saveToNBTList());
		nbt.setTag("discoveryMobs",discoveryMobs.saveToNBTList());
	}

	@Override
	public void loadNBTData(NBTTagCompound nbt){
		fragmentAmount = nbt.getInteger("kfs");
		discoveredBlocks.loadFromNBTList(nbt.getTagList("discoveredBlocks",NBT.TAG_STRING));
		discoveredItems.loadFromNBTList(nbt.getTagList("discoveredItems",NBT.TAG_STRING));
		discoveryMobs.loadFromNBTList(nbt.getTagList("discoveryMobs",NBT.TAG_STRING));
	}
	
	// SERIALIZERS
	
	private static class DiscoveryBlockSerializer implements IObjectSerializer<Block>{
		@Override
		public String serialize(Block object){
			UniqueIdentifier identifier = GameRegistry.findUniqueIdentifierFor(object);
			return identifier.modId+":"+identifier.name;
		}

		@Override
		public Block deserialize(String data){
			int colonIndex = data.indexOf(":");
			return GameRegistry.findBlock(data.substring(0,colonIndex),data.substring(colonIndex+1));
		}
	}
	
	private static class DiscoveryItemSerializer implements IObjectSerializer<Item>{
		@Override
		public String serialize(Item object){
			UniqueIdentifier identifier = GameRegistry.findUniqueIdentifierFor(object);
			return identifier.modId+":"+identifier.name;
		}

		@Override
		public Item deserialize(String data){
			int colonIndex = data.indexOf(":");
			return GameRegistry.findItem(data.substring(0,colonIndex),data.substring(colonIndex+1));
		}
	}
	
	private static class DiscoveryMobSerializer implements IObjectSerializer<Class<? extends EntityLivingBase>>{
		@Override
		public String serialize(Class<? extends EntityLivingBase> object){
			return (String)EntityList.classToStringMapping.get(object.getClass());
		}

		@Override
		public Class<? extends EntityLivingBase> deserialize(String data){
			return (Class<? extends EntityLivingBase>)EntityList.stringToClassMapping.get(data);
		}
	}
}
