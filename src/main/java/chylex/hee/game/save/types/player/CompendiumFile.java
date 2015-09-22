package chylex.hee.game.save.types.player;
import gnu.trove.set.hash.TIntHashSet;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraftforge.common.util.Constants.NBT;
import org.apache.commons.lang3.StringUtils;
import chylex.hee.game.save.types.PlayerFile;
import chylex.hee.mechanics.compendium.KnowledgeRegistrations;
import chylex.hee.mechanics.compendium.content.KnowledgeFragment;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.objects.IKnowledgeObjectInstance;
import chylex.hee.mechanics.compendium.objects.ObjectBlock;
import chylex.hee.mechanics.compendium.objects.ObjectBlock.BlockMetaWrapper;
import chylex.hee.mechanics.compendium.objects.ObjectDummy;
import chylex.hee.mechanics.compendium.objects.ObjectItem;
import chylex.hee.mechanics.compendium.objects.ObjectMob;
import chylex.hee.mechanics.compendium.player.PlayerDiscoveryList;
import chylex.hee.mechanics.compendium.player.PlayerDiscoveryList.IObjectSerializer;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CompendiumFile extends PlayerFile{
	private boolean seenHelp;
	private int pointAmount;
	
	private final PlayerDiscoveryList<ObjectBlock,BlockMetaWrapper> discoveredBlocks = new PlayerDiscoveryList<>(new DiscoveryBlockSerializer());
	private final PlayerDiscoveryList<ObjectItem,Item> discoveredItems = new PlayerDiscoveryList<>(new DiscoveryItemSerializer());
	private final PlayerDiscoveryList<ObjectMob,Class<? extends EntityLiving>> discoveredMobs = new PlayerDiscoveryList<>(new DiscoveryMobSerializer());
	private final PlayerDiscoveryList<ObjectDummy,String> discoveredMisc = new PlayerDiscoveryList<>(new DiscoveryStringSerializer());
	
	private final TIntHashSet unlockedFragments = new TIntHashSet();
	
	public CompendiumFile(String filename){
		super("compendium",filename);
	}
	
	@SideOnly(Side.CLIENT)
	public CompendiumFile(NBTTagCompound nbt){
		super("","");
		onLoad(nbt);
	}
	
	public boolean seenHelp(){
		return seenHelp;
	}
	
	public void setSeenHelp(){
		seenHelp = true;
		setModified();
	}
	
	public int getPoints(){
		return pointAmount;
	}
	
	public void givePoints(int amount){
		pointAmount += Math.max(0,amount);
		setModified();
	}
	
	public void payPoints(int amount){
		pointAmount = Math.max(0,pointAmount-amount);
		setModified();
	}
	
	public boolean tryDiscoverObject(KnowledgeObject<?> object, boolean addReward){
		IKnowledgeObjectInstance<?> obj = object.getObject();
		
		if (obj instanceof ObjectBlock)return tryDiscoverBlock((KnowledgeObject<ObjectBlock>)object,addReward);
		else if (obj instanceof ObjectItem)return tryDiscoverItem((KnowledgeObject<ObjectItem>)object,addReward);
		else if (obj instanceof ObjectMob)return tryDiscoverMob((KnowledgeObject<ObjectMob>)object,addReward);
		else if (obj instanceof ObjectDummy)return discoveredMisc.addObject(((KnowledgeObject<ObjectDummy>)object).getObject());
		else return false;
	}
	
	public boolean hasDiscoveredObject(KnowledgeObject<?> object){
		if (object == KnowledgeRegistrations.HELP)return true;
		
		IKnowledgeObjectInstance<?> obj = object.getObject();
		
		if (obj instanceof ObjectBlock)return discoveredBlocks.hasDiscoveredObject((ObjectBlock)obj);
		else if (obj instanceof ObjectItem)return discoveredItems.hasDiscoveredObject((ObjectItem)obj);
		else if (obj instanceof ObjectMob)return discoveredMobs.hasDiscoveredObject((ObjectMob)obj);
		else if (obj instanceof ObjectDummy)return discoveredMisc.hasDiscoveredObject((ObjectDummy)obj);
		else return false;
	}
	
	public boolean tryDiscoverBlock(KnowledgeObject<ObjectBlock> block, boolean addReward){
		if (discoveredBlocks.addObject(block.getObject())){
			onDiscover(block,addReward);
			return true;
		}
		else return false;
	}
	
	public boolean hasDiscoveredBlock(KnowledgeObject<ObjectBlock> block){
		return discoveredBlocks.hasDiscoveredObject(block.getObject());
	}
	
	public boolean tryDiscoverItem(KnowledgeObject<ObjectItem> item, boolean addReward){
		if (discoveredItems.addObject(item.getObject())){
			onDiscover(item,addReward);
			return true;
		}
		else return false;
	}
	
	public boolean hasDiscoveredItem(KnowledgeObject<ObjectItem> item){
		return discoveredItems.hasDiscoveredObject(item.getObject());
	}
	
	public boolean tryDiscoverMob(KnowledgeObject<ObjectMob> mob, boolean addReward){
		if (discoveredMobs.addObject(mob.getObject())){
			onDiscover(mob,addReward);
			return true;
		}
		else return false;
	}
	
	public boolean hasDiscoveredMob(KnowledgeObject<ObjectMob> mob){
		return discoveredMobs.hasDiscoveredObject(mob.getObject());
	}
	
	private void onDiscover(KnowledgeObject<?> object, boolean addReward){
		if (addReward){
			unlockDiscoveryFragments(object);
			pointAmount += object.getDiscoveryReward();
		}
		
		setModified();
	}
	
	private void unlockDiscoveryFragments(KnowledgeObject<?> object){
		for(KnowledgeFragment fragment:object.getFragments()){
			if (fragment.isUnlockedOnDiscovery())tryUnlockFragment(fragment);
		}
	}
	
	public boolean tryUnlockFragment(KnowledgeFragment fragment){
		if (unlockedFragments.add(fragment.globalID)){
			for(int cascade:fragment.getUnlockCascade())tryUnlockFragment(KnowledgeFragment.getById(cascade));
			setModified();
			return true;
		}
		else return false;
	}
	
	public boolean hasUnlockedFragment(KnowledgeFragment fragment){
		return fragment != null && unlockedFragments.contains(fragment.globalID);
	}
	
	public FragmentPurchaseStatus canPurchaseFragment(KnowledgeFragment fragment){
		if (!fragment.isBuyable())return FragmentPurchaseStatus.NOT_BUYABLE;
		if (pointAmount < fragment.getPrice())return FragmentPurchaseStatus.NOT_ENOUGH_POINTS;
		
		for(int requirement:fragment.getUnlockRequirements()){
			if (!unlockedFragments.contains(requirement))return FragmentPurchaseStatus.REQUIREMENTS_UNFULFILLED;
		}
		
		return FragmentPurchaseStatus.CAN_PURCHASE;
	}
	
	public void reset(){
		onLoad(new NBTTagCompound());
		setModified();
	}
	
	@Override
	public void onSave(NBTTagCompound nbt){
		nbt.setBoolean("hlp",seenHelp);
		nbt.setInteger("kps",pointAmount);
		nbt.setTag("fndBlocks",discoveredBlocks.saveToNBTList());
		nbt.setTag("fndItems",discoveredItems.saveToNBTList());
		nbt.setTag("fndMobs",discoveredMobs.saveToNBTList());
		nbt.setTag("fndMisc",discoveredMisc.saveToNBTList());
		nbt.setTag("unlocked",new NBTTagIntArray(unlockedFragments.toArray()));
	}

	@Override
	protected void onLoad(NBTTagCompound nbt){
		seenHelp = nbt.getBoolean("hlp");
		pointAmount = nbt.getInteger("kps");
		discoveredBlocks.loadFromNBTList(nbt.getTagList("fndBlocks",NBT.TAG_STRING));
		discoveredItems.loadFromNBTList(nbt.getTagList("fndItems",NBT.TAG_STRING));
		discoveredMobs.loadFromNBTList(nbt.getTagList("fndMobs",NBT.TAG_STRING));
		discoveredMisc.loadFromNBTList(nbt.getTagList("fndMisc",NBT.TAG_STRING));
		unlockedFragments.clear();
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
			int secondColonIndex = data.lastIndexOf(":");
			String meta = data.substring(secondColonIndex+1);
			
			if (!meta.equals("-1") && !StringUtils.isNumeric(meta))return null;
			else return new BlockMetaWrapper(GameData.getBlockRegistry().getObject(data.substring(0,secondColonIndex)),Integer.parseInt(meta));
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
			return GameData.getItemRegistry().getObject(data);
		}
	}
	
	private static class DiscoveryMobSerializer implements IObjectSerializer<Class<? extends EntityLiving>>{
		@Override
		public String serialize(Class<? extends EntityLiving> object){
			return (String)EntityList.classToStringMapping.get(object);
		}

		@Override
		public Class<? extends EntityLiving> deserialize(String data){
			return (Class<? extends EntityLiving>)EntityList.stringToClassMapping.get(data);
		}
	}
	
	private static class DiscoveryStringSerializer implements IObjectSerializer<String>{
		@Override
		public String serialize(String object){
			return object;
		}

		@Override
		public String deserialize(String data){
			return data;
		}
	}
	
	// ENUMS
	
	public enum FragmentPurchaseStatus{
		CAN_PURCHASE, NOT_BUYABLE, NOT_ENOUGH_POINTS, REQUIREMENTS_UNFULFILLED
	}
}
