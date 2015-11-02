package chylex.hee.mechanics.compendium.handlers;
import gnu.trove.map.hash.TByteObjectHashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import chylex.hee.game.save.types.player.CompendiumFile;
import chylex.hee.gui.GuiButtonPageArrow;
import chylex.hee.gui.GuiEnderCompendium;
import chylex.hee.gui.helpers.GuiHelper;
import chylex.hee.mechanics.compendium.content.KnowledgeFragment;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.elements.CompendiumPurchaseElement;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.server.S01CompendiumReadFragments;
import chylex.hee.packets.server.S02CompendiumPurchase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CompendiumPageHandler{
	public static final int pageWidth = 152, pageHeight = 226, innerWidth = 116, innerHeight = 166;
	public static final ResourceLocation texPage = new ResourceLocation("hardcoreenderexpansion:textures/gui/ender_compendium_page.png");
	
	private final GuiEnderCompendium gui;
	private final TByteObjectHashMap<Map<KnowledgeFragment,Boolean>> currentObjectPages = new TByteObjectHashMap<>(5);
	private final List<CompendiumPurchaseElement> purchaseElements = new ArrayList<>(2);
	private final GuiButtonPageArrow[] pageArrows = new GuiButtonPageArrow[2];
	private final byte[] pageArrowIds = new byte[2];
	
	private int pageX, pageY, innerX, innerY;
	
	private CompendiumFile compendiumFile;
	private KnowledgeObject<?> currentObject;
	private int pageIndex;
	
	public CompendiumPageHandler(GuiEnderCompendium compendium){
		this.gui = compendium;
	}
	
	public void init(List buttonList, int leftArrowId, int rightArrowId){
		pageX = (gui.width-pageWidth)/2;
		pageY = (gui.height-pageHeight)/2;
		innerX = pageX+17;
		innerY = pageY+20;
				
		buttonList.add(pageArrows[0] = new GuiButtonPageArrow(this.pageArrowIds[0] = (byte)leftArrowId,pageX+pageWidth/2-(pageWidth*3/10)-10,pageY+pageHeight-32,false));
		buttonList.add(pageArrows[1] = new GuiButtonPageArrow(this.pageArrowIds[1] = (byte)rightArrowId,pageX+pageWidth/2+(pageWidth*3/10)-10,pageY+pageHeight-32,true));
		for(int a = 0; a < 2; a++)pageArrows[a].visible = false;
		
		onRefresh();
	}
	
	public void setFile(CompendiumFile file){
		this.compendiumFile = file;
	}
	
	public boolean isMouseInside(int mouseX, int mouseY){
		return currentObject != null && mouseX >= pageX+8 && mouseX <= pageX+pageWidth-8 && mouseY >= pageY+9 && mouseY <= pageY+pageHeight-14; // adjust size to exclude edges
	}
	
	public void onButtonClick(GuiButton button){
		if (button.id == pageArrowIds[0])changePage(false);
		else if (button.id == pageArrowIds[1])changePage(true);
	}
	
	public boolean onMouseClick(int mouseX, int mouseY, int mouseButton){
		if (currentObject != null && isMouseInside(mouseX,mouseY)){
			for(CompendiumPurchaseElement element:purchaseElements){
				if (element.isMouseOver(mouseX,mouseY)){
					if (compendiumFile.getPoints() >= element.price)GuiEnderCompendium.sendPacketToServer(new S02CompendiumPurchase(element.fragment));
					return true;
				}
			}
			
			int y = innerY;
			
			for(Entry<KnowledgeFragment,Boolean> entry:currentObjectPages.get((byte)pageIndex).entrySet()){
				if (entry.getKey().onClick(gui,innerX,y,mouseX,mouseY,mouseButton,entry.getValue()))return true;
				y += 8+entry.getKey().getHeight(gui,entry.getValue());
			}
		}
		
		return false;
	}
	
	public boolean onMouseWheel(int mouseX, int mouseY, int value){
		if (currentObject != null && isMouseInside(mouseX,mouseY)){
			changePage(value < 0);
			return true;
		}
		else return false;
	}
	
	public boolean onKeyboardDown(int keyCode){
		if (currentObject == null)return false;
		
		if (keyCode == GuiHelper.keyArrowLeft || keyCode == GuiHelper.keyPageUp || keyCode == gui.mc.gameSettings.keyBindLeft.getKeyCode()){
			changePage(false);
		}
		else if (keyCode == GuiHelper.keyArrowRight || keyCode == GuiHelper.keyPageDown || keyCode == gui.mc.gameSettings.keyBindRight.getKeyCode()){
			changePage(true);
		}
		else if (keyCode == GuiHelper.keyHome){
			pageIndex = 0;
			onRefresh();
		}
		else if (keyCode == GuiHelper.keyEnd){
			pageIndex = currentObjectPages.size()-1;
			onRefresh();
		}
		else return false;
		
		return true;
	}
	
	public void showObject(KnowledgeObject<?> obj){
		if (currentObject != null){
			currentObjectPages.clear();
			purchaseElements.clear();
			
			if (currentObject != obj)pageIndex = 0;
		}

		if ((currentObject = obj) == null)return;
		
		byte page = 0;
		int yy = 0, height = 0;
		boolean isUnlocked = false;
		Map<KnowledgeFragment,Boolean> pageMap = new LinkedHashMap<>();
		Iterator<KnowledgeFragment> iter = currentObject.getFragments().iterator();
		
		while(true){
			KnowledgeFragment fragment = iter.hasNext() ? iter.next() : null;
			
			if (fragment == null || yy+(height = 8+fragment.getHeight(gui,isUnlocked = compendiumFile.canSeeFragment(obj,fragment))) > innerHeight){
				currentObjectPages.put(page++,pageMap);
				
				if (fragment == null)break;
				else{
					pageMap = new LinkedHashMap<>();
					pageMap.put(fragment,isUnlocked);
					yy = height;
					continue;
				}
			}
			
			pageMap.put(fragment,isUnlocked);
			yy += height;
		}
		
		onRefresh();
	}
	
	public void changePage(boolean next){
		pageIndex = next ? Math.min(currentObjectPages.size()-1,pageIndex+1) : Math.max(0,pageIndex-1);
		onRefresh();
	}
	
	private void onRefresh(){
		purchaseElements.clear();
		
		if (currentObject != null){
			Set<KnowledgeFragment> fragments = currentObjectPages.get((byte)pageIndex).keySet();
			Set<KnowledgeFragment> unread = fragments.stream().filter(fragment -> !compendiumFile.hasReadFragment(fragment) && compendiumFile.canSeeFragment(currentObject,fragment)).collect(Collectors.toSet());
			
			if (!unread.isEmpty()){
				for(KnowledgeFragment fragment:unread)compendiumFile.markFragmentAsRead((short)fragment.globalID);
				GuiEnderCompendium.sendPacketToServer(new S01CompendiumReadFragments(unread));
			}
			
			if (!compendiumFile.isDiscovered(currentObject) && currentObject.getPrice() != 0){
				purchaseElements.add(new CompendiumPurchaseElement(currentObject,pageX+pageWidth/2,innerY+20));
				return;
			}
			
			int yy = innerY+12, height;
			
			for(Entry<KnowledgeFragment,Boolean> entry:currentObjectPages.get((byte)pageIndex).entrySet()){
				height = entry.getKey().getHeight(gui,entry.getValue());
				if (!entry.getValue())purchaseElements.add(new CompendiumPurchaseElement(entry.getKey(),pageX+pageWidth/2,yy+height/2));
				yy += 8+height;
			}
		}
	}
	
	public void render(int mouseX, int mouseY){
		for(int a = 0; a < 2; a++)pageArrows[a].visible = false;
		
		if (currentObject == null)return;
		
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glColor4f(1F,1F,1F,1F);
		RenderHelper.disableStandardItemLighting();
		
		gui.mc.getTextureManager().bindTexture(texPage);
		gui.drawTexturedModalRect(pageX,pageY,0,0,pageWidth,pageHeight);
		
		final int distance = 16;
		int titleWidth = (currentObject.isHidden() ? 0 : distance+12)+gui.mc.fontRenderer.getStringWidth(currentObject.getTranslatedTooltip());
		int left = (gui.width-titleWidth)/2+4;
		
		gui.mc.fontRenderer.drawString(currentObject.getTranslatedTooltip(),left+distance,innerY,0x404040);
		
		if (!currentObject.isHidden()){
			int iconY = innerY-4;
			RenderHelper.enableGUIStandardItemLighting();
			GL11.glPushMatrix();
			GL11.glTranslatef(left+8,iconY+8,0F);
			GL11.glScaled(0.75F,0.75F,1F);
			GL11.glTranslatef(-left-8,-iconY-8,0F);
			GuiEnderCompendium.renderItem.renderItemIntoGUI(gui.mc.fontRenderer,gui.mc.getTextureManager(),currentObject.holder.getDisplayItemStack(),left,iconY,true);
			GL11.glPopMatrix();
		}
		
		int x = innerX, y = innerY+12;
		
		for(Entry<KnowledgeFragment,Boolean> entry:currentObjectPages.get((byte)pageIndex).entrySet()){
			entry.getKey().onRender(gui,x,y,mouseX,mouseY,entry.getValue());
			y += 8+entry.getKey().getHeight(gui,entry.getValue());
		}
		
		for(int a = 0; a < 2; a++)pageArrows[a].visible = true;
		pageArrows[0].visible = pageIndex > 0;
		pageArrows[1].visible = pageIndex < currentObjectPages.size()-1;
		
		for(CompendiumPurchaseElement element:purchaseElements)element.render(gui,mouseX,mouseY);
		
		/*if (!compendiumFile.isDiscovered(currentObject)){ // TODO
			RenderHelper.disableStandardItemLighting();
			String msg = I18n.format("compendium.cannotBuy");
			mc.fontRenderer.drawString(msg,x-(mc.fontRenderer.getStringWidth(msg)>>1),y-7,0x404040);
		}*/

		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}
}
