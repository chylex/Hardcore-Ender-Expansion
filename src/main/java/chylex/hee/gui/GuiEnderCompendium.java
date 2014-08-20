package chylex.hee.gui;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import chylex.hee.gui.helpers.AnimatedFloat;
import chylex.hee.gui.helpers.AnimatedFloat.Easing;
import chylex.hee.gui.helpers.GuiEndPortalRenderer;
import chylex.hee.gui.helpers.GuiItemRenderHelper;
import chylex.hee.gui.helpers.GuiItemRenderHelper.ITooltipRenderer;
import chylex.hee.mechanics.compendium.KnowledgeCategories;
import chylex.hee.mechanics.knowledge.util.IGuiItemStackRenderer;
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiEnderCompendium extends GuiScreen implements ITooltipRenderer{
	public static final int guiPageTexWidth = 152, guiPageTexHeight = 226;
	public static final int guiPageWidth = 126, guiPageHeight = 176, guiLeft = 18, guiTop = 20;
	
	public static final RenderItem renderItem = new RenderItem();
	public static final ResourceLocation texPage = new ResourceLocation("hardcoreenderexpansion:textures/gui/knowledge_book.png");
	private static final ResourceLocation texBack = new ResourceLocation("hardcoreenderexpansion:textures/gui/ender_compendium_back.png");
	
	private static float ptt(float value, float prevValue, float partialTickTime){
		return prevValue+(value-prevValue)*partialTickTime;
	}
	
	private GuiEndPortalRenderer portalRenderer;
	private List<AnimatedFloat> animationList = new ArrayList<>();
	private AnimatedFloat offsetX, offsetY, portalScale;
	private float prevOffsetX, prevOffsetY, prevPortalScale;
	private int prevMouseX, prevMouseY;
	
	private List<CategoryDisplayElement> categoryElements = new ArrayList<CategoryDisplayElement>();
	
	public GuiEnderCompendium(){
		animationList.add(offsetX = new AnimatedFloat(Easing.CUBIC));
		animationList.add(offsetY = new AnimatedFloat(Easing.CUBIC));
		animationList.add(portalScale = new AnimatedFloat(Easing.LINEAR));
		
		int alphaDelay = 10;
		for(KnowledgeCategories category:KnowledgeCategories.categoryList)categoryElements.add(new CategoryDisplayElement(category,alphaDelay += 2));
		
		portalScale.startAnimation(2.5F,1F,0.6F);
	}
	
	@Override
	public void initGui(){
		this.portalRenderer = new GuiEndPortalRenderer(this,width-32,height-32,0);
		
		buttonList.add(new GuiButton(0,(width>>1)-110,height-48+21,98,20,I18n.format("gui.back")));
		buttonList.add(new GuiButton(4,(width>>1)+12,height-48+21,98,20,I18n.format("gui.done")));
	}
	
	@Override
	protected void actionPerformed(GuiButton button){
		if (!(button.enabled && button.visible))return;
		
		if (button.id == 4){
			mc.displayGuiScreen((GuiScreen)null);
			mc.setIngameFocus();
		}
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int buttonId){
		if (buttonId == 1)actionPerformed((GuiButton)buttonList.get(0));
		super.mouseClicked(mouseX,mouseY,buttonId);
	}
	
	@Override
	protected void keyTyped(char key, int keyCode){
		if (keyCode == 1)actionPerformed((GuiButton)buttonList.get(0));
	}
	
	@Override
	public void updateScreen(){
		prevOffsetX = offsetX.value();
		prevOffsetY = offsetY.value();
		prevPortalScale = portalScale.value();
		
		for(AnimatedFloat animation:animationList)animation.update(0.05F);
		for(CategoryDisplayElement element:categoryElements)element.update();
	}

	private void renderScreen(int mouseX, int mouseY, float partialTickTime){
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glColor4f(1F,1F,1F,1F);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);

		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL11.GL_DEPTH_TEST);
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
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		for(CategoryDisplayElement element:categoryElements)element.render(this,(int)offsetX.value(),(int)offsetY.value(),partialTickTime);
		
		for(CategoryDisplayElement element:categoryElements){
			if (element.isMouseOver(mouseX,mouseY,(int)offsetX.value(),(int)offsetY.value())){
				GuiItemRenderHelper.drawTooltip(this,fontRendererObj,mouseX,mouseY,element.category.getTooltip());
			}
		}
		
		prevMouseX = mouseX;
		prevMouseY = mouseY;
		
		offsetX.set(width>>1);
		offsetY.set(height>>1);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTickTime){
		drawDefaultBackground();
		GL11.glDepthFunc(GL11.GL_GEQUAL);
		GL11.glPushMatrix();
		GL11.glTranslatef(0F,0F,-200F);
		portalRenderer.draw(ptt(offsetX.value(),prevOffsetX,partialTickTime),ptt(offsetY.value(),prevOffsetY,partialTickTime),ptt(portalScale.value(),prevPortalScale,partialTickTime));
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glEnable(GL11.GL_CULL_FACE);
		renderScreen(mouseX,mouseY,partialTickTime);
		GL11.glPopMatrix();
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		super.drawScreen(mouseX,mouseY,partialTickTime);
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
	
	private static class RegistrationDisplayElement{
		private final IGuiItemStackRenderer element;
		
		public RegistrationDisplayElement(IGuiItemStackRenderer element){
			this.element = element;
		}
		
		public void render(GuiScreen gui, int offsetX, int offsetY, float partialTickTime){
			RenderHelper.disableStandardItemLighting();
			gui.mc.getTextureManager().bindTexture(texBack);
			gui.drawTexturedModalRect(element.getX()-3+offsetX,element.getY()-3+offsetY,0,50,22,22);
			RenderHelper.enableGUIStandardItemLighting();
			renderItem.renderItemIntoGUI(gui.mc.fontRenderer,gui.mc.getTextureManager(),element.getItemStack(),element.getX()+offsetX,element.getY()+offsetY);
		}
	}
	
	private static class CategoryDisplayElement{
		public final KnowledgeCategories category;
		private final AnimatedFloat alpha = new AnimatedFloat(Easing.LINEAR);
		private float prevAlpha;
		private byte alphaStartDelay;
		
		public CategoryDisplayElement(KnowledgeCategories category, int alphaStartDelay){
			this.category = category;
			this.alphaStartDelay = (byte)alphaStartDelay;
		}
		
		public void update(){
			if (alphaStartDelay > 0 && --alphaStartDelay == 0)alpha.startAnimation(0F,1F,0.4F);
			prevAlpha = alpha.value();
			alpha.update(0.05F);
		}

		public void render(GuiScreen gui, int offsetX, int offsetY, float partialTickTime){
			RenderHelper.disableStandardItemLighting();
			GL11.glColor4f(1F,1F,1F,prevAlpha+(alpha.value()-prevAlpha)*partialTickTime);			
			gui.mc.getTextureManager().bindTexture(texBack);
			gui.drawTexturedModalRect(category.getX()+offsetX+2,category.getY()+offsetY,23,50,40,40);
			
			GL11.glPushMatrix();
			GL11.glScalef(0.5F,0.5F,1F);
			gui.drawTexturedModalRect(2*(category.getX()+offsetX+8),2*(category.getY()+offsetY+4),(category.id%4)*56,194-62*(category.id>>2),56,62);
			GL11.glPopMatrix();
			
			GL11.glColor4f(1F,1F,1F,1F);
		}
		
		public boolean isMouseOver(int mouseX, int mouseY, int offsetX, int offsetY){
			int x = category.getX()+offsetX, y = category.getY()+offsetY;
			return mouseX >= x+2 && mouseY >= y && mouseX <= x+42 && mouseY <= y+40 && MathUtil.floatEquals(alpha.value(),1F);
		}
	}
}
