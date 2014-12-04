package chylex.hee.mechanics.compendium.content;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import chylex.hee.mechanics.compendium.objects.IKnowledgeObjectInstance;
import chylex.hee.mechanics.compendium.util.IGuiItemStackRenderer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class KnowledgeObject<T extends IKnowledgeObjectInstance<?>> implements IGuiItemStackRenderer{
	private static int lastUsedID = 0;
	private static final int iconDist = 12;
	private static final TIntObjectHashMap<KnowledgeObject<?>> allObjects = new TIntObjectHashMap<>();
	
	public static <T extends IKnowledgeObjectInstance<?>> KnowledgeObject<T> getObject(Object o){
		for(KnowledgeObject<?> knowledgeObject:allObjects.valueCollection()){
			if (knowledgeObject.theObject.checkEquality(o))return (KnowledgeObject<T>)knowledgeObject;
		}
		
		return null;
	}
	
	public static <T extends IKnowledgeObjectInstance<?>> KnowledgeObject<T> getObjectById(int id){
		return (KnowledgeObject<T>)allObjects.get(id);
	}
	
	public static ImmutableList<KnowledgeObject<?>> getAllObjects(){
		return ImmutableList.copyOf(allObjects.valueCollection());
	}
	
	public final int globalID;
	private final T theObject;
	private final ItemStack itemToRender;
	private final String tooltip;
	private final Set<KnowledgeFragment> fragmentSet = new LinkedHashSet<>();
	private ImmutableSet<KnowledgeFragment> fragmentSetImmutable = ImmutableSet.of();
	private int x, y, unlockPrice, reward;
	
	public KnowledgeObject(T theObject){
		this(theObject,theObject.createItemStackToRender());
	}
	
	public KnowledgeObject(T theObject, ItemStack itemToRender){
		this(theObject,itemToRender,itemToRender.getDisplayName());
	}
	
	public KnowledgeObject(T theObject, String tooltip){
		this(theObject,theObject.createItemStackToRender(),tooltip);
	}
	
	public KnowledgeObject(T theObject, ItemStack itemToRender, String tooltip){
		this.theObject = theObject;
		this.itemToRender = itemToRender;
		this.tooltip = tooltip;
		this.globalID = ++lastUsedID;
		allObjects.put(globalID,this);
	}
	
	public KnowledgeObject setPos(int x, int y){
		if (x < 0 || y < 0)throw new IllegalArgumentException("Invalid KnowledgeObject position, x and y have to be >= 0 ("+x+","+y+")");
		this.x = (x+1)*iconDist;
		this.y = y*iconDist;
		return this;
	}
	
	public KnowledgeObject setCategoryObject(KnowledgeCategory category){
		this.x = Integer.MIN_VALUE;
		category.setCategoryObject(this);
		return this;
	}
	
	public KnowledgeObject setUnlockPrice(int price){
		this.unlockPrice = price;
		return this;
	}
	
	public KnowledgeObject setNonBuyable(){
		this.unlockPrice = -1;
		return this;
	}
	
	public KnowledgeObject setDiscoveryReward(int reward){
		this.reward = reward;
		return this;
	}
	
	public KnowledgeObject addFragments(KnowledgeFragment[] fragments){
		for(KnowledgeFragment fragment:fragments)fragmentSet.add(fragment);
		fragmentSetImmutable = ImmutableSet.copyOf(fragmentSet);
		return this;
	}
	
	public Set<KnowledgeFragment> getFragments(){
		return fragmentSetImmutable;
	}
	
	public T getObject(){
		return theObject;
	}
	
	public boolean isCategoryObject(){
		return x == Integer.MIN_VALUE;
	}
	
	public boolean isBuyable(){
		return unlockPrice != -1;
	}
	
	public int getUnlockPrice(){
		return unlockPrice;
	}
	
	public int getDiscoveryReward(){
		return reward;
	}
	
	@Override
	public int getX(){
		return x;
	}

	@Override
	public int getY(){
		return y;
	}

	@Override
	public ItemStack getItemStack(){
		return itemToRender;
	}

	@Override
	public String getTooltip(){
		return I18n.format(tooltip);
	}
	
	@Override
	public boolean equals(Object o){
		if (o instanceof KnowledgeObject){
			KnowledgeObject obj = (KnowledgeObject)o;
			return obj.globalID == globalID || obj.theObject == theObject;
		}
		else return false;
	}
	
	@Override
	public int hashCode(){
		return globalID;
	}
	
	public static final class LinkedKnowledgeObject<T extends IKnowledgeObjectInstance<?>> extends KnowledgeObject<T>{
		private final KnowledgeObject<T> linkedObject;
		
		public LinkedKnowledgeObject(KnowledgeObject<T> linkedObject){
			this(linkedObject,linkedObject.getItemStack());
		}
		
		public LinkedKnowledgeObject(KnowledgeObject<T> linkedObject, ItemStack itemToRender){
			this(linkedObject,itemToRender,itemToRender.getDisplayName());
		}
		
		public LinkedKnowledgeObject(KnowledgeObject<T> linkedObject, String tooltip){
			this(linkedObject,linkedObject.getItemStack(),tooltip);
		}
		
		public LinkedKnowledgeObject(KnowledgeObject<T> linkedObject, ItemStack itemToRender, String tooltip){
			super(linkedObject.getObject(),itemToRender,tooltip);
			this.linkedObject = linkedObject;
		}
		
		@Override
		public KnowledgeObject setUnlockPrice(int price){
			throw new UnsupportedOperationException("Cannot modify unlock price in linked Knowledge Objects.");
		}
		
		@Override
		public KnowledgeObject setDiscoveryReward(int reward){
			throw new UnsupportedOperationException("Cannot modify discovery reward in linked Knowledge Objects.");
		}
		
		@Override
		public KnowledgeObject addFragments(KnowledgeFragment[] fragments){
			throw new UnsupportedOperationException("Cannot modify fragments in linked Knowledge Objects.");
		}
		
		@Override
		public boolean isBuyable(){
			return linkedObject.isBuyable();
		}
		
		@Override
		public int getUnlockPrice(){
			return linkedObject.getUnlockPrice();
		}
		
		@Override
		public int getDiscoveryReward(){
			return linkedObject.getDiscoveryReward();
		}
		
		@Override
		public Set<KnowledgeFragment> getFragments(){
			return linkedObject.getFragments();
		}
	}
}
