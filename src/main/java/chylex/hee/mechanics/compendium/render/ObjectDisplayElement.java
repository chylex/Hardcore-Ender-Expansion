package chylex.hee.mechanics.compendium.render;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;
import chylex.hee.game.save.types.player.CompendiumFile;
import chylex.hee.gui.GuiEnderCompendium;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.content.KnowledgeObject.ILineCallback;
import chylex.hee.mechanics.compendium.content.objects.IObjectHolder;
import chylex.hee.system.abstractions.Vec;
import chylex.hee.system.util.MathUtil;

public class ObjectDisplayElement{
	public static final ILineCallback lineRenderer = (x1, y1, x2, y2) -> {
		if (x1 == x2 || y1 == y2){
			Gui.drawRect(x1-1,y1-1,x2+1,y2+1,(255<<24)|(255<<16)|(255<<8)|255);
		}
		else{ // TODO um... improve?
			Vec vec = Vec.xz(x2-x1,y2-y1);
			int len = MathUtil.ceil(vec.length());
			vec = vec.normalized();
			
			float x = x1, y = y1;
			
			for(int a = 0; a < len; a++){
				Gui.drawRect((int)x-1,(int)y-1,(int)x+1,(int)y+1,(255<<24)|(255<<16)|(255<<8)|255);
				x += vec.x;
				y += vec.z;
			}
		}
	};
	
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
	
	public void render(GuiScreen gui, CompendiumFile compendiumFile, int yLowerBound, int yUpperBound){
		int x = gui.width/2-11+object.getX(), y = this.y-11+object.getY();
		if (y < yLowerBound || y > yUpperBound)return;
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(1F,1F,1F,1F);
		
		BackgroundTile tile = compendiumFile.isDiscovered(object) ? BackgroundTile.PLAIN : BackgroundTile.DISABLED;
		
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
		
		RenderHelper.disableStandardItemLighting();
		gui.mc.getTextureManager().bindTexture(GuiEnderCompendium.texBack);
		gui.drawTexturedModalRect(x,y,tile.x,tile.y,22,22);
		RenderHelper.enableGUIStandardItemLighting();
		GuiEnderCompendium.renderItem.renderItemIntoGUI(gui.mc.fontRenderer,gui.mc.getTextureManager(),object.holder.getDisplayItemStack(),x+3,y+3,true);
	}
	
	public boolean isMouseOver(int mouseX, int mouseY, int centerX, int offsetY){
		int x = centerX-11+object.getX(), y = this.y+object.getY()-11+offsetY;
		return mouseX >= x && mouseY >= y && mouseX <= x+20 && mouseY <= y+20;
	}
}