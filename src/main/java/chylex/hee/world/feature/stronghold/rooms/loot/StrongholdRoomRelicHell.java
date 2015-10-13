package chylex.hee.world.feature.stronghold.rooms.loot;
import java.util.Arrays;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.abstractions.Meta.BlockColor;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.world.feature.stronghold.rooms.StrongholdRoom;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.structure.util.IBlockPicker;
import chylex.hee.world.util.Size;

public class StrongholdRoomRelicHell extends StrongholdRoom{
	public static StrongholdRoomRelicHell[] generateRelicRooms(){
		return Arrays.stream(Facing4.list).map(facing -> new StrongholdRoomRelicHell(facing)).toArray(StrongholdRoomRelicHell[]::new);
	}
	
	private final Facing4 entranceFrom;
	
	public StrongholdRoomRelicHell(Facing4 entranceFrom){
		super(new Size(entranceFrom.getX() != 0 ? 25 : 11,7,entranceFrom.getZ() != 0 ? 25 : 11),null);
		this.entranceFrom = entranceFrom;
		
		if (entranceFrom == Facing4.SOUTH_POSZ)addConnection(Facing4.NORTH_NEGZ,maxX/2,0,0,fromRoom);
		else if (entranceFrom == Facing4.NORTH_NEGZ)addConnection(Facing4.SOUTH_POSZ,maxX/2,0,maxZ,fromRoom);
		else if (entranceFrom == Facing4.WEST_NEGX)addConnection(Facing4.EAST_POSX,maxX,0,maxZ/2,fromRoom);
		else if (entranceFrom == Facing4.EAST_POSX)addConnection(Facing4.WEST_NEGX,0,0,maxZ/2,fromRoom);
	}
	
	@Override
	public void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		super.generate(inst,world,rand,x,y,z);
		
		Facing4 left = entranceFrom.rotateLeft(), right = entranceFrom.rotateRight();
		Connection connection = connections.get(0);
		PosMutable mpos = new PosMutable();
		
		// floor pattern
		mpos.set(x+connection.offsetX,0,z+connection.offsetZ).move(entranceFrom);
		placeLine(world,rand,IBlockPicker.basic(Blocks.stonebrick,Meta.stoneBrickChiseled),mpos.x,y,mpos.z,mpos.x+14*entranceFrom.getX(),y,mpos.z+14*entranceFrom.getZ());
		
