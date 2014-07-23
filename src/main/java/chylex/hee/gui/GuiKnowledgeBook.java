package chylex.hee.gui;
import gnu.trove.stack.array.TShortArrayStack;
import java.nio.FloatBuffer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import chylex.hee.gui.GuiItemRenderHelper.ITooltipRenderer;
import chylex.hee.mechanics.knowledge.KnowledgeRegistrations;
import chylex.hee.mechanics.knowledge.data.KnowledgeCategory;
import chylex.hee.mechanics.knowledge.data.KnowledgeRegistration;
import chylex.hee.mechanics.knowledge.fragment.KnowledgeFragment;
import chylex.hee.mechanics.knowledge.fragment.TextKnowledgeFragment;
import chylex.hee.mechanics.knowledge.util.IGuiItemStackRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiKnowledgeBook extends GuiScreen implements ITooltipRenderer{
	public static final int guiWidth = 256, guiHeight = 256, guiTopOffset = -14;
	public static final int guiPageTexWidth = 152, guiPageTexHeight = 226;
	public static final int guiPageWidth = 126, guiPageHeight = 176, guiLeft = 18, guiTop = 20;
	
	public static int mouseX,mouseY;
	
	public static final ResourceLocation texPage = new ResourceLocation("hardcoreenderexpansion:textures/gui/knowledge_book.png");
	private static final ResourceLocation texBack = new ResourceLocation("hardcoreenderexpansion:textures/gui/knowledge_book_back.png");
	private static final ResourceLocation texPortalSky = new ResourceLocation("textures/environment/end_sky.png");
	private static final ResourceLocation texPortal = new ResourceLocation("textures/entity/end_portal.png");
	private static final Random consistentRandom = new Random(31100L);
	public static final RenderItem renderItem = new RenderItem();
	
	private final FloatBuffer floatBuffer = GLAllocation.createDirectFloatBuffer(16);
	
	private final ItemStack bookItemStack;
	private float offsetX, offsetY;
	private float prevOffsetX, prevOffsetY;
	private float startOffsetX, startOffsetY;
	private float targetOffsetX, targetOffsetY;
	private float time;
	
	private KnowledgeCategory highlightedCategory;
	private KnowledgeRegistration highlightedRegistration;
	private Map<KnowledgeFragment,Boolean> activeFragments = new LinkedHashMap<>();
	private short pageIndex;
	private TShortArrayStack pageIndexPrev = new TShortArrayStack();
	private GuiButton[] pageArrows = new GuiButton[2];
	private GuiButton helpButton;
	
	public GuiKnowledgeBook(ItemStack bookItemStack){
		this.bookItemStack = bookItemStack;
	}
	
	@Override
	public void initGui(){
		int pageLeft = (width-guiPageTexWidth)>>1,top = ((height-guiHeight)>>1)+guiTopOffset;
		
		buttonList.add(new GuiButton(0,(width>>1)-100,top+guiHeight+20,98,20,I18n.format("gui.back")));
		buttonList.add(new GuiButton(4,(width>>1)+2,top+guiHeight+20,98,20,I18n.format("gui.done")));
		buttonList.add(pageArrows[0] = new GuiButtonPageArrow(1,pageLeft+18,top+guiPageTexHeight-18,false));
		buttonList.add(pageArrows[1] = new GuiButtonPageArrow(2,pageLeft+guiPageTexWidth-23-18,top+guiPageTexHeight-18,true));
		buttonList.add(helpButton = new GuiButton(3,(width>>1)-10,(height>>1)-10+guiTopOffset,20,20,"?"));
		for(int a = 0; a < 2; a++)pageArrows[a].visible = false;
		helpButton.visible = false;
		
		if (bookItemStack.stackTagCompound != null && bookItemStack.stackTagCompound.hasKey("knowledgeLast")){
			NBTTagCompound tag = bookItemStack.stackTagCompound.getCompoundTag("knowledgeLast");
			String catName = tag.getString("cat"),regName = tag.getString("reg");
			byte fid = tag.getByte("fid");
			
			for(KnowledgeCategory category:KnowledgeCategory.categories){
				if (category.identifier.equals(catName)){
					for(KnowledgeRegistration registration:category.registrations){
						if (registration.identifier.equals(regName)){
							highlightedCategory = category;
							prevOffsetX = offsetX = targetOffsetX = -category.getTargetOffsetX();
							prevOffsetY = offsetY = targetOffsetY = -category.getTargetOffsetY();
							openRegistration(registration);
							
							int index = 0, yy = 0, fragmentHeight;
							top = guiTop+((height-guiPageTexHeight)>>1);
							
							for(Entry<KnowledgeFragment,Boolean> entry:activeFragments.entrySet()){
								if (top+yy+(fragmentHeight = entry.getKey().getHeight(mc,entry.getValue())+10) > top+guiPageHeight){
									pageIndexPrev.push(pageIndex);
									pageIndex = (short)index;
									top = guiTop+((height-guiPageTexHeight)>>1);
									break;
								}
								
								++index;
								if (entry.getKey().id >= fid)break;
								yy += fragmentHeight;
							}
							
							break;
						}
					}
					
					break;
				}
			}
			
			bookItemStack.stackTagCompound.removeTag("knowledgeLast");
		}
		else if (bookItemStack.stackTagCompound != null){
			MovingObjectPosition mop = mc.objectMouseOver;
			// TODO stuff
		}
	}
	
	@Override
	protected void actionPerformed(GuiButton button){
		if (!(button.enabled && button.visible))return;
		
		if (button.id == 0){
			if (highlightedRegistration != null)highlightedRegistration = null;
			else if (highlightedCategory != null){
				if ((targetOffsetX == 0 && targetOffsetY == 0) || (offsetX == targetOffsetX && offsetY == targetOffsetY)){
					targetOffsetX = targetOffsetY = 0;
					if (time > 0F && time < 0.5F)time = Math.min(time+0.3F,0.5F);
				}
			}
			else{
				mc.displayGuiScreen((GuiScreen)null);
				mc.setIngameFocus();
			}
		}
		else if (button.id == 4){
			mc.displayGuiScreen((GuiScreen)null);
			mc.setIngameFocus();
		}
		else if (button.id == 1)pageIndex = pageIndexPrev.size() == 0 ? 0 : pageIndexPrev.pop();
		else if (button.id == 2){
			pageIndexPrev.push(pageIndex);
			
			int top = guiTop+((height-guiPageTexHeight)>>1)+guiTopOffset, yy = 0, index = 0, fragmentHeight;
			
			for(Entry<KnowledgeFragment,Boolean> entry:activeFragments.entrySet()){
				if (++index < pageIndex)continue;
				
				if (top+yy+(fragmentHeight = entry.getKey().getHeight(mc,entry.getValue())+10) > top+guiPageHeight)break;
				yy += fragmentHeight;
			}
			
			pageIndex = (short)index;
		}
		else if (button.id == 3){
			highlightedRegistration = KnowledgeRegistrations.HELP;
			activeFragments.clear();
			for(KnowledgeFragment fragment:KnowledgeRegistrations.HELP.fragmentSet.getAllFragments()){
				activeFragments.put(fragment,true);
			}
		}
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int buttonId){
		if (buttonId == 1){
			actionPerformed((GuiButton)buttonList.get(0));
		}
		else if (!((GuiButton)buttonList.get(1)).mousePressed(mc,mouseX,mouseY) && highlightedRegistration != null){
			if (mouseX < ((width-guiPageTexWidth)>>1) || mouseX > ((width+guiPageTexWidth)>>1) || mouseY < ((height-guiPageTexHeight)>>1) || mouseY > ((height+guiPageTexHeight)>>1)){
				highlightedRegistration = null;
				return;
			}
		}
		
		super.mouseClicked(mouseX,mouseY,buttonId);
	}
	
	@Override
	protected void keyTyped(char key, int keyCode){
		if (keyCode == 1){
			if (highlightedRegistration != null)highlightedRegistration = null;
			else if (highlightedCategory != null){
				if ((targetOffsetX == 0 && targetOffsetY == 0) || (offsetX == targetOffsetX && offsetY == targetOffsetY)){
					targetOffsetX = targetOffsetY = 0;
					if (time > 0F && time < 0.5F)time = Math.min(time+0.3F,0.5F);
				}
			}
			else{
				mc.displayGuiScreen(null);
				mc.setIngameFocus();
			}
		}
	}

	@Override
	public void updateScreen(){
		prevOffsetX = offsetX;
		prevOffsetY = offsetY;
		
		if (offsetX != targetOffsetX || offsetY != targetOffsetY){
			if (time == 0){
				startOffsetX = offsetX;
				startOffsetY = offsetY;
			}
			
			offsetX = cubicEase(time,startOffsetX,targetOffsetX-startOffsetX,0.65F);
			offsetY = cubicEase(time,startOffsetY,targetOffsetY-startOffsetY,0.65F);

			if ((time += 0.05F) >= 0.65F){
				offsetX = targetOffsetX;
				offsetY = targetOffsetY;
				time = 0;
				
				if (offsetX == 0 && offsetY == 0)highlightedCategory = null;
			}
		}
	}
	
	private float cubicEase(float time, float startValue, float step, float duration){
		if ((time /= duration/2F) < 1)return step/2F*time*time*time+startValue;
		time -= 2F;
		return step/2F*(time*time*time+2F)+startValue;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTickTime){
		drawDefaultBackground();
		GL11.glDepthFunc(GL11.GL_GEQUAL);
		GL11.glPushMatrix();
		GL11.glTranslatef(0F,0F,-200F);
		renderPortalBackground(partialTickTime);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glEnable(GL11.GL_CULL_FACE);
		renderScreen(mouseX,mouseY,partialTickTime);
		GL11.glPopMatrix();
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		super.drawScreen(mouseX,mouseY,partialTickTime);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}
	
	private void renderScreen(int mouseX, int mouseY, float partialTickTime){
		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glColor4f(1F,1F,1F,1F);
		RenderHelper.disableStandardItemLighting();
		
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
		mc.getTextureManager().bindTexture(texBack);
		int l = (width-guiWidth)>>1,t = ((height-guiHeight)>>1)+guiTopOffset;
		drawTexturedModalRect(l-16,t-16,0,0,24,24);
		drawTexturedModalRect(l+256-8,t-16,25,0,24,24);
		drawTexturedModalRect(l-16,t+256-8,0,25,24,24);
		drawTexturedModalRect(l+256-8,t+256-8,25,25,24,24);
		for(int a = 0; a < 2; a++){
			drawTexturedModalRect(l+8+120*a,t-16,50,0,120,24);
			drawTexturedModalRect(l+256-8,t+8+120*a,232,0,24,120);
			drawTexturedModalRect(l+8+120*a,t+256-8,50,25,120,24);
			drawTexturedModalRect(l-16,t+8+120*a,206,0,24,120);
		}
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		GL11.glPushMatrix();
		GL11.glTranslatef(prevOffsetX+(offsetX-prevOffsetX)*partialTickTime,prevOffsetY+(offsetY-prevOffsetY)*partialTickTime,0F);
		helpButton.visible = true;
		helpButton.xPosition = (width>>1)-10;
		helpButton.yPosition = (height>>1)-10+guiTopOffset;
		helpButton.drawButton(mc,mouseX,mouseY);
		if (helpButton.mousePressed(mc,mouseX,mouseY) && Mouse.isButtonDown(0) && highlightedRegistration == null && highlightedCategory == null){
			highlightedRegistration = KnowledgeRegistrations.HELP;
			activeFragments.clear();
			for(KnowledgeFragment fragment:KnowledgeRegistrations.HELP.fragmentSet.getAllFragments()){
				activeFragments.put(fragment,true);
			}
		}
		helpButton.visible = false;
		GL11.glPopMatrix();

		RenderHelper.enableGUIStandardItemLighting();
		GL11.glColor4f(1F,1F,1F,1F);
		GL11.glTranslatef((width>>1)-11,(height>>1)-11,0F);
		GL11.glTranslatef(prevOffsetX+(offsetX-prevOffsetX)*partialTickTime,prevOffsetY+(offsetY-prevOffsetY)*partialTickTime,0F);

		for(KnowledgeCategory category:KnowledgeCategory.categories)renderIcon(category);
		
		if (highlightedCategory != null){
			for(KnowledgeRegistration registration:highlightedCategory.registrations){
				renderRegistration(registration);
			}
		}
		
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
		
		for(KnowledgeCategory category:KnowledgeCategory.categories){
			int cx = correctX((int)(category.getX()*1.5F),partialTickTime),cy = correctY((int)(category.getY()*1.5F),partialTickTime);
			
			if (mouseX >= cx-18 && mouseX <= cx+19 && mouseY >= cy-18 && mouseY <= cy+18 && highlightedRegistration == null){
				GuiItemRenderHelper.drawTooltip(this,fontRendererObj,mouseX,mouseY,category.getTooltip());
				
				if (Mouse.isButtonDown(0) && time == 0){
					targetOffsetX = -category.getTargetOffsetX();
					targetOffsetY = -category.getTargetOffsetY();
					highlightedCategory = category;
				}
			}
		}
		
		if (highlightedCategory != null){
			for(KnowledgeRegistration registration:highlightedCategory.registrations){
				int cx = correctX(registration.getX(),partialTickTime),cy = correctY(registration.getY(),partialTickTime);
				
				if (checkRegistrationHover(registration,mouseX,mouseY,cx,cy) && highlightedRegistration == null){
					GuiItemRenderHelper.drawTooltip(this,fontRendererObj,mouseX,mouseY,registration.getRenderer().getTooltip());
					
					if (Mouse.isButtonDown(0) && time == 0)openRegistration(registration);
				}
			}
		}
		
		if (highlightedRegistration != null){
			GL11.glPushMatrix();
			GL11.glColor4f(1F,1F,1F,1F);
			GL11.glDepthFunc(GL11.GL_LEQUAL);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
	   
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);

			mc.getTextureManager().bindTexture(texPage);
			int left = (width-guiPageTexWidth)>>1,top = ((height-guiPageTexHeight)>>1)+guiTopOffset;
			drawTexturedModalRect(left,top,0,0,guiPageTexWidth,guiPageTexHeight);
			
			left += guiLeft;
			top += guiTop;
			
			int index = 0,yy = 0;
			boolean hasNextPage = false;
			int fragmentHeight;
			
			GuiKnowledgeBook.mouseX = mouseX;
			GuiKnowledgeBook.mouseY = mouseY;
			
			for(Entry<KnowledgeFragment,Boolean> entry:activeFragments.entrySet()){
				if (++index < pageIndex)continue;
				
				if (top+yy+(fragmentHeight = entry.getKey().getHeight(mc,entry.getValue())+10) > top+guiPageHeight){
					hasNextPage = true;
					break;
				}
				
				entry.getKey().render(left,top+yy,mc,entry.getValue());
				yy += fragmentHeight;
			}
			
			pageArrows[0].visible = pageIndex > 0;
			pageArrows[1].visible = hasNextPage;
			
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glDepthFunc(GL11.GL_LEQUAL);
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glPopMatrix();
		}
		else{
			for(int a = 0; a < 2; a++)pageArrows[a].visible = false;
		}
	}
	
	private void openRegistration(KnowledgeRegistration registration){
		highlightedRegistration = registration;
		activeFragments.clear();
		
		int[] unlocked = registration.fragmentSet.getUnlockedFragments(bookItemStack);
		for(KnowledgeFragment fragment:registration.fragmentSet.getAllFragments()){
			if (fragment.canShow(unlocked))activeFragments.put(fragment,ArrayUtils.contains(unlocked,fragment.id));
		}
		
		if (activeFragments.isEmpty()){
			activeFragments.put(new TextKnowledgeFragment(-1).setLocalizedText("(no available knowledge)"),true);
		}
		
		pageIndex = 0;
		pageIndexPrev.clear();
	}
	
	private int correctX(int x, float partialTickTime){
		return (width>>1)+x+(int)(prevOffsetX+(offsetX-prevOffsetX)*partialTickTime);
	}
	
	private int correctY(int y, float partialTickTime){
		return (height>>1)+y+(int)(prevOffsetY+(offsetX-prevOffsetX)*partialTickTime)+guiTopOffset;
	}
	
	private void renderIcon(IGuiItemStackRenderer guiItemStackRenderer){
		RenderHelper.disableStandardItemLighting();
		mc.getTextureManager().bindTexture(texBack);
		drawTexturedModalRect((int)((guiItemStackRenderer.getX()-5)*1.5F)-1,(int)((guiItemStackRenderer.getY()-6)*1.5F)+guiTopOffset,23,50,40,40);
		RenderHelper.enableGUIStandardItemLighting();
		GL11.glPushMatrix();
		GL11.glScalef(1.5F,1.5F,1F);
		renderItem.renderItemIntoGUI(mc.fontRenderer,mc.getTextureManager(),guiItemStackRenderer.getItemStack(),guiItemStackRenderer.getX(),(int)(guiItemStackRenderer.getY()-1+guiTopOffset/1.5));
		GL11.glPopMatrix();
	}
	
	private void renderRegistration(KnowledgeRegistration registration){
		RenderHelper.disableStandardItemLighting();
		mc.getTextureManager().bindTexture(texBack);
		switch(registration.getBackgroundTextureIndex()){
			case 0: drawTexturedModalRect(registration.getX(),registration.getY()-3+guiTopOffset,0,50,22,22); break;
			case 1: drawTexturedModalRect(registration.getX(),registration.getY()-26+guiTopOffset,64,50,22,47); break;
			case 2: drawTexturedModalRect(registration.getX()-9,registration.getY()-22+guiTopOffset,87,50,40,49); break;
			case 3: drawTexturedModalRect(registration.getX()-85,registration.getY()-31+guiTopOffset,0,99,192,51); break;
		}
		RenderHelper.enableGUIStandardItemLighting();
		registration.getRenderer().render(mc,registration.getX()+3,registration.getY()+guiTopOffset);
	}
	
	private boolean checkRegistrationHover(KnowledgeRegistration registration, int mouseX, int mouseY, int cx, int cy){
		switch(registration.getBackgroundTextureIndex()){
			case 0: return mouseX >= cx-10 && mouseX <= cx+10 && mouseY >= cy-14 && mouseY <= cy+8;
			case 1: return mouseX >= cx-10 && mouseX <= cx+10 && mouseY >= cy-35 && mouseY <= cy+10;
			case 2: return mouseX >= cx-18 && mouseX <= cx+16 && mouseY >= cy-32 && mouseY <= cy+14;
			case 3: return mouseX >= cx-96 && mouseX <= cx+94 && mouseY >= cy-40 && mouseY <= cy+10;
		}
		return false;
	}
	
	private void renderPortalBackground(float partialTickTime){
		int hw = width>>1,hh = height>>1;

		GL11.glDisable(GL11.GL_LIGHTING);
		consistentRandom.setSeed(31100L);

		for(int layer = 0; layer < 16; ++layer){
			GL11.glPushMatrix();

			float revLayer = (16-layer), scale = 0.09625F, colorMultiplier = 1F/(revLayer+1F);

			if (layer == 0){
				mc.getTextureManager().bindTexture(texPortalSky);
				colorMultiplier = 0.1F;
				revLayer = 15F;
				scale = 1.125F;
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
			}

			if (layer >= 1){
				mc.getTextureManager().bindTexture(texPortal);
				
				if (layer == 1){
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glBlendFunc(GL11.GL_ONE,GL11.GL_ONE);
					scale = 0.2F;
				}
			}

			// magic stuff
			GL11.glTranslatef(0,revLayer*1000,0);
			GL11.glRotatef(120F,1F,0F,1F);
			GL11.glTranslatef(0F,5500F,0F);
			GL11.glScalef(100F,100F,100F);
			// end of magic stuff
			GL11.glTexGeni(GL11.GL_S,GL11.GL_TEXTURE_GEN_MODE,GL11.GL_EYE_LINEAR);
			GL11.glTexGeni(GL11.GL_T,GL11.GL_TEXTURE_GEN_MODE,GL11.GL_EYE_LINEAR);
			GL11.glTexGeni(GL11.GL_R,GL11.GL_TEXTURE_GEN_MODE,GL11.GL_EYE_LINEAR);
			GL11.glTexGeni(GL11.GL_Q,GL11.GL_TEXTURE_GEN_MODE,GL11.GL_EYE_LINEAR);
			GL11.glTexGen(GL11.GL_S,GL11.GL_OBJECT_PLANE,insertIntoBufferAndFlip(1F,0F,0F,0F));
			GL11.glTexGen(GL11.GL_T,GL11.GL_OBJECT_PLANE,insertIntoBufferAndFlip(0F,0F,1F,0F));
			GL11.glTexGen(GL11.GL_R,GL11.GL_OBJECT_PLANE,insertIntoBufferAndFlip(0F,0F,0F,1F));
			GL11.glTexGen(GL11.GL_Q,GL11.GL_EYE_PLANE,insertIntoBufferAndFlip(0F,1F,0F,0F));
			GL11.glEnable(GL11.GL_TEXTURE_GEN_S);
			GL11.glEnable(GL11.GL_TEXTURE_GEN_T);
			GL11.glEnable(GL11.GL_TEXTURE_GEN_R);
			GL11.glEnable(GL11.GL_TEXTURE_GEN_Q);
			GL11.glPopMatrix();
			GL11.glMatrixMode(GL11.GL_TEXTURE);
			GL11.glPushMatrix();
			GL11.glLoadIdentity();
			GL11.glTranslatef(0F,(Minecraft.getSystemTime()%200000L)/200000F,0F);
			GL11.glScalef(scale,scale,scale);
			GL11.glTranslatef(0.5F,0.5F,0F);
			GL11.glRotatef((layer*layer*4321+layer*9)*4F,0F,0F,1F);
			GL11.glTranslatef(-0.5F,-0.5F,0.0F);
			GL11.glTranslatef((prevOffsetX+(offsetX-prevOffsetX)*partialTickTime)*0.015F,(prevOffsetY+(offsetY-prevOffsetY)*partialTickTime)*0.015F,0F); // TRANSLATE
			Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			
			float red = consistentRandom.nextFloat()*0.5F+0.1F;
			float green = consistentRandom.nextFloat()*0.5F+0.4F;
			float blue = consistentRandom.nextFloat()*0.5F+0.5F;
			if (layer == 0)red = green = blue = 1F;

			tessellator.setColorRGBA_F(red*colorMultiplier,green*colorMultiplier,blue*colorMultiplier,1F);
			tessellator.addVertexWithUV(hw-guiWidth/2,hh+guiHeight/2+guiTopOffset,0D,0D,1D);
			tessellator.addVertexWithUV(hw+guiWidth/2,hh+guiHeight/2+guiTopOffset,0D,1D,1D);
			tessellator.addVertexWithUV(hw+guiWidth/2,hh-guiHeight/2+guiTopOffset,0D,1D,0D);
			tessellator.addVertexWithUV(hw-guiWidth/2,hh-guiHeight/2+guiTopOffset,0D,0D,0D);
			tessellator.draw();
			GL11.glPopMatrix();
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
		}

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_GEN_S);
		GL11.glDisable(GL11.GL_TEXTURE_GEN_T);
		GL11.glDisable(GL11.GL_TEXTURE_GEN_R);
		GL11.glDisable(GL11.GL_TEXTURE_GEN_Q);
		GL11.glEnable(GL11.GL_LIGHTING);
	}
	
	private FloatBuffer insertIntoBufferAndFlip(float value1, float value2, float value3, float value4){
		floatBuffer.clear();
		floatBuffer.put(value1).put(value2).put(value3).put(value4);
		floatBuffer.flip();
		return floatBuffer;
	}

	@Override
	public boolean doesGuiPauseGame(){
		return false;
	}

	@Override
	public int getWidth(){
		return width;
	}

	@Override
	public int getHeight(){
		return height;
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
