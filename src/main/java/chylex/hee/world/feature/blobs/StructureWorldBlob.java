package chylex.hee.world.feature.blobs;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.world.structure.StructureWorldPart;

public class StructureWorldBlob extends StructureWorldPart{
	private final List<Pos> generatedEndStone = new ArrayList<>(64);
	
	public StructureWorldBlob(World world, int radX, int sizeY, int radZ){
		super(world,radX,sizeY,radZ);
	}
	
	public StructureWorldBlob(int radX, int sizeY, int radZ){
		super(null,radX,sizeY,radZ);
	}
	
	@Override
	public boolean setBlock(int x, int y, int z, Block block, int metadata){
		if (!isInside(x,y,z))return false;
		
		int index = toIndex(x,y,z);
		
		if (this.blocks[index] == Blocks.end_stone){
			if (block != Blocks.end_stone)generatedEndStone.remove(Pos.at(x,y,z));
		}
		else{
			if (block == Blocks.end_stone)generatedEndStone.add(Pos.at(x,y,z));
		}
		
		this.blocks[index] = block;
		this.metadata[index] = (byte)metadata;
		return true;
	}
	
	/**
	 * Returns a copy of the list of End Stone blocks.
	 */
	public List<Pos> getEndStoneBlocks(){
		return new ArrayList<>(generatedEndStone);
	}
	
	public int getCenterY(){
		return sizeY/2;
	}
}
