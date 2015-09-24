package chylex.hee.system.abstractions;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.system.abstractions.facing.IFacing;
import chylex.hee.system.util.MathUtil;

public class Pos{
	public static final Pos at(int x, int y, int z){
		return new Pos(x,y,z);
	}
	
	public static Pos at(int[] array3){
		if (array3 == null || array3.length != 3)array3 = new int[]{ 0, 0, 0 };
		return new Pos(array3[0],array3[1],array3[2]);
	}

	public static final Pos at(double x, double y, double z){
		return new Pos(MathUtil.floor(x),MathUtil.floor(y),MathUtil.floor(z));
	}
	
	public static Pos at(Pos pos){
		return new Pos(pos.getX(),pos.getY(),pos.getZ());
	}
	
	public static Pos at(Entity entity){
		return new Pos(MathUtil.floor(entity.posX),MathUtil.floor(entity.posY),MathUtil.floor(entity.posZ));
	}
	
	public static Pos at(TileEntity tile){
		return new Pos(tile.xCoord,tile.yCoord,tile.zCoord);
	}
	
	public static Pos at(MovingObjectPosition mop){
		return mop.typeOfHit == MovingObjectType.BLOCK ? new Pos(mop.blockX,mop.blockY,mop.blockZ) : mop.typeOfHit == MovingObjectType.ENTITY ? at(mop.entityHit) : at(0,0,0);
	}
	
	public static Pos at(long serialized){
		return new Pos((int)(serialized>>38),(int)(serialized<<26>>52),(int)(serialized<<38>>38));
	}
	
	/* === STATIC METHODS === */
	
	/**
	 * Runs a function for every block inside the specified locations.
	 */
	public static void forEachBlock(Pos firstPos, Pos secondPos, Consumer<PosMutable> action){
		int x1 = Math.min(firstPos.getX(),secondPos.getX()), x2 = Math.max(firstPos.getX(),secondPos.getX());
		int y1 = Math.min(firstPos.getY(),secondPos.getY()), y2 = Math.max(firstPos.getY(),secondPos.getY());
		int z1 = Math.min(firstPos.getZ(),secondPos.getZ()), z2 = Math.max(firstPos.getZ(),secondPos.getZ());
		PosMutable mutablePos = new PosMutable();
		
		for(int x = x1; x <= x2; x++){
			for(int y = y1; y <= y2; y++){
				for(int z = z1; z <= z2; z++){
					action.accept(mutablePos.set(x,y,z));
				}
			}
		}
	}
	
	/**
	 * Returns a bounding box containing all blocks between the specified locations (the edge is extended).
	 */
	public static AxisAlignedBB getBoundingBox(Pos loc1, Pos loc2){
		return AxisAlignedBB.getBoundingBox(Math.min(loc1.getX(),loc2.getX()),Math.min(loc1.getY(),loc2.getY()),Math.min(loc1.getZ(),loc2.getZ()),1+Math.max(loc1.getX(),loc2.getX()),1+Math.max(loc1.getY(),loc2.getY()),1+Math.max(loc1.getZ(),loc2.getZ()));
	}

	/**
	 * Finds the first non-air block from the top.
	 */
	public static Pos getTopBlock(World world, int x, int z){
		return getTopBlock(world,x,z,world.getHeight());
	}

	/**
	 * Finds the first non-air block from the top.
	 */
	public static Pos getTopBlock(World world, int x, int z, int startY){
		PosMutable mpos = new PosMutable(x,startY,z);
		while(mpos.isAir(world) && --mpos.y >= 0);
		return mpos.immutable();
	}
	
	/**
	 * Finds the first block from the top for which the check function returns true.
	 */
	public static Pos getTopBlock(World world, int x, int z, Function<BlockInfo,Boolean> checkFunc){
		return getTopBlock(world,x,z,world.getHeight(),checkFunc);
	}
	
	/**
	 * Finds the first block from the top for which the check function returns true.
	 */
	public static Pos getTopBlock(World world, int x, int z, int startY, Function<BlockInfo,Boolean> checkFunc){
		PosMutable mpos = new PosMutable(x,startY,z);
		while(!checkFunc.apply(mpos.getInfo(world)) && --mpos.y >= 0);
		return mpos.immutable();
	}
	
	/* === IMMUTABLE POS CLASS === */
	
	private final int x, y, z;
	
