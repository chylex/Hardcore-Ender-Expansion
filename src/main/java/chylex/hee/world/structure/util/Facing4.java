package chylex.hee.world.structure.util;
import net.minecraft.util.EnumFacing;

public enum Facing4{
	NORTH_NEGZ, SOUTH_POSZ, WEST_NEGX, EAST_POSX, INVALID;
	
	public static final Facing4[] list = new Facing4[]{ NORTH_NEGZ, SOUTH_POSZ, WEST_NEGX, EAST_POSX };
	
	public Facing4 opposite(){
		switch(this){
			case NORTH_NEGZ: return SOUTH_POSZ;
			case SOUTH_POSZ: return NORTH_NEGZ;
			case WEST_NEGX: return EAST_POSX;
			case EAST_POSX: return WEST_NEGX;
			default: return INVALID;
		}
	}
	
	public Facing4 perpendicular(){
		switch(this){
			case NORTH_NEGZ:
			case SOUTH_POSZ: return EAST_POSX;
			case WEST_NEGX:
			case EAST_POSX: return SOUTH_POSZ;
			default: return INVALID;
		}
	}
	
	public Facing4 rotateLeft(){
		switch(this){
			case NORTH_NEGZ: return WEST_NEGX;
			case WEST_NEGX: return SOUTH_POSZ;
			case SOUTH_POSZ: return EAST_POSX;
			case EAST_POSX: return NORTH_NEGZ;
			default: return INVALID;
		}
	}
	
	public Facing4 rotateRight(){
		switch(this){
			case NORTH_NEGZ: return EAST_POSX;
			case EAST_POSX: return SOUTH_POSZ;
			case SOUTH_POSZ: return WEST_NEGX;
			case WEST_NEGX: return NORTH_NEGZ;
			default: return INVALID;
		}
	}
	
	public Facing4 rotateLeft(int amount){
		Facing4 facing = this;
		while(--amount >= 0)facing = facing.rotateLeft();
		return facing;
	}
	
	public Facing4 rotateRight(int amount){
		Facing4 facing = this;
		while(--amount >= 0)facing = facing.rotateRight();
		return facing;
	}
	
	public EnumFacing toEnumFacing(){
		switch(this){
			case NORTH_NEGZ: return EnumFacing.NORTH;
			case WEST_NEGX: return EnumFacing.EAST; // wtf
			case SOUTH_POSZ: return EnumFacing.SOUTH;
			case EAST_POSX: return EnumFacing.WEST; // wtf
			default: return EnumFacing.DOWN;
		}
	}
	
	public int getX(){
		switch(this){
			case EAST_POSX: return 1;
			case WEST_NEGX: return -1;
			default: return 0;
		}
	}
	
	public int getZ(){
		switch(this){
			case SOUTH_POSZ: return 1;
			case NORTH_NEGZ: return -1;
			default: return 0;
		}
	}
}
