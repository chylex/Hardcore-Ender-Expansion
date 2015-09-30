package chylex.hee.world.feature.stronghold.rooms.loot;
import java.util.Arrays;
import java.util.Random;
import net.minecraft.init.Blocks;
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
		super(new Size(entranceFrom.getX() != 0 ? 23 : 13,14,entranceFrom.getZ() != 0 ? 23 : 13),null);
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
			point2 = Pos.at(point1).offset(sideFacing,3).offset(entranceFrom,21);
			placeCube(world,rand,IBlockPicker.basic(Blocks.flowing_water),point1.getX(),y+1,point1.getZ(),point2.getX(),y+1,point2.getZ());
		}
		
		// road
		point1 = Pos.at(mpos).offset(left);
		point2 = Pos.at(mpos).offset(right).offset(entranceFrom,21);
		placeCube(world,rand,placeStoneBrick,point1.getX(),y+1,point1.getZ(),point2.getX(),y+1,point2.getZ());
		
		// water streams
		for(int side = 0; side < 2; side++){
			Facing4 sideFacing = side == 0 ? left : right;
			
			for(int stream = 0; stream < 3; stream++){
				mpos.set(x+connection.offsetX,0,z+connection.offsetZ).move(sideFacing,5).move(entranceFrom,4+7*stream);
				
				placeBlock(world,rand,placeStoneBrickStairs(sideFacing,true),mpos.x,y+7,mpos.z); // bottom lower stair
				placeBlock(world,rand,placeStoneBrickStairs(sideFacing,true),mpos.x,y+8,mpos.z); // bottom upper stair
				placeBlock(world,rand,placeStoneBrickStairs(sideFacing,false),mpos.x-sideFacing.getX(),y+8,mpos.z-sideFacing.getZ()); // front stair
				placeBlock(world,rand,IBlockPicker.basic(Blocks.flowing_water),mpos.x,y+9,mpos.z); // water
				
				// side stairs
				mpos.move(entranceFrom.opposite());
				placeBlock(world,rand,placeStoneBrickStairs(entranceFrom,true),mpos.x,y+9,mpos.z);
				placeBlock(world,rand,placeStoneBrickStairs(sideFacing,true),mpos.x-sideFacing.getX(),y+9,mpos.z-sideFacing.getZ());
				
				mpos.move(entranceFrom,2);
				placeBlock(world,rand,placeStoneBrickStairs(entranceFrom.opposite(),true),mpos.x,y+9,mpos.z);
				placeBlock(world,rand,placeStoneBrickStairs(sideFacing,true),mpos.x-sideFacing.getX(),y+9,mpos.z-sideFacing.getZ());
			}
		}
		
		// chest
		// TODO
	}
}
