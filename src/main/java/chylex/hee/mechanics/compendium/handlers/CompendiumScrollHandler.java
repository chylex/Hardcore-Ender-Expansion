package chylex.hee.mechanics.compendium.handlers;
import static chylex.hee.gui.GuiEnderCompendium.getScaleMultiplier;
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
	
	private float offsetPrev, offsetDrag, offsetDragPeak, offsetInertia, scrollBy;
	private int dragMouseY;
	private long lastWheelTime;
	
	public CompendiumScrollHandler(GuiEnderCompendium compendium, int totalHeight){
		this.gui = compendium;
		this.totalHeight = totalHeight;
		this.offset = new AnimatedFloat(Easing.CUBIC);
		this.offset.set(extraHeight);
		this.scrollSpeed = new AnimatedFloat(Easing.LINEAR);
	}
	
	public void init(){
		KeyState.startTracking(GuiHelper.keyHome);
		KeyState.startTracking(GuiHelper.keyEnd);
		KeyState.startTracking(GuiHelper.keyArrowUp);
		KeyState.startTracking(GuiHelper.keyArrowDown);
		KeyState.startTracking(GuiHelper.keyPageUp);
		KeyState.startTracking(GuiHelper.keyPageDown);
		KeyState.startTracking(gui.mc.gameSettings.keyBindForward.getKeyCode());
		KeyState.startTracking(gui.mc.gameSettings.keyBindBack.getKeyCode());
	}
	
	public float getOffset(float partialTickTime){
		return offsetPrev+(offset.value()-offsetPrev)*partialTickTime;
	}
	
	public boolean isAnimating(){
		return offset.isAnimating();
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
			
			if (Math.abs(offsetDrag) > Math.abs(offsetDragPeak) || !MathUtil.floatEquals(Math.signum(offsetDrag),Math.signum(offsetDragPeak))){
				offsetDragPeak = offsetDrag;
			}
		}
	}
	
	public void onMouseRelease(int mouseX, int mouseY){
		if (dragMouseY != Integer.MIN_VALUE){
			dragMouseY = Integer.MIN_VALUE;
			
			if (Math.abs(offsetDrag/getScaleMultiplier()) > 2F && Math.abs(offsetDragPeak/getScaleMultiplier()) > 20F){
				offsetInertia = (float)Math.pow(Math.abs(offsetDragPeak*getScaleMultiplier()),1.25D)*Math.signum(offsetDragPeak);
				offsetDragPeak = 0F;
			}
		}
	}
	
	public void onMouseWheel(int value){
		scrollBy = Math.max(Math.abs(scrollBy),Math.abs(value/2)*getScaleMultiplier())*Math.signum(value);
		scrollSpeed.startAnimation(scrollSpeed.value(),1F,0F,lastWheelTime == 0L);
		lastWheelTime = System.nanoTime();
	}
	
	public boolean onKeyboardDown(int keyCode){
		if (keyCode == GuiHelper.keyArrowUp || keyCode == gui.mc.gameSettings.keyBindForward.getKeyCode()){
			startScrolling(gui.height*getScaleMultiplier()/10);
		}
		else if (keyCode == GuiHelper.keyArrowDown || keyCode == gui.mc.gameSettings.keyBindBack.getKeyCode()){
			startScrolling(-gui.height*getScaleMultiplier()/10);
		}
		else if (keyCode == GuiHelper.keyPageUp){
			startScrolling(gui.height*getScaleMultiplier()/2);
		}
		else if (keyCode == GuiHelper.keyPageDown){
			startScrolling(-gui.height*getScaleMultiplier()/2);
		}
		else if (keyCode == GuiHelper.keyHome){
			stopScrolling(true);
			moveTo(Integer.MAX_VALUE,true);
		}
		else if (keyCode == GuiHelper.keyEnd){
			stopScrolling(true);
			moveTo(Integer.MIN_VALUE,true);
		}
		else return false;
		
		return true;
	}
	
	public void onKeyboardUp(int keyCode){
		if (!restoreHeldKey())stopScrolling(false);
	}
	
	private boolean restoreHeldKey(){
		for(int code:new int[]{
			GuiHelper.keyHome, GuiHelper.keyEnd, GuiHelper.keyPageUp, GuiHelper.keyPageDown, GuiHelper.keyArrowUp, GuiHelper.keyArrowDown,
			gui.mc.gameSettings.keyBindForward.getKeyCode(), gui.mc.gameSettings.keyBindBack.getKeyCode()
		}){
			if (KeyState.isHeld(code)){
				onKeyboardDown(code);
				return true;
			}
		}
		
		return false;
	}
	
	private void startScrolling(float by){
		scrollBy = by;
		scrollSpeed.startAnimation(scrollSpeed.value(),1F,0.4F,true);
	}
	
	private void stopScrolling(boolean force){
		if (force){
			scrollBy = 0F;
			scrollSpeed.set(0F);
		}
		else scrollSpeed.startAnimation(scrollSpeed.value(),0F,0.2F,true);
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
		
		if (!MathUtil.floatEquals(scrollBy,0F)){
			if (MathUtil.floatEquals(scrollSpeed.value(),0F) && !scrollSpeed.isAnimating())scrollBy = 0F;
			else offset.set(clampOffset(offset.value()+scrollBy*scrollSpeed.value()));
		}
		
		if (!MathUtil.floatEquals(offsetDrag,0F)){
			offset.set(clampOffset(offset.value()-offsetDrag));
			offsetDrag = 0F;
			
			if (Math.abs(offsetInertia) <= 0.5F && isHoldingAnimationKey())restoreHeldKey();
		}
		
		if (Math.abs(offsetInertia) > 0.5F){
			if (Math.abs(offsetInertia *= 0.7F) <= (isHoldingAnimationKey() ? 8F : 2F)){
				offsetInertia = 0F;
				restoreHeldKey();
			}
			else offset.set(offset.value()-offsetInertia);
		}
		
		if (!offset.isAnimating())offset.set(clampOffset(offset.value()));
		
		if (lastWheelTime != 0L && TimeUnit.NANOSECONDS.toMillis(System.nanoTime()-lastWheelTime) > 100){
			stopScrolling(false);
			restoreHeldKey();
			lastWheelTime = 0L;
		}
	}
	
	/**
	 * Returns whether a key which uses animation system is being held. Used to restore key input after calling offset.set()
	 */
	private static boolean isHoldingAnimationKey(){
		return KeyState.isHeld(GuiHelper.keyHome) || KeyState.isHeld(GuiHelper.keyEnd);
	}
}
