package chylex.hee.system.util;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;

public class BlockPosM{
	private static final BlockPosM global = new BlockPosM();
	
	public static BlockPosM global(int x, int y, int z){
		return global.set(x,y,z);
	}
	
	public static BlockPosM global(int[] array3){
		return global.set(array3);
	}
	
	public static BlockPosM global(double x, double y, double z){
		return global.set(x,y,z);
	}
	
	public static BlockPosM global(Entity entity){
		return global.set(entity);
	}
	
	public static BlockPosM global(long serialized){
		return global.set(serialized);
	}
	
	public int x, y, z;
	
	public BlockPosM(){}
	
	public BlockPosM(int x, int y, int z){
		set(x,y,z);
	}
	
	public BlockPosM(int[] array3){
		set(array3);
	}
	
	public BlockPosM(double x, double y, double z){
		set(x,y,z);
	}
	
	public BlockPosM(Entity entity){
		set(entity);
	}
	
	public BlockPosM(long serialized){
		set(serialized);
	}
	
	public BlockPosM set(int x, int y, int z){
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	
	public BlockPosM set(int[] array3){
		if (array3 == null || array3.length != 3)array3 = new int[]{ 0, 0, 0 };
		return set(array3[0],array3[1],array3[2]);
	}
	
	public BlockPosM set(double x, double y, double z){
		return set(MathUtil.floor(x),MathUtil.floor(y),MathUtil.floor(z));
	}
	
	public BlockPosM set(Entity entity){
		return set(MathUtil.floor(entity.posX),MathUtil.floor(entity.posY),MathUtil.floor(entity.posZ));
	}
	
	public BlockPosM set(long serialized){
		return set((int)(serialized>>38),(int)(serialized<<26>>52),(int)(serialized<<38>>38));
	}
	
	public BlockPosM move(int x, int y, int z){
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}
	
	public BlockPosM move(EnumFacing facing){
		return move(facing.getFrontOffsetX(),facing.getFrontOffsetY(),facing.getFrontOffsetZ());
	}
	
	public BlockPosM move(EnumFacing facing, int amount){
		return move(facing.getFrontOffsetX()*amount,facing.getFrontOffsetY()*amount,facing.getFrontOffsetZ()*amount);
	}
	
	public BlockPosM moveUp(){
		return move(EnumFacing.UP);
	}
	
	public BlockPosM moveDown(){
		return move(EnumFacing.DOWN);
	}
	
	public BlockPosM moveNorth(){
		return move(EnumFacing.NORTH);
	}
	
	public BlockPosM moveSouth(){
		return move(EnumFacing.SOUTH);
	}
	
	public BlockPosM moveEast(){
		return move(EnumFacing.EAST);
	}
	
	public BlockPosM moveWest(){
		return move(EnumFacing.WEST);
	}
	
	public long toLong(){
		return (x&(1L<<26)-1L)<<38|(y&(1L<<12)-1L)<<26|(z&(1L<<26)-1L);
	}
	
	@Override
	public boolean equals(Object obj){
		if (obj instanceof BlockPosM){
			BlockPosM pos = (BlockPosM)obj;
			return pos.x == x && pos.y == y && pos.z == z;
		}
		else return super.equals(obj);
	}
	
	@Override
	public int hashCode(){
		return (y+z*31)*31+x;
	}
	
	@Override
	public String toString(){
		return new StringBuilder().append("{ ").append(x).append(", ").append(y).append(", ").append(z).append(" }").toString();
	}
}