package chylex.hee.system.util;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

public class BlockPosM{
	@Deprecated
	public static AxisAlignedBB getBoundingBox(BlockPosM loc1, BlockPosM loc2){
		return AxisAlignedBB.getBoundingBox(Math.min(loc1.x,loc2.x),loc1.y,Math.min(loc1.z,loc2.z),Math.max(loc1.x,loc2.x),loc2.y,Math.max(loc1.z,loc2.z));
	}

	@Deprecated
	public static BlockPosM fromNBT(NBTTagCompound nbt, String key){
		return nbt.hasKey(key,NBT.TAG_LONG) ? new BlockPosM(nbt.getLong(key)) : new BlockPosM(nbt.getIntArray(key));
	}
	
	/* === TEMPORARY BLOCKPOS === */
	
	private static final ThreadLocal<BlockPosM> temporary = new ThreadLocal<BlockPosM>(){
		@Override
		protected BlockPosM initialValue(){
			return new BlockPosM();
		}
	};
	
	@Deprecated
	public static BlockPosM tmp(){
		return temporary.get().set(0,0,0);
	}

	@Deprecated
	public static BlockPosM tmp(int x, int y, int z){
		return temporary.get().set(x,y,z);
	}

	@Deprecated
	public static BlockPosM tmp(int[] array3){
		return temporary.get().set(array3);
	}

	@Deprecated
	public static BlockPosM tmp(double x, double y, double z){
		return temporary.get().set(x,y,z);
	}

	@Deprecated
	public static BlockPosM tmp(BlockPosM pos){
		return temporary.get().set(pos);
	}

	@Deprecated
	public static BlockPosM tmp(Entity entity){
		return temporary.get().set(entity);
	}

	@Deprecated
	public static BlockPosM tmp(long serialized){
		return temporary.get().set(serialized);
	}

	@Deprecated
	public int x, y, z;
	
	/* === CONSTRUCTORS === */

	@Deprecated
	public BlockPosM(){}

	@Deprecated
	public BlockPosM(int x, int y, int z){
		set(x,y,z);
	}

	@Deprecated
	public BlockPosM(int[] array3){
		set(array3);
	}

	@Deprecated
	public BlockPosM(double x, double y, double z){
		set(x,y,z);
	}

	@Deprecated
	public BlockPosM(Entity entity){
		set(entity);
	}

	@Deprecated
	public BlockPosM(long serialized){
		set(serialized);
	}
	
	/* === POSITION SETTING === */

	@Deprecated
	public BlockPosM setX(int x){
		this.x = x;
		return this;
	}

	@Deprecated
	public BlockPosM setY(int y){
		this.y = y;
		return this;
	}

	@Deprecated
	public BlockPosM setZ(int z){
		this.z = z;
		return this;
	}

	@Deprecated
	public BlockPosM set(int x, int y, int z){
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	@Deprecated
	public BlockPosM set(int[] array3){
		if (array3 == null || array3.length != 3)array3 = new int[]{ 0, 0, 0 };
		return set(array3[0],array3[1],array3[2]);
	}

	@Deprecated
	public BlockPosM set(double x, double y, double z){
		return set(MathUtil.floor(x),MathUtil.floor(y),MathUtil.floor(z));
	}

	@Deprecated
	public BlockPosM set(BlockPosM pos){
		return set(pos.x,pos.y,pos.z);
	}

	@Deprecated
	public BlockPosM set(Entity entity){
		return set(MathUtil.floor(entity.posX),MathUtil.floor(entity.posY),MathUtil.floor(entity.posZ));
	}

	@Deprecated
	public BlockPosM set(long serialized){
		return set((int)(serialized>>38),(int)(serialized<<26>>52),(int)(serialized<<38>>38));
	}
	
	/* === POSITION ALTERING === */

	@Deprecated
	public BlockPosM move(int x, int y, int z){
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	@Deprecated
	public BlockPosM move(int side){
		return move(EnumFacing.values()[side]);
	}

	@Deprecated
	public BlockPosM move(int side, int amount){
		return move(EnumFacing.values()[side],amount);
	}

	@Deprecated
	public BlockPosM move(EnumFacing facing){
		return move(facing.getFrontOffsetX(),facing.getFrontOffsetY(),facing.getFrontOffsetZ());
	}

	@Deprecated
	public BlockPosM move(EnumFacing facing, int amount){
		return move(facing.getFrontOffsetX()*amount,facing.getFrontOffsetY()*amount,facing.getFrontOffsetZ()*amount);
	}

	@Deprecated
	public BlockPosM moveUp(){
		return move(EnumFacing.UP);
	}

	@Deprecated
	public BlockPosM moveDown(){
		return move(EnumFacing.DOWN);
	}

	@Deprecated
	public BlockPosM moveNorth(){
		return move(EnumFacing.NORTH);
	}

	@Deprecated
	public BlockPosM moveSouth(){
		return move(EnumFacing.SOUTH);
	}

	@Deprecated
	public BlockPosM moveEast(){
		return move(EnumFacing.EAST);
	}

	@Deprecated
	public BlockPosM moveWest(){
		return move(EnumFacing.WEST);
	}
	
	/* === WORLD === */

	@Deprecated
	public boolean setAir(World world){
		return world.setBlockToAir(x,y,z);
	}

	@Deprecated
	public boolean setBlock(World world, Block block){
		return world.setBlock(x,y,z,block);
	}

	@Deprecated
	public boolean setBlock(World world, Block block, int metadata){
		return world.setBlock(x,y,z,block,metadata,3);
	}

	@Deprecated
	public boolean setBlock(World world, Block block, int metadata, int flags){
		return world.setBlock(x,y,z,block,metadata,flags);
	}

	@Deprecated
	public boolean setMetadata(World world, int metadata){
		return world.setBlockMetadataWithNotify(x,y,z,metadata,3);
	}

	@Deprecated
	public boolean setMetadata(World world, int metadata, int flags){
		return world.setBlockMetadataWithNotify(x,y,z,metadata,flags);
	}

	@Deprecated
	public boolean isAir(World world){
		return world.isAirBlock(x,y,z);
	}

	@Deprecated
	public Block getBlock(IBlockAccess world){
		return world.getBlock(x,y,z);
	}

	@Deprecated
	public int getMetadata(IBlockAccess world){
		return world.getBlockMetadata(x,y,z);
	}

	@Deprecated
	public Material getMaterial(IBlockAccess world){
		return world.getBlock(x,y,z).getMaterial();
	}

	@Deprecated
	public TileEntity getTileEntity(IBlockAccess world){
		return world.getTileEntity(x,y,z);
	}

	@Deprecated
	public boolean checkBlock(World world, Block block, int metadata){
		return world.getBlock(x,y,z) == block && world.getBlockMetadata(x,y,z) == metadata;
	}
	
	/* === SERIALIZATION AND UTILITIES === */

	@Deprecated
	public BlockPosM copy(){
		return new BlockPosM(x,y,z);
	}

	@Deprecated
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