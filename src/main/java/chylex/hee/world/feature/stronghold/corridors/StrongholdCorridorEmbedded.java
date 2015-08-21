package chylex.hee.world.feature.stronghold.corridors;
import java.util.Random;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.world.feature.stronghold.StrongholdPiece;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.structure.util.Facing4;
import chylex.hee.world.structure.util.Size;

public abstract class StrongholdCorridorEmbedded extends StrongholdPiece{
	protected final boolean dirX;
	
	public StrongholdCorridorEmbedded(boolean dirX, Size size){
		super(Type.CORRIDOR,size);
		this.dirX = dirX;
	}
	
	@Override
	public final void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		int x1 = x+maxX/2-2, x2 = x+maxX/2+2, z1 = z+maxZ/2-2, z2 = z+maxZ/2+2;
		
		placeCube(world,rand,placeStoneBrick,x1,y,z1,x2,y,z2);
		placeCube(world,rand,placeStoneBrick,x1,y+maxY,z1,x2,y+maxY,z2);
		
		placeLine(world,rand,placeStoneBrick,x1,y+1,z1,x1,y+maxY-1,z1);
		placeLine(world,rand,placeStoneBrick,x2,y+1,z1,x2,y+maxY-1,z1);
		placeLine(world,rand,placeStoneBrick,x1,y+1,z2,x1,y+maxY-1,z2);
		placeLine(world,rand,placeStoneBrick,x2,y+1,z2,x2,y+maxY-1,z2);
		
		generateEmbedded(inst,world,rand,x,y,z);
	}
	
	protected abstract void generateEmbedded(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z);
	
	protected void placeOutsideWall(StructureWorld world, Random rand, int x, int y, int z, Facing4 goingTo, int distance){
		PosMutable pos = new PosMutable(x+maxX/2,0,z+maxZ/2);
		pos.move(goingTo,distance);
		
		Facing4 perpendicular = goingTo.perpendicular();
		placeCube(world,rand,placeStoneBrick,pos.x-perpendicular.getX(),y+1,pos.z-perpendicular.getZ(),pos.x+perpendicular.getX(),y+3,pos.z+perpendicular.getZ());
	}
	
	protected final Facing4 getRandomFacing(Random rand){
		return dirX ? (rand.nextBoolean() ? Facing4.NORTH_NEGZ : Facing4.SOUTH_POSZ) : (rand.nextBoolean() ? Facing4.EAST_POSX : Facing4.WEST_NEGX);
	}
}
