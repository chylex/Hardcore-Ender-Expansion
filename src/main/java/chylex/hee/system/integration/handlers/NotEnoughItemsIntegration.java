package chylex.hee.system.integration.handlers;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.events.CompendiumEventsClient;
import chylex.hee.mechanics.compendium.objects.IKnowledgeObjectInstance;
import chylex.hee.mechanics.compendium.util.KnowledgeUtils;
import chylex.hee.system.integration.IIntegrationHandler;
import codechicken.nei.LayoutManager;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.guihook.IContainerInputHandler;
import codechicken.nei.recipe.GuiRecipe;
import cpw.mods.fml.common.FMLCommonHandler;
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
				if (is == null || Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))return false;
				
				KnowledgeObject<? extends IKnowledgeObjectInstance<?>> obj = KnowledgeUtils.tryGetFromItemStack(is);
				
				if (obj != null){
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
					else if (LayoutManager.itemPanel != null)return handleItemStack(LayoutManager.itemPanel.getStackMouseOver(mouseX,mouseY));
				}
				
				return false;
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
