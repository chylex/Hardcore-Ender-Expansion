package chylex.hee.gui;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import chylex.hee.game.save.types.player.CompendiumFile;
import chylex.hee.gui.helpers.AnimatedFloat;
import chylex.hee.gui.helpers.AnimatedFloat.Easing;
import chylex.hee.gui.helpers.GuiEndPortalRenderer;
import chylex.hee.gui.helpers.GuiHelper;
import chylex.hee.gui.helpers.GuiItemRenderHelper;
import chylex.hee.gui.helpers.KeyState;
import chylex.hee.init.ItemList;
import chylex.hee.mechanics.compendium.KnowledgeRegistrations;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.elements.CompendiumObjectElement;
import chylex.hee.mechanics.compendium.handlers.CompendiumPageHandler;
import chylex.hee.mechanics.compendium.handlers.CompendiumScrollHandler;
import chylex.hee.mechanics.compendium.handlers.CompendiumTabHandler;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiEnderCompendium extends GuiScreen{
	public static final RenderItem renderItem = new RenderItem();
	public static final ResourceLocation texBack = new ResourceLocation("hardcoreenderexpansion:textures/gui/ender_compendium_back.png");
	public static final ResourceLocation texFragments = new ResourceLocation("hardcoreenderexpansion:textures/gui/ender_compendium_fragments.png");
	public static final ItemStack knowledgeFragmentIS = new ItemStack(ItemList.knowledge_note);
	
	public static boolean pausesGame;
	
	private final GuiEndPortalRenderer portalRenderer;
	private AnimatedFloat portalSpeed;
	
	private final CompendiumPageHandler pageHandler;
	private final CompendiumScrollHandler scrollHandler;
	private final CompendiumTabHandler tabHandler;
	
	private CompendiumFile compendiumFile;
	
	private List<CompendiumObjectElement> objectElements = new ArrayList<>();
	private KnowledgeObject<?> currentObject = null;
	
	private GuiButtonState btnHelp;
	
	public GuiEnderCompendium(CompendiumFile compendiumFile){
		this.compendiumFile = compendiumFile;
		
		this.portalSpeed = new AnimatedFloat(Easing.CUBIC);
		this.portalSpeed.startAnimation(30F,15F,1.5F);
		
		this.portalRenderer = new GuiEndPortalRenderer(this);
		this.pageHandler = new CompendiumPageHandler(this);
		
		int maxY = 0;
		
		for(KnowledgeObject<?> obj:KnowledgeObject.getAllObjects()){
			if (!obj.isHidden())objectElements.add(new CompendiumObjectElement(obj));
			if (obj.getY() > maxY)maxY = obj.getY();
		}
		
		this.scrollHandler = new CompendiumScrollHandler(this,maxY);
		this.tabHandler = new CompendiumTabHandler(this);
	}
	
	public void updateCompendiumData(CompendiumFile newData){
		this.compendiumFile = newData;
		pageHandler.setFile(this.compendiumFile);
		showObject(currentObject);
	}
	
	@Override
	public void initGui(){
		portalRenderer.init(width-48,height-48,0);
		
		buttonList.add(new GuiButton(0,width/2-120,height-27,98,20,I18n.format("gui.achievements")));
		buttonList.add(btnHelp = new GuiButtonState(1,width/2-10,height-27,20,20,"?"));
		buttonList.add(new GuiButton(2,width/2+22,height-27,98,20,I18n.format("gui.back")));
		
		scrollHandler.init();
		
		pageHandler.init(buttonList,3,4);
		pageHandler.setFile(compendiumFile);
		
		if (currentObject == KnowledgeRegistrations.HELP)btnHelp.forcedHover = true;
	}
	
	@Override
	protected void actionPerformed(GuiButton button){
		if (!(button.enabled && button.visible))return;
		
		if (button.id == 0)mc.displayGuiScreen(new GuiAchievementsCustom(this,mc.thePlayer.getStatFileWriter()));
		else if (button.id == 1)showObject(KnowledgeRegistrations.HELP);
		else if (button.id == 2)goBack();
		else pageHandler.onButtonClick(button);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int buttonId){
		if (buttonId == 1)goBack();
		else if (buttonId == 0){
			if (tabHandler.onMouseClick(mouseX,mouseY,buttonId))return;
			if (pageHandler.onMouseClick(mouseX,mouseY,buttonId))return;
			
			Optional<GuiButton> pressedButton = ((List<GuiButton>)buttonList).stream().filter(btn -> btn.mousePressed(mc,mouseX,mouseY)).findFirst();
			
			if (pressedButton.isPresent()){
				pressedButton.get().func_146113_a(mc.getSoundHandler());
				actionPerformed(pressedButton.get());
			}
			else{
				if (!(mouseX < 24 || mouseX > width-24 || mouseY < 24 || mouseY > height-24)){
					int offY = (int)scrollHandler.getOffset(1F);
					
					for(CompendiumObjectElement element:objectElements){
						if (element.isMouseOver(mouseX,mouseY,width/2,offY)){
							showObject(element.object);
							return;
						}
					}
				}
				
				scrollHandler.onMouseClick(mouseX,mouseY);
			}
		}
	}
	
	@Override
	protected void mouseMovedOrUp(int mouseX, int mouseY, int event){
		if (event == 0 && !pageHandler.isMouseInside(mouseX,mouseY))scrollHandler.onMouseRelease(mouseX,mouseY);
		super.mouseMovedOrUp(mouseX,mouseY,event);
	}
	
	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int lastButton, long timeSinceClick){
		if (lastButton == 0 && !pageHandler.isMouseInside(mouseX,mouseY))scrollHandler.onMouseDrag(mouseX,mouseY);
	}
	
	@Override
	public void handleKeyboardInput(){
		int keyCode = Keyboard.getEventKey();
		boolean isDown = Keyboard.getEventKeyState();
		
		KeyState.setState(keyCode,isDown);
		
		if (isDown)keyTyped(Keyboard.getEventCharacter(),keyCode);
		else scrollHandler.onKeyboardUp(keyCode);

		mc.func_152348_aa(); // OBFUSCATED handleInput
	}
	
	@Override
	protected void keyTyped(char key, int keyCode){
		if (pageHandler.onKeyboardDown(keyCode));
		else if (scrollHandler.onKeyboardDown(keyCode));
		else if (keyCode == GuiHelper.keyEscape)goBack();
		else if (keyCode == GuiHelper.keyF1)showObject(currentObject == KnowledgeRegistrations.HELP ? null : KnowledgeRegistrations.HELP);
	}
	
	private void goBack(){
		if (currentObject != null)showObject(null);
		else mc.setIngameFocus();
	}
	
	public CompendiumScrollHandler getScrollHandler(){
		return scrollHandler;
	}
	
	@Override
	public void updateScreen(){
		scrollHandler.update();

		if (!portalSpeed.isAnimating()){
			if (currentObject != null){
				if (MathUtil.floatEquals(portalSpeed.value(),15F))portalSpeed.startAnimation(15F,5F,0.5F);
			}
			else if (MathUtil.floatEquals(portalSpeed.value(),5F))portalSpeed.startAnimation(5F,15F,0.5F);
		}
		
		portalRenderer.update((int)portalSpeed.value());
		portalSpeed.update(0.05F);
	}
	
	public void showObject(KnowledgeObject<?> obj){
		this.currentObject = obj;
		pageHandler.showObject(obj);
	}
	
	public void moveToObject(final KnowledgeObject<?> obj, final boolean animate){
		objectElements.stream().filter(ele -> ele.object == obj).mapToInt(ele -> -(ele.object.getY()-height/2)).findFirst().ifPresent(y -> scrollHandler.moveTo(y,animate));
	}
	
	public void moveToCurrentObject(final boolean animate){
		if (currentObject != null)moveToObject(currentObject,animate);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTickTime){
		partialTickTime = Minecraft.getMinecraft().timer.elapsedPartialTicks; // if doesGuiPauseGame() returns true, Minecraft freezes renderPartialTicks
		
		drawDefaultBackground();
		GL11.glDepthFunc(GL11.GL_GEQUAL);
		GL11.glPushMatrix();
		GL11.glTranslatef(0F,0F,-200F);
		portalRenderer.render(0,-scrollHandler.getOffset(partialTickTime)*0.49F,1F/getScaleMultiplier(),partialTickTime);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glEnable(GL11.GL_CULL_FACE);
		renderScreen(mouseX,mouseY,partialTickTime);
		GL11.glPopMatrix();
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		RenderHelper.disableStandardItemLighting();
		super.drawScreen(mouseX,mouseY,partialTickTime);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	private void renderScreen(int mouseX, int mouseY, float partialTickTime){
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glColor4f(1F,1F,1F,1F);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);

		renderBackgroundGUI();
		
		float offY = scrollHandler.getOffset(partialTickTime);
		int yLowerBound = -(int)offY-32, yUpperBound = -(int)offY+height;
		
		tabHandler.render(mouseX,mouseY);
		
		GL11.glPushMatrix();
		GL11.glTranslatef(0F,offY,0F);
		
		for(CompendiumObjectElement element:objectElements){
			if (!element.object.isHidden())element.renderLine(this,compendiumFile,yLowerBound,yUpperBound);
		}
		
		for(CompendiumObjectElement element:objectElements){
			if (!element.object.isHidden()){
				element.renderObject(this,compendiumFile,yLowerBound,yUpperBound);
				if (currentObject == null && element.isMouseOver(mouseX,mouseY,width/2,(int)offY))GuiItemRenderHelper.setupTooltip(mouseX,mouseY,element.getTooltip(compendiumFile));
			}
		}
		
		RenderHelper.disableStandardItemLighting();
		GL11.glPopMatrix();

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		renderFragmentCount(width-90,24);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		pageHandler.render(mouseX,mouseY);
		
		GuiItemRenderHelper.drawTooltip(this,fontRendererObj);
		
		int wheel = Mouse.getDWheel();
		
		if (wheel != 0){
			if (pageHandler.onMouseWheel(mouseX,mouseY,wheel)); // empty statement
			else scrollHandler.onMouseWheel(wheel);
		}
	}
	
	private void renderBackgroundGUI(){
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		RenderHelper.disableStandardItemLighting();
		mc.getTextureManager().bindTexture(texBack);
		
		int d = 24;
		
		for(int a = 0, amt = ((width-d*2)>>2)-1; a < amt-2; a++){
			drawTexturedModalRect(d+8+4*a,d-16,50,0,4,24);
			drawTexturedModalRect(d+8+4*a,height-d-8,50,25,4,24);
		}
		
		for(int a = 0, amt = ((height-d*2)>>2)-1; a < amt-2; a++){
			drawTexturedModalRect(d-16,d+8+4*a,0,50,24,4);
			drawTexturedModalRect(width-d-8,d+8+4*a,26,50,24,4);
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
		
		String pointAmount = String.valueOf(compendiumFile.getPoints());
		fontRendererObj.drawString(pointAmount,x+50-fontRendererObj.getStringWidth(pointAmount),y+6,0x404040);
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}
	
	@Override
	public boolean doesGuiPauseGame(){
		return pausesGame;
	}
	
	/**
	 * Magical number that, when used correctly, makes experience with using GUIs consistently amazing.
	 */
	public static final float getScaleMultiplier(){
		switch(Minecraft.getMinecraft().gameSettings.guiScale){
			case 0: return 1.35F; // auto
			case 1: return 0.275F; // small
			case 2: return 0.6F; // normal
			default: return 1F; // large
		}
	}
}
