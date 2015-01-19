package chylex.hee.system.util;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockPosM extends BlockPos{
	public int x, y, z;
	
	public BlockPosM(){
		this(0,0,0);
	}
	
	public BlockPosM(int x, int y, int z){
		super(0,0,0);
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public BlockPosM(BlockPos pos){
		this(pos.getX(),pos.getY(),pos.getZ());
	}
	
	public BlockPosM(double x, double y, double z){
		this(MathUtil.floor(x),MathUtil.floor(y),MathUtil.floor(z));
	}
	
	public BlockPosM(Entity entity){
		this(MathUtil.floor(entity.posX),MathUtil.floor(entity.posY),MathUtil.floor(entity.posZ));
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
	
	/*
	 * MOVE
	 */
	
	public BlockPosM moveTo(int x, int y, int z){
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	
	public BlockPosM moveTo(double x, double y, double z){
		return moveTo(MathUtil.floor(x),MathUtil.floor(y),MathUtil.floor(z));
	}
	
	public BlockPosM moveTo(Entity entity){
		return moveTo(MathUtil.floor(entity.posX),MathUtil.floor(entity.posY),MathUtil.floor(entity.posZ));
	}
	
	public BlockPosM moveBy(int x, int y, int z){
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}
	
	public BlockPosM moveBy(EnumFacing facing, int amount){
		x += facing.getFrontOffsetX()*amount;
		y += facing.getFrontOffsetY()*amount;
		z += facing.getFrontOffsetZ()*amount;
		return this;
	}
	
	public BlockPosM moveUp(){
		return moveBy(EnumFacing.UP,1);
	}
	
	public BlockPosM moveDown(){
		return moveBy(EnumFacing.DOWN,1);
	}
	
	public BlockPosM moveEast(){
		return moveBy(EnumFacing.EAST,1);
	}
	
	public BlockPosM moveWest(){
		return moveBy(EnumFacing.WEST,1);
	}
	
	public BlockPosM moveNorth(){
		return moveBy(EnumFacing.NORTH,1);
	}
	
	public BlockPosM moveSouth(){
		return moveBy(EnumFacing.SOUTH,1);
	}
	
	/*
	 * WORLD
	 */
	
	public boolean setToAir(World world){
		return world.setBlockToAir(this);
	}
	
	public boolean setBlock(World world, IBlockState state){
		return world.setBlockState(this,state);
	}
	
	public boolean setBlock(World world, IBlockState state, int flags){
		return world.setBlockState(this,state,flags);
	}
	
	public boolean setBlock(World world, Block block){
		return world.setBlockState(this,block.getDefaultState());
	}
	
	public boolean isAir(World world){
		return world.isAirBlock(this);
	}
	
	public Block getBlock(World world){
		return world.getBlockState(this).getBlock();
	}
	
	public Material getBlockMaterial(World world){
		return world.getBlockState(this).getBlock().getMaterial();
	}
}
