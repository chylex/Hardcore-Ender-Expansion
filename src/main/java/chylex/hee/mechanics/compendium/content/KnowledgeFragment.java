package chylex.hee.mechanics.compendium.content;
import chylex.hee.gui.GuiEnderCompendium;
import chylex.hee.mechanics.compendium.content.fragments.FragmentType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class KnowledgeFragment{
	public final int globalID;
	public final FragmentType type;
	public final int price;
	
	public KnowledgeFragment(int globalID, FragmentType type){
		this(globalID,type,0);
	}
	
	public KnowledgeFragment(int globalID, FragmentType type, int price){
		if ((price == 0) ^ (type == FragmentType.SECRET)){
			throw new IllegalArgumentException(price == 0 ? "Secret fragments need to have a price!" : "Only secret fragments can have a price!");
		}
		
		this.globalID = globalID;
		this.type = type;
		this.price = price;
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
}
