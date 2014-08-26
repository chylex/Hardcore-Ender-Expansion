package chylex.hee.gui.helpers;

public final class AnimatedFloat{
	private final Easing easing;
	private float time;
	private float startValue;
	private float endValue;
	private float duration;
	private float currentValue;
	private boolean isAnimating;
	
	public AnimatedFloat(Easing easing){
		this.easing = easing;
		this.isAnimating = false;
	}
	
	public void startAnimation(float startValue, float endValue){
		startAnimation(startValue,endValue,1F);
	}
	
	public void startAnimation(float startValue, float endValue, float duration){
		if (isAnimating)return;
		
		this.time = 0F;
		this.startValue = this.currentValue = startValue;
		this.endValue = endValue;
		this.duration = duration;
		this.isAnimating = true;
	}
	
	public void update(float timeAdd){
		if (!isAnimating)return;
		
		if ((time += (timeAdd/duration)) > 1F){
			time = 1F;
			isAnimating = false;
		}
		
		currentValue = easing.getValue(time,startValue,endValue-startValue);
	}
	
	public void set(float newValue){
		if (isAnimating)isAnimating = false;
		currentValue = newValue;
	}
	
	public boolean isAnimating(){
		return isAnimating;
	}
	
	public float value(){
		return currentValue;
	}
	
	public enum Easing{
		LINEAR, CUBIC;
		
		public float getValue(float time, float startValue, float fullStep){
			switch(this){
				case CUBIC:
					if ((time *= 2F) < 1)return fullStep/2F*time*time*time+startValue;
					time -= 2F;
					return fullStep/2F*(time*time*time+2F)+startValue;
					
				case LINEAR:
				default:
					return startValue+(fullStep*time);
			}
		}
	}
}
