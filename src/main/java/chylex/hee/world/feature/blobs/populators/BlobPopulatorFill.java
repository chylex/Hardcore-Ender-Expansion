package chylex.hee.world.feature.blobs.populators;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.facing.Facing6;
import chylex.hee.world.feature.blobs.StructureWorldBlob;

public class BlobPopulatorFill extends BlobPopulator{
	protected Block block = Blocks.air;
	
	public BlobPopulatorFill(int weight){
		super(weight);
	}
	
	public BlobPopulatorFill setFillBlock(Block block){
		this.block = block;
		return this;
	}

	@Override
	public void populate(StructureWorldBlob world, Random rand){
		List<Pos> toRemove = world.getEndStoneBlocks();
		
		for(Iterator<Pos> iter = toRemove.iterator(); iter.hasNext();){
			Pos pos = iter.next();
			
			for(Facing6 facing:Facing6.list){
				Pos offset = pos.offset(facing);
				
				if (world.getBlock(offset.getX(),offset.getY(),offset.getZ()) != Blocks.end_stone){
					iter.remove();
					break;
				}
			}
		}
		
		for(Pos pos:toRemove)world.setBlock(pos.getX(),pos.getY(),pos.getZ(),block);
	}
}
