package chylex.hee.world.structure.util;
import net.minecraft.util.EnumFacing;
import chylex.hee.system.abstractions.facing.IFacing;

public enum Facing6 implements IFacing{
	NORTH_NEGZ, SOUTH_POSZ, DOWN_NEGY, UP_POSY, WEST_NEGX, EAST_POSX, INVALID;
	
	public static final Facing6[] list = new Facing6[]{ NORTH_NEGZ, SOUTH_POSZ, DOWN_NEGY, UP_POSY, WEST_NEGX, EAST_POSX };
	
	public Facing6 opposite(){
		switch(this){
			case NORTH_NEGZ: return SOUTH_POSZ;
			case SOUTH_POSZ: return NORTH_NEGZ;
			case DOWN_NEGY: return UP_POSY;
			case UP_POSY: return DOWN_NEGY;
			case WEST_NEGX: return EAST_POSX;
			case EAST_POSX: return WEST_NEGX;
			default: return INVALID;
		}
	}
	
	@Override
	public EnumFacing toEnumFacing(){
		switch(this){
			case NORTH_NEGZ: return EnumFacing.NORTH;
			case WEST_NEGX: return EnumFacing.EAST; // wtf
			case DOWN_NEGY: return EnumFacing.DOWN;
			case SOUTH_POSZ: return EnumFacing.SOUTH;
			case EAST_POSX: return EnumFacing.WEST; // wtf
			case UP_POSY: return EnumFacing.UP;
			default: return EnumFacing.DOWN; // 1.8 fix the wtfs
		}
	}
	
	public Facing4 toFacing4(){
		switch(this){
			case NORTH_NEGZ: return Facing4.NORTH_NEGZ;
			case WEST_NEGX: return Facing4.WEST_NEGX;
			case SOUTH_POSZ: return Facing4.SOUTH_POSZ;
			case EAST_POSX: return Facing4.EAST_POSX;
			default: return Facing4.INVALID;
		}
	}
	
	@Override
	public int getX(){
		switch(this){
			case EAST_POSX: return 1;
			case WEST_NEGX: return -1;
			default: return 0;
		}
	}
	
	@Override
	public int getY(){
		switch(this){
			case UP_POSY: return 1;
			case DOWN_NEGY: return -1;
			default: return 0;
		}
	}
	
	@Override
	public int getZ(){
		switch(this){
			case SOUTH_POSZ: return 1;
			case NORTH_NEGZ: return -1;
			default: return 0;
		}
	}
}
