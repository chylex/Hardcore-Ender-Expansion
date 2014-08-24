package chylex.hee.mechanics.compendium.player;
import gnu.trove.set.hash.TIntHashSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.util.Constants.NBT;
import chylex.hee.mechanics.compendium.content.objects.ObjectBlock;
import chylex.hee.mechanics.compendium.content.objects.ObjectBlock.BlockMetaWrapper;
import chylex.hee.mechanics.compendium.content.objects.ObjectItem;
import chylex.hee.mechanics.compendium.content.objects.ObjectMob;
import chylex.hee.mechanics.compendium.content.type.KnowledgeFragment;
import chylex.hee.mechanics.compendium.content.type.KnowledgeObject;
import chylex.hee.mechanics.compendium.player.PlayerDiscoveryList.IObjectSerializer;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PlayerCompendiumData implements IExtendedEntityProperties{
	private int pointAmount;
	
	private PlayerDiscoveryList<ObjectBlock,BlockMetaWrapper> discoveredBlocks = new PlayerDiscoveryList<>(new DiscoveryBlockSerializer());
	private PlayerDiscoveryList<ObjectItem,Item> discoveredItems = new PlayerDiscoveryList<>(new DiscoveryItemSerializer());
	private PlayerDiscoveryList<ObjectMob,Class<? extends EntityLivingBase>> discoveryMobs = new PlayerDiscoveryList<>(new DiscoveryMobSerializer());
	
	private TIntHashSet unlockedFragments = new TIntHashSet();
	
	public PlayerCompendiumData(){}
	
	@SideOnly(Side.CLIENT)
	public PlayerCompendiumData(NBTTagCompound nbt){
		loadNBTData(nbt);
	}
	
	public int getPoints(){
		return pointAmount;
	}
	
	public void payPoints(int amount){
		pointAmount = Math.max(0,pointAmount-amount);
	}
	
	public boolean tryDiscoverBlock(KnowledgeObject<ObjectBlock> block){
		if (discoveredBlocks.addObject(block.getObject())){
			unlockDiscoveryFragments(block);
			return true;
		}
		else return false;
	}
	
	public boolean hasDiscoveredBlock(KnowledgeObject<ObjectBlock> block){
		return discoveredBlocks.hasDiscoveredObject(block.getObject());
	}
	
	public boolean tryDiscoverItem(KnowledgeObject<ObjectItem> item){
		if (discoveredItems.addObject(item.getObject())){
			unlockDiscoveryFragments(item);
			return true;
		}
		else return false;
	}
	
	public boolean hasDiscoveredItem(KnowledgeObject<ObjectItem> item){
		return discoveredItems.hasDiscoveredObject(item.getObject());
	}
	
	public boolean tryDiscoverMob(KnowledgeObject<ObjectMob> mob){
		if (discoveryMobs.addObject(mob.getObject())){
			unlockDiscoveryFragments(mob);
			return true;
		}
		else return false;
	}
	
	public boolean hasDiscoveredMob(KnowledgeObject<ObjectMob> mob){
		return discoveryMobs.hasDiscoveredObject(mob.getObject());
	}
	
	private void unlockDiscoveryFragments(KnowledgeObject<?> object){
		for(KnowledgeFragment fragment:object.getFragments()){
			if (fragment.isUnlockedOnDiscovery())unlockFragment(fragment);
		}
	}
	
	public void unlockFragment(KnowledgeFragment fragment){
		unlockedFragments.add(fragment.globalID);
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
	
	private static class DiscoveryBlockSerializer implements IObjectSerializer<BlockMetaWrapper>{
		@Override
		public String serialize(BlockMetaWrapper object){
			UniqueIdentifier identifier = GameRegistry.findUniqueIdentifierFor(object.block);
			return identifier.modId+":"+identifier.name+":"+object.metadata;
		}

		@Override
		public BlockMetaWrapper deserialize(String data){
			int colonIndex = data.indexOf(":"), nextColonIndex = data.indexOf(":",colonIndex+1);
			return new BlockMetaWrapper(GameRegistry.findBlock(data.substring(0,colonIndex),data.substring(colonIndex+1,nextColonIndex)),Integer.parseInt(data.substring(nextColonIndex+1)));
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
