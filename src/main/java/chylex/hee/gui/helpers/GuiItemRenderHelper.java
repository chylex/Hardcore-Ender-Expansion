package chylex.hee.gui.helpers;
import java.util.List;
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
import chylex.hee.system.abstractions.GL;
import com.google.common.base.Joiner;

public class GuiItemRenderHelper{
	private static final RenderItem renderItem = new RenderItem();
	private static final RenderBlocks renderBlocks = new RenderBlocks();

	public static void renderItemIntoGUI(TextureManager textureManager, ItemStack is, int x, int y){
		Item item = is.getItem();
		int damage = is.getItemDamage();
		Object object = is.getIconIndex();
		Block block = item instanceof ItemBlock ? Block.getBlockFromItem(item) : null;
		
		if (is.getItemSpriteNumber() == 0 && block != null && RenderBlocks.renderItemIn3d(block.getRenderType())){
			RenderHelper.enableGUIStandardItemLighting();
			textureManager.bindTexture(TextureMap.locationBlocksTexture);
			GL.pushMatrix();
			GL.translate((x-2), (y+3), -3.0F+renderItem.zLevel);
			GL.scale(10F, 10F, 10F);
			GL.translate(1F, 0.5F, 1F);
			GL.scale(1F, 1F, -1F);
			GL.rotate(210F, 1F, 0F, 0F);
			GL.rotate(45F, 0F, 1F, 0F);
			
			int col = item.getColorFromItemStack(is, 0);
			if (renderItem.renderWithColor)GL.color((col>>16&255)/255F, (col>>8&255)/255F, (col&255)/255F, 1F);

			GL.rotate(-90F, 0F, 1F, 0F);
			renderBlocks.useInventoryTint = renderItem.renderWithColor;
			renderBlocks.renderBlockAsItem(block, damage, 1F);
			renderBlocks.useInventoryTint = true;
			GL.popMatrix();
			RenderHelper.disableStandardItemLighting();
		}
		else if (item.requiresMultipleRenderPasses()){
			GL.disableLighting();

			for(int pass = 0; pass < item.getRenderPasses(damage); ++pass){
				textureManager.bindTexture(is.getItemSpriteNumber() == 0?TextureMap.locationBlocksTexture:TextureMap.locationItemsTexture);
				IIcon icon = item.getIcon(is, pass);
				
				int col = item.getColorFromItemStack(is, pass);
				if (renderItem.renderWithColor)GL.color((col>>16&255)/255F, (col>>8&255)/255F, (col&255)/255F, 1F);

				renderItem.renderIcon(x, y, icon, 16, 16);
			}

			GL.enableLighting();
		}
		else{
			GL.disableLighting();
			ResourceLocation resourcelocation = textureManager.getResourceLocation(is.getItemSpriteNumber());
			textureManager.bindTexture(resourcelocation);

			if (object == null)object = ((TextureMap)textureManager.getTexture(resourcelocation)).getAtlasSprite("missingno");

			int col = item.getColorFromItemStack(is, 0);
			if (renderItem.renderWithColor)GL.color((col>>16&255)/255F, (col>>8&255)/255F, (col&255)/255F, 1F);

			renderItem.renderIcon(x, y, (IIcon)object, 16, 16);
			GL.enableLighting();
		}

		GL.enableCullFace();
		
		renderItem.renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRenderer, textureManager, is, x, y);
		GL.disableLighting();
	}
	
	private static int tooltipX, tooltipY;
	private static String tooltipString;
	
	public static void setupTooltip(int x, int y, List<String> tooltip){
		setupTooltip(x, y, Joiner.on('\n').join(tooltip));
	}
	
	public static void setupTooltip(int x, int y, String tooltip){
		tooltipX = x;
		tooltipY = y;
		tooltipString = tooltip;
	}
	
	public static void drawTooltip(GuiScreen gui, FontRenderer fontRendererObj){
		drawTooltip(gui, fontRendererObj, 0, 0);
	}
	
	public static void drawTooltip(GuiScreen gui, FontRenderer fontRendererObj, int guiLeft, int guiTop){
		if (tooltipString == null)return;
		String[] strings = tooltipString.split("\n");
		
		GL.disableRescaleNormal();
		RenderHelper.disableStandardItemLighting();
		GL.disableLighting();
		GL.disableDepthTest();
		
		int maxWidth = 0, xx = tooltipX-guiLeft+12, yy = tooltipY-guiTop-12, height = strings.length > 1 ? 10+(strings.length-1)*10 : 8;
		
		for(String s:strings)maxWidth = Math.max(maxWidth, fontRendererObj.getStringWidth(s));
		
		if (xx+maxWidth > gui.width)xx -= 28+maxWidth;
		if (yy+height+6 > gui.height)yy -= gui.height-height-6;

		renderItem.zLevel = 300F;
		
		int grad1 = -267386864, grad2 = 1347420415, grad3 = (grad2&16711422)>>1|grad2&-16777216;
		
		GuiHelper.renderGradient(xx-3, yy-4, xx+maxWidth+3, yy-3, grad1, grad1, 300F);
		GuiHelper.renderGradient(xx-3, yy+height+3, xx+maxWidth+3, yy+height+4, grad1, grad1, 300F);
		GuiHelper.renderGradient(xx-3, yy-3, xx+maxWidth+3, yy+height+3, grad1, grad1, 300F);
		GuiHelper.renderGradient(xx-4, yy-3, xx-3, yy+height+3, grad1, grad1, 300F);
		GuiHelper.renderGradient(xx+maxWidth+3, yy-3, xx+maxWidth+4, yy+height+3, grad1, grad1, 300F);
		
		GuiHelper.renderGradient(xx-3, yy-2, xx-2, yy+height+2, grad2, grad3, 300F);
		GuiHelper.renderGradient(xx+maxWidth+2, yy-2, xx+maxWidth+3, yy+height+2, grad2, grad3, 300F);
		GuiHelper.renderGradient(xx-3, yy-3, xx+maxWidth+3, yy-2, grad2, grad2, 300F);
		GuiHelper.renderGradient(xx-3, yy+height+2, xx+maxWidth+3, yy+height+3, grad3, grad3, 300F);

		for(int a = 0; a < strings.length; ++a){
			fontRendererObj.drawStringWithShadow(strings[a], xx, yy, -1);
			yy += a == 0 ? 12 : 10;
		}

		renderItem.zLevel = 0F;
		
		GL.enableDepthTest();
		GL.enableRescaleNormal();
		
		tooltipString = null;
	}
}
