package chylex.hee.world.structure.util;

public final class Size{
	public final int sizeX, sizeY, sizeZ;
	
	public Size(int sizeX, int sizeY, int sizeZ){
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
	}
	
	@Override
	public boolean equals(Object obj){
		if (obj instanceof Size){
			Size size = (Size)obj;
			return size.sizeX == sizeX && size.sizeY == sizeY && size.sizeZ == sizeZ;
		}
		else return false;
	}
	
	@Override
	public int hashCode(){
		return 9973*sizeZ+113*sizeX+sizeY;
	}
}