		// side walls
		for(int side = 0; side < 2; side++){
			final Facing4 facing = side == 0 ? left : right;
			
			for(int level = 0; level < 2; level++){
				mpos.set(x+connection.offsetX,0,z+connection.offsetZ).move(entranceFrom).move(facing,2);
				int stairY = level == 0 ? y+1 : y+5;
				
				for(int part = 0; part < 2; part++){
					placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(facing,level == 1)),mpos.x,stairY,mpos.z);
					mpos.move(facing);
					placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(entranceFrom.opposite(),level == 1)),mpos.x,stairY,mpos.z);
					mpos.move(facing);
					placeLine(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(facing,level == 1)),mpos.x,stairY,mpos.z,mpos.x+4*entranceFrom.getX(),stairY,mpos.z+4*entranceFrom.getZ());
					mpos.move(entranceFrom,4).move(facing.opposite());
					placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(entranceFrom,level == 1)),mpos.x,stairY,mpos.z);
					mpos.move(facing.opposite());
					placeLine(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(facing,level == 1)),mpos.x,stairY,mpos.z,mpos.x+entranceFrom.getX(),stairY,mpos.z+entranceFrom.getZ());
					mpos.move(entranceFrom,2);
				}
				
				placeLine(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(entranceFrom.opposite(),level == 1)),mpos.x,stairY,mpos.z,mpos.x+2*facing.getX(),stairY,mpos.z+2*facing.getZ());
			}
			
			for(int fountain = 0; fountain < 2; fountain++){
				mpos.set(x+connection.offsetX,0,z+connection.offsetZ).move(entranceFrom,6*(1+fountain)).move(facing,3);
				placeBlock(world,rand,IBlockPicker.basic(Blocks.flowing_lava),mpos.x,y+5,mpos.z);
				mpos.move(facing);
				placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(facing,false)),mpos.x,y+2,mpos.z);
				placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(facing,true)),mpos.x,y+4,mpos.z);
			}
		}
		
		// relic area
		for(int level = 0; level < 2; level++){
			int py = level == 0 ? y+1 : y+5;
			IBlockPicker placeSlab = IBlockPicker.basic(Blocks.stone_slab,level == 0 ? Meta.slabStoneSmoothBottom : Meta.slabStoneSmoothTop);

			mpos.set(x+connection.offsetX,0,z+connection.offsetZ).move(entranceFrom,16);
			placeBlock(world,rand,placeSlab,mpos.x,py,mpos.z);
			mpos.move(entranceFrom);
			placeLine(world,rand,placeStoneBrickPlain,mpos.x,py,mpos.z,mpos.x+6*entranceFrom.getX(),py,mpos.z+6*entranceFrom.getZ());
		
			for(int side = 0; side < 2; side++){
				final Facing4 facing = side == 0 ? left : right;
				
				mpos.set(x+connection.offsetX,0,z+connection.offsetZ).move(entranceFrom,16).move(facing);
				placeLine(world,rand,placeSlab,mpos.x,py,mpos.z,mpos.x+2*entranceFrom.getX(),py,mpos.z+2*entranceFrom.getZ());
				mpos.move(facing).move(entranceFrom,2);
				placeBlock(world,rand,placeSlab,mpos.x,py,mpos.z);
				mpos.move(facing);
				placeLine(world,rand,placeSlab,mpos.x,py,mpos.z,mpos.x+4*entranceFrom.getX(),py,mpos.z+4*entranceFrom.getZ());
				
				mpos.set(x+connection.offsetX,0,z+connection.offsetZ).move(entranceFrom,19).move(facing);
				placeBlock(world,rand,placeStoneBrickPlain,mpos.x,py,mpos.z);
				placeLine(world,rand,IBlockPicker.basic(Blocks.obsidian),mpos.x+entranceFrom.getX(),py,mpos.z+entranceFrom.getZ(),mpos.x+3*entranceFrom.getX(),py,mpos.z+3*entranceFrom.getZ());
				mpos.move(facing);
				placeLine(world,rand,placeStoneBrickPlain,mpos.x,py,mpos.z,mpos.x+3*entranceFrom.getX(),py,mpos.z+3*entranceFrom.getZ());
				
				mpos.set(x+connection.offsetX,0,z+connection.offsetZ).move(entranceFrom,23).move(facing);
				placeLine(world,rand,IBlockPicker.basic(Blocks.obsidian),mpos.x,y+2,mpos.z,mpos.x,y+5,mpos.z);
				mpos.move(facing);
				placeLine(world,rand,placeStoneBrickPlain,mpos.x,y+2,mpos.z,mpos.x,y+5,mpos.z);
				mpos.move(facing);
				placeBlock(world,rand,IBlockPicker.basic(Blocks.flowing_lava),mpos.x,y+5,mpos.z);
				mpos.move(facing);
				placeLine(world,rand,placeStoneBrickPlain,mpos.x,y+1,mpos.z,mpos.x,y+5,mpos.z);
			}
		}
		
		// relic chest and glass for lighting
		mpos.set(x+connection.offsetX,0,z+connection.offsetZ).move(entranceFrom,23);
		placeBlock(world,rand,IBlockPicker.basic(Blocks.stained_glass,Meta.getColor(BlockColor.GRAY)),mpos.x,y+1,mpos.z);
		placeBlock(world,rand,IBlockPicker.basic(Blocks.stained_glass,Meta.getColor(BlockColor.GRAY)),mpos.x+left.getX(),y+1,mpos.z+left.getZ());
		placeBlock(world,rand,IBlockPicker.basic(Blocks.stained_glass,Meta.getColor(BlockColor.GRAY)),mpos.x+right.getX(),y+1,mpos.z+right.getZ());
		placeBlock(world,rand,IBlockPicker.basic(Blocks.chest),mpos.x,y+2,mpos.z);
		world.setTileEntity(mpos.x,y+2,mpos.z,Meta.generateChest(entranceFrom.opposite(),null)); // TODO loot
	}
}
