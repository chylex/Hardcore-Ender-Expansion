package chylex.hee.mechanics.compendium.content;
import gnu.trove.map.hash.TIntObjectHashMap;
import chylex.hee.gui.GuiEnderCompendium;
import chylex.hee.mechanics.compendium.content.fragments.KnowledgeFragmentType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class KnowledgeFragment<T extends KnowledgeFragment>{
	private static final TIntObjectHashMap<KnowledgeFragment<?>> allFragments = new TIntObjectHashMap<>();
	
	public static KnowledgeFragment<?> fromID(int id){
		return allFragments.get(id);
	}
	
	public final int globalID;
	private KnowledgeFragmentType type;
	private int price;
	
	public KnowledgeFragment(int globalID){
		this.globalID = globalID;
		allFragments.put(globalID,this);
	}
	
	public T setType(KnowledgeFragmentType type){
		return setType(type,0);
	}
	
	public T setType(KnowledgeFragmentType type, int price){
		if ((price == 0) ^ (type == KnowledgeFragmentType.SECRET)){
			throw new IllegalArgumentException(price == 0 ? "Secret fragments need to have a price!" : "Only secret fragments can have a price!");
		}
		
		this.type = type;
		this.price = 0;
		return (T)this;
	}
	
	public KnowledgeFragmentType getType(){
		return type;
	}
	
	public int getPrice(){
		return price;
	}
	
	public final boolean equals(KnowledgeFragment fragment){
		return fragment.globalID == globalID;
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
	
	protected static final boolean checkRect(int mouseX, int mouseY, int x, int y, int w, int h){
		return mouseX >= x && mouseX <= x+w && mouseY >= y && mouseY <= y+h;
	}
}
