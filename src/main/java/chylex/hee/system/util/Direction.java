package chylex.hee.system.util;

public final class Direction{
	public static final byte[] offsetX = new byte[]{ -1, 0, 1, 0 }, offsetZ = new byte[]{ 0, -1, 0, 1 };
	public static final byte[] rotateOpposite = new byte[]{ 2, 3, 0, 1 };
	
	private Direction(){}
}