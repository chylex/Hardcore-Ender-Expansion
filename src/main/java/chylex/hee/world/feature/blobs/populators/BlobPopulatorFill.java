package chylex.hee.world.feature.blobs.populators;
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
		world.getEndStoneBlocks().forEach(pos -> {
			for(Facing6 facing:Facing6.list){
				Pos offset = pos.offset(facing);
				if (world.getBlock(offset.getX(),offset.getY(),offset.getZ()) != Blocks.end_stone)return;
			}
			
			world.setBlock(pos.getX(),pos.getY(),pos.getZ(),block);
		});
	}
}
