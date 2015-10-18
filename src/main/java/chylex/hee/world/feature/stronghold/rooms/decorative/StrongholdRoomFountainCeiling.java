package chylex.hee.world.feature.stronghold.rooms.decorative;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.init.BlockList;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.world.feature.stronghold.rooms.StrongholdRoom;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.structure.util.IBlockPicker;
import chylex.hee.world.util.Size;

public class StrongholdRoomFountainCeiling extends StrongholdRoom{
	public StrongholdRoomFountainCeiling(){
		super(new Size(9,7,9));
	}
	
	@Override
	public void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		super.generate(inst,world,rand,x,y,z);
		
		int centerX = x+maxX/2, centerZ = z+maxZ/2;
		PosMutable mpos = new PosMutable();
		Facing4 facing;
		
		// floor outline
		placeOutline(world,rand,IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneSmoothBottom),centerX-1,y+1,centerZ-1,centerX+1,y+1,centerZ+1,1);
		
		// ceiling
		placeOutline(world,rand,IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneSmoothTop),centerX-2,y+maxY-1,centerZ-2,centerX+2,y+maxY-1,centerZ+2,1);
		placeCube(world,rand,placeStoneBrickPlain,centerX-1,y+maxY-1,centerZ-1,centerX+1,y+maxY-1,centerZ+1);
		placeBlock(world,rand,IBlockPicker.basic(Blocks.flowing_water),centerX,y+maxY-2,centerZ);
		placeStairOutline(world,rand,Blocks.stone_brick_stairs,centerX,y+maxY-2,centerZ,1,true,true);
		
		// corner walls
		facing = rand.nextBoolean() ? Facing4.NORTH_NEGZ : Facing4.WEST_NEGX;
		mpos.move(centerX,0,centerZ).move(facing,3).move(facing = facing.rotateRight(),3);
		
		for(int cycle = 0; cycle < 2; cycle++){
			placeBlock(world,rand,IBlockPicker.basic(BlockList.stone_brick_wall),mpos.x,y+3,mpos.z);
			placeBlock(world,rand,IBlockPicker.basic(Blocks.torch,Meta.torchGround),mpos.x,y+4,mpos.z);
			if (cycle == 0)mpos.move(facing.rotateRight(),6).move(facing.rotateRight(2),6);
		}
	}
}
