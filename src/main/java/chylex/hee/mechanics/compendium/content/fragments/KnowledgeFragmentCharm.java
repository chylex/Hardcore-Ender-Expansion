package chylex.hee.mechanics.compendium.content.fragments;
import java.util.Map.Entry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import chylex.hee.gui.GuiEnderCompendium;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.charms.CharmRecipe;
import chylex.hee.mechanics.charms.RuneType;
import chylex.hee.mechanics.compendium.content.KnowledgeFragment;

public class KnowledgeFragmentCharm extends KnowledgeFragment{
	private ItemStack[] runes = new ItemStack[5];
	private ItemStack charm;
	
	public KnowledgeFragmentCharm(int globalID){
		super(globalID);
	}
	
	public KnowledgeFragmentCharm setRecipe(CharmRecipe recipe){
		this.charm = new ItemStack(ItemList.charm,1,recipe.id);
		
		int a = 0;
		for(Entry<RuneType,Byte> entry:recipe.getRunes().entrySet()){
			runes[a++] = new ItemStack(ItemList.rune,entry.getValue(),entry.getKey().damage);
		}
		
		return this;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getHeight(GuiEnderCompendium gui, boolean isUnlocked){
		return 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean onClick(GuiEnderCompendium gui, int x, int y, int mouseX, int mouseY, int buttonId, boolean isUnlocked){
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onRender(GuiEnderCompendium gui, int x, int y, int mouseX, int mouseY, boolean isUnlocked){
		
	}
}
