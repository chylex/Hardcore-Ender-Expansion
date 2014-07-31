package chylex.hee.world.structure;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

public abstract class ComponentScatteredFeatureCustom extends StructureComponent{
	protected int sizeX,sizeY,sizeZ;
	
	public ComponentScatteredFeatureCustom(){}

	protected ComponentScatteredFeatureCustom(Random rand, int x, int y, int z, int sizeX, int sizeY, int sizeZ){
		super(0);
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		
		coordBaseMode = rand.nextInt(4);
		boundingBox = (coordBaseMode == 0 || coordBaseMode == 2) ? new StructureBoundingBox(x,y,z,x+sizeX-1,y+sizeY-1,z+sizeZ-1) : new StructureBoundingBox(x,y,z,x+sizeZ-1,y+sizeY-1,z+sizeX-1);
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
	protected void func_143012_a(NBTTagCompound nbt){ // OBFUSCATED save structure to nbt
		nbt.setInteger("Width",sizeX);
		nbt.setInteger("Height",sizeY);
		nbt.setInteger("Depth",sizeZ);
	}

	@Override
	protected void func_143011_b(NBTTagCompound nbt){ // OBFUSCATED load structure from nbt
		sizeX = nbt.getInteger("Width");
		sizeY = nbt.getInteger("Height");
		sizeZ = nbt.getInteger("Depth");
	}
	
	public final boolean placeBlockWithoutUpdate(Block block, int metadata, int x, int y, int z, World world, StructureBoundingBox bb){
		int xx = this.getXWithOffset(x,z), yy = this.getYWithOffset(y), zz = this.getZWithOffset(x,z);
		if (bb.isVecInside(xx,yy,zz)){
			world.setBlock(xx,yy,zz,block,metadata,2);
			return true;
		}
		else return false;
	}
	
	public final boolean placeBlockAndUpdate(Block block, int metadata, int x, int y, int z, World world, StructureBoundingBox bb){
		int xx = this.getXWithOffset(x,z), yy = this.getYWithOffset(y), zz = this.getZWithOffset(x,z);
		if (bb.isVecInside(xx,yy,zz)){
			world.setBlock(xx,yy,zz,block,metadata,3);
			world.markBlockForUpdate(xx,yy,zz);
			return true;
		}
		else return false;
	}
	
	public final TileEntity getBlockTileEntity(int x, int y, int z, World world, StructureBoundingBox bb){
		int xx = this.getXWithOffset(x,z), yy = this.getYWithOffset(y), zz = this.getZWithOffset(x,z);
		return bb.isVecInside(xx,yy,zz) ? world.getTileEntity(xx,yy,zz) : null;
	}
}
