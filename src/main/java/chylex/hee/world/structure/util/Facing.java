package chylex.hee.world.structure.util;

public enum Facing{
	DOWN, UP, NORTH_NEGZ, SOUTH_POSZ, WEST_NEGX, EAST_POSX;
	
	public int get4Directional(){
		return this == SOUTH_POSZ ? 0 : this == WEST_NEGX ? 1 : this == NORTH_NEGZ ? 2 : this == EAST_POSX ? 3 : -1;
	}
	
	public int get6Directional(){
		return this == DOWN ? 0 : this == UP ? 1 : this == NORTH_NEGZ ? 2 : this == SOUTH_POSZ ? 3 : this == WEST_NEGX ? 4 : 5;
	}
	
	public int getStairs(){
		return this == EAST_POSX ? 0 : this == WEST_NEGX ? 1 : this == SOUTH_POSZ ? 2 : 3;
	}
	
	public int getSkull(){
		return this == NORTH_NEGZ ? 2 : this == SOUTH_POSZ ? 3 : this == EAST_POSX ? 4 : 5;
	}
	
	public int getAnvil(){
		return this == NORTH_NEGZ || this == SOUTH_POSZ ? 0 : 1;
	}
	
	public Facing getRotatedLeft(){
		switch(this){
			case NORTH_NEGZ: return EAST_POSX;
			case EAST_POSX: return SOUTH_POSZ;
			case SOUTH_POSZ: return WEST_NEGX;
			case WEST_NEGX: return NORTH_NEGZ;
			default: return this;
		}
	}
	
	public Facing getRotatedRight(){
		switch(this){
			case NORTH_NEGZ: return WEST_NEGX;
			case WEST_NEGX: return SOUTH_POSZ;
			case SOUTH_POSZ: return EAST_POSX;
			case EAST_POSX: return NORTH_NEGZ;
			default: return this;
		}
	}
	
	public Facing getReversed(){
		switch(this){
			case DOWN: return UP;
			case UP: return DOWN;
			case NORTH_NEGZ: return SOUTH_POSZ;
			case SOUTH_POSZ: return NORTH_NEGZ;
			case WEST_NEGX: return EAST_POSX;
			case EAST_POSX: return WEST_NEGX;
			default: return this;
		}
	}
	
	public static Facing from4Directional(int index){
		return index == 0 ? SOUTH_POSZ : index == 1 ? WEST_NEGX : index == 2 ? NORTH_NEGZ : index == 3 ? EAST_POSX : null;
	}
}
