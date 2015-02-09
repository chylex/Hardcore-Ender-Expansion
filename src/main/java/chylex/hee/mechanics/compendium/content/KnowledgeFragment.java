package chylex.hee.mechanics.compendium.content;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.Collection;
import java.util.Collections;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ArrayUtils;
import chylex.hee.gui.GuiEnderCompendium;

public abstract class KnowledgeFragment{
	private static final TIntObjectMap<KnowledgeFragment> allFragments = new TIntObjectHashMap<>();
	
	public static final Collection<KnowledgeFragment> getAllFragments(){
		return Collections.unmodifiableCollection(allFragments.valueCollection());
	}
	
	public static final KnowledgeFragment getById(int globalID){
		return allFragments.get(globalID);
	}
	
	public final int globalID;
	private int price;
	private boolean unlockOnDiscovery;
	private int[] unlockRequirements = ArrayUtils.EMPTY_INT_ARRAY;
	private int[] unlockCascade = ArrayUtils.EMPTY_INT_ARRAY;
	
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
	
	public int[] getUnlockRequirements(){
		return unlockRequirements;
	}
	
	public KnowledgeFragment setUnlockCascade(int...cascade){
		this.unlockCascade = cascade;
		return this;
	}
	
	public int[] getUnlockCascade(){
		return unlockCascade;
	}

	@SideOnly(Side.CLIENT)
	public abstract int getHeight(GuiEnderCompendium gui, boolean isUnlocked);
	
	@SideOnly(Side.CLIENT)
	public abstract boolean onClick(GuiEnderCompendium gui, int x, int y, int mouseX, int mouseY, int buttonId, boolean isUnlocked);
	
	@SideOnly(Side.CLIENT)
	public abstract void onRender(GuiEnderCompendium gui, int x, int y, int mouseX, int mouseY, boolean isUnlocked);
	
	@Override
	public final boolean equals(Object o){
		return o instanceof KnowledgeFragment && ((KnowledgeFragment)o).globalID == globalID;
	}
	
	@Override
	public final int hashCode(){
		return globalID;
	}
}
