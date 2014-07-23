package chylex.hee.system.util;

public final class ColorUtil{
	public static final float[] hsvToRgb(float hue, float saturation, float value){
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
				return new float[]{  t, p, value };
			case 5:
				return new float[]{ value, p, q };
		}
		
		return new float[3];
	}
	
	private ColorUtil(){}
}
