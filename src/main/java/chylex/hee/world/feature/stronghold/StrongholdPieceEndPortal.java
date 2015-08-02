package chylex.hee.world.feature.stronghold;
import java.util.Random;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.util.Facing4;
import chylex.hee.world.structure.util.Size;

public class StrongholdPieceEndPortal extends StrongholdPiece{
	public StrongholdPieceEndPortal(){
		super(Type.ROOM,new Size(9,10,9));
		addConnection(Facing4.NORTH_NEGZ,0,0,4);
		addConnection(Facing4.SOUTH_POSZ,8,0,4);
	}

	@Override
	public void generate(StructureWorld world, Random rand, int x, int y, int z){
		placeCube(world,rand,placeStoneBrick,x,y,z,x+size.sizeX-1,y,z+size.sizeZ-1);
	}
}
