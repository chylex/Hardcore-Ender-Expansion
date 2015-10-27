package chylex.hee.mechanics.compendium.elements;
import chylex.hee.gui.GuiEnderCompendium;
import chylex.hee.gui.helpers.AnimatedFloat;
import chylex.hee.gui.helpers.AnimatedFloat.Easing;
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CompendiumScrollHandler{
	private static final int extraHeight = 32;
	
	private final GuiEnderCompendium gui;
	private final int totalHeight;
	private final AnimatedFloat offset;
	
	private float offsetPrev, offsetDrag, offsetInertia;
	private int dragMouseY;
	
	public CompendiumScrollHandler(GuiEnderCompendium compendium, int totalHeight){
		this.gui = compendium;
		this.totalHeight = totalHeight;
		this.offset = new AnimatedFloat(Easing.CUBIC);
	} // TODO reset offset on init?
	
	public float getOffset(float partialTickTime){
		return offsetPrev+(offset.value()-offsetPrev)*partialTickTime;
	}
	
	public void moveTo(float newY, boolean animate){
		if (animate)offset.startAnimation(offset.value(),newY,0.5F);
		else offset.set(newY);
	}
	
	public void onMouseClick(int mouseX, int mouseY){
		dragMouseY = mouseY;
	}
	
	public void onMouseDrag(int mouseX, int mouseY){
		if (dragMouseY != Integer.MIN_VALUE){
			offsetDrag += dragMouseY-mouseY;
			dragMouseY = mouseY;
		}
	}
	
	public void onMouseRelease(int mouseX, int mouseY){
		if (dragMouseY != Integer.MIN_VALUE){
			dragMouseY = Integer.MIN_VALUE;
			
			if (Math.abs(offsetDrag) > 2F){
				offsetInertia = (float)Math.pow(Math.abs(offsetDrag),1.2D)*Math.signum(offsetDrag);
			}
		}
	}
	
	public void update(){
		offsetPrev = offset.value();
		
		offset.update(0.05F);
		
		if (!MathUtil.floatEquals(offsetDrag,0F)){
			offset.set(offset.value()-offsetDrag);
			offsetDrag = 0F;
		}
		
		if (Math.abs(offsetInertia) > 0.5F){
			if (Math.abs(offsetInertia *= 0.7F) <= 0.5F)offsetInertia = 0F;
			else offset.set(offset.value()-offsetInertia);
		}
		
		if (offset.value() > 0)offset.set(0F);
		else if (offset.value() < -totalHeight+gui.height)offset.set(totalHeight > gui.height ? -totalHeight+gui.height : 0);
	}
}