	public Pos(int x, int y, int z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public int getZ(){
		return z;
	}
	
	public long toLong(){
		return (getX()&(1L<<26)-1L)<<38|(getY()&(1L<<12)-1L)<<26|(getZ()&(1L<<26)-1L);
	}
	
	public Pos immutable(){
		return this;
	}
	
	/* === OFFSET === */
	
	public Pos offset(int x, int y, int z){
		return new Pos(getX()+x,getY()+y,getZ()+z);
	}
	
	public Pos offset(int side){
		return offset(EnumFacing.values()[side]);
	}
	
	public Pos offset(int side, int amount){
		return offset(EnumFacing.values()[side],amount);
	}
	
	public Pos offset(EnumFacing facing){
		return offset(facing.getFrontOffsetX(),facing.getFrontOffsetY(),facing.getFrontOffsetZ());
	}
	
	public Pos offset(EnumFacing facing, int amount){
		return offset(facing.getFrontOffsetX()*amount,facing.getFrontOffsetY()*amount,facing.getFrontOffsetZ()*amount);
	}
	
	public Pos offset(IFacing facing){
		return offset(facing.getX(),0,facing.getZ());
	}
	
	public Pos offset(IFacing facing, int amount){
		return offset(facing.getX()*amount,0,facing.getZ()*amount);
	}
	
	public Pos getUp(){
		return offset(EnumFacing.UP);
	}
	
	public Pos getDown(){
		return offset(EnumFacing.DOWN);
	}
	
	public Pos getNorth(){
		return offset(Facing4.NORTH_NEGZ);
	}
	
	public Pos getSouth(){
		return offset(Facing4.SOUTH_POSZ);
	}
	
	public Pos getEast(){
		return offset(Facing4.EAST_POSX);
	}
	
	public Pos getWest(){
		return offset(Facing4.WEST_NEGX);
	}
	
	/* === DISTANCE === */
	
	public double distance(int x, int y, int z){
		return MathUtil.distance(x-getX(),y-getY(),z-getZ());
	}
	
	public double distance(Pos pos){
		return MathUtil.distance(pos.getX()-getX(),pos.getY()-getY(),pos.getZ()-getZ());
	}
	
	public double distance(Entity entity){
		return MathUtil.distance(entity.posX-(getX()+0.5D),entity.posY-(getY()+0.5D),entity.posZ-(getZ()+0.5D));
	}
	
	public double distance(TileEntity tile){
		return MathUtil.distance(tile.xCoord-getX(),tile.yCoord-getY(),tile.zCoord-getZ());
	}
	
	/* === WORLD === */
	
	public boolean setAir(World world){
		return world.setBlockToAir(getX(),getY(),getZ());
	}
	
	public boolean setBlock(World world, Block block){
		return world.setBlock(getX(),getY(),getZ(),block);
	}
	
	public boolean setBlock(World world, Block block, int metadata){
		return world.setBlock(getX(),getY(),getZ(),block,metadata,3);
	}
	
	public boolean setBlock(World world, Block block, int metadata, int flags){
		return world.setBlock(getX(),getY(),getZ(),block,metadata,flags);
	}
	
	public boolean setMetadata(World world, int metadata){
		return world.setBlockMetadataWithNotify(getX(),getY(),getZ(),metadata,3);
	}
	
	public boolean setMetadata(World world, int metadata, int flags){
		return world.setBlockMetadataWithNotify(getX(),getY(),getZ(),metadata,flags);
	}
	
	public boolean setBlock(World world, BlockInfo info){
		return world.setBlock(getX(),getY(),getZ(),info.block,info.meta,3);
	}
	
	public boolean setBlock(World world, BlockInfo info, int flags){
		return world.setBlock(getX(),getY(),getZ(),info.block,info.meta,flags);
	}
	
	public boolean breakBlock(World world, boolean drop){
		return world.func_147480_a(getX(),getY(),getZ(),drop);
	}
	
	public boolean isAir(IBlockAccess world){
		return world.isAirBlock(getX(),getY(),getZ());
	}
	
	public Block getBlock(IBlockAccess world){
		return world.getBlock(getX(),getY(),getZ());
	}
	
	public int getMetadata(IBlockAccess world){
		return world.getBlockMetadata(getX(),getY(),getZ());
	}
	
	public BlockInfo getInfo(IBlockAccess world){
		return new BlockInfo(getBlock(world),getMetadata(world));
	}
	
	public Material getMaterial(IBlockAccess world){
		return world.getBlock(getX(),getY(),getZ()).getMaterial();
	}
	
	public <T extends TileEntity> T getTileEntity(IBlockAccess world){
		return (T)world.getTileEntity(getX(),getY(),getZ());
	}
	
	public <T extends TileEntity> Optional<T> tryGetTileEntity(IBlockAccess world){
		return Optional.ofNullable((T)world.getTileEntity(getX(),getY(),getZ()));
	}
	
	public <T extends TileEntity> Optional<T> castTileEntity(IBlockAccess world, Class<T> type){
		TileEntity tile = world.getTileEntity(getX(),getY(),getZ());
		return tile == null || !type.isAssignableFrom(tile.getClass()) ? Optional.empty() : Optional.of((T)tile);
	}
	
	public boolean checkBlock(IBlockAccess world, Block block, int metadata){
		return world.getBlock(getX(),getY(),getZ()) == block && world.getBlockMetadata(getX(),getY(),getZ()) == metadata;
	}
	
	/* === OBJECT METHODS === */
	
	@Override
	public boolean equals(Object obj){
		if (obj instanceof Pos){
			Pos pos = (Pos)obj;
			return pos.getX() == getX() && pos.getY() == getY() && pos.getZ() == getZ();
		}
		else return super.equals(obj);
	}
	
	public boolean equals(Pos pos){
		return pos.getX() == getX() && pos.getY() == getY() && pos.getZ() == getZ();
	}
	
	@Override
	public int hashCode(){
		return (getY()+getZ()*31)*31+getX();
	}
	
	@Override
	public String toString(){
		return new StringBuilder().append("{ ").append(getX()).append(", ").append(getY()).append(", ").append(getZ()).append(" }").toString();
	}
	
	/* === MUTABLE POS CLASS === */
	
	public static final class PosMutable extends Pos{
		public int x, y, z;
		
		public PosMutable(){
			this(0,0,0);
		}
		
		public PosMutable(Pos pos){
			this(pos.getX(),pos.getY(),pos.getZ());
		}
		
		public PosMutable(int x, int y, int z){
			super(0,0,0);
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		@Override
		public int getX(){
			return x;
		}
		
		@Override
		public int getY(){
			return y;
		}
		
		@Override
		public int getZ(){
			return z;
		}
		
		@Override
		public Pos immutable(){
			return Pos.at(x,y,z);
		}
		
		public PosMutable copy(){
			return new PosMutable(x,y,z);
		}
		
		/* === SETTERS === */
		
		public PosMutable set(Pos pos){
			return set(pos.getX(),pos.getY(),pos.getZ());
		}
		
		public PosMutable set(int x, int y, int z){
			this.x = x;
			this.y = y;
			this.z = z;
			return this;
		}
		
		public PosMutable set(double x, double y, double z){
			return set(MathUtil.floor(x),MathUtil.floor(y),MathUtil.floor(z));
		}
		
		public PosMutable set(Entity entity){
			return set(MathUtil.floor(entity.posX),MathUtil.floor(entity.posY),MathUtil.floor(entity.posZ));
		}
		
		public PosMutable setX(int x){
			this.x = x;
			return this;
		}
		
		public PosMutable setY(int y){
			this.y = y;
			return this;
		}
		
		public PosMutable setZ(int z){
			this.z = z;
			return this;
		}
		
		/* === OFFSET === */
		
		public PosMutable move(int x, int y, int z){
			this.x += x;
			this.y += y;
			this.z += z;
			return this;
		}
		
		public PosMutable move(EnumFacing facing){
			return move(facing.getFrontOffsetX(),facing.getFrontOffsetY(),facing.getFrontOffsetZ());
		}
		
		public PosMutable move(EnumFacing facing, int amount){
			return move(facing.getFrontOffsetX()*amount,facing.getFrontOffsetY()*amount,facing.getFrontOffsetZ()*amount);
		}
		
		public PosMutable move(IFacing facing){
			return move(facing.getX(),0,facing.getZ());
		}
		
		public PosMutable move(IFacing facing, int amount){
			return move(facing.getX()*amount,0,facing.getZ()*amount);
		}
		
		public PosMutable moveUp(){
			++y;
			return this;
		}
	}
}
