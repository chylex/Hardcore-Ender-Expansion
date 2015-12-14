package chylex.hee.mechanics.compendium.elements;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;
import chylex.hee.game.save.types.player.CompendiumFile;
import chylex.hee.gui.GuiEnderCompendium;
import chylex.hee.gui.helpers.GuiHelper;
import chylex.hee.mechanics.compendium.content.KnowledgeFragment;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.content.fragments.KnowledgeFragmentType;
import chylex.hee.mechanics.compendium.content.objects.IObjectHolder;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class CompendiumObjectElement{
	public enum ObjectShape{
		PLAIN(150,0), IMPORTANT(150,27), SPECIAL(150,54);
		
		final int x, y;
		
		private ObjectShape(int x, int y){
			this.x = x;
			this.y = y;
		}
	}
	
	private enum ObjectStatus{
		NONE_UNLOCKED(255,255,255),
		ALL_UNLOCKED(255,227,72,"ec.tooltip.allUnlocked"),
		ALL_BUT_SECRET(92,255,72,"ec.tooltip.allButSecret"),
		UNREAD_HINT(255,158,72,"ec.tooltip.unreadHint"),
		HINTS_ONLY(255,158,72,"ec.tooltip.hintsOnly"),
		ESSENTIAL_ONLY(72,188,255,"ec.tooltip.essentialOnly"),
		VISIBLE_ONLY(132,72,255,"ec.tooltip.visibleOnly"),
		DEFAULT(255,255,255);
		
		final float red, green, blue;
		final String title;
		
		private ObjectStatus(int red, int green, int blue, String title){
			this.red = red/255F;
			this.green = green/255F;
			this.blue = blue/255F;
			this.title = title;
		}
		
		private ObjectStatus(int red, int green, int blue){
			this(red,green,blue,null);
		}
	}
	
	public final KnowledgeObject<? extends IObjectHolder<?>> object;
	private boolean hasUnreadFragments;
	private boolean blinkState;
	private long lastBlinkSwitch;
	
	public CompendiumObjectElement(KnowledgeObject<? extends IObjectHolder<?>> object){
		this.object = object;
		this.lastBlinkSwitch = System.nanoTime();
	}
	
	public void renderLine(GuiScreen gui, CompendiumFile file, int yLowerBound, int yUpperBound){
		if (object.getChildren().stream().allMatch(obj -> file.getDiscoveryDistance(obj) >= CompendiumFile.distanceLimit-1))return;
		
		final int offX = gui.width/2;
		final int color = (255<<24)|(224<<16)|(224<<8)|224;
		
		object.connectToChildren((x1, y1, x2, y2) -> {
			if (!(y1 > yUpperBound || y2 < yLowerBound))GuiHelper.renderLine(offX+x1,y1,offX+x2,y2,color);
		});
	}
	
	public void renderObject(GuiScreen gui, CompendiumFile file, int yLowerBound, int yUpperBound){
		int x = gui.width/2+object.getX(), y = object.getY();
		if (y < yLowerBound || y > yUpperBound)return;
		
		hasUnreadFragments = object.getFragments().stream().anyMatch(fragment -> file.canSeeFragment(object,fragment) && !file.hasReadFragment(fragment));
		
		if (hasUnreadFragments){
			long now = System.nanoTime();
			
			if (TimeUnit.NANOSECONDS.toMillis(System.nanoTime()-lastBlinkSwitch) >= 900){
				lastBlinkSwitch = now;
				blinkState = !blinkState;
			}
		}
		
		renderObject(object,x,y,file,gui,blinkState);
	}
	
	public boolean isVisible(CompendiumFile file){
		return !object.isHidden() && file.getDiscoveryDistance(object) != CompendiumFile.distanceLimit;
	}
	
	public boolean isMouseOver(int mouseX, int mouseY, int centerX, int offsetY){
		int x = centerX-11+object.getX(), y = object.getY()-11+offsetY;
		return mouseX >= x && mouseY >= y && mouseX <= x+21 && mouseY <= y+21;
	}
	
	public String getTooltip(CompendiumFile file){
		ObjectStatus status = getStatus(object,file);
		return object.getTranslatedTooltip()+(status.title == null ? "" : "\n"+EnumChatFormatting.GRAY+I18n.format(status.title));
	}
	
	private static ObjectStatus getStatus(KnowledgeObject<?> object, CompendiumFile file){
		ObjectStatus outline = ObjectStatus.DEFAULT;
		
		Set<Entry<KnowledgeFragment,Boolean>> fragments = object.getFragments().stream().collect(Collectors.toMap(f -> f,f -> file.canSeeFragment(object,f))).entrySet();
		Set<KnowledgeFragment> unlocked = fragments.stream().filter(entry -> entry.getValue().booleanValue()).map(entry -> entry.getKey()).collect(Collectors.toSet());
		
		if (unlocked.isEmpty())outline = ObjectStatus.NONE_UNLOCKED;
		else{
			if (unlocked.stream().allMatch(fragment -> fragment.getType() == KnowledgeFragmentType.HINT))outline = ObjectStatus.HINTS_ONLY;
			else if (unlocked.stream().anyMatch(fragment -> fragment.getType() == KnowledgeFragmentType.HINT && !file.hasReadFragment(fragment)))outline = ObjectStatus.UNREAD_HINT;
			else if (unlocked.size() == fragments.size())outline = ObjectStatus.ALL_UNLOCKED;
			else if (unlocked.stream().allMatch(fragment -> fragment.getType() == KnowledgeFragmentType.ESSENTIAL))outline = ObjectStatus.ESSENTIAL_ONLY;
			else if (unlocked.stream().allMatch(fragment -> fragment.getType() == KnowledgeFragmentType.VISIBLE))outline = ObjectStatus.VISIBLE_ONLY;
			else if (fragments.stream().filter(entry -> entry.getKey().getType() == KnowledgeFragmentType.SECRET).findAny().isPresent() &&
					 unlocked.stream().allMatch(fragment -> fragment.getType() != KnowledgeFragmentType.SECRET))outline = ObjectStatus.ALL_BUT_SECRET;
		}
		
		return outline;
	}
	
	public static void renderObject(KnowledgeObject<?> object, int x, int y, CompendiumFile file, Gui gui, boolean blink){
		Minecraft mc = Minecraft.getMinecraft();
		
		ObjectShape shape = object.getShape();
		ObjectStatus outline = getStatus(object,file);
		
		// render background
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(1F,1F,1F,1F);
		
		RenderHelper.disableStandardItemLighting();
		mc.getTextureManager().bindTexture(GuiEnderCompendium.texBack);
		
		gui.drawTexturedModalRect(x-13,y-13,outline == ObjectStatus.NONE_UNLOCKED && !file.isDiscovered(object) ? shape.x+27 : shape.x,shape.y,26,26);
		
		if (blink)GL11.glColor4f(1F,1F,1F,1F);
		else GL11.glColor4f(outline.red,outline.green,outline.blue,1F);
		
		gui.drawTexturedModalRect(x-13,y-13,shape.x+54,shape.y,26,26);
		GL11.glColor4f(1F,1F,1F,1F);
		
		// render item
		RenderHelper.enableGUIStandardItemLighting();
		GuiEnderCompendium.renderItem.renderItemIntoGUI(mc.fontRenderer,mc.getTextureManager(),object.holder.getDisplayItemStack(),x-8,y-8,true);
	}
}