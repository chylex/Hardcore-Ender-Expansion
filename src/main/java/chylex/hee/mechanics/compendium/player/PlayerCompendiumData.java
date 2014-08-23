package chylex.hee.mechanics.compendium.player;
import gnu.trove.set.hash.TIntHashSet;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.util.Constants.NBT;
import chylex.hee.mechanics.compendium.player.PlayerDiscoveryList.IObjectSerializer;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;

public class PlayerCompendiumData implements IExtendedEntityProperties{
	private int pointAmount;
	
	private PlayerDiscoveryList<Block> discoveredBlocks = new PlayerDiscoveryList<>(new DiscoveryBlockSerializer());
	private PlayerDiscoveryList<Item> discoveredItems = new PlayerDiscoveryList<>(new DiscoveryItemSerializer());
	private PlayerDiscoveryList<Class<? extends EntityLivingBase>> discoveryMobs = new PlayerDiscoveryList<>(new DiscoveryMobSerializer());
	
	private TIntHashSet unlockedFragments = new TIntHashSet();
	
	public int getPoints(){
		return pointAmount;
	}
	
	public void payPoints(int amount){
		pointAmount = Math.max(0,pointAmount-amount);
	}
	
	public boolean tryDiscoverBlock(Block block){
		if (discoveredBlocks.addObject(block)){
			// unlock discovery fragments
			return true;
		}
		else return false;
	}
	
	public boolean hasDiscoveredBlock(Block block){
		return discoveredBlocks.hasDiscoveredObject(block);
	}
	
	public boolean tryDiscoverItem(Item item){
		if (discoveredItems.addObject(item)){
			// unlock discovery fragments
			return true;
		}
		else return false;
	}
	
	public boolean hasDiscoveredItem(Item item){
		return discoveredItems.hasDiscoveredObject(item);
	}
	
	public boolean tryDiscoverMob(EntityLivingBase mob){
		if (discoveryMobs.addObject(mob.getClass())){
			// unlock discovery fragments
			return true;
		}
		else return false;
	}
	
	public boolean hasDiscoveredMob(EntityLivingBase mob){
		return discoveryMobs.hasDiscoveredObject(mob.getClass());
	}
	
	@Override
	public void init(Entity entity, World world){}
	
	@Override
	public void saveNBTData(NBTTagCompound nbt){
		nbt.setInteger("kps",pointAmount);
		nbt.setTag("fndBlocks",discoveredBlocks.saveToNBTList());
		nbt.setTag("fndItems",discoveredItems.saveToNBTList());
		nbt.setTag("fndMobs",discoveryMobs.saveToNBTList());
		nbt.setTag("unlocked",new NBTTagIntArray(unlockedFragments.toArray()));
	}

	@Override
	public void loadNBTData(NBTTagCompound nbt){
		pointAmount = nbt.getInteger("kps");
		discoveredBlocks.loadFromNBTList(nbt.getTagList("fndBlocks",NBT.TAG_STRING));
		discoveredItems.loadFromNBTList(nbt.getTagList("fndItems",NBT.TAG_STRING));
		discoveryMobs.loadFromNBTList(nbt.getTagList("fndMobs",NBT.TAG_STRING));
		unlockedFragments.addAll(nbt.getIntArray("unlocked"));
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
