package chylex.hee.system.util;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockPosM{
	public static AxisAlignedBB getBoundingBox(BlockPosM loc1, BlockPosM loc2){
		return AxisAlignedBB.getBoundingBox(Math.min(loc1.x,loc2.x),loc1.y,Math.min(loc1.z,loc2.z),Math.max(loc1.x,loc2.x),loc2.y,Math.max(loc1.z,loc2.z));
	}
	
	/* === TEMPORARY BLOCKPOS === */
	
	private static final BlockPosM temporary = new BlockPosM();
	
	public static BlockPosM tmp(){
		return temporary.set(0,0,0);
	}
	
	public static BlockPosM tmp(int x, int y, int z){
		return temporary.set(x,y,z);
	}
	
	public static BlockPosM tmp(int[] array3){
		return temporary.set(array3);
	}
	
	public static BlockPosM tmp(double x, double y, double z){
		return temporary.set(x,y,z);
	}
	
	public static BlockPosM tmp(Entity entity){
		return temporary.set(entity);
	}
	
	public static BlockPosM tmp(long serialized){
		return temporary.set(serialized);
	}
	
	public int x, y, z;
	
	/* === CONSTRUCTORS === */
	
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
	
	/* === POSITION SETTING === */
	
	public BlockPosM setX(int x){
		this.x = x;
		return this;
	}
	
	public BlockPosM setY(int y){
		this.y = y;
		return this;
	}
	
	public BlockPosM setZ(int z){
		this.z = z;
		return this;
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
	
	/* === POSITION ALTERING === */
	
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
	
	/* === WORLD === */
	
	public boolean setAir(World world){
		return world.setBlockToAir(x,y,z);
	}
	
	public boolean setBlock(World world, Block block){
		return world.setBlock(x,y,z,block);
	}
	
	public boolean setBlock(World world, Block block, int metadata){
		return world.setBlock(x,y,z,block,metadata,3);
	}
	
	public boolean setBlock(World world, Block block, int metadata, int flags){
		return world.setBlock(x,y,z,block,metadata,flags);
	}
	
	public boolean isAir(World world){
		return world.isAirBlock(x,y,z);
	}
	
	public Block getBlock(World world){
		return world.getBlock(x,y,z);
	}
	
	public int getMetadata(World world){
		return world.getBlockMetadata(x,y,z);
	}
	
	public Material getMaterial(World world){
		return world.getBlock(x,y,z).getMaterial();
	}
	
	public boolean checkBlock(World world, Block block, int metadata){
		return world.getBlock(x,y,z) == block && world.getBlockMetadata(x,y,z) == metadata;
	}
	
	/* === SERIALIZATION AND UTILITIES === */
	
	public BlockPosM copy(){
		return new BlockPosM(x,y,z);
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