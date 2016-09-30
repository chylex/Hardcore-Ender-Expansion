package chylex.hee.world.feature.stronghold.rooms.loot;
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

public class StrongholdRoomChestStraight extends StrongholdRoom{
	public static StrongholdRoomChestStraight[] generateChestRooms(){
		return new StrongholdRoomChestStraight[]{
			new StrongholdRoomChestStraight(true),
			new StrongholdRoomChestStraight(false)
		};
	}
	
	private final boolean dirX;
	
	public StrongholdRoomChestStraight(boolean dirX){
		super(new Size(11, 11, 11), new Facing4[]{ dirX ? Facing4.EAST_POSX : Facing4.SOUTH_POSZ, dirX ? Facing4.WEST_NEGX : Facing4.NORTH_NEGZ });
		this.dirX = dirX;
	}
	
	@Override
	public void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		super.generate(inst, world, rand, x, y, z);
		int centerX = x+maxX/2, centerZ = z+maxZ/2;
		
		// corners and bottom floor stairs
		placeStairOutline(world, rand, Blocks.stone_brick_stairs, centerX, y+1, centerZ, 4, false, false);
		placeStairOutline(world, rand, Blocks.stone_brick_stairs, centerX, y+4, centerZ, 4, false, true);
		
		for(int cornerX = 0; cornerX < 2; cornerX++){
			for(int cornerZ = 0; cornerZ < 2; cornerZ++){
				placeLine(world, rand, placeStoneBrick, x+1+8*cornerX, y+1, z+1+8*cornerZ, x+1+8*cornerX, y+4, z+1+8*cornerZ);
			}
		}
		
		for(Connection connection:connections){
			PosMutable mpos = new PosMutable(x+connection.offsetX, 0, z+connection.offsetZ).move(connection.facing.opposite());
			Facing4 perpendicular = connection.facing.perpendicular();
			placeLine(world, rand, placeAir, mpos.x-perpendicular.getX(), y+1, mpos.z-perpendicular.getZ(), mpos.x+perpendicular.getX(), y+1, mpos.z+perpendicular.getZ());
		}
		
		// floor separation
		placeCube(world, rand, IBlockPicker.basic(Blocks.stone_slab, Meta.slabStoneBrickTop), x+2, y+4, z+2, x+maxX-2, y+4, z+maxZ-2);
		
		// ladders
		Facing4 vineFacing = dirX ? Facing4.NORTH_NEGZ : Facing4.WEST_NEGX;
		
		for(int vineSide = 0; vineSide < 2; vineSide++){
			for(int vineY = 0; vineY < 4; vineY++){
				placeBlock(world, rand, placeStoneBrick, centerX+4*vineFacing.getX(), y+1+vineY, centerZ+4*vineFacing.getZ());
				placeBlock(world, rand, IBlockPicker.basic(BlockList.dry_vine, Meta.getVine(vineFacing)), centerX+3*vineFacing.getX(), y+1+vineY, centerZ+3*vineFacing.getZ());
			}
			
			if (vineSide == 0)vineFacing = vineFacing.opposite();
		}
		
		// top floor chest
		placeOutline(world, rand, IBlockPicker.basic(Blocks.stone_slab, Meta.slabStoneSmoothBottom), centerX-2, y+5, centerZ-2, centerX+2, y+5, centerZ+2, 1);
		placeCube(world, rand, placeStoneBrick, centerX-1, y+5, centerZ-1, centerX+1, y+5, centerZ+1);
		
		placeBlock(world, rand, placeChest, centerX, y+6, centerZ);
		world.setTileEntity(centerX, y+6, centerZ, Meta.generateChest(rand.nextBoolean() ? vineFacing : vineFacing.opposite(), generateLootGeneral));
		
		// top floor lights
		for(int cornerX = 0; cornerX < 2; cornerX++){
			for(int cornerZ = 0; cornerZ < 2; cornerZ++){
				placeBlock(world, rand, placeStoneBrickWall, x+2+6*cornerX, y+maxY-1, z+2+6*cornerZ);
				placeBlock(world, rand, placeEtherealLantern, x+2+6*cornerX, y+maxY-2, z+2+6*cornerZ);
			}
		}
	}
}
