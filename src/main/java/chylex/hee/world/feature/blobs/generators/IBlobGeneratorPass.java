package chylex.hee.world.feature.blobs.generators;
import net.minecraft.init.Blocks;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.facing.Facing6;
import chylex.hee.world.feature.blobs.StructureWorldBlob;

public interface IBlobGeneratorPass{
	void run(StructureWorldBlob world);
	
	public static final IBlobGeneratorPass passSmoothing = world -> {
		for(Pos pos:world.getEndStoneBlocks()){
			int air = 0;
			
			for(Facing6 facing:Facing6.list){
				Pos offset = pos.offset(facing);
				
				if (world.getBlock(offset.getX(),offset.getY(),offset.getZ()) == Blocks.end_stone){
					if (++air == 4){
						world.setBlock(pos.getX(),pos.getY(),pos.getZ(),Blocks.air);
						break;
					}
				}
			}
		}
	};
}