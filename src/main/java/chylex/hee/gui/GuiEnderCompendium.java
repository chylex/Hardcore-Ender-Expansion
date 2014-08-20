package chylex.hee.gui;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import chylex.hee.gui.helpers.AnimatedFloat;
import chylex.hee.gui.helpers.AnimatedFloat.Easing;
import chylex.hee.gui.helpers.GuiEndPortalRenderer;
import chylex.hee.gui.helpers.GuiItemRenderHelper.ITooltipRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiEnderCompendium extends GuiScreen implements ITooltipRenderer{
	public static final int guiWidth = 256, guiHeight = 256, guiTopOffset = -14;
	public static final int guiPageTexWidth = 152, guiPageTexHeight = 226;
	public static final int guiPageWidth = 126, guiPageHeight = 176, guiLeft = 18, guiTop = 20;
	
	public static final RenderItem renderItem = new RenderItem();
	public static final ResourceLocation texPage = new ResourceLocation("hardcoreenderexpansion:textures/gui/knowledge_book.png");
	private static final ResourceLocation texBack = new ResourceLocation("hardcoreenderexpansion:textures/gui/knowledge_book_back.png");
	
	private final GuiEndPortalRenderer portalRenderer;
	
	private List<AnimatedFloat> animationList = new ArrayList<>();
	private AnimatedFloat offsetX, offsetY, portalScale;
	private float prevOffsetX, prevOffsetY, prevPortalScale;
	
	public GuiEnderCompendium(){
		this.portalRenderer = new GuiEndPortalRenderer(this,guiWidth,guiHeight,guiTopOffset);
		
		animationList.add(offsetX = new AnimatedFloat(Easing.CUBIC));
		animationList.add(offsetY = new AnimatedFloat(Easing.CUBIC));
		animationList.add(portalScale = new AnimatedFloat(Easing.LINEAR));
		
		portalScale.startAnimation(1.5F,1F,2F);
	}
	
	@Override
	public void updateScreen(){
		prevOffsetX = offsetX.value();
		prevOffsetY = offsetY.value();
		prevPortalScale = portalScale.value();
		
		for(AnimatedFloat animation:animationList)animation.update(0.05F);
	}

	private void renderScreen(int mouseX, int mouseY, float partialTickTime){
		
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTickTime){
		drawDefaultBackground();
		GL11.glDepthFunc(GL11.GL_GEQUAL);
		GL11.glPushMatrix();
		GL11.glTranslatef(0F,0F,-200F);
		portalRenderer.draw(prevOffsetX+(offsetX.value()-prevOffsetX)*partialTickTime+mouseX,prevOffsetY+(offsetY.value()-prevOffsetY)*partialTickTime+mouseY,prevPortalScale+(portalScale.value()-prevPortalScale)*partialTickTime);
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
}
