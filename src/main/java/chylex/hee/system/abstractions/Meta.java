package chylex.hee.system.abstractions;
import chylex.hee.world.structure.util.Facing4;

public final class Meta{
	public static final byte
		stoneBrickPlain = 0,
		stoneBrickMossy = 1,
		stoneBrickCracked = 2,
		stoneBrickChiseled = 3,
		
		silverfishPlain = 2,
		silverfishMossy = 3,
		silverfishCracked = 4,
		silverfishChiseled = 5,
		
		slabStoneBrickBottom = 5,
		slabStoneBrickTop = 5+8;
	
	public static byte getStairs(Facing4 ascendsTowards, boolean flip){
		switch(ascendsTowards){
			case EAST_POSX: return (byte)(0+(flip ? 4 : 0));
			case WEST_NEGX: return (byte)(1+(flip ? 4 : 0));
			case SOUTH_POSZ: return (byte)(2+(flip ? 4 : 0));
			case NORTH_NEGZ: return (byte)(3+(flip ? 4 : 0));
			default: return 0;
		}
	}
	
	public static byte getDoor(Facing4 opensTowards, boolean upper){
		if (upper)return 8;
		
		switch(opensTowards){
			case EAST_POSX: return 0;
			case SOUTH_POSZ: return 1;
			case WEST_NEGX: return 2;
			case NORTH_NEGZ: return 3;
			default: return 0;
		}
	}
	
	public static byte getChest(Facing4 facingTo){
		switch(facingTo){
			case EAST_POSX: return 5;
			case WEST_NEGX: return 4;
			case SOUTH_POSZ: return 3;
			case NORTH_NEGZ: return 2;
			default: return 0;
		}
	}
	
	private Meta(){}
}
