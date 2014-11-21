package chylex.hee.mechanics.compendium.render;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;
import chylex.hee.gui.GuiEnderCompendium;
import chylex.hee.mechanics.compendium.content.KnowledgeFragment;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.objects.IKnowledgeObjectInstance;
import chylex.hee.mechanics.compendium.player.PlayerCompendiumData;

public class ObjectDisplayElement{
	private enum BackgroundTile{
		PLAIN(113,0), DISABLED(113,23), CHECKERED(136,0), BRIGHT(136,23), GOLD(159,0);
		
		final byte x, y;
		
		BackgroundTile(int x, int y){
			this.x = (byte)x;
			this.y = (byte)y;
		}
	}
	
	public final KnowledgeObject<IKnowledgeObjectInstance<?>> object;
	private final int y;
	
	public ObjectDisplayElement(KnowledgeObject<IKnowledgeObjectInstance<?>> object, int y){
		this.object = object;
		this.y = y;
	}
	
	public void render(GuiScreen gui, PlayerCompendiumData compendiumData){
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(1F,1F,1F,1F);
		
		int x = GuiEnderCompendium.guiObjLeft+object.getX(), y = this.y+object.getY();
		
		BackgroundTile tile = BackgroundTile.DISABLED;
		
		if (compendiumData.hasDiscoveredObject(object)){
			boolean hasAll = true;
			
			for(KnowledgeFragment fragment:object.getFragments()){
				if (!compendiumData.hasUnlockedFragment(fragment)){
					hasAll = false;
					break;
				}
			}
			
			tile = hasAll ? BackgroundTile.GOLD : BackgroundTile.PLAIN;
		}
		
		RenderHelper.disableStandardItemLighting();
		gui.mc.getTextureManager().bindTexture(GuiEnderCompendium.texBack);
		gui.drawTexturedModalRect(x,y,tile.x,tile.y,22,22);
		RenderHelper.enableGUIStandardItemLighting();
		GuiEnderCompendium.renderItem.renderItemIntoGUI(gui.mc.fontRenderer,gui.mc.getTextureManager(),object.getItemStack(),x+3,y+3);
	}
	
	public boolean isMouseOver(int mouseX, int mouseY, int offsetY){
		int x = GuiEnderCompendium.guiObjLeft+object.getX(), y = this.y+object.getY()+offsetY;
		return mouseX >= x-2 && mouseY >= y-1 && mouseX <= x+18 && mouseY <= y+18;
	}
}