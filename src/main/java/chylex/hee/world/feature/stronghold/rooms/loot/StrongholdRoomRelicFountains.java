package chylex.hee.world.feature.stronghold.rooms.loot;
import java.util.Arrays;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.init.BlockList;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.world.feature.stronghold.rooms.StrongholdRoom;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.structure.util.IBlockPicker;
import chylex.hee.world.util.Size;

public class StrongholdRoomRelicFountains extends StrongholdRoom{
	public static StrongholdRoomRelicFountains[] generateRelicRooms(){
		return Arrays.stream(Facing4.list).map(facing -> new StrongholdRoomRelicFountains(facing)).toArray(StrongholdRoomRelicFountains[]::new);
	}
	
	private final Facing4 entranceFrom;
	
	public StrongholdRoomRelicFountains(Facing4 entranceFrom){
		super(new Size(entranceFrom.getX() != 0 ? 19 : 13,12,entranceFrom.getZ() != 0 ? 19 : 13),null);
		this.entranceFrom = entranceFrom;
		
		if (entranceFrom == Facing4.SOUTH_POSZ)addConnection(Facing4.NORTH_NEGZ,maxX/2,1,0,fromRoom);
		else if (entranceFrom == Facing4.NORTH_NEGZ)addConnection(Facing4.SOUTH_POSZ,maxX/2,1,maxZ,fromRoom);
		else if (entranceFrom == Facing4.WEST_NEGX)addConnection(Facing4.EAST_POSX,maxX,1,maxZ/2,fromRoom);
		else if (entranceFrom == Facing4.EAST_POSX)addConnection(Facing4.WEST_NEGX,0,1,maxZ/2,fromRoom);
	}
	
	@Override
	public void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		super.generate(inst,world,rand,x,y,z);

		Facing4 left = entranceFrom.rotateLeft(), right = entranceFrom.rotateRight();
		Connection connection = connections.get(0);
		PosMutable mpos = new PosMutable();
		Pos point1, point2;
		
		// water
		mpos.set(x+connection.offsetX,0,z+connection.offsetZ).move(entranceFrom);
		
		for(int side = 0; side < 2; side++){
			Facing4 sideFacing = side == 0 ? left : right;
			point1 = Pos.at(mpos).offset(sideFacing,2);
			point2 = Pos.at(point1).offset(sideFacing,3).offset(entranceFrom,16);
			placeCube(world,rand,IBlockPicker.basic(Blocks.flowing_water),point1.getX(),y+1,point1.getZ(),point2.getX(),y+1,point2.getZ());
		}
		
		// road
		point1 = Pos.at(mpos).offset(left);
		point2 = Pos.at(mpos).offset(right).offset(entranceFrom,16);
		placeCube(world,rand,placeStoneBrick,point1.getX(),y+1,point1.getZ(),point2.getX(),y+1,point2.getZ());
		
		// water streams
		for(int side = 0; side < 2; side++){
			Facing4 sideFacing = side == 0 ? left : right;
			
			for(int stream = 0; stream < 3; stream++){
				mpos.set(x+connection.offsetX,0,z+connection.offsetZ).move(sideFacing,5).move(entranceFrom,4+5*stream);
				
				placeBlock(world,rand,placeStoneBrickStairs(sideFacing,true),mpos.x,y+5,mpos.z); // bottom lower stair
				placeBlock(world,rand,placeStoneBrickStairs(sideFacing,true),mpos.x,y+6,mpos.z); // bottom upper stair
				placeBlock(world,rand,placeStoneBrickStairs(sideFacing,false),mpos.x-sideFacing.getX(),y+6,mpos.z-sideFacing.getZ()); // front stair
				placeBlock(world,rand,IBlockPicker.basic(Blocks.flowing_water),mpos.x,y+7,mpos.z); // water
				
				// side stairs
				mpos.move(entranceFrom.opposite());
				placeBlock(world,rand,placeStoneBrickStairs(entranceFrom,true),mpos.x,y+7,mpos.z);
				placeBlock(world,rand,placeStoneBrickStairs(sideFacing,true),mpos.x-sideFacing.getX(),y+7,mpos.z-sideFacing.getZ());
				
				mpos.move(entranceFrom);
				placeBlock(world,rand,IBlockPicker.basic(BlockList.ethereal_lantern),mpos.x+sideFacing.getX(),y+7,mpos.z+sideFacing.getZ());
				
				mpos.move(entranceFrom);
				placeBlock(world,rand,placeStoneBrickStairs(entranceFrom.opposite(),true),mpos.x,y+7,mpos.z);
				placeBlock(world,rand,placeStoneBrickStairs(sideFacing,true),mpos.x-sideFacing.getX(),y+7,mpos.z-sideFacing.getZ());
			}
		}
		
		// wall outlines
		placeOutline(world,rand,IBlockPicker.basic(BlockList.stone_brick_wall),x+1,y+maxY-1,z+1,x+maxX-1,y+maxY-1,z+maxZ-1,1);
		
		mpos.set(x+connection.offsetX,0,z+connection.offsetZ).move(entranceFrom);
		placeBlock(world,rand,IBlockPicker.basic(BlockList.stone_brick_wall),mpos.x,y+5,mpos.z);
		
		for(int side = 0; side < 2; side++){
			Facing4 sideFacing = side == 0 ? left : right;
			mpos.set(x+connection.offsetX,0,z+connection.offsetZ).move(entranceFrom).move(sideFacing);
			placeBlock(world,rand,IBlockPicker.basic(BlockList.stone_brick_wall),mpos.x,y+5,mpos.z);
			mpos.move(sideFacing);
			placeLine(world,rand,IBlockPicker.basic(BlockList.stone_brick_wall),mpos.x,y+5,mpos.z,mpos.x,y+2,mpos.z);
			mpos.move(sideFacing);
			placeLine(world,rand,IBlockPicker.basic(BlockList.stone_brick_wall),mpos.x,y+2,mpos.z,mpos.x+sideFacing.getX(),y+2,mpos.z+sideFacing.getZ());
			mpos.move(sideFacing,2);
			placeLine(world,rand,IBlockPicker.basic(BlockList.stone_brick_wall),mpos.x,y+2,mpos.z,mpos.x+16*entranceFrom.getX(),y+2,mpos.z+16*entranceFrom.getZ());
			mpos.move(entranceFrom,16).move(sideFacing.opposite());
			placeLine(world,rand,IBlockPicker.basic(BlockList.stone_brick_wall),mpos.x,y+2,mpos.z,mpos.x-2*sideFacing.getX(),y+2,mpos.z-2*sideFacing.getZ());
		}
		
		// chest
		mpos.set(x+connection.offsetX,0,z+connection.offsetZ).move(entranceFrom,17);
		placeBlock(world,rand,placeStoneBrickPlain,mpos.x,y+2,mpos.z);
		placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(entranceFrom,true)),mpos.x+left.getX(),y+2,mpos.z+left.getZ());
		placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(entranceFrom,true)),mpos.x+right.getX(),y+2,mpos.z+right.getZ());
		
		placeBlock(world,rand,IBlockPicker.basic(BlockList.loot_chest),mpos.x,y+3,mpos.z);
		world.setTileEntity(mpos.x,y+3,mpos.z,Meta.generateChest(entranceFrom.opposite(),null)); // TODO relic
	}
}
