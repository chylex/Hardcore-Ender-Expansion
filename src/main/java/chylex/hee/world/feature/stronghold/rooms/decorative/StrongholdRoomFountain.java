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

public class StrongholdRoomFountain extends StrongholdRoom{
	public StrongholdRoomFountain(){
		super(new Size(13,8,13));
	}
	
	@Override
	public void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		super.generate(inst,world,rand,x,y,z);
		
		int centerX = x+maxX/2, centerZ = z+maxZ/2;
		PosMutable mpos = new PosMutable();
		
		// floor pattern
		placeStairFloorLine(world,rand,Facing4.NORTH_NEGZ,3,x,y,z);
		placeStairFloorLine(world,rand,Facing4.SOUTH_POSZ,3,x,y,z);
		placeStairFloorLine(world,rand,Facing4.WEST_NEGX,2,x,y,z);
		placeStairFloorLine(world,rand,Facing4.EAST_POSX,2,x,y,z);
		
		// stairs around fountain
		for(Facing4 facing:Facing4.list){
			mpos.set(centerX,0,centerZ).move(facing,2);
			placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(facing.opposite(),false)),mpos.x,y+1,mpos.z);
			mpos.move(facing.rotateRight());
			placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(facing.rotateLeft(),false)),mpos.x,y+1,mpos.z);
			mpos.move(facing.rotateLeft(),2);
			placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(facing.rotateRight(),false)),mpos.x,y+1,mpos.z);
			mpos.move(facing.opposite());
			placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(facing.rotateRight(),false)),mpos.x,y+1,mpos.z);
		}
		
		// fountain
		placeLine(world,rand,placeStoneBrick,centerX,y+1,centerZ,centerX,y+3,centerZ);
		placeBlock(world,rand,IBlockPicker.basic(Blocks.flowing_water),centerX,y+4,centerZ);
		
		// ceiling
		placeOutline(world,rand,IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneBrickTop),x+1,y+maxY-1,z+1,x+maxX-1,y+maxY-1,z+maxZ-1,1);
	}
	
	private void placeStairFloorLine(StructureWorld world, Random rand, Facing4 facing, int halfLength, int x, int y, int z){
		IBlockPicker placeStairs = IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(facing.opposite(),false));
		Facing4 perpendicular = facing.perpendicular();
		
		x = x+maxX/2+3*facing.getX();
		z = z+maxZ/2+3*facing.getZ();
		
		placeLine(world,rand,placeStairs,x-halfLength*perpendicular.getX(),y,z-halfLength*perpendicular.getZ(),x+halfLength*perpendicular.getX(),y,z+halfLength*perpendicular.getZ());
	}
}
