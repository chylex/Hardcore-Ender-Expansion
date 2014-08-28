package chylex.hee.mechanics.compendium.content.type;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.client.Minecraft;
import org.apache.commons.lang3.ArrayUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class KnowledgeFragment{
	private static final TIntObjectMap<KnowledgeFragment> allFragments = new TIntObjectHashMap<>();
	
	public static final Collection<KnowledgeFragment> getAllFragments(){
		return Collections.unmodifiableCollection(allFragments.valueCollection());
	}
	
	public static final KnowledgeFragment getById(int globalID){
		return allFragments.get(globalID);
	}
	
	public final int globalID;
	private int price = 10;
	private boolean unlockOnDiscovery = false;
	private int[] unlockRequirements = ArrayUtils.EMPTY_INT_ARRAY;
	
	public KnowledgeFragment(int globalID){
		this.globalID = globalID;
		if (allFragments.putIfAbsent(globalID,this) != null)throw new IllegalArgumentException("Could not initialize Knowledge Fragments, global fragment ID "+globalID+" is already taken!");
	}
	
	public KnowledgeFragment setPrice(int price){
		this.price = price;
		return this;
	}
	
	public int getPrice(){
		return price;
	}
	
	public KnowledgeFragment setNonBuyable(){
		this.price = -1;
		return this;
	}
	
	public boolean isBuyable(){
		return price != -1;
	}
	
	public KnowledgeFragment setUnlockOnDiscovery(){
		this.unlockOnDiscovery = true;
		return this;
	}
	
	public boolean isUnlockedOnDiscovery(){
		return unlockOnDiscovery;
	}
	
	public KnowledgeFragment setUnlockRequirements(int...requirements){
		this.unlockRequirements = requirements;
		return this;
	}

	@SideOnly(Side.CLIENT)
	public abstract int getHeight(Minecraft mc, boolean isUnlocked);
	
	@SideOnly(Side.CLIENT)
	public abstract void render(int x, int y, Minecraft mc, boolean isUnlocked);
	
	@Override
	public final boolean equals(Object o){
		return o instanceof KnowledgeFragment && ((KnowledgeFragment)o).globalID == globalID;
	}
	
	@Override
	public final int hashCode(){
		return globalID;
	}
}
