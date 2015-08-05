package chylex.hee.world.feature.stronghold;
import java.util.Random;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.util.Facing4;
import chylex.hee.world.structure.util.Size;

public class StrongholdPieceEndPortal extends StrongholdPiece{
	public StrongholdPieceEndPortal(){
		super(Type.ROOM,new Size(13,10,13));
		addConnection(Facing4.NORTH_NEGZ,6,0,0);
		addConnection(Facing4.SOUTH_POSZ,6,0,12);
		addConnection(Facing4.EAST_POSX,0,0,6);
		addConnection(Facing4.WEST_NEGX,12,0,6);
	}

	@Override
	public void generate(StructureWorld world, Random rand, int x, int y, int z){
		placeCube(world,rand,placeStoneBrick,x,y,z,x+size.sizeX-1,y,z+size.sizeZ-1);
	}
}
