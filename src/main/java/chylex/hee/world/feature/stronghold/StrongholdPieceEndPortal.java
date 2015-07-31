package chylex.hee.world.feature.stronghold;
import java.util.Random;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.util.Size;

public class StrongholdPieceEndPortal extends StrongholdPiece{
	public StrongholdPieceEndPortal(){
		super(new Size(8,5,8));
	}

	@Override
	public void generate(StructureWorld world, Random rand, int x, int y, int z){
		world.setBlock(x,y,z,placeStoneBrick.pick(rand)); // TODO
	}
}
