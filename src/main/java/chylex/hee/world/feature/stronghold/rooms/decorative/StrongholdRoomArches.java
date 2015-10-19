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

public class StrongholdRoomArches extends StrongholdRoom{
	public StrongholdRoomArches(){
		super(new Size(11,7,11));
	}
	
	@Override
	public void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		super.generate(inst,world,rand,x,y,z);
		final int centerX = x+maxX/2, centerZ = z+maxZ/2;
		
		// wall layout
		IBlockPicker placeArchTop = rand.nextInt(6) == 0 ? placeStoneBrickChiseled : placeStoneBrickPlain;
		
		PosMutable mpos = new PosMutable();
		
		for(Facing4 facing:Facing4.list){
			mpos.set(centerX,0,centerZ).move(facing,4).move(facing.rotateRight(),4);
			facing = facing.opposite();
			
			placeLine(world,rand,placeStoneBrick,mpos.x,y+1,mpos.z,mpos.x,y+maxY-1,mpos.z); // corner pillar
			mpos.move(facing,2);
			
			placeLine(world,rand,placeStoneBrickPlain,mpos.x,y+1,mpos.z,mpos.x,y+maxY-1,mpos.z); // left half arch pillar
			mpos.move(facing);
			placeBlock(world,rand,placeStoneBrickStairs(facing.opposite(),true),mpos.x,y+maxY-2,mpos.z); // left half arch
			placeBlock(world,rand,placeStoneBrickPlain,mpos.x,y+maxY-1,mpos.z); // left half arch top
			
			mpos.move(facing);
			placeBlock(world,rand,placeArchTop,mpos.x,y+maxY-1,mpos.z); // arch top
			mpos.move(facing);
			
			placeBlock(world,rand,placeStoneBrickStairs(facing,true),mpos.x,y+maxY-2,mpos.z); // right half arch
			placeBlock(world,rand,placeStoneBrickPlain,mpos.x,y+maxY-1,mpos.z); // right half arch top
			mpos.move(facing);
			placeLine(world,rand,placeStoneBrickPlain,mpos.x,y+1,mpos.z,mpos.x,y+maxY-1,mpos.z); // right half arch pillar
			
			mpos.move(facing.rotateRight());
			placeBlock(world,rand,placeStoneBrickStairs(facing.rotateLeft(),true),mpos.x,y+maxY-1,mpos.z); // connection stair
			mpos.move(facing.rotateRight());
			placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneBrickTop),mpos.x,y+maxY-1,mpos.z); // connection slab
			mpos.move(facing);
			placeBlock(world,rand,placeStoneBrickStairs(facing,true),mpos.x,y+maxY-1,mpos.z); // connection stair
		}
		
		// cobble walls
		for(int cornerX = 0; cornerX < 2; cornerX++){
			for(int cornerZ = 0; cornerZ < 2; cornerZ++){
				placeBlock(world,rand,placeStoneBrickWall,x+3+4*cornerX,y+1,z+3+4*cornerZ);
				placeBlock(world,rand,IBlockPicker.basic(Blocks.torch,Meta.torchGround),x+3+4*cornerX,y+2,z+3+4*cornerZ);
			}
		}
	}
}
