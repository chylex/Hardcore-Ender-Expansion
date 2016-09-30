package chylex.hee.world.feature.stronghold.rooms.decorative;
import java.util.Random;
import chylex.hee.world.feature.stronghold.rooms.StrongholdRoom;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.util.Size;

public class StrongholdRoomLargeIntersection extends StrongholdRoom{
	public StrongholdRoomLargeIntersection(){
		super(new Size(11, 7, 11));
	}

	@Override
	public void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		super.generate(inst, world, rand, x, y, z);
		
		placeCube(world, rand, placeStoneBrick, x+1, y+1, z+1, x+3, y+maxY-1, z+3);
		placeCube(world, rand, placeStoneBrick, x+maxX-3, y+1, z+1, x+maxX-1, y+maxY-1, z+3);
		placeCube(world, rand, placeStoneBrick, x+1, y+1, z+maxZ-3, x+3, y+maxY-1, z+maxZ-1);
		placeCube(world, rand, placeStoneBrick, x+maxX-3, y+1, z+maxZ-3, x+maxX-1, y+maxY-1, z+maxZ-1);
	}
	
	@Override
	protected float getWeightFactor(){
		return 1.75F;
	}
	
	@Override
	protected float getWeightMultiplier(){
		return 4F;
	}
}
