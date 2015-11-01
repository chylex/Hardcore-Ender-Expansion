package chylex.hee.mechanics.compendium.handlers;
import java.util.ArrayList;
import java.util.List;
import chylex.hee.gui.GuiEnderCompendium;
import chylex.hee.gui.helpers.GuiItemRenderHelper;
import chylex.hee.mechanics.compendium.KnowledgeCategories;
import chylex.hee.mechanics.compendium.content.KnowledgeCategory;
import chylex.hee.mechanics.compendium.elements.CompendiumCategoryElement;

public final class CompendiumTabHandler{
	private final GuiEnderCompendium gui;
	private final List<CompendiumCategoryElement> categories = new ArrayList<>(KnowledgeCategories.list.length);
	private CompendiumCategoryElement selected;
	
	public CompendiumTabHandler(GuiEnderCompendium compendium){
		this.gui = compendium;
		
		int tabY = 0;
		
		for(KnowledgeCategory category:KnowledgeCategories.list){
			categories.add(new CompendiumCategoryElement(category,tabY));
			tabY += 25;
		}
		
		selected = categories.get(0);
	}
	
	public boolean onMouseClick(int mouseX, int mouseY, int mouseButton){
		for(CompendiumCategoryElement element:categories){
			if (element.isMouseOver(mouseX,mouseY,element == selected)){
				selected = element;
				return true;
			}
		}
		
		return false;
	}
	
	public void render(int mouseX, int mouseY){
		for(CompendiumCategoryElement element:categories){
			element.render(gui,element == selected);
			if (element.isMouseOver(mouseX,mouseY,element == selected))GuiItemRenderHelper.setupTooltip(mouseX,mouseY,element.category.getTranslatedTooltip());
		}
	}
}
