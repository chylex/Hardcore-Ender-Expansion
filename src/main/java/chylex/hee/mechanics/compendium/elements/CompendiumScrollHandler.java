package chylex.hee.mechanics.compendium.elements;
import java.util.concurrent.TimeUnit;
import chylex.hee.gui.GuiEnderCompendium;
import chylex.hee.gui.helpers.AnimatedFloat;
import chylex.hee.gui.helpers.AnimatedFloat.Easing;
import chylex.hee.gui.helpers.GuiHelper;
import chylex.hee.gui.helpers.KeyState;
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CompendiumScrollHandler{
	private static final int extraHeight = 48;
	
	private final GuiEnderCompendium gui;
	private final int totalHeight;
	private final AnimatedFloat offset;
	private final AnimatedFloat scrollSpeed;
	
	private float offsetPrev, offsetDrag, offsetInertia, scrollBy, lastScrollBy;
	private int dragMouseY;
	private long lastScrollTime; // TODO rename to lastWheelTime
	
	public CompendiumScrollHandler(GuiEnderCompendium compendium, int totalHeight){
		this.gui = compendium;
		this.totalHeight = totalHeight;
		this.offset = new AnimatedFloat(Easing.CUBIC);
		this.offset.set(extraHeight);
		this.scrollSpeed = new AnimatedFloat(Easing.LINEAR);
		
		KeyState.startTracking(GuiHelper.keyArrowUp);
		KeyState.startTracking(GuiHelper.keyArrowDown);
		KeyState.startTracking(GuiHelper.keyPageUp);
		KeyState.startTracking(GuiHelper.keyPageDown);
	}
	
	public float getOffset(float partialTickTime){
		return offsetPrev+(offset.value()-offsetPrev)*partialTickTime;
	}
	
	public void moveTo(float newY, boolean animate){
		newY = clampOffset(newY);
		
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
	
	public void onMouseWheel(int value){
		lastScrollBy = scrollBy = value/2;
		scrollSpeed.startAnimation(scrollSpeed.value(),1F,0.1F,false);
		lastScrollTime = System.nanoTime();
	}
	
	public boolean onKeyboardDown(int keyCode){
		if (keyCode == GuiHelper.keyArrowUp || keyCode == gui.mc.gameSettings.keyBindForward.getKeyCode()){
			startScrolling(gui.height/10);
		}
		else if (keyCode == GuiHelper.keyArrowDown || keyCode == gui.mc.gameSettings.keyBindBack.getKeyCode()){
			startScrolling(-gui.height/10);
		}
		else if (keyCode == GuiHelper.keyPageUp){
			startScrolling(gui.height/2);
		}
		else if (keyCode == GuiHelper.keyPageDown){
			startScrolling(-gui.height/2);
		}
		else if (keyCode == GuiHelper.keyHome){
			stopScrolling();
			moveTo(Integer.MAX_VALUE,true);
		}
		else if (keyCode == GuiHelper.keyEnd){
			stopScrolling();
			moveTo(Integer.MIN_VALUE,true);
		}
		else return false;
		
		return true;
	}
	
	public void onKeyboardUp(int keyCode){
		for(int code:new int[]{
			GuiHelper.keyPageUp, GuiHelper.keyPageDown, GuiHelper.keyArrowUp, GuiHelper.keyArrowDown,
			gui.mc.gameSettings.keyBindForward.getKeyCode(), gui.mc.gameSettings.keyBindBack.getKeyCode()
		}){
			if (KeyState.isHeld(code)){
				onKeyboardDown(code);
				return;
			}
		}
		
		stopScrolling();
	}
	
	private void startScrolling(float by){
		lastScrollBy = scrollBy = by;
		scrollSpeed.startAnimation(scrollSpeed.value(),1F,0.1F,true);
	}
	
	private void stopScrolling(){
		scrollBy = 0F;
		scrollSpeed.startAnimation(scrollSpeed.value(),0F,0.2F,true);
	}
	
	private float clampOffset(float newValue){
		if (newValue > extraHeight)return extraHeight;
		else if (newValue < -totalHeight+gui.height-extraHeight)return totalHeight+extraHeight > gui.height ? -totalHeight+gui.height-extraHeight : extraHeight;
		else return newValue;
	}
	
	public void update(){
		offsetPrev = offset.value();
		
		offset.update(0.05F);
		scrollSpeed.update(0.05F);
		
		if (!MathUtil.floatEquals(lastScrollBy,0F)){
			if (MathUtil.floatEquals(scrollSpeed.value(),0F) && !scrollSpeed.isAnimating())lastScrollBy = 0F;
			else offset.set(clampOffset(offset.value()+lastScrollBy*scrollSpeed.value()));
		}
		
		if (!MathUtil.floatEquals(offsetDrag,0F)){
			offset.set(offset.value()-offsetDrag);
			offsetDrag = 0F;
		}
		
		if (Math.abs(offsetInertia) > 0.5F){
			if (Math.abs(offsetInertia *= 0.7F) <= 0.5F)offsetInertia = 0F;
			else offset.set(offset.value()-offsetInertia);
		}
		
		if (!offset.isAnimating())offset.set(clampOffset(offset.value()));
		
		if (lastScrollTime != 0L && TimeUnit.NANOSECONDS.toMillis(System.nanoTime()-lastScrollTime) > 50){
			stopScrolling();
			lastScrollTime = 0L;
		}
	}
}
