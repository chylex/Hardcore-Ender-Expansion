package chylex.hee.world.structure;
import net.minecraft.block.Block;
import net.minecraft.world.World;

/**
 * Represents a {@link StructureWorldPart} made for a single feature. Contains statistics of the generated content to allow special handling and conditions in worldgen.
 */
public class StructureWorldFeature extends StructureWorldPart{
	private int minX, minZ, maxX, maxZ;
	private int generatedBlocks;
	
	public StructureWorldFeature(World world, int radX, int sizeY, int radZ){
		super(world, radX, sizeY, radZ);
	}
	
	public StructureWorldFeature(int radX, int sizeY, int radZ){
		super(radX, sizeY, radZ);
	}
	
	@Override
	public boolean setBlock(int x, int y, int z, Block block, int metadata){
		boolean wasAir = isAir(x, y, z);
		
		if (super.setBlock(x, y, z, block, metadata)){
			if (x < minX)minX = x;
			else if (x > maxX)maxX = x;
			
			if (z < minZ)minZ = z;
			else if (z > maxZ)maxZ = z;
			
			boolean isAir = isAir(x, y, z);
			
			if (isAir && !wasAir)--generatedBlocks;
			else if (!isAir && wasAir)++generatedBlocks;
			
			return true;
		}
		else return false;
	};
	
	@Override
	public void clearArea(Block block, int metadata){
		super.clearArea(block, metadata);
		
		if (isAir(0, 0, 0))minX = maxX = minZ = maxZ = 0;
		else{
			minX = -radX;
			maxX = radX;
			minZ = -radZ;
			maxZ = radZ;
		}
	}
	
	public final int getGeneratedSizeX(){
		return maxX-minX;
	}
	
	public final int getGeneratedSizeZ(){
		return maxZ-minZ;
	}
	
	public final int getCenterOffsetX(){
		return -(minX+maxX)/2;
	}
	
	public final int getCenterOffsetZ(){
		return -(minZ+maxZ)/2;
	}
	
	public final int getGeneratedBlocks(){
		return generatedBlocks;
	}
}
