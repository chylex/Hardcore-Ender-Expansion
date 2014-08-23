package chylex.hee.mechanics.compendium.content.fragments;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.ArrayUtils;

public abstract class KnowledgeFragment{
	private static final Set<KnowledgeFragment> allFragments = new HashSet<>();
	
	public static final Set<KnowledgeFragment> getAllFragments(){
		return Collections.unmodifiableSet(allFragments);
	}
	
	public final int globalID;
	private int price = 10;
	private int[] unlockRequirements = ArrayUtils.EMPTY_INT_ARRAY;
	
	public KnowledgeFragment(int globalID){
		this.globalID = globalID;
		if (!allFragments.add(this))throw new IllegalArgumentException("Could not initialize Knowledge Fragments, global fragment ID "+globalID+" is already taken!");
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
	
	public KnowledgeFragment setUnlockRequirements(int...requirements){
		this.unlockRequirements = requirements;
		return this;
	}
	
	@Override
	public final boolean equals(Object o){
		return o instanceof KnowledgeFragment && ((KnowledgeFragment)o).globalID == globalID;
	}
	
	@Override
	public final int hashCode(){
		return globalID;
	}
}
