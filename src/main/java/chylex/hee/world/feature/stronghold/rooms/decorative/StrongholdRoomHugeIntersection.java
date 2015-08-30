package chylex.hee.world.feature.stronghold.rooms.decorative;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.world.feature.stronghold.rooms.StrongholdRoom;
import chylex.hee.world.structure.IBlockPicker;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.structure.util.Facing4;
import chylex.hee.world.structure.util.Size;

public class StrongholdRoomHugeIntersection extends StrongholdRoom{
	public StrongholdRoomHugeIntersection(){
		super(new Size(13,12,13));
		
		addConnection(Facing4.NORTH_NEGZ,maxX/2,5,0,fromRoom);
		addConnection(Facing4.SOUTH_POSZ,maxX/2,5,maxZ,fromRoom);
		addConnection(Facing4.EAST_POSX,maxX,5,maxZ/2,fromRoom);
		addConnection(Facing4.WEST_NEGX,0,5,maxZ/2,fromRoom);
	}
	
	@Override
	public void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		super.generate(inst,world,rand,x,y,z);
		int centerX = x+maxX/2, centerZ = z+maxZ/2;
		
		// floors
		IBlockPicker placeChiseledBrick = IBlockPicker.basic(Blocks.stonebrick,Meta.stoneBrickChiseled);
		IBlockPicker placeDoubleStoneSlab = IBlockPicker.basic(Blocks.double_stone_slab,Meta.slabStoneSmoothDouble);
		
		for(int level = 0, py; level < 2; level++){
			py = y+5*level;
			
			placeBlock(world,rand,placeChiseledBrick,centerX,py,centerZ);
			
			for(int line = 0; line < 2; line++){
				placeLine(world,rand,placeDoubleStoneSlab,centerX-1+2*line,py,centerZ-5,centerX-1+2*line,py,centerZ+5);
				placeLine(world,rand,placeDoubleStoneSlab,centerX-5,py,centerZ-1+2*line,centerX+5,py,centerZ-1+2*line);
			}
			
			for(Facing4 facing:Facing4.list){
				placeLine(world,rand,placeChiseledBrick,centerX+facing.getX(),py,centerZ+facing.getZ(),centerX+5*facing.getX(),py,centerZ+5*facing.getZ());
			}
		}
		
		// ceiling upper stair layer
		PosMutable mpos = new PosMutable();
		int topY = y+maxY-1;
		
		for(Facing4 facing:Facing4.list){
			mpos.set(centerX,0,centerZ).move(facing,5);
			facing = facing.rotateRight();
			placeLine(world,rand,placeStoneBrickStairs(facing.rotateLeft(),true),mpos.x,topY,mpos.z,mpos.x+4*facing.getX(),topY,mpos.z+4*facing.getZ());
			
			mpos.move(facing,3).move(facing = facing.rotateRight());
			placeLine(world,rand,placeStoneBrickStairs(facing.rotateLeft(),true),mpos.x,topY,mpos.z,mpos.x+facing.getX(),topY,mpos.z+facing.getZ());
			
			mpos.move(facing).move(facing = facing.rotateLeft());
			placeLine(world,rand,placeStoneBrickStairs(facing.rotateLeft(),true),mpos.x,topY,mpos.z,mpos.x+facing.getX(),topY,mpos.z+facing.getZ());
			
			mpos.move(facing).move(facing.rotateRight());
			placeLine(world,rand,placeStoneBrickStairs(facing.rotateLeft(),true),mpos.x,topY,mpos.z,mpos.x+2*facing.getX(),topY,mpos.z+2*facing.getZ());
		}
		
		// ceiling lower stair layer
		topY = y+maxY-2;
		for(Facing4 facing:Facing4.list){
			mpos.set(centerX,0,centerZ).move(facing,5).move(facing = facing.rotateRight(),4);
			facing = facing.rotateRight();
			placeLine(world,rand,placeStoneBrickStairs(facing.rotateLeft(),true),mpos.x,topY,mpos.z,mpos.x+facing.getX(),topY,mpos.z+facing.getZ());
			
			mpos.move(facing).move(facing = facing.rotateLeft());
			placeBlock(world,rand,placeStoneBrickStairs(facing.rotateLeft(),true),mpos.x,topY,mpos.z);
		}
		
		// alternating content
		Facing4 contentFacing = Facing4.list[rand.nextInt(Facing4.list.length)];
		generateStairs(world,rand,centerX,y,centerZ,contentFacing = contentFacing.rotateRight());
		generateFountain(world,rand,centerX,y,centerZ,contentFacing = contentFacing.rotateRight());
		generateStairs(world,rand,centerX,y,centerZ,contentFacing = contentFacing.rotateRight());
		generateFountain(world,rand,centerX,y,centerZ,contentFacing = contentFacing.rotateRight());
	}
	
	private void generateStairs(StructureWorld world, Random rand, final int centerX, final int y, final int centerZ, Facing4 facing){
		PosMutable mpos = new PosMutable();
		
		// block fountain hole
		mpos.set(centerX,y,centerZ);
		mpos.move(facing,5).move(facing.rotateRight(),5);
		placeLine(world,rand,placeStoneBrick,mpos.x,y+maxY-3,mpos.z,mpos.x,y+maxY-1,mpos.z);
		
		// place stairs
		mpos.set(centerX,y,centerZ);
		mpos.move(facing,4).move(facing = facing.rotateRight(),2);
		
		for(int cycle = 0; cycle < 2; cycle++){
			mpos.moveUp();
			placeBlock(world,rand,placeStoneBrickStairs(facing,false),mpos.x,mpos.y,mpos.z);
			mpos.move(facing);
			placeBlock(world,rand,placeStoneBrickStairs(facing.opposite(),true),mpos.x,mpos.y,mpos.z);
		}
		
		mpos.move(facing = facing.rotateRight());
		placeBlock(world,rand,placeStoneBrickStairs(facing.opposite(),true),mpos.x,mpos.y,mpos.z);
		mpos.moveUp();
		placeBlock(world,rand,placeStoneBrickStairs(facing,false),mpos.x,mpos.y,mpos.z);
		mpos.move(facing);
		placeBlock(world,rand,placeStoneBrickStairs(facing.opposite(),true),mpos.x,mpos.y,mpos.z);
		mpos.move(facing = facing.rotateRight());
		
		for(int cycle = 0; cycle < 2; cycle++){
			placeBlock(world,rand,placeStoneBrickStairs(facing.opposite(),true),mpos.x,mpos.y,mpos.z);
			mpos.moveUp();
			placeBlock(world,rand,placeStoneBrickStairs(facing,false),mpos.x,mpos.y,mpos.z);
			if (cycle == 0)mpos.move(facing);
		}
	}
	
	private void generateFountain(StructureWorld world, Random rand, final int centerX, final int y, final int centerZ, Facing4 facing){
		PosMutable mpos = new PosMutable();
		
		// water and internal blockage
		mpos.set(centerX,0,centerZ);
		mpos.move(facing,5).move(facing = facing.rotateRight(),5);
		placeBlock(world,rand,IBlockPicker.basic(Blocks.flowing_water),mpos.x,y+maxY-1,mpos.z);
		
		for(int block = 0; block < 3; block++){
			mpos.move(facing = facing.rotateRight());
			placeBlock(world,rand,placeStoneBrick,mpos.x,y+maxY-1,mpos.z);
		}
		
		mpos.move(facing = facing.rotateRight()); // reset facing and move to corner
		
		// bottom decoration
		for(int block = 0; block < 3; block++){
			mpos.move(facing = facing.rotateRight());
			placeBlock(world,rand,placeStoneBrickStairs(facing.opposite(),true),mpos.x,y+2,mpos.z);
		}
		
		mpos.move(facing = facing.rotateRight()).move(facing = facing.rotateRight(),2);
		facing = facing.rotateRight();
		placeLine(world,rand,placeStoneBrickStairs(facing.opposite(),false),mpos.x,y+1,mpos.z,mpos.x+2*facing.getX(),y+1,mpos.z+2*facing.getZ());
		
		mpos.move(facing,2).move(facing = facing.rotateRight());
		placeLine(world,rand,placeStoneBrickStairs(facing.opposite(),false),mpos.x,y+1,mpos.z,mpos.x+facing.getX(),y+1,mpos.z+facing.getZ());
	}
}
