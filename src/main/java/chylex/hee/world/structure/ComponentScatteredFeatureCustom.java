package chylex.hee.world.structure;
import java.util.Random;
import chylex.hee.system.util.BlockPosM;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

public abstract class ComponentScatteredFeatureCustom extends StructureComponent{
	protected int sizeX, sizeY, sizeZ;
	private BlockPosM pos;
	
	public ComponentScatteredFeatureCustom(){}

	protected ComponentScatteredFeatureCustom(Random rand, int x, int y, int z, int sizeX, int sizeY, int sizeZ){
		super(0);
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		
		coordBaseMode = EnumFacing.getHorizontal(rand.nextInt(4));
		boundingBox = (coordBaseMode.getHorizontalIndex() == 0 || coordBaseMode.getHorizontalIndex() == 2) ? new StructureBoundingBox(x,y,z,x+sizeX-1,y+sizeY-1,z+sizeZ-1) : new StructureBoundingBox(x,y,z,x+sizeZ-1,y+sizeY-1,z+sizeX-1);
	}
	
	public int getSizeX(){
		return sizeX;
	}
	
	public int getSizeY(){
		return sizeY;
	}
	
	public int getSizeZ(){
		return sizeZ;
	}

	@Override
	protected void writeStructureToNBT(NBTTagCompound nbt){
		nbt.setInteger("Width",sizeX);
		nbt.setInteger("Height",sizeY);
		nbt.setInteger("Depth",sizeZ);
	}

	@Override
	protected void readStructureFromNBT(NBTTagCompound nbt){
		sizeX = nbt.getInteger("Width");
		sizeY = nbt.getInteger("Height");
		sizeZ = nbt.getInteger("Depth");
	}
	
	public final boolean placeBlockWithoutUpdate(Block block, int metadata, int x, int y, int z, World world, StructureBoundingBox bb){
		if (bb.isVecInside(updatePos(x,y,z))){
			world.setBlock(xx,yy,zz,block,metadata,2);
			return true;
		}
		else return false;
	}
	
	public final boolean placeBlockAndUpdate(Block block, int metadata, int x, int y, int z, World world, StructureBoundingBox bb){
		if (bb.isVecInside(updatePos(x,y,z))){
			world.setBlock(xx,yy,zz,block,metadata,3);
			world.markBlockForUpdate(xx,yy,zz);
			return true;
		}
		else return false;
	}
	
	public final TileEntity getBlockTileEntity(int x, int y, int z, World world, StructureBoundingBox bb){
		return bb.isVecInside(updatePos(x,y,z)) ? world.getTileEntity(pos) : null;
	}
	
	/**
	 * Unsafe version of placeBlockWithoutUpdate that checks for existing chunks instead of provided bounding box.
	 * It seems to provide better results in special cases without causing issues.
	 */
	public final boolean placeBlockUnsafe(Block block, int metadata, int x, int y, int z, World world, StructureBoundingBox bb){
		if (placeBlockWithoutUpdate(block,metadata,x,y,z,world,bb))return true;
		
		if (world.isBlockLoaded(updatePos(x,y,z))){
			world.setBlock(xx,yy,zz,block,metadata,2);
			return true;
		}
		else return false;
	}
	
	/**
	 * Unsafe version of placeBlockAndUpdate that checks for existing chunks instead of provided bounding box.
	 * It seems to provide better results in special cases without causing issues.
	 */
	public final boolean placeBlockAndUpdateUnsafe(Block block, int metadata, int x, int y, int z, World world, StructureBoundingBox bb){
		if (placeBlockAndUpdate(block,metadata,x,y,z,world,bb))return true;
		
		if (world.isBlockLoaded(updatePos(x,y,z))){
			world.setBlock(xx,yy,zz,block,metadata,3);
			world.markBlockForUpdate(pos);
			return true;
		}
		else return false;
	}
	
	/**
	 * Unsafe version of placeBlockAndUpdate that checks for existing chunks instead of provided bounding box.
	 * It seems to provide better results in special cases without causing issues.
	 */
	public final TileEntity getBlockTileEntityUnsafe(int x, int y, int z, World world, StructureBoundingBox bb){
		TileEntity te = getBlockTileEntity(x,y,z,world,bb);
		if (te != null)return te;
		
		return world.isBlockLoaded(updatePos(x,y,z)) ? world.getTileEntity(pos) : null;
	}
	
	private BlockPosM updatePos(int x, int y, int z){
		pos.x = super.getXWithOffset(x,z);
		pos.y = super.getYWithOffset(y);
		pos.z = super.getZWithOffset(x,z);
		return pos;
	}
	
	@Override
	public int getXWithOffset(int x, int z){
		return super.getXWithOffset(x,z);
	}
	
	@Override
	public int getYWithOffset(int y){
		return super.getYWithOffset(y);
	}
	
	@Override
	public int getZWithOffset(int x, int z){
		return super.getZWithOffset(x,z);
	}
}
