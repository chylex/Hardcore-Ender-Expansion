package chylex.hee.system.integration.handlers;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import chylex.hee.item.ItemList;
import chylex.hee.item.ItemSpawnEggs;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.events.CompendiumEvents;
import chylex.hee.mechanics.compendium.events.CompendiumEventsClient;
import chylex.hee.mechanics.compendium.objects.IKnowledgeObjectInstance;
import chylex.hee.mechanics.compendium.objects.ObjectItem;
import chylex.hee.mechanics.compendium.objects.ObjectMob;
import chylex.hee.system.integration.IIntegrationHandler;
import codechicken.nei.LayoutManager;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.guihook.IContainerInputHandler;
import codechicken.nei.recipe.GuiRecipe;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import cpw.mods.fml.relauncher.Side;

public class NotEnoughItemsIntegration implements IIntegrationHandler{
	@Override
	public String getModId(){
		return "NotEnoughItems";
	}

	@Override
	public void integrate(){
		if (FMLCommonHandler.instance().getSide() != Side.CLIENT)return;
		
		GuiContainerManager.inputHandlers.addFirst(new IContainerInputHandler(){
			private boolean handleItemStack(ItemStack is){
				if (is == null)return false;
				
				UniqueIdentifier uniqueId = null;
				
				try{
					uniqueId = GameRegistry.findUniqueIdentifierFor(is.getItem());
				}
				catch(Exception e){} // protection against idiots who can't register their shit properly
				
				if (uniqueId != null && uniqueId.modId.equalsIgnoreCase("hardcoreenderexpansion")){
					KnowledgeObject<? extends IKnowledgeObjectInstance<?>> obj = null;
					
					if (is.getItem() == ItemList.spawn_eggs)obj = KnowledgeObject.<ObjectMob>getObject(ItemSpawnEggs.getMobFromDamage(is.getItemDamage()));
					else if (is.getItem() instanceof ItemBlock)obj = CompendiumEvents.getBlockObject(is);
					else obj = KnowledgeObject.<ObjectItem>getObject(is.getItem());
					
					CompendiumEventsClient.openCompendium(obj);
					return true;
				}
				
				return false;
			}
			
			@Override
			public boolean lastKeyTyped(GuiContainer gui, char keyChar, int keyCode){
				if (keyCode == NEIClientConfig.getKeyBinding("gui.usage") || keyCode == NEIClientConfig.getKeyBinding("gui.recipe")){
					return handleItemStack(GuiContainerManager.getStackMouseOver(gui));
				}
				else return false;
			}
			
			@Override
			public boolean mouseClicked(GuiContainer gui, int mouseX, int mouseY, int button){
				if (button == 0 || button == 1){
					if (gui instanceof GuiRecipe)return handleItemStack(GuiContainerManager.getStackMouseOver(gui));
					else return handleItemStack(LayoutManager.itemPanel.getStackMouseOver(mouseX,mouseY));
				}
				else return false;
			}
			
			@Override
			public void onMouseUp(GuiContainer gui, int mouseX, int mouseY, int button){}
			
			@Override
			public void onMouseScrolled(GuiContainer gui, int mouseX, int mouseY, int scrolled){}
			
			@Override
			public void onMouseDragged(GuiContainer gui, int mouseX, int mouseY, int button, long heldTime){}
			
			@Override
			public void onMouseClicked(GuiContainer gui, int mouseX, int mouseY, int button){}
			
			@Override
			public void onKeyTyped(GuiContainer gui, char keyChar, int keyCode){}
			
			@Override
			public boolean mouseScrolled(GuiContainer gui, int mouseX, int mouseY, int scrolled){
				return false;
			}
			
			@Override
			public boolean keyTyped(GuiContainer gui, char keyChar, int keyCode){
				return false;
			}
		});
	}
}
