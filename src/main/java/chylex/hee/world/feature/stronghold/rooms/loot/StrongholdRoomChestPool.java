package chylex.hee.world.feature.stronghold.rooms.loot;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.init.BlockList;
import chylex.hee.system.abstractions.BlockInfo;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.world.feature.stronghold.rooms.StrongholdRoom;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.structure.util.IBlockPicker;
import chylex.hee.world.util.Size;

public class StrongholdRoomChestPool extends StrongholdRoom{
	public static StrongholdRoomChestPool[] generateRooms(){
		return new StrongholdRoomChestPool[]{
			new StrongholdRoomChestPool(0),
			new StrongholdRoomChestPool(1),
			new StrongholdRoomChestPool(2)
		};
	}
	
	private final byte type; // 0 = above floor, 1 = in floor, 2 = empty in floor
	
	public StrongholdRoomChestPool(int type){
		super(type > 0 ? new Size(11,7,11) : new Size(13,6,13),type > 0 ? null : Facing4.list);
		
		if (type > 0){
			addConnection(Facing4.NORTH_NEGZ,maxX/2,1,0,fromRoom);
			addConnection(Facing4.SOUTH_POSZ,maxX/2,1,maxZ,fromRoom);
			addConnection(Facing4.EAST_POSX,maxX,1,maxZ/2,fromRoom);
			addConnection(Facing4.WEST_NEGX,0,1,maxZ/2,fromRoom);
		}
		
		this.type = (byte)type;
	}
	
	@Override
	public void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		super.generate(inst,world,rand,x,y,z);
		
		final int centerX = x+maxX/2, centerZ = z+maxZ/2;
		
		int cornerOff = type > 0 ? 3 : 4;
		
		// basic layout
		if (type == 0){
			placeOutline(world,rand,placeStoneBrick,centerX-2,y+1,centerZ-2,centerX+2,y+1,centerZ+2,1);
			placeBlock(world,rand,placeStoneBrick,centerX,y+1,centerZ);
		}
		else placeCube(world,rand,placeStoneBrick,x+1,y+1,z+1,x+maxX-1,y+1,z+maxZ-1);
		
		for(int cornerX = 0; cornerX < 2; cornerX++){
			for(int cornerZ = 0; cornerZ < 2; cornerZ++){
				placeBlock(world,rand,placeStoneBrickWall,x+cornerOff+4*cornerX,y+2,z+cornerOff+4*cornerZ);
				world.setAttentionWhore(x+cornerOff+4*cornerX,y+3,z+cornerOff+4*cornerZ,new BlockInfo(Blocks.torch,Meta.torchGround));
			}
		}
		
		if (type == 2){
			for(Facing4 facing:Facing4.list){
				IBlockPicker placeStair = IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(facing,false));
				placeLine(world,rand,placeStair,centerX+2*facing.getX()+facing.rotateLeft().getX(),y+1,centerZ+2*facing.getZ()+facing.rotateLeft().getZ(),centerX+2*facing.getX()+facing.rotateRight().getX(),y+1,centerZ+2*facing.getZ()+facing.rotateRight().getZ());
			}
			
			placeOutline(world,rand,placeAir,centerX-1,y+1,centerZ-1,centerX+1,y+1,centerZ+1,1);
		}
		else placeOutline(world,rand,placeWater,centerX-1,y+1,centerZ-1,centerX+1,y+1,centerZ+1,1);
		
		// chest
		placeBlock(world,rand,placeChest,centerX,y+2,centerZ);
		world.setTileEntity(centerX,y+2,centerZ,Meta.generateChest(Facing4.list[rand.nextInt(Facing4.list.length)],generateLootGeneral));
	}
}
