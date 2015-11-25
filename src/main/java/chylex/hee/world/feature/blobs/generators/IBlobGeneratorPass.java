package chylex.hee.world.feature.blobs.generators;
import net.minecraft.init.Blocks;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.facing.Facing6;
import chylex.hee.world.feature.blobs.StructureWorldBlob;

public interface IBlobGeneratorPass{
	void run(StructureWorldBlob world);
	
	public static final IBlobGeneratorPass passSmoothing = world -> {
		runSmoothingPass(world,4);
		runSmoothingPass(world,5);
	};
	
	static void runSmoothingPass(StructureWorldBlob world, final int airAmount){
		for(Pos pos:world.getEndStoneBlocks()){
			if (checkAdjacentAir(world,pos,airAmount)){
				world.setBlock(pos,Blocks.air);
			}
		}
	}
	
	static boolean checkAdjacentAir(StructureWorldBlob world, Pos pos, final int targetAmount){
		int air = 0;
		
		for(Facing6 facing:Facing6.list){
			Pos offset = pos.offset(facing);
			
			if (world.getBlock(offset) != Blocks.end_stone){
				if (++air == targetAmount)return true;
			}
		}
		
		return air == targetAmount; // makes it work for targetAmount == 0, but it's not used so the logic isn't handled in the cycle
	}
}