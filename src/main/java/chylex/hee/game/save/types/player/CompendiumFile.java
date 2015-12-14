package chylex.hee.game.save.types.player;
import gnu.trove.set.hash.TIntHashSet;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagString;
import chylex.hee.game.save.types.PlayerFile;
import chylex.hee.mechanics.compendium.content.KnowledgeFragment;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.content.fragments.KnowledgeFragmentType;
import chylex.hee.mechanics.compendium.content.objects.IObjectHolder;
import chylex.hee.mechanics.compendium.util.KnowledgeSerialization;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C19CompendiumData;
import chylex.hee.system.util.MathUtil;
import chylex.hee.system.util.NBTUtil;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;
import com.google.common.collect.TreeRangeSet;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CompendiumFile extends PlayerFile{
	public static final byte distanceLimit = 2; // 0 = discovered, 2 = unavailable
	
	private int points;
	private final TIntHashSet extraFragments = new TIntHashSet();
	private final TreeRangeSet<Integer> readFragments = TreeRangeSet.create();
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
	
	public boolean tryDiscoverObject(EntityPlayer player, KnowledgeObject<? extends IObjectHolder<?>> obj){
		if (unlockObject(obj)){
			points += obj.getReward();
			PacketPipeline.sendToPlayer(player,new C19CompendiumData(this,obj));
			return true;
		}
		else return false;
	}
	
	private boolean unlockObject(KnowledgeObject<? extends IObjectHolder<?>> obj){
		boolean added = discoveredObjects.add(obj);
		if (added)setModified();
		return added;
	}
	
	public boolean isDiscovered(KnowledgeObject<? extends IObjectHolder<?>> obj){
		return discoveredObjects.contains(obj);
	}
	
	private int getDiscoveryDistanceInner(KnowledgeObject<? extends IObjectHolder<?>> obj, int currentDistance){
		if (currentDistance == distanceLimit+1 || discoveredObjects.contains(obj))return currentDistance-1;
		if (obj.isCategoryObject())return currentDistance;
		
		int lowest = distanceLimit;
		
		for(KnowledgeObject<? extends IObjectHolder<?>> parent:obj.getParents()){
			lowest = Math.min(lowest,getDiscoveryDistanceInner(parent,currentDistance+1));
		}
		
		return lowest;
	}
	
	public int getDiscoveryDistance(KnowledgeObject<? extends IObjectHolder<?>> obj){
		return getDiscoveryDistanceInner(obj,0);
	}
	
	// Fragments
	
	public boolean tryPurchaseFragment(EntityPlayer player, KnowledgeFragment fragment){
		if (points >= fragment.getPrice() && unlockFragment(fragment)){
			offsetPoints(-fragment.getPrice());
			PacketPipeline.sendToPlayer(player,new C19CompendiumData(this));
			return true;
		}
		else return false;
	}
	
	public boolean tryUnlockHintFragment(EntityPlayer player, KnowledgeFragment fragment){
		if (fragment.getType() != KnowledgeFragmentType.HINT)return false;
		
		if (extraFragments.add(fragment.globalID)){
			PacketPipeline.sendToPlayer(player,new C19CompendiumData(this));
			return true;
		}
		else return false;
	}
	
	public boolean unlockFragment(KnowledgeFragment fragment){
		if (fragment.getType() != KnowledgeFragmentType.SECRET && fragment.getType() != KnowledgeFragmentType.HINT)return false;
		
		boolean added = extraFragments.add(fragment.globalID);
		if (added)markFragmentAsRead(fragment.globalID); // also calls setModified
		return added;
	}
	
	public boolean canSeeFragment(KnowledgeObject<? extends IObjectHolder<?>> obj, KnowledgeFragment fragment){
		switch(fragment.getType()){
			case VISIBLE: return true;
			case ESSENTIAL: return isDiscovered(obj) && getDiscoveryDistance(obj) <= 1;
			case DISCOVERY: return isDiscovered(obj);
			default: return extraFragments.contains(fragment.globalID);
		}
	}
	
	public void markFragmentAsRead(int id){
		readFragments.add(Range.singleton(id).canonical(DiscreteDomain.integers()));
		setModified();
	}
	
	public boolean hasReadFragment(KnowledgeFragment fragment){
		return readFragments.contains(fragment.globalID);
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
		
		StringBuilder build = new StringBuilder();
		
		for(Range<Integer> range:readFragments.asRanges()){
			build.append((char)(32+range.lowerEndpoint().shortValue())).append((char)(32+range.upperEndpoint().shortValue()));
		}
		
		nbt.setString("rfg",build.toString());
	}

	@Override
	protected void onLoad(NBTTagCompound nbt){
		points = nbt.getShort("pts");
		extraFragments.clear();
		extraFragments.addAll(nbt.getIntArray("efg"));
		discoveredObjects.clear();
		NBTUtil.readStringList(nbt,"obj").map(KnowledgeSerialization::deserialize).filter(Objects::nonNull).forEach(discoveredObjects::add);
		
		readFragments.clear();
		String src = nbt.getString("rfg");
		
		for(int chr = 0; chr < src.length()-1; chr += 2){
			readFragments.add(Range.closedOpen(src.charAt(chr)-32,src.charAt(chr+1)-32)); // closedOpen because ranges be weird
		}
	}
}
