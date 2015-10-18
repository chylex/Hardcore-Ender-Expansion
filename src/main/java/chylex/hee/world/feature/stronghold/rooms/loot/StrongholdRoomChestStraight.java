package chylex.hee.world.feature.stronghold.rooms.loot;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.init.BlockList;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.world.feature.stronghold.rooms.StrongholdRoom;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.structure.util.IBlockPicker;
import chylex.hee.world.util.Size;

public class StrongholdRoomChestStraight extends StrongholdRoom{
	public static StrongholdRoomChestStraight[] generateChestRooms(){
		return new StrongholdRoomChestStraight[]{
			new StrongholdRoomChestStraight(true),
			new StrongholdRoomChestStraight(false)
		};
	}
	
	private final boolean dirX;
	
	public StrongholdRoomChestStraight(boolean dirX){
		super(new Size(11,11,11),new Facing4[]{ dirX ? Facing4.EAST_POSX : Facing4.SOUTH_POSZ, dirX ? Facing4.WEST_NEGX : Facing4.NORTH_NEGZ });
		this.dirX = dirX;
	}
	
	@Override
	public void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		super.generate(inst,world,rand,x,y,z);
		int centerX = x+maxX/2, centerZ = z+maxZ/2;
		
		// corners and bottom floor stairs
		placeStairOutline(world,rand,Blocks.stone_brick_stairs,centerX,y+1,centerZ,4,false,false);
		placeStairOutline(world,rand,Blocks.stone_brick_stairs,centerX,y+4,centerZ,4,false,true);
		
		for(int cornerX = 0; cornerX < 2; cornerX++){
			for(int cornerZ = 0; cornerZ < 2; cornerZ++){
				placeLine(world,rand,placeStoneBrick,x+1+8*cornerX,y+1,z+1+8*cornerZ,x+1+8*cornerX,y+4,z+1+8*cornerZ);
			}
		}
		
		// floor separation
		placeCube(world,rand,IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneBrickTop),x+2,y+4,z+2,x+maxX-2,y+4,z+maxZ-2);
		
		// ladders
		Facing4 ladderFacing = dirX ? Facing4.NORTH_NEGZ : Facing4.WEST_NEGX;
		
		for(int ladderSide = 0; ladderSide < 2; ladderSide++){
			for(int ladderY = 0; ladderY < 4; ladderY++){
				placeBlock(world,rand,placeStoneBrick,centerX+4*ladderFacing.getX(),y+1+ladderY,centerZ+4*ladderFacing.getZ());
				placeBlock(world,rand,IBlockPicker.basic(Blocks.ladder,Meta.getLadder(ladderFacing)),centerX+3*ladderFacing.getX(),y+1+ladderY,centerZ+3*ladderFacing.getZ());
			}
			
			if (ladderSide == 0)ladderFacing = ladderFacing.opposite();
		}
		
		// top floor chest
		placeOutline(world,rand,IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneSmoothBottom),centerX-2,y+5,centerZ-2,centerX+2,y+5,centerZ+2,1);
		placeCube(world,rand,placeStoneBrick,centerX-1,y+5,centerZ-1,centerX+1,y+5,centerZ+1);
		
		placeBlock(world,rand,IBlockPicker.basic(Blocks.chest),centerX,y+6,centerZ);
		world.setTileEntity(centerX,y+6,centerZ,Meta.generateChest(rand.nextBoolean() ? ladderFacing : ladderFacing.opposite(),generateLootGeneral));
		
		// top floor lights
		for(int cornerX = 0; cornerX < 2; cornerX++){
			for(int cornerZ = 0; cornerZ < 2; cornerZ++){
				placeBlock(world,rand,IBlockPicker.basic(BlockList.stone_brick_wall),x+2+6*cornerX,y+maxY-1,z+2+6*cornerZ);
				placeBlock(world,rand,IBlockPicker.basic(BlockList.ethereal_lantern),x+2+6*cornerX,y+maxY-2,z+2+6*cornerZ);
			}
		}
	}
}
