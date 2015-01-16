package chylex.hee.system.util;

public final class MathUtil{
	private static final float F_D2R = 0.01745329251994327F;
	private static final float F_R2D = 57.2957795130823208F;
	private static final double D_D2R = 0.01745329251994327D;
	private static final double D_R2D = 57.2957795130823208D;

	public static float toRad(float deg){
		return deg*F_D2R;
	}
	
	public static double toRad(double deg){
		return deg*D_D2R;
	}

	public static float toDeg(float rad){
		return rad*F_R2D;
	}
	
	public static double toDeg(double rad){
		return rad*D_R2D;
	}
	
	public static int square(int n){
		return n*n;
	}
	
	public static float square(float n){
		return n*n;
	}
	
	public static double square(double n){
		return n*n;
	}
	
	public static double distance(double xDiff, double zDiff){
		return Math.sqrt(xDiff*xDiff+zDiff*zDiff);
	}
	
	public static double distance(double xDiff, double yDiff, double zDiff){
		return Math.sqrt(xDiff*xDiff+yDiff*yDiff+zDiff*zDiff);
	}
	
	public static double distanceSquared(double xDiff, double yDiff, double zDiff){
		return xDiff*xDiff+yDiff*yDiff+zDiff*zDiff;
	}
	
	public static boolean triangle(int xx, int yy, int x1, int y1, int x2, int y2, int x3, int y3){
		int a0, a1, a2, a3;
		a0 = Math.abs((x2-x1)*(y3-y1)-(x3-x1)*(y2-y1));
		a1 = Math.abs((x1-xx)*(y2-yy)-(x2-xx)*(y1-yy));
		a2 = Math.abs((x2-xx)*(y3-yy)-(x3-xx)*(y2-yy));
		a3 = Math.abs((x3-xx)*(y1-yy)-(x1-xx)*(y3-yy));
		return (Math.abs(a1+a2+a3-a0) <= 1D/256D);
	}

	public static double lendirx(double len, double dir){
		return Math.sin(MathUtil.toRad(dir))*len;
	}

	public static double lendiry(double len, double dir){
		return Math.cos(MathUtil.toRad(dir))*len;
	}
	
	public static int floor(double n){
		return (int)Math.floor(n);
	}
	
	public static int floor(float n){
		return (int)Math.floor(n);
	}
	
	public static int ceil(double n){
		return (int)Math.ceil(n);
	}
	
	public static int ceil(float n){
		return (int)Math.ceil(n);
	}
	
	public static int clamp(int value, int min, int max){
		return Math.min(Math.max(value,min),max);
	}
	
	public static float clamp(float value, float min, float max){
		return Math.min(Math.max(value,min),max);
	}
	
	public static double clamp(double value, double min, double max){
		return Math.min(Math.max(value,min),max);
	}
	
	public static boolean floatEquals(float val1, float val2){
		return Math.abs(val1-val2) < 0.00001F;
	}
	
	private MathUtil(){}
}
