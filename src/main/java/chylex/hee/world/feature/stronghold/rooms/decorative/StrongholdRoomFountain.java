package chylex.hee.world.feature.stronghold.rooms.decorative;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.world.feature.stronghold.rooms.StrongholdRoom;
import chylex.hee.world.structure.IBlockPicker;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.structure.util.Size;

public class StrongholdRoomFountain extends StrongholdRoom{
	public StrongholdRoomFountain(){
		super(new Size(13,8,13));
	}
	
	@Override
	public void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		super.generate(inst,world,rand,x,y,z);
		
		final int centerX = x+maxX/2, centerZ = z+maxZ/2;
		PosMutable mpos = new PosMutable();
		
		// floor pattern
		placeStairOutline(world,rand,Blocks.stone_brick_stairs,centerX,y,centerZ,3,true,false);
		
		// stairs around fountain
		for(Facing4 facing:Facing4.list){
			mpos.set(centerX,0,centerZ).move(facing,2);
			placeBlock(world,rand,placeStoneBrickStairs(facing.opposite(),false),mpos.x,y+1,mpos.z);
			mpos.move(facing.rotateRight());
			placeBlock(world,rand,placeStoneBrickStairs(facing.rotateLeft(),false),mpos.x,y+1,mpos.z);
			mpos.move(facing.rotateLeft(),2);
			placeBlock(world,rand,placeStoneBrickStairs(facing.rotateRight(),false),mpos.x,y+1,mpos.z);
			mpos.move(facing.opposite());
			placeBlock(world,rand,placeStoneBrickStairs(facing.rotateRight(),false),mpos.x,y+1,mpos.z);
		}
		
		// fountain
		placeLine(world,rand,placeStoneBrick,centerX,y+1,centerZ,centerX,y+3,centerZ);
		placeBlock(world,rand,IBlockPicker.basic(Blocks.flowing_water),centerX,y+4,centerZ);
		
		// ceiling
		placeOutline(world,rand,IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneBrickTop),x+1,y+maxY-1,z+1,x+maxX-1,y+maxY-1,z+maxZ-1,1);
	}
}
