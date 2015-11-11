package chylex.hee.mechanics.compendium.content;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import chylex.hee.mechanics.compendium.content.objects.IObjectHolder;
import chylex.hee.mechanics.compendium.elements.CompendiumObjectElement.ObjectShape;
import chylex.hee.mechanics.compendium.util.KnowledgeUtils;
import chylex.hee.system.collections.CollectionUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class KnowledgeObject<T extends IObjectHolder<?>>{
	private static int prevID = 0;
	private static final TIntObjectHashMap<KnowledgeObject<?>> allObjects = new TIntObjectHashMap<>();
	
	public static final Collection<KnowledgeObject<?>> getAllObjects(){
		return allObjects.valueCollection();
	}
	
	public static final <T extends IObjectHolder<?>> KnowledgeObject<T> fromObject(Object o){
		return (KnowledgeObject<T>)getAllObjects().stream().filter(knowledgeObj -> knowledgeObj.holder.checkEquality(o)).findAny().orElse(null);
	}
	
	public static final <T extends IObjectHolder<?>> KnowledgeObject<T> fromObject(ItemStack is){
		if (!KnowledgeUtils.isItemStackViable(is))return null;
		return (KnowledgeObject<T>)getAllObjects().stream().filter(knowledgeObj -> knowledgeObj.holder.checkEquality(is)).findAny().orElse(null);
	}
	
	public static final <T extends IObjectHolder<?>> KnowledgeObject<T> fromID(int id){
		return (KnowledgeObject<T>)allObjects.get(id);
	}
	
	public final int globalID;
	public final T holder;
	private final String tooltip;
	
	private KnowledgeObject<?> parent;
	private final List<KnowledgeObject<?>> children;
	private final Set<KnowledgeFragment> fragments;
	
	private final List<byte[]> parentLineNodes;
	private final List<byte[]> childLineNodes;
	
	private int x, y, price, reward;
	private ObjectShape shape = ObjectShape.PLAIN;
	private boolean isCategoryObject;
	
	public KnowledgeObject(T holder){
		this(holder,holder.getDisplayItemStack().getDisplayName());
	}
	
	public KnowledgeObject(T holder, String unlocalizedTooltip){
		this.globalID = ++prevID;
		this.holder = holder;
		this.tooltip = unlocalizedTooltip;
		this.children = new ArrayList<>(4);
		this.fragments = new LinkedHashSet<>(6);
		this.parentLineNodes = CollectionUtil.newList(new byte[]{ 0, 0 });
		this.childLineNodes = CollectionUtil.newList(new byte[]{ 0, 0 });
		allObjects.put(globalID,this);
	}
	
	// Category
	
	public KnowledgeObject<T> setCategoryObject(){
		this.isCategoryObject = true;
		return this;
	}
	
	public boolean isCategoryObject(){
		return isCategoryObject;
	}
	
	// Fragments
	
	public KnowledgeObject<T> addFragments(KnowledgeFragment...fragments){
		for(KnowledgeFragment fragment:fragments)this.fragments.add(fragment);
		return this;
	}
	
	public Set<KnowledgeFragment> getFragments(){
		return Collections.unmodifiableSet(fragments);
	}
	
	// Relationship
	
	public KnowledgeObject<T> setParent(KnowledgeObject<?> obj, int offX, int offY){
		this.x = obj.x+offX*12;
		this.y = obj.y+offY*12;
		this.parent = obj;
		this.parent.children.add(this);
		return this;
	}
	
	public KnowledgeObject<?> getParent(){
		return parent;
	}
	
	public List<KnowledgeObject<?>> getChildren(){
		return children;
	}
	
	// Position
	
	public KnowledgeObject<T> setPos(int x, int y){
		this.x = x*12;
		this.y = y*12;
		return this;
	}
	
	public KnowledgeObject<T> setHidden(){
		this.y = -1;
		return this;
	}
	
	public boolean isHidden(){
		return y == -1;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	// Shapes
	
	public KnowledgeObject<T> setImportant(){
		this.shape = ObjectShape.IMPORTANT;
		return this;
	}
	
	public KnowledgeObject<T> setSpecial(){
		this.shape = ObjectShape.SPECIAL;
		return this;
	}
	
	public ObjectShape getShape(){
		return shape;
	}
	
	// Line Nodes
	
	public KnowledgeObject<T> addParentLine(int offsetX, int offsetY){
		parentLineNodes.add(new byte[]{ (byte)(offsetX*12), (byte)(offsetY*12) });
		return this;
	}
	
	public KnowledgeObject<T> addChildLine(int offsetX, int offsetY){
		childLineNodes.add(new byte[]{ (byte)(offsetX*12), (byte)(offsetY*12) });
		return this;
	}
	
	public void connectToChildren(ILineCallback callback){
		byte[] node, lastChildNode;
		
		for(int childIndex = 0; childIndex < childLineNodes.size()-1; childIndex++){
			lastChildNode = childLineNodes.get(childIndex);
			node = childLineNodes.get(childIndex+1);
			callback.call(x+lastChildNode[0],y+lastChildNode[1],x+node[0],y+node[1]);
		}
		
		lastChildNode = childLineNodes.get(childLineNodes.size()-1);
		
		for(KnowledgeObject<?> child:children){
			node = child.parentLineNodes.get(child.parentLineNodes.size()-1);
			callback.call(x+lastChildNode[0],y+lastChildNode[1],child.x+node[0],child.y+node[1]);
			
			byte[] prevParentNode = node;
			
			for(int parentIndex = child.parentLineNodes.size()-2; parentIndex >= 0; parentIndex--){
				node = child.parentLineNodes.get(parentIndex);
				callback.call(child.x+prevParentNode[0],child.y+prevParentNode[1],child.x+node[0],child.y+node[1]);
				prevParentNode = child.parentLineNodes.get(parentIndex);
			}
		}
	}
	
	// Points
	
	public KnowledgeObject<T> setPrice(int points){
		this.price = points;
		return this;
	}
	
	public int getPrice(){
		return price;
	}
	
	public KnowledgeObject<T> setReward(int points){
		this.reward = points;
		return this;
	}
	
	public int getReward(){
		return reward;
	}
	
	// Utilities
	
	public void reset(){
		x = y = price = reward = 0;
		parent = null;
		children.clear();
		fragments.clear();
		parentLineNodes.clear();
		parentLineNodes.add(new byte[]{ 0, 0 });
		childLineNodes.clear();
		childLineNodes.add(new byte[]{ 0, 0 });
	}
	
	@SideOnly(Side.CLIENT)
	public String getTranslatedTooltip(){
		return I18n.format(tooltip);
	}
	
	@Override
	public boolean equals(Object o){
		if (o instanceof KnowledgeObject<?>){
			KnowledgeObject<?> obj = (KnowledgeObject<?>)o;
			return obj.globalID == globalID || obj.holder == holder;
		}
		else return false;
	}
	
	@Override
	public int hashCode(){
		return globalID;
	}
	
	// Line Interface
	
	public static interface ILineCallback{
		void call(int x1, int y1, int x2, int y2);
	}
}
