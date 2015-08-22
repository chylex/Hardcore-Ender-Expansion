package chylex.hee.world.feature.stronghold.rooms.traps;
import java.util.Arrays;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.abstractions.Meta.Skull;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.world.feature.stronghold.rooms.StrongholdRoom;
import chylex.hee.world.structure.IBlockPicker;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.structure.util.BoundingBox;
import chylex.hee.world.structure.util.Facing4;
import chylex.hee.world.structure.util.Size;

public class StrongholdRoomPrisonTrap extends StrongholdRoom{
	public static StrongholdRoomPrisonTrap[] generatePrisons(){
		return Arrays.stream(Facing4.list).map(facing -> new StrongholdRoomPrisonTrap(facing)).toArray(StrongholdRoomPrisonTrap[]::new);
	}
	
	private final Facing4 facing;
	
	public StrongholdRoomPrisonTrap(Facing4 facing){
		super(new Size(facing.getX() != 0 ? 10 : 11,5,facing.getZ() != 0 ? 10 : 11),null);
		
		if (facing.getZ() != 0){
			addConnection(Facing4.NORTH_NEGZ,5-3*facing.getZ(),0,0,fromRoom);
			addConnection(Facing4.SOUTH_POSZ,5-3*facing.getZ(),0,9,fromRoom);
		}
		else{
			addConnection(Facing4.WEST_NEGX,0,0,5+3*facing.getX(),fromRoom);
			addConnection(Facing4.EAST_POSX,9,0,5+3*facing.getX(),fromRoom);
		}
		
		this.facing = facing;
	}

	@Override
	public void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		super.generate(inst,world,rand,x,y,z);

		PosMutable posStart = new PosMutable(), posEnd = new PosMutable();
		Connection connection = connections.stream().filter(testConnection -> testConnection.facing.opposite() == this.facing).findFirst().get();
		
		// prison wall
		posStart.set(x+connection.offsetX,0,z+connection.offsetZ).move(facing).move(facing.rotateLeft(),2);
		posEnd.set(posStart).move(facing,7);
		placeLine(world,rand,placeStoneBrick,posStart.x,y+4,posStart.z,posEnd.x,y+4,posEnd.z); // top line
		placeLine(world,rand,placeStoneBrick,posStart.x,y+1,posStart.z,posStart.x,y+3,posStart.z); // first part of arch
		
		posStart.move(facing);
		placeBlock(world,rand,IBlockPicker.basic(Blocks.iron_door,Meta.getDoor(facing.rotateLeft(),false)),posStart.x,y+1,posStart.z); // bottom door
		placeBlock(world,rand,IBlockPicker.basic(Blocks.iron_door,Meta.getDoor(facing.rotateLeft(),true)),posStart.x,y+2,posStart.z); // top door
		placeBlock(world,rand,placeStoneBrick,posStart.x,y+3,posStart.z); // top part of arch
		
		posStart.move(facing);
		placeLine(world,rand,placeStoneBrick,posStart.x,y+1,posStart.z,posStart.x,y+3,posStart.z); // second part of arch
		
		posStart.move(facing.rotateRight());
		placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_button,Meta.getButton(facing.rotateLeft())),posStart.x,y+2,posStart.z); // door button
		posStart.move(facing.rotateLeft());
		
		posStart.move(facing);
		posEnd.set(posStart).move(facing,4);
		placeLine(world,rand,IBlockPicker.basic(Blocks.iron_bars),posStart.x,y+1,posStart.z,posEnd.x,y+3,posEnd.z); // prison bars
		
		// redstone blood
		posStart.set(x+connection.offsetX,0,z+connection.offsetZ).move(facing).move(facing.rotateLeft(),3);
		posEnd.set(posStart).move(facing,7).move(facing.rotateLeft(),4);
		
		BoundingBox prison = new BoundingBox(posStart,posEnd);
		IBlockPicker placeRedstone = IBlockPicker.basic(Blocks.redstone_wire);
		
		for(int blood = 4+rand.nextInt(5+rand.nextInt(4)); blood > 0; blood--){
			placeBlock(world,rand,placeRedstone,prison.x1+rand.nextInt(1+prison.x2-prison.x1),y+1,prison.z1+rand.nextInt(1+prison.z2-prison.z1));
		}
		
		// skull
		boolean skullCorner = rand.nextBoolean();
		posStart.move(facing.rotateLeft(),4).move(facing,skullCorner ? 0 : 7);
		
		placeBlock(world,rand,IBlockPicker.basic(Blocks.skull,Meta.skullGround),posStart.x,y+1,posStart.z);
		world.setTileEntity(posStart.x,y+1,posStart.z,Meta.generateSkullGround(Skull.SKELETON,posStart,posStart.offset(facing.rotateRight()).offset(skullCorner ? facing : facing.opposite())));
		
		// spawner
		// TODO
	}
}
