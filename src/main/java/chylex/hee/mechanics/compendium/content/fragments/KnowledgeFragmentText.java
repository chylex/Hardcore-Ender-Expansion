package chylex.hee.mechanics.compendium.content.fragments;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL11;
import chylex.hee.gui.GuiEnderCompendium;
import chylex.hee.mechanics.compendium.content.KnowledgeFragment;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class KnowledgeFragmentText extends KnowledgeFragment{
	public static boolean enableSmoothRendering = false;
	
	private String content;
	
	public KnowledgeFragmentText(int globalID){
		super(globalID);
	}

	public KnowledgeFragmentText setContents(String text){
		this.content = text;
		return this;
	}


	@Override
	@SideOnly(Side.CLIENT)
	public int getHeight(GuiEnderCompendium gui, boolean isUnlocked){
		boolean origFont = gui.mc.fontRenderer.getUnicodeFlag();
		gui.mc.fontRenderer.setUnicodeFlag(true);
		int h = gui.mc.fontRenderer.listFormattedStringToWidth(getString(isUnlocked),GuiEnderCompendium.guiPageWidth-10).size()*gui.mc.fontRenderer.FONT_HEIGHT;
		gui.mc.fontRenderer.setUnicodeFlag(origFont);
		return h;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(GuiEnderCompendium gui, int x, int y, int mouseX, int mouseY, boolean isUnlocked){
		renderString(getString(isUnlocked),x+1,y,GuiEnderCompendium.guiPageWidth-10,gui);
	}
	
	protected String getString(boolean isUnlocked){
		return isUnlocked ? content : StringUtils.repeat('?',content.length());
	}
	
	public static void renderString(String str, int x, int y, GuiEnderCompendium gui){
		renderString(str,x,y,9999,gui);
	}
	
	public static void renderString(String str, int x, int y, int maxWidth, GuiEnderCompendium gui){
		renderString(str,x,y,maxWidth,255<<24,240<<24,gui);
	}
	
	public static void renderString(String str, int x, int y, int normalColor, int smoothColor, GuiEnderCompendium gui){
		renderString(str,x,y,9999,normalColor,smoothColor,gui);
	}
	
	public static void renderString(String str, int x, int y, int maxWidth, int normalColor, int smoothColor, GuiEnderCompendium gui){
		boolean origFont = gui.mc.fontRenderer.getUnicodeFlag();
		gui.mc.fontRenderer.setUnicodeFlag(true);
		
		if (enableSmoothRendering){
			gui.mc.fontRenderer.drawSplitString(str,x,y,maxWidth,smoothColor);
			
			GL11.glTranslatef(-0.25F,0F,0F);
			gui.mc.fontRenderer.drawSplitString(str,x,y,maxWidth,smoothColor);
			GL11.glTranslatef(0.25F,0F,0F);
			
			GL11.glTranslatef(0F,-0.25F,0F);
			gui.mc.fontRenderer.drawSplitString(str,x,y,maxWidth,smoothColor);
			GL11.glTranslatef(0F,0.25F,0F);
		}
		else gui.mc.fontRenderer.drawSplitString(str,x,y,maxWidth,normalColor);
		
		gui.mc.fontRenderer.setUnicodeFlag(origFont);
	}
}
