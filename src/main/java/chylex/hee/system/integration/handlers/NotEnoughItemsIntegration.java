package chylex.hee.system.integration.handlers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import chylex.hee.init.BlockList;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.events.CompendiumEventsClient;
import chylex.hee.mechanics.compendium.objects.IKnowledgeObjectInstance;
import chylex.hee.mechanics.compendium.util.KnowledgeUtils;
import chylex.hee.system.integration.IIntegrationHandler;
import codechicken.nei.api.API;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.guihook.IContainerInputHandler;
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
		
		API.hideItem(new ItemStack(BlockList.special_effects,1,OreDictionary.WILDCARD_VALUE));
		
		GuiContainerManager.inputHandlers.addFirst(new IContainerInputHandler(){
			private boolean handleItemStack(ItemStack is){
				if (is == null)return false;
				
				KnowledgeObject<? extends IKnowledgeObjectInstance<?>> obj = KnowledgeUtils.tryGetFromItemStack(is);
				
				if (obj != null && CompendiumEventsClient.canOpenCompendium()){
					CompendiumEventsClient.openCompendium(obj,Minecraft.getMinecraft().currentScreen);
					return true;
				}
				
				return false;
			}
			
			@Override
			public boolean lastKeyTyped(GuiContainer gui, char keyChar, int keyCode){
				return keyCode == CompendiumEventsClient.getCompendiumKeyCode() ? handleItemStack(GuiContainerManager.getStackMouseOver(gui)) : false;
			}
			
			@Override
			public boolean mouseClicked(GuiContainer gui, int mouseX, int mouseY, int button){
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
