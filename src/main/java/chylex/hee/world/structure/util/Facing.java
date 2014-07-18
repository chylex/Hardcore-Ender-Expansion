package chylex.hee.world.structure.util;

public enum Facing{
	DOWN, UP, NORTH_NEGZ, SOUTH_POSZ, WEST_NEGX, EAST_POSX;
	
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
		return this == NORTH_NEGZ || this == SOUTH_POSZ?0:1;
	}
}
