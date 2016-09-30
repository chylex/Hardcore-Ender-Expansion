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
	private int prevOffset = Integer.MIN_VALUE;
	
	public CompendiumTabHandler(GuiEnderCompendium compendium){
		this.gui = compendium;
		
		int tabY = 0;
		
		for(KnowledgeCategory category:KnowledgeCategories.list){
			categories.add(new CompendiumCategoryElement(category, tabY));
			tabY += 25;
		}
		
		selected = categories.get(0);
	}
	
	public boolean onMouseClick(int mouseX, int mouseY, int mouseButton){
		for(CompendiumCategoryElement element:categories){
			if (element.isMouseOver(mouseX, mouseY, element == selected)){
				selected = element;
				gui.moveToObject(element.category.getTargetObj(), true);
				return true;
			}
		}
		
		return false;
	}
	
	public void render(int mouseX, int mouseY){
		int offset = -(int)gui.getScrollHandler().getOffset(1F);
		
		if (offset != prevOffset && !gui.getScrollHandler().isAnimating()){
			prevOffset = offset;
			
			if (offset < 0)selected = categories.get(0);
			else{
				for(CompendiumCategoryElement element:categories){
					int y = element.category.getTargetObj().getY()-gui.height/2;
					
					if (offset >= y)selected = element;
					else break;
				}
			}
		}
		
		for(CompendiumCategoryElement element:categories){
			element.render(gui, element == selected);
			if (element.isMouseOver(mouseX, mouseY, element == selected))GuiItemRenderHelper.setupTooltip(mouseX, mouseY, element.category.getTranslatedTooltip());
		}
	}
}
