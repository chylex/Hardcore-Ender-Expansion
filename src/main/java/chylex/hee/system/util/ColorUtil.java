package chylex.hee.system.util;

public final class ColorUtil{
	public static float[] hsvToRgb(float hue, float saturation, float value){
		int h = (int)(hue*6);
		float f = hue*6-h;
		float p = value*(1-saturation);
		float q = value*(1-f*saturation);
		float t = value*(1-(1-f)*saturation);

		switch(h){
			case 0:
				return new float[]{ value, t, p };
			case 1:
				return new float[]{ value, t, p };
			case 2:
				return new float[]{ p, value, t };
			case 3:
				return new float[]{ p, q, value };
			case 4:
				return new float[]{ t, p, value };
			case 5:
				return new float[]{ value, p, q };
			default:
		}
		
		return new float[3];
	}
	
	public static float getHue(float red, float green, float blue){
		float min = Math.min(red, Math.min(green, blue));
		float max = Math.max(red, Math.max(green, blue));
		float hue = 0F;
		
		if (max == red)hue = (green-blue)/(max-min);
		else if (max == green)hue = 2F+(blue-red)/(max-min);
		else if (max == blue)hue = 4F+(red-green)/(max-min);
		
		hue *= 60F;
		if (hue < 0F)hue += 360F;
		
		return hue/360F;
	}
	
	private ColorUtil(){}
}
