package chylex.hee.world.feature.stronghold.corridors;
import java.util.Arrays;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.world.feature.stronghold.StrongholdPiece;
import chylex.hee.world.structure.IBlockPicker;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.structure.util.Facing4;
import chylex.hee.world.structure.util.Size;

public class StrongholdStairsVertical extends StrongholdPiece{
	public static StrongholdStairsVertical[] generateStairs(final int levels){
		return Arrays.stream(Facing4.list).flatMap(entrance -> {
			return Arrays.stream(Facing4.list).map(exit -> new StrongholdStairsVertical(entrance,exit,levels));
		}).toArray(StrongholdStairsVertical[]::new);
	}
	
	/*
	 * base height for levels = 1 is 9
	 * if exit is to the left of entrance, add 0
	 * if exit is the same as entrance (straight), add 1
	 * if exit is to the right of entrance, add 2
	 * if exit is opposite of entrance (return), add 3
	 * base height for levels = 2 is 13
	 */
	private static int calculateHeight(Facing4 toEntrance, Facing4 toExit, int levels){
		int height = 9+(levels-1)*4;
		
		if (toExit == toEntrance)height += 1;
		else if (toExit == toEntrance.rotateRight())height += 2;
		else if (toExit == toEntrance.opposite())height += 3;
		
		return height;
	}
	
	private final Facing4 toEntrance, toExit;
	
	public StrongholdStairsVertical(Facing4 toEntrance, Facing4 toExit, int levels){
		super(Type.CORRIDOR,new Size(5,calculateHeight(toEntrance,toExit,levels),5));
		this.toEntrance = toEntrance;
		this.toExit = toExit;
		
		Facing4 oppositeEntrance = toEntrance.opposite();
		addConnection(oppositeEntrance,maxX/2+2*oppositeEntrance.getX(),0,maxZ/2+2*oppositeEntrance.getZ(),withAnything);
		addConnection(toExit,maxX/2+2*toExit.getX(),size.sizeY-5,maxZ/2+2*toExit.getZ(),withAnything);
	}

	@Override
	public void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		boolean useStairs = rand.nextBoolean();
		boolean addPillar = rand.nextBoolean();
		
		// basic layout
		placeCube(world,rand,placeStoneBrick,x,y,z,x+maxX,y,z+maxZ);
		placeCube(world,rand,placeStoneBrick,x,y+maxY,z,x+maxX,y+maxY,z+maxZ);
		placeWalls(world,rand,placeStoneBrick,x,y+1,z,x+maxX,y+maxY-1,z+maxZ);
		
		// entrance and exit
		for(Connection connection:connections){
			if (inst.isConnectionFree(connection))continue;
			
			Facing4 facing = connection.facing, perpendicular = facing.perpendicular();
			
			int posX = x+maxX/2+2*facing.getX(), posZ = z+maxZ/2+2*facing.getZ();
			placeCube(world,rand,placeAir,posX-perpendicular.getX(),y+connection.offsetY+1,posZ-perpendicular.getZ(),posX+perpendicular.getX(),y+connection.offsetY+3,posZ+perpendicular.getZ());
		}
		
		// stairs or slabs
		Facing4 towards = toEntrance;
		PosMutable pos = new PosMutable(x+maxX/2,y+1,z+maxZ/2);
		pos.move(toEntrance.rotateLeft()).move(toEntrance,-1);
		
		for(int index = 0; index <= maxY-5; index++){
			pos.move(towards);
			placeBlock(world,rand,useStairs ? placeStoneBrickStairs(towards,false) : IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneBrickBottom),pos.x,pos.y,pos.z);
			pos.move(towards);
			placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneBrickTop),pos.x,pos.y,pos.z);
			towards = towards.rotateRight();
			++pos.y;
		}
		
		// pillar
		if (addPillar){
			placeLine(world,rand,placeStoneBrick,x+maxX/2,y+1,z+maxZ/2,x+maxX/2,y+maxY-1,z+maxZ/2);
			
			if (rand.nextBoolean()){
				PosMutable endPos = new PosMutable(x+maxX/2,0,z+maxZ/2).move(toExit);
				placeLine(world,rand,placeStoneBrick,endPos.x,y+maxY-3,endPos.z,endPos.x,y+maxY-1,endPos.z);
				endPos.move(toExit.rotateRight());
				placeLine(world,rand,placeStoneBrick,endPos.x,y+maxY-3,endPos.z,endPos.x,y+maxY-1,endPos.z);
			}
		}
	}
}
