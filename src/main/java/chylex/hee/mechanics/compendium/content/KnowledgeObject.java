package chylex.hee.mechanics.compendium.content;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.resources.I18n;
import chylex.hee.mechanics.compendium.content.objects.IObjectHolder;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class KnowledgeObject<T extends IObjectHolder<?>>{
	private static int prevID = 0;
	
	public final int globalID;
	public final T holder;
	private final String tooltip;
	
	private KnowledgeObject<?> parent;
	private final List<KnowledgeObject<?>> children;
	private final Set<KnowledgeFragment> fragments;
	
	private int x, y, price, reward;
	
	public KnowledgeObject(T holder){
		this(holder,holder.getDisplayItemStack().getUnlocalizedName());
	}
	
	public KnowledgeObject(T holder, String unlocalizedTooltip){
		this.globalID = ++prevID;
		this.holder = holder;
		this.tooltip = unlocalizedTooltip;
		this.children = new ArrayList<>(4);
		this.fragments = new LinkedHashSet<>(6);
	}
	
	public KnowledgeObject<T> addFragments(KnowledgeFragment...fragments){
		for(KnowledgeFragment fragment:fragments)this.fragments.add(fragment);
		return this;
	}
	
	public Set<KnowledgeFragment> getFragments(){
		return Collections.unmodifiableSet(fragments);
	}
	
	public KnowledgeObject<T> setParent(KnowledgeObject<?> obj, int offX, int offY){
		this.x = obj.getX()+offX;
		this.y = obj.getY()+offY;
		this.parent = obj;
		this.parent.children.add(this);
		return this;
	}
	
	public List<KnowledgeObject<?>> getChildren(){
		return children;
	}
	
	public KnowledgeObject<T> setPos(int x, int y){
		this.x = x;
		this.y = y;
		return this;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
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
}
