package chylex.hee.gui.helpers;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class GuiItemRenderHelper{
	private static final RenderItem renderItem = new RenderItem();
	private static final RenderBlocks renderBlocks = new RenderBlocks();

	public static void renderItemIntoGUI(TextureManager textureManager, ItemStack is, int x, int y){
		Item item = is.getItem();
		int damage = is.getItemDamage();
		Object object = is.getIconIndex();

		Block block = (item instanceof ItemBlock ? Block.getBlockFromItem(item) : null);
		if (is.getItemSpriteNumber() == 0 && block != null && RenderBlocks.renderItemIn3d(block.getRenderType())){
			RenderHelper.enableGUIStandardItemLighting();
			textureManager.bindTexture(TextureMap.locationBlocksTexture);
			GL11.glPushMatrix();
			GL11.glTranslatef((x-2),(y+3),-3.0F+renderItem.zLevel);
			GL11.glScalef(10F,10F,10F);
			GL11.glTranslatef(1F,0.5F,1F);
			GL11.glScalef(1F,1F,-1F);
			GL11.glRotatef(210F,1F,0F,0F);
			GL11.glRotatef(45F,0F,1F,0F);
			
			int col = item.getColorFromItemStack(is,0);
			if (renderItem.renderWithColor)GL11.glColor4f((col>>16&255)/255F,(col>>8&255)/255F,(col&255)/255F,1F);

			GL11.glRotatef(-90F,0F,1F,0F);
			renderBlocks.useInventoryTint = renderItem.renderWithColor;
			renderBlocks.renderBlockAsItem(block,damage,1F);
			renderBlocks.useInventoryTint = true;
			GL11.glPopMatrix();
			RenderHelper.disableStandardItemLighting();
		}
		else if (item.requiresMultipleRenderPasses()){
			GL11.glDisable(GL11.GL_LIGHTING);

			for(int pass = 0; pass < item.getRenderPasses(damage); ++pass){
				textureManager.bindTexture(is.getItemSpriteNumber() == 0?TextureMap.locationBlocksTexture:TextureMap.locationItemsTexture);
				IIcon icon = item.getIcon(is,pass);
				
				int col = item.getColorFromItemStack(is,pass);
				if (renderItem.renderWithColor)GL11.glColor4f((col>>16&255)/255F,(col>>8&255)/255F,(col&255)/255F,1F);

				renderItem.renderIcon(x,y,icon,16,16);
			}

			GL11.glEnable(GL11.GL_LIGHTING);
		}
		else{
			GL11.glDisable(GL11.GL_LIGHTING);
			ResourceLocation resourcelocation = textureManager.getResourceLocation(is.getItemSpriteNumber());
			textureManager.bindTexture(resourcelocation);

			if (object == null)object = ((TextureMap)textureManager.getTexture(resourcelocation)).getAtlasSprite("missingno");

			int col = item.getColorFromItemStack(is,0);
			if (renderItem.renderWithColor)GL11.glColor4f((col>>16&255)/255F,(col>>8&255)/255F,(col&255)/255F,1F);

			renderItem.renderIcon(x,y,(IIcon)object,16,16);
			GL11.glEnable(GL11.GL_LIGHTING);
		}

		GL11.glEnable(GL11.GL_CULL_FACE);
		
		renderItem.renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRenderer,textureManager,is,x,y);
	}
	
	public static void drawTooltip(ITooltipRenderer gui, FontRenderer fontRendererObj, int x, int y, String tooltip){
		if (tooltip == null)return;
		String[] strings = tooltip.split("\n");
		
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		int maxWidth = 0, xx = x+12, yy = y-12, height = strings.length > 1 ? 10+(strings.length-1)*10 : 8;
		
		for(String s:strings)maxWidth = Math.max(maxWidth,fontRendererObj.getStringWidth(s));

		GuiScreen guiScreen = (GuiScreen)gui;
		if (xx+maxWidth > guiScreen.width)xx -= 28+maxWidth;
		if (yy+height+6 > guiScreen.height)yy -= guiScreen.height-height-6;

		renderItem.zLevel = 300F;
		gui.setZLevel(300F);
		
		int grad1 = -267386864,grad2 = 1347420415,grad3 = (grad2&16711422)>>1|grad2&-16777216;
		
		gui.callDrawGradientRect(xx-3,yy-4,xx+maxWidth+3,yy-3,grad1,grad1);
		gui.callDrawGradientRect(xx-3,yy+height+3,xx+maxWidth+3,yy+height+4,grad1,grad1);
		gui.callDrawGradientRect(xx-3,yy-3,xx+maxWidth+3,yy+height+3,grad1,grad1);
		gui.callDrawGradientRect(xx-4,yy-3,xx-3,yy+height+3,grad1,grad1);
		gui.callDrawGradientRect(xx+maxWidth+3,yy-3,xx+maxWidth+4,yy+height+3,grad1,grad1);
		
		gui.callDrawGradientRect(xx-3,yy-2,xx-2,yy+height+2,grad2,grad3);
		gui.callDrawGradientRect(xx+maxWidth+2,yy-2,xx+maxWidth+3,yy+height+2,grad2,grad3);
		gui.callDrawGradientRect(xx-3,yy-3,xx+maxWidth+3,yy-2,grad2,grad2);
		gui.callDrawGradientRect(xx-3,yy+height+2,xx+maxWidth+3,yy+height+3,grad3,grad3);

		for(int a = 0; a < strings.length; ++a){
			fontRendererObj.drawStringWithShadow(strings[a],xx,yy,-1);
			yy += a == 0?12:10;
		}

		renderItem.zLevel = 0F;
		gui.setZLevel(0F);
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
	}
	
	public static interface ITooltipRenderer{
		void setZLevel(float newZLevel);
		void callDrawGradientRect(int x1, int y1, int x2, int y2, int color1, int color2);
	}
}
