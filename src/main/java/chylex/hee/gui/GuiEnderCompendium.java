package chylex.hee.gui;
import gnu.trove.map.hash.TByteObjectHashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.gui.helpers.AnimatedFloat;
import chylex.hee.gui.helpers.AnimatedFloat.Easing;
import chylex.hee.gui.helpers.GuiEndPortalRenderer;
import chylex.hee.gui.helpers.GuiItemRenderHelper;
import chylex.hee.gui.helpers.GuiItemRenderHelper.ITooltipRenderer;
import chylex.hee.init.ItemList;
import chylex.hee.mechanics.compendium.KnowledgeCategories;
import chylex.hee.mechanics.compendium.KnowledgeRegistrations;
import chylex.hee.mechanics.compendium.content.KnowledgeCategory;
import chylex.hee.mechanics.compendium.content.KnowledgeFragment;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.content.fragments.KnowledgeFragmentText;
import chylex.hee.mechanics.compendium.objects.IKnowledgeObjectInstance;
import chylex.hee.mechanics.compendium.player.PlayerCompendiumData;
import chylex.hee.mechanics.compendium.player.PlayerCompendiumData.FragmentPurchaseStatus;
import chylex.hee.mechanics.compendium.render.CategoryDisplayElement;
import chylex.hee.mechanics.compendium.render.ObjectDisplayElement;
import chylex.hee.mechanics.compendium.render.PurchaseDisplayElement;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.server.S02CompendiumPurchase;
import chylex.hee.packets.server.S03SimpleEvent;
import chylex.hee.packets.server.S03SimpleEvent.EventType;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.ConfigHandler;
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiEnderCompendium extends GuiScreen implements ITooltipRenderer{
	public static final int guiPageTexWidth = 152, guiPageTexHeight = 226, guiPageWidth = 126, guiPageHeight = 176, guiPageLeft = 18, guiPageTop = 20, guiObjLeft = 32;
	
	public static final RenderItem renderItem = new RenderItem();
	public static final ResourceLocation texPage = new ResourceLocation("hardcoreenderexpansion:textures/gui/ender_compendium_page.png");
	public static final ResourceLocation texBack = new ResourceLocation("hardcoreenderexpansion:textures/gui/ender_compendium_back.png");
	public static final ResourceLocation texFragments = new ResourceLocation("hardcoreenderexpansion:textures/gui/ender_compendium_fragments.png");
	public static final ItemStack knowledgeFragmentIS = new ItemStack(ItemList.knowledge_note);
	
	private static float ptt(float value, float prevValue, float partialTickTime){
		return prevValue+(value-prevValue)*partialTickTime;
	}
	
	private PlayerCompendiumData compendiumData;
	private GuiEndPortalRenderer portalRenderer;
	private List<AnimatedFloat> animationList = new ArrayList<>();
	private AnimatedFloat offsetY, portalSpeed;
	private float prevOffsetY;
	private int prevMouseX, totalHeight;
	
	private boolean hasClickedButton = false;
	private int dragMouseY = Integer.MIN_VALUE;
	private float prevDragOffsetY;
	
	private List<CategoryDisplayElement> categoryElements = new ArrayList<>();
	private List<ObjectDisplayElement> objectElements = new ArrayList<>();
	private List<PurchaseDisplayElement> purchaseElements = new ArrayList<>();
	
	private KnowledgeObject<? extends IKnowledgeObjectInstance<?>> currentObject = null;
	private TByteObjectHashMap<Map<KnowledgeFragment,Boolean>> currentObjectPages = new TByteObjectHashMap<>(5);
	private byte pageIndex;
	private GuiButtonPageArrow[] pageArrows = new GuiButtonPageArrow[2];
	
	private GuiButtonState btnHelp;
	private byte hoverTriggerTimer = Byte.MIN_VALUE;
	
	public GuiEnderCompendium(PlayerCompendiumData compendiumData){
		if (!(this.compendiumData = compendiumData).seenHelp())hoverTriggerTimer = 0;
		
		animationList.add(offsetY = new AnimatedFloat(Easing.CUBIC));
		animationList.add(portalSpeed = new AnimatedFloat(Easing.CUBIC));
		
		int y = 0, maxY = 0;
		
		for(KnowledgeCategory category:KnowledgeCategories.categoryList){
			y += 32;
			categoryElements.add(new CategoryDisplayElement(category,y));
			y += 44;
			
			for(KnowledgeObject obj:category.getAllObjects()){
				if (!obj.isCategoryObject()){
					objectElements.add(new ObjectDisplayElement(obj,y));
					if (obj.getY() > maxY)maxY = obj.getY();
				}
			}
			
			y += maxY+30;
			maxY = 0;
		}
		
		this.totalHeight = y;
		
		portalSpeed.startAnimation(30F,15F,1.5F);
	}
	
	public void updateCompendiumData(PlayerCompendiumData newData){
		this.compendiumData = newData;
		showObject(currentObject);
	}
	
	@Override
	public void initGui(){
		this.portalRenderer = new GuiEndPortalRenderer(this,width-48,height-48,0);
		
		buttonList.add(new GuiButton(0,(width>>1)-120,height-27,98,20,I18n.format("gui.achievements")));
		buttonList.add(btnHelp = new GuiButtonState(1,(width>>1)-10,height-27,20,20,"?"));
		buttonList.add(new GuiButton(2,(width>>1)+22,height-27,98,20,I18n.format("gui.back")));
		buttonList.add(pageArrows[0] = new GuiButtonPageArrow(3,0,((height+guiPageTexHeight)>>1)-32,false));
		buttonList.add(pageArrows[1] = new GuiButtonPageArrow(4,0,((height+guiPageTexHeight)>>1)-32,true));
		buttonList.add(new GuiButton(5,width-60,height-27,20,20,""));
		
		for(int a = 0; a < 2; a++)pageArrows[a].visible = false;
		
		if (currentObject == KnowledgeRegistrations.HELP)btnHelp.forcedHover = true;
		
		offsetY.set(0);
		
		updatePurchaseElements();
	}
	
	@Override
	protected void actionPerformed(GuiButton button){
		hasClickedButton = true;
		
		if (!(button.enabled && button.visible))return;
		
		if (button.id == 0 && !offsetY.isAnimating()){
			mc.displayGuiScreen(new GuiAchievementsCustom(this,mc.thePlayer.getStatFileWriter()));
		}
		else if (button.id == 1)showObject(KnowledgeRegistrations.HELP);
		else if (button.id == 2 && !offsetY.isAnimating()){
			if (currentObject != null)showObject(null);
			else{
				mc.displayGuiScreen((GuiScreen)null);
				mc.setIngameFocus();
			}
		}
		else if (button.id == 3)pageIndex = (byte)Math.max(0,pageIndex-1);
		else if (button.id == 4)pageIndex = (byte)Math.min(currentObjectPages.size()-1,pageIndex+1);
		else if (button.id == 5){
			if (++KnowledgeFragmentText.smoothRenderingMode > 2)KnowledgeFragmentText.smoothRenderingMode = 0;
			
			for(IConfigElement element:ConfigHandler.getGuiConfigElements()){
				if (element.getName().equals("compendiumSmoothText") && element.isProperty()){
					element.set(KnowledgeFragmentText.smoothRenderingMode);
					break;
				}
			}
			
			HardcoreEnderExpansion.proxy.loadConfiguration(); // does not reload, just saves changes
		}
		
		if (button.id == 3 || button.id == 4)updatePurchaseElements();
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int buttonId){
		if (buttonId == 1)actionPerformed((GuiButton)buttonList.get(2));
		else if (buttonId == 0 && !(mouseX < 24 || mouseX > width-24 || mouseY < 24 || mouseY > height-24)){
			int offY = (int)offsetY.value();
			
			for(CategoryDisplayElement element:categoryElements){
				if (element.isMouseOver(mouseX,mouseY,offY)){
					showObject(element.category.getCategoryObject());
					return;
				}
			}
			
			for(ObjectDisplayElement element:objectElements){
				if (element.isMouseOver(mouseX,mouseY,offY)){
					showObject(element.object);
					return;
				}
			}
			
			KnowledgeObject<? extends IKnowledgeObjectInstance<?>> redirect = null;
			
			for(PurchaseDisplayElement element:purchaseElements){
				if (element.isMouseOver(mouseX,mouseY,(width>>1)+(width>>2)+4)){
					if (element.getStatus() == FragmentPurchaseStatus.NOT_BUYABLE && element.fragmentHasRedirect){
						redirect = ((KnowledgeFragment)element.object).getUnlockRedirect();
						break;
					}
					else if (element.getStatus() == FragmentPurchaseStatus.CAN_PURCHASE && compendiumData.getPoints() >= element.price){
						Object obj = element.object;
						
						if (obj instanceof KnowledgeObject)PacketPipeline.sendToServer(new S02CompendiumPurchase((KnowledgeObject)obj));
						else if (obj instanceof KnowledgeFragment)PacketPipeline.sendToServer(new S02CompendiumPurchase((KnowledgeFragment)obj));
						else continue;
						
						return;
					}
				}
			}
			
			if (redirect != null){
				showObject(redirect);
				moveToCurrentObject(true);
			}
		}
		
		if (currentObject != null){
			int x = (width>>1)+(width>>2)+4-(guiPageTexWidth>>1)+guiPageLeft;
			int y = (height>>1)-(guiPageTexHeight>>1)+guiPageTop;
			
			for(Entry<KnowledgeFragment,Boolean> entry:currentObjectPages.get(pageIndex).entrySet()){
				if (entry.getKey().onClick(this,x,y,mouseX,mouseY,buttonId,entry.getValue()))return;
				y += 8+entry.getKey().getHeight(this,entry.getValue());
			}
		}
		
		hasClickedButton = false;
		super.mouseClicked(mouseX,mouseY,buttonId);
		
		if (hasClickedButton)hasClickedButton = false;
		else dragMouseY = mouseY;
	}
	
	@Override
	protected void mouseMovedOrUp(int mouseX, int mouseY, int event){
		if (dragMouseY != Integer.MIN_VALUE && event == 0)dragMouseY = Integer.MIN_VALUE;
		super.mouseMovedOrUp(mouseX,mouseY,event);
	}
	
	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int lastButton, long timeSinceClick){
		if (dragMouseY != Integer.MIN_VALUE && lastButton == 0){
			prevDragOffsetY = MathUtil.floor((dragMouseY-mouseY)*6);
			dragMouseY = mouseY;
		}
		
		super.mouseClickMove(mouseX,mouseY,lastButton,timeSinceClick);
	}
	
	@Override
	protected void keyTyped(char key, int keyCode){
		if (keyCode == 1)actionPerformed((GuiButton)buttonList.get(2));
	}
	
	@Override
	public void updateScreen(){
		prevOffsetY = offsetY.value();

		if (!portalSpeed.isAnimating()){
			if (currentObject != null){
				if (MathUtil.floatEquals(portalSpeed.value(),15F))portalSpeed.startAnimation(15F,5F,0.5F);
			}
			else if (MathUtil.floatEquals(portalSpeed.value(),5F))portalSpeed.startAnimation(5F,15F,0.5F);
		}
		
		portalRenderer.update((int)portalSpeed.value());
		for(AnimatedFloat animation:animationList)animation.update(0.05F);
		
		int wheel = Mouse.getDWheel();
		
		if (wheel != 0){
			if (currentObject != null && (currentObject == KnowledgeRegistrations.HELP || prevMouseX >= width>>1)){
				if (wheel > 0)actionPerformed((GuiButton)buttonList.get(3));
				else if (wheel < 0)actionPerformed((GuiButton)buttonList.get(4));
			}
			else offsetY.set(offsetY.value()+(wheel > 0 ? 80 : -80));
		}
		
		if (!MathUtil.floatEquals(prevDragOffsetY,0F)){
			offsetY.set(offsetY.value()-prevDragOffsetY);
			prevDragOffsetY = 0F;
		}
		
		if (offsetY.value() > 0)offsetY.set(0F);
		else if (offsetY.value() < -totalHeight+height-32)offsetY.set(-totalHeight+height-32);
		
		if (hoverTriggerTimer != Byte.MIN_VALUE && ++hoverTriggerTimer > 12){
			if (!compendiumData.seenHelp())btnHelp.forcedHover = !btnHelp.forcedHover;
			else if (currentObject == KnowledgeRegistrations.HELP){
				if (pageIndex == 0)pageArrows[1].forcedHover = !pageArrows[1].forcedHover;
				else{
					hoverTriggerTimer = Byte.MIN_VALUE;
					pageArrows[1].forcedHover = false;
				}
			}
			
			if (hoverTriggerTimer != Byte.MIN_VALUE)hoverTriggerTimer = 0;
		}
	}
	
	public void showObject(KnowledgeObject<? extends IKnowledgeObjectInstance<?>> object){
		if (currentObject != null){
			currentObjectPages.clear();
			purchaseElements.clear();
			
			if (currentObject != object){
				pageIndex = 0;
				btnHelp.forcedHover = false;
			}
		}

		if ((currentObject = object) == null)return;
		
		byte page = 0;
		int yy = 0, height = 0;
		boolean isUnlocked = false;
		Map<KnowledgeFragment,Boolean> pageMap = new LinkedHashMap<>();
		Iterator<KnowledgeFragment> iter = currentObject.getFragments().iterator();
		
		while(true){
			KnowledgeFragment fragment = iter.hasNext() ? iter.next() : null;
			
			if (fragment == null || yy+(height = 8+fragment.getHeight(this,isUnlocked = (object == KnowledgeRegistrations.HELP || compendiumData.hasUnlockedFragment(fragment)))) > guiPageHeight){
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
		
		if (object == KnowledgeRegistrations.HELP){
			btnHelp.forcedHover = true;
			
			if (!compendiumData.seenHelp()){
				PacketPipeline.sendToServer(new S03SimpleEvent(EventType.OPEN_COMPENDIUM_HELP));
				compendiumData.setSeenHelp();
			}
			
			purchaseElements.clear();
			return;
		}
		
		updatePurchaseElements();
	}
	
	public void moveToCurrentObject(boolean animate){
		if (currentObject == null)return;
		
		int newY = Integer.MIN_VALUE;
		
		for(ObjectDisplayElement element:objectElements){
			if (element.object.getObject() == currentObject.getObject()){
				newY = -(element.y+element.object.getY()-(height>>1)+11);
				break;
			}
		}
		
		if (newY == Integer.MIN_VALUE){
			for(CategoryDisplayElement element:categoryElements){
				if (element.category.getCategoryObject().getObject() == currentObject.getObject()){
					newY = -(element.y+element.category.getCategoryObject().getY()-(height>>1)+20);
					break;
				}
			}
		}
		
		if (newY != Integer.MIN_VALUE){
			if (animate)offsetY.startAnimation(offsetY.value(),newY,0.5F);
			else offsetY.set(newY);
		}
	}
	
	private void updatePurchaseElements(){
		purchaseElements.clear();
		
		if (currentObject != null){
			if (!compendiumData.hasDiscoveredObject(currentObject)){
				if (currentObject.isBuyable())purchaseElements.add(new PurchaseDisplayElement(currentObject,(this.height>>1)-3,compendiumData.getPoints() >= currentObject.getUnlockPrice() ? FragmentPurchaseStatus.CAN_PURCHASE : FragmentPurchaseStatus.NOT_ENOUGH_POINTS));
				return;
			}
			
			int yy = ((this.height-guiPageTexHeight)>>1)+guiPageTop, height;
			
			for(Entry<KnowledgeFragment,Boolean> entry:currentObjectPages.get(pageIndex).entrySet()){
				height = entry.getKey().getHeight(this,entry.getValue());
				if (!entry.getValue())purchaseElements.add(new PurchaseDisplayElement(entry.getKey(),yy+(height>>1)+2,compendiumData.canPurchaseFragment(entry.getKey())));
				yy += 8+height;
			}
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTickTime){
		drawDefaultBackground();
		GL11.glDepthFunc(GL11.GL_GEQUAL);
		GL11.glPushMatrix();
		GL11.glTranslatef(0F,0F,-200F);
		portalRenderer.draw(0,-ptt(offsetY.value(),prevOffsetY,partialTickTime)*0.49F,1F,partialTickTime);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glEnable(GL11.GL_CULL_FACE);
		renderScreen(mouseX,mouseY,partialTickTime);
		GL11.glPopMatrix();
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		RenderHelper.disableStandardItemLighting();
		super.drawScreen(mouseX,mouseY,partialTickTime);
		renderScreenPost(mouseX,mouseY,partialTickTime);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	private void renderScreen(int mouseX, int mouseY, float partialTickTime){
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glColor4f(1F,1F,1F,1F);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);

		renderBackgroundGUI();
		
		float offY = ptt(offsetY.value(),prevOffsetY,partialTickTime);
		int yLowerBound = -(int)offY-32, yUpperBound = -(int)offY+height;
		
		GL11.glPushMatrix();
		GL11.glTranslatef(0F,offY,0F);
		for(CategoryDisplayElement element:categoryElements)element.render(this,yLowerBound,yUpperBound);
		for(ObjectDisplayElement element:objectElements)element.render(this,compendiumData,yLowerBound,yUpperBound);
		RenderHelper.disableStandardItemLighting();
		GL11.glPopMatrix();
		
		if (!(mouseX < 24 || mouseX > width-24 || mouseY < 24 || mouseY > height-24)){
			for(CategoryDisplayElement element:categoryElements){
				if (element.isMouseOver(mouseX,mouseY,(int)offY)){
					GuiItemRenderHelper.setupTooltip(mouseX,mouseY,element.category.getTooltip());
				}
			}
			
			for(ObjectDisplayElement element:objectElements){
				if (element.isMouseOver(mouseX,mouseY,(int)offY)){
					GuiItemRenderHelper.setupTooltip(mouseX,mouseY,element.object.getTooltip());
				}
			}
		}

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		renderFragmentCount((width>>1)-25,24);
		
		for(int a = 0; a < 2; a++)pageArrows[a].visible = false;

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		if (currentObject == KnowledgeRegistrations.HELP)renderPaper(width>>1,height>>1,mouseX,mouseY);
		else if (currentObject != null)renderPaper((width>>1)+(width>>2)+4,height>>1,mouseX,mouseY);
		
		prevMouseX = mouseX;
	}
	
	private void renderScreenPost(int mouseX, int mouseY, float partialTickTime){
		mc.getTextureManager().bindTexture(texBack);
		GuiButton button = (GuiButton)buttonList.get(5);
		drawTexturedModalRect(button.xPosition,button.yPosition,KnowledgeFragmentText.smoothRenderingMode > 0 ? 56 : 77,29,20,20);
		
		if (mouseX >= button.xPosition && mouseX <= button.xPosition+button.width && mouseY > button.yPosition && mouseY < button.yPosition+button.height){
			StringBuilder build = new StringBuilder(110);
			build.append(I18n.format("compendium.smoothText.title")).append('\n');
			build.append(EnumChatFormatting.GRAY).append(I18n.format("compendium.smoothText.line1")).append('\n');
			build.append(EnumChatFormatting.GRAY).append(I18n.format("compendium.smoothText.line2")).append('\n');
			build.append(EnumChatFormatting.DARK_GREEN).append(I18n.format("compendium.smoothText.mode")).append(": ");
			build.append(KnowledgeFragmentText.smoothRenderingMode == 0 ? I18n.format("compendium.smoothText.disabled") : String.valueOf(KnowledgeFragmentText.smoothRenderingMode));
			GuiItemRenderHelper.setupTooltip(mouseX,mouseY-24,build.toString());
		}
		
		GuiItemRenderHelper.drawTooltip(this,fontRendererObj);
	}
	
	private void renderBackgroundGUI(){
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		RenderHelper.disableStandardItemLighting();
		mc.getTextureManager().bindTexture(texBack);
		
		int d = 24;
		
		for(int a = 0, amt = ((width-d*2)>>2)-1; a < amt; a++){
			drawTexturedModalRect(d+8+4*a,d-16,50,0,4,24);
			drawTexturedModalRect(d+8+4*a,height-d-8,50,25,4,24);
		}
		
		for(int a = 0, amt = ((height-d*2)>>2)-1; a < amt; a++){
			drawTexturedModalRect(d-16,d+8+4*a,206,0,24,4);
			drawTexturedModalRect(width-d-8,d+8+4*a,232,0,24,4);
		}
		
		drawTexturedModalRect(d-16,d-16,0,0,24,24);
		drawTexturedModalRect(width-d-8,d-16,25,0,24,24);
		drawTexturedModalRect(d-16,height-d-8,0,25,24,24);
		drawTexturedModalRect(width-d-8,height-d-8,25,25,24,24);
		
		String title = ModCommonProxy.hardcoreEnderbacon ? "Hardcore Bacon Expansion - Bacon Compendium" : "Hardcore Ender Expansion - Ender Compendium";
		fontRendererObj.drawString(title,(width>>1)-(fontRendererObj.getStringWidth(title)>>1),14,0x404040);
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}
	
	private void renderFragmentCount(int x, int y){
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glColor4f(1F,1F,1F,1F);
		
		mc.getTextureManager().bindTexture(texBack);
		drawTexturedModalRect(x,y,56,0,56,20);
		
		RenderHelper.enableGUIStandardItemLighting();
		renderItem.renderItemIntoGUI(fontRendererObj,mc.getTextureManager(),knowledgeFragmentIS,x+3,y+1);
		
		String pointAmount = String.valueOf(compendiumData.getPoints());
		fontRendererObj.drawString(pointAmount,x+50-fontRendererObj.getStringWidth(pointAmount),y+6,0x404040);
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}
	
	private void renderPaper(int x, int y, int mouseX, int mouseY){
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		pageArrows[0].xPosition = x-(guiPageTexWidth*3/10)-10;
		pageArrows[1].xPosition = x+(guiPageTexWidth*3/10)-10;
		
		GL11.glColor4f(1F,1F,1F,1F);
		RenderHelper.disableStandardItemLighting();
		
		mc.getTextureManager().bindTexture(texPage);
		drawTexturedModalRect(x-(guiPageTexWidth>>1),y-(guiPageTexHeight>>1),0,0,guiPageTexWidth,guiPageTexHeight);
		
		if (compendiumData.hasDiscoveredObject(currentObject)){
			x = x-(guiPageTexWidth>>1)+guiPageLeft;
			y = y-(guiPageTexHeight>>1)+guiPageTop;
			
			for(Entry<KnowledgeFragment,Boolean> entry:currentObjectPages.get(pageIndex).entrySet()){
				entry.getKey().onRender(this,x,y,mouseX,mouseY,entry.getValue());
				y += 8+entry.getKey().getHeight(this,entry.getValue());
			}
			
			for(int a = 0; a < 2; a++)pageArrows[a].visible = true;
			pageArrows[0].visible = pageIndex > 0;
			pageArrows[1].visible = pageIndex < currentObjectPages.size()-1;
			
			x = x+(guiPageTexWidth>>1)-guiPageLeft;
		}
		
		for(PurchaseDisplayElement element:purchaseElements)element.render(this,mouseX,mouseY,x);
		
		if (!currentObject.isBuyable() && !compendiumData.hasDiscoveredObject(currentObject)){
			RenderHelper.disableStandardItemLighting();
			String msg = I18n.format("compendium.cannotBuy");
			mc.fontRenderer.drawString(msg,x-(mc.fontRenderer.getStringWidth(msg)>>1),y-7,0x404040);
		}

		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}
	
	@Override
	public boolean doesGuiPauseGame(){
		return false;
	}

	@Override
	public void setZLevel(float newZLevel){
		this.zLevel = newZLevel;
	}

	@Override
	public void callDrawGradientRect(int x1, int y1, int x2, int y2, int color1, int color2){
		drawGradientRect(x1,y1,x2,y2,color1,color2);
	}
}
