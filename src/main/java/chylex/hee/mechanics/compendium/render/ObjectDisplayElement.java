package chylex.hee.mechanics.compendium.render;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;
import chylex.hee.game.save.types.player.CompendiumFile;
import chylex.hee.gui.GuiEnderCompendium;
import chylex.hee.gui.helpers.GuiHelper;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.content.objects.IObjectHolder;

public class ObjectDisplayElement{
	private enum BackgroundTile{
		PLAIN(113,0), DISABLED(113,23), CHECKERED(136,0), BRIGHT(136,23), GOLD(159,0);
		
		final byte x, y;
		
		BackgroundTile(int x, int y){
			this.x = (byte)x;
			this.y = (byte)y;
		}
	}
	
	public final KnowledgeObject<? extends IObjectHolder<?>> object;
	public final int y;
	
	public ObjectDisplayElement(KnowledgeObject<? extends IObjectHolder<?>> object, int y){
		this.object = object;
		this.y = y;
	}
	
	public void renderLine(GuiScreen gui, CompendiumFile file, int yLowerBound, int yUpperBound){
		if (file.getDiscoveryDistance(object) == CompendiumFile.distanceLimit)return;
		
		final int offX = gui.width/2, offY = this.y; // TODO handle bounds
		int brightness = 224;
		
		for(KnowledgeObject<?> child:object.getChildren()){
			int childBrightness = 224-64*file.getDiscoveryDistance(child);
			if (childBrightness < brightness)brightness = childBrightness;
		}
		
		final int color = (255<<24)|(brightness<<16)|(brightness<<8)|brightness;
		
		object.connectToChildren((x1, y1, x2, y2) -> GuiHelper.renderLine(offX+x1,offY+y1,offX+x2,offY+y2,color));
	}
	
	public void renderObject(GuiScreen gui, CompendiumFile file, int yLowerBound, int yUpperBound){
		if (file.getDiscoveryDistance(object) == CompendiumFile.distanceLimit)return;
		
		int x = gui.width/2+object.getX(), y = this.y+object.getY();
		if (y < yLowerBound || y > yUpperBound)return;
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(1F,1F,1F,1F);
		
		BackgroundTile tile = file.isDiscovered(object) ? BackgroundTile.PLAIN : BackgroundTile.DISABLED;
		
		/* TODO if (compendiumFile.hasDiscoveredObject(object)){
			boolean hasAll = true;
			
			for(KnowledgeFragment fragment:object.getFragments()){
				if (!compendiumFile.hasUnlockedFragment(fragment)){
					hasAll = false;
					break;
				}
			}
			
			tile = hasAll ? BackgroundTile.GOLD : BackgroundTile.PLAIN;
		}*/
		
		renderObject(object,x,y,file,gui);
	}
	
	public boolean isMouseOver(int mouseX, int mouseY, int centerX, int offsetY){
		int x = centerX-11+object.getX(), y = this.y+object.getY()-11+offsetY;
		return mouseX >= x && mouseY >= y && mouseX <= x+20 && mouseY <= y+20;
	}
	
	public static void renderObject(KnowledgeObject<?> object, int x, int y, CompendiumFile file, Gui gui){
		Minecraft mc = Minecraft.getMinecraft();
		
		RenderHelper.disableStandardItemLighting();
		mc.getTextureManager().bindTexture(GuiEnderCompendium.texBack);
		// TODO gui.drawTexturedModalRect(x-11,y-11,tile.x,tile.y,22,22);
		RenderHelper.enableGUIStandardItemLighting();
		GuiEnderCompendium.renderItem.renderItemIntoGUI(mc.fontRenderer,mc.getTextureManager(),object.holder.getDisplayItemStack(),x+3,y+3,true);
	}
}