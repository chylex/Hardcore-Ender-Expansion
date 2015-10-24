package chylex.hee.game.save.types.player;
import gnu.trove.set.hash.TIntHashSet;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagString;
import chylex.hee.game.save.types.PlayerFile;
import chylex.hee.mechanics.compendium.content.KnowledgeFragment;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.content.fragments.KnowledgeFragmentType;
import chylex.hee.mechanics.compendium.content.objects.IObjectHolder;
import chylex.hee.mechanics.compendium.util.KnowledgeSerialization;
import chylex.hee.system.util.MathUtil;
import chylex.hee.system.util.NBTUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CompendiumFile extends PlayerFile{
	private static final byte distanceLimit = 4; // 0 = discovered, 4 = unavailable
	
	private int points;
	private final TIntHashSet extraFragments = new TIntHashSet();
	private final Set<KnowledgeObject<? extends IObjectHolder<?>>> discoveredObjects = new HashSet<>(32);
	
	public CompendiumFile(String filename){
		super("compendium",filename);
	}
	
	@SideOnly(Side.CLIENT)
	public CompendiumFile(NBTTagCompound nbt){
		super("","");
		onLoad(nbt);
	}
	
	// Points
	
	public int getPoints(){
		return points;
	}
	
	public void offsetPoints(int amount){
		points = MathUtil.clamp(points+amount,0,Short.MAX_VALUE);
		setModified();
	}
	
	// Discovery
	
	public void discoverObject(KnowledgeObject<? extends IObjectHolder<?>> obj){
		discoveredObjects.add(obj);
		setModified();
	}
	
	public boolean isDiscovered(KnowledgeObject<? extends IObjectHolder<?>> obj){
		return discoveredObjects.contains(obj);
	}
	
	public int getDiscoveryDistance(KnowledgeObject<? extends IObjectHolder<?>> obj, int limit){
		for(int level = 0; level < limit; level++){
			if (discoveredObjects.contains(obj))return level;
			else obj = obj.getParent();
		}
		
		return limit;
	}
	
	// Fragments
	
	public void unlockFragment(KnowledgeFragment fragment){
		if (fragment.getType() != KnowledgeFragmentType.SECRET || fragment.getType() != KnowledgeFragmentType.HINT){
			throw new IllegalArgumentException("Fragment is not eligible for manual unlocking!");
		}
		
		extraFragments.add(fragment.globalID);
		setModified();
	}
	
	public boolean canSeeFragment(KnowledgeObject<? extends IObjectHolder<?>> obj, KnowledgeFragment fragment){
		switch(fragment.getType()){
			case VISIBLE: return true;
			case ESSENTIAL: return getDiscoveryDistance(obj,distanceLimit) != distanceLimit;
			case DISCOVERY: return getDiscoveryDistance(obj,distanceLimit) == 0;
			default: return extraFragments.contains(fragment.globalID);
		}
	}
	
	// Saving & Loading
	
	public void reset(){
		onLoad(new NBTTagCompound());
		setModified();
	}
	
	@Override
	public void onSave(NBTTagCompound nbt){
		nbt.setShort("pts",(short)points);
		nbt.setTag("efg",new NBTTagIntArray(extraFragments.toArray()));
		NBTUtil.writeList(nbt,"obj",discoveredObjects.stream().map(obj -> new NBTTagString(KnowledgeSerialization.serialize(obj))));
	}

	@Override
	protected void onLoad(NBTTagCompound nbt){
		points = nbt.getShort("pts");
		extraFragments.clear();
		extraFragments.addAll(nbt.getIntArray("efg"));
		discoveredObjects.clear();
		NBTUtil.readStringList(nbt,"obj").map(KnowledgeSerialization::deserialize).filter(Objects::nonNull).forEach(discoveredObjects::add);
	}
}
