package chylex.hee.world.feature.stronghold.rooms.general;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.world.feature.stronghold.rooms.StrongholdRoom;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.structure.util.IBlockPicker;
import chylex.hee.world.util.Size;

public class StrongholdRoomLibrary extends StrongholdRoom{
	public static StrongholdRoomLibrary[] generateLibraries(){
		return new StrongholdRoomLibrary[]{
			new StrongholdRoomLibrary(true),
			new StrongholdRoomLibrary(false)
		};
	}
	
	private static final int[] levels = new int[]{ 0, 6, 11 };
	private static final IBlockPicker placeBookshelf = IBlockPicker.basic(Blocks.bookshelf);
	
	public final boolean rotated;
	
	public StrongholdRoomLibrary(boolean rotated){
		super(new Size(19,17,19),null);
		this.rotated = rotated;
		
		Facing4 connectionFacing = rotated ? Facing4.NORTH_NEGZ : Facing4.WEST_NEGX;
		
		for(int level = 0; level < 3; level++){
			connectionFacing = connectionFacing.rotateRight();
			addConnection(connectionFacing,levels[level]);
			addConnection(connectionFacing.opposite(),levels[level]);
		}
	}
	
	private void addConnection(Facing4 facing, int y){
		switch(facing){
			case NORTH_NEGZ: addConnection(Facing4.NORTH_NEGZ,maxX/2,y,0,fromRoom); break;
			case SOUTH_POSZ: addConnection(Facing4.SOUTH_POSZ,maxX/2,y,maxZ,fromRoom); break;
			case EAST_POSX: addConnection(Facing4.EAST_POSX,maxX,y,maxZ/2,fromRoom); break;
			case WEST_NEGX: addConnection(Facing4.WEST_NEGX,0,y,maxZ/2,fromRoom); break;
			default:
		}
	}
	
	@Override
	public void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		super.generate(inst,world,rand,x,y,z);
		
		final int centerX = x+maxX/2, centerZ = z+maxZ/2;
		PosMutable mpos = new PosMutable();
		
		// center pillar
		placeLine(world,rand,IBlockPicker.basic(Blocks.stonebrick,Meta.stoneBrickChiseled),x+maxX/2,y+1,z+maxZ/2,x+maxX/2,y+maxY-1,z+maxZ/2);
		
		// entrance decorations
		for(int entrance = 0; entrance < connections.size(); entrance++){
			final Connection connection = connections.get(entrance);
			final Facing4 facing = connection.facing.opposite(), perpendicular = facing.perpendicular();
			final Pos startPos = Pos.at(x+connection.offsetX,y+connection.offsetY+1,z+connection.offsetZ);
			final int perX = perpendicular.getX(), perZ = perpendicular.getZ();
			
			// entrance decoration
			for(int side = 0; side < 2; side++){
				final Facing4 sideFacing = side == 0 ? facing.rotateLeft() : facing.rotateRight();
				
				mpos.set(startPos).move(facing).move(sideFacing,2);
				placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(sideFacing.opposite(),false)),mpos.x,mpos.y,mpos.z);
				placeBlock(world,rand,placeStoneBrickPlain,mpos.x,mpos.y+1,mpos.z);
				placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(sideFacing.opposite(),true)),mpos.x,mpos.y+2,mpos.z);
				mpos.move(facing);
				placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(facing.opposite(),false)),mpos.x,mpos.y,mpos.z);
				placeBlock(world,rand,placeStoneBrickPlain,mpos.x,mpos.y+1,mpos.z);
				placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(facing.opposite(),true)),mpos.x,mpos.y+2,mpos.z);
				mpos.move(facing.opposite()).move(sideFacing);
				placeLine(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(sideFacing.opposite(),true)),mpos.x,mpos.y+3,mpos.z,mpos.x+facing.getX(),mpos.y+3,mpos.z+facing.getZ());
			}
			
			mpos.set(startPos).move(facing);
			placeCube(world,rand,placeStoneBrickPlain,mpos.x-2*perX,mpos.y+3,mpos.z-2*perZ,mpos.x+2*perX+facing.getX(),mpos.y+3,mpos.z+2*perZ+facing.getZ());
			
			// entrance top
			if (connection.offsetY == levels[0]){
				placeCube(world,rand,placeStoneBrickPlain,mpos.x-3*perX,mpos.y+4,mpos.z-3*perZ,mpos.x+3*perX+2*facing.getX(),mpos.y+4,mpos.z+3*perZ+2*facing.getZ());
				placeCube(world,rand,IBlockPicker.basic(Blocks.planks,Meta.planksSpruce),mpos.x-3*perX,mpos.y+5,mpos.z-3*perZ,mpos.x+3*perX+2*facing.getX(),mpos.y+5,mpos.z+3*perZ+2*facing.getZ());
			}
			else if (connection.offsetY == levels[1]){
				placeCube(world,rand,IBlockPicker.basic(Blocks.planks,Meta.planksSpruce),mpos.x-3*perX,mpos.y+4,mpos.z-3*perZ,mpos.x+3*perX+2*facing.getX(),mpos.y+4,mpos.z+3*perZ+2*facing.getZ());
			}
			
			mpos.move(facing,2);
			placeLine(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(facing.opposite(),true)),mpos.x-3*perX,mpos.y+3,mpos.z-3*perZ,mpos.x+3*perX,mpos.y+3,mpos.z+3*perZ);
			
			// entrance bottom
			if (connection.offsetY != levels[0]){
				mpos.set(startPos).move(facing);
				placeLine(world,rand,placeStoneBrickPlain,mpos.x,mpos.y-1,mpos.z,mpos.x+facing.getX(),mpos.y-1,mpos.z+facing.getZ());
				
				for(int side = 0; side < 2; side++){
					final Facing4 sideFacing = side == 0 ? facing.rotateLeft() : facing.rotateRight();
					
					mpos.set(startPos).move(facing).move(sideFacing);
					placeBlock(world,rand,placeStoneBrickPlain,mpos.x,mpos.y-1,mpos.z);
					mpos.move(sideFacing);
					placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(sideFacing.opposite(),true)),mpos.x,mpos.y-1,mpos.z);
					mpos.move(facing);
					placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(facing.opposite(),true)),mpos.x,mpos.y-1,mpos.z);
					mpos.move(sideFacing.opposite());
					placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(sideFacing.opposite(),true)),mpos.x,mpos.y-1,mpos.z);
				}
			}
		}
		
		// floor bottom
		for(int entrance = 2; entrance < connections.size(); entrance++){
			final Connection connection = connections.get(entrance);
			final Facing4 facing = connection.facing.opposite(), perpendicular = facing.perpendicular();
			final Pos startPos = Pos.at(x+connection.offsetX,y+connection.offsetY,z+connection.offsetZ);
			
			boolean doubleBricks = entrance <= 3;
			
			// stone bricks
			if (entrance%2 == 0){
				mpos.set(centerX,startPos.getY(),centerZ);
				placeBlock(world,rand,placeStoneBrickPlain,mpos.x,mpos.y,mpos.x);
				for(Facing4 offFacing:Facing4.list)placeLine(world,rand,placeStoneBrickPlain,mpos.x+offFacing.getX(),mpos.y,mpos.z+offFacing.getZ(),mpos.x+6*offFacing.getX(),mpos.y,mpos.z+6*offFacing.getZ());
				
				mpos.move(perpendicular,5);
				if (doubleBricks)placeLine(world,rand,placeStoneBrickPlain,mpos.x+facing.getX(),mpos.y,mpos.z+facing.getZ(),mpos.x+facing.opposite().getX(),mpos.y,mpos.z+facing.opposite().getZ());
				mpos.move(perpendicular,2);
				placeLine(world,rand,placeStoneBrickPlain,mpos.x+facing.getX(),mpos.y,mpos.z+facing.getZ(),mpos.x+facing.opposite().getX(),mpos.y,mpos.z+facing.opposite().getZ());
				mpos.move(perpendicular,-12);
				if (doubleBricks)placeLine(world,rand,placeStoneBrickPlain,mpos.x+facing.getX(),mpos.y,mpos.z+facing.getZ(),mpos.x+facing.opposite().getX(),mpos.y,mpos.z+facing.opposite().getZ());
				mpos.move(perpendicular,-2);
				placeLine(world,rand,placeStoneBrickPlain,mpos.x+facing.getX(),mpos.y,mpos.z+facing.getZ(),mpos.x+facing.opposite().getX(),mpos.y,mpos.z+facing.opposite().getZ());
			}
			
			// wood sides
			for(int side = 0; side < 2; side++){
				final Facing4 sideFacing = side == 0 ? facing.rotateLeft() : facing.rotateRight();
				
				mpos.set(startPos).move(facing).move(sideFacing);
				placeLine(world,rand,IBlockPicker.basic(Blocks.spruce_stairs,Meta.getStairs(sideFacing.opposite(),true)),mpos.x,mpos.y,mpos.z,mpos.x+7*facing.getX(),mpos.y,mpos.z+7*facing.getZ());
				mpos.move(sideFacing);
				placeLine(world,rand,IBlockPicker.basic(Blocks.wooden_slab,Meta.slabWoodSpruceTop),mpos.x,mpos.y,mpos.z,mpos.x+6*facing.getX(),mpos.y,mpos.z+6*facing.getZ());
				mpos.move(facing,7);
				placeLine(world,rand,IBlockPicker.basic(Blocks.spruce_stairs,Meta.getStairs(facing,true)),mpos.x,mpos.y,mpos.z,mpos.x+(doubleBricks ? 2 : 3)*sideFacing.getX(),mpos.y,mpos.z+(doubleBricks ? 2 : 3)*sideFacing.getZ());
				mpos.move(facing.opposite()).move(sideFacing);
				placeLine(world,rand,IBlockPicker.basic(Blocks.wooden_slab,Meta.slabWoodSpruceTop),mpos.x,mpos.y,mpos.z,mpos.x+2*sideFacing.getX(),mpos.y,mpos.z+2*sideFacing.getZ());
				mpos.move(facing.opposite());
				placeLine(world,rand,IBlockPicker.basic(Blocks.wooden_slab,Meta.slabWoodSpruceTop),mpos.x,mpos.y,mpos.z,mpos.x+2*sideFacing.getX(),mpos.y,mpos.z+2*sideFacing.getZ());
				mpos.move(facing.opposite()).move(sideFacing,2);
				placeLine(world,rand,IBlockPicker.basic(Blocks.wooden_slab,Meta.slabWoodSpruceTop),mpos.x,mpos.y,mpos.z,mpos.x+3*sideFacing.getX(),mpos.y,mpos.z+3*sideFacing.getZ());
			}
		}
		
		// floors
		generateFirstFloor(world,rand,x,y+levels[0],z);
		generateSecondFloor(world,rand,x,y+levels[1],z);
		generateThirdFloor(world,rand,x,y+levels[2],z);
	}
	
	private void generateFirstFloor(StructureWorld world, Random rand, int x, int y, int z){
		final int centerX = x+maxX/2, centerZ = z+maxZ/2;
		PosMutable mpos = new PosMutable();
		
		// center
		boolean fillHoles = rand.nextBoolean();
		boolean extraBottomShelves = rand.nextBoolean();
		
		for(Facing4 facing:Facing4.list){
			mpos.set(centerX,y+1,centerZ).move(facing);
			placeLine(world,rand,placeBookshelf,mpos.x,mpos.y,mpos.z,mpos.x+facing.getX(),mpos.y,mpos.z+facing.getZ());
			placeBlock(world,rand,placeBookshelf,mpos.x+facing.getX(),mpos.y+1,mpos.z+facing.getZ());
			placeBlock(world,rand,placeBookshelf,mpos.x,mpos.y+2,mpos.z);
			placeBlock(world,rand,IBlockPicker.basic(Blocks.spruce_stairs,Meta.getStairs(facing.opposite(),false)),mpos.x+facing.getX(),mpos.y+2,mpos.z+facing.getZ());
			placeBlock(world,rand,IBlockPicker.basic(Blocks.spruce_stairs,Meta.getStairs(facing.opposite(),false)),mpos.x,mpos.y+3,mpos.z);
			placeBlock(world,rand,IBlockPicker.basic(Blocks.stonebrick,Meta.stoneBrickChiseled),mpos.x,mpos.y+4,mpos.z);
			
			if (fillHoles)placeBlock(world,rand,placeBookshelf,mpos.x,mpos.y+1,mpos.z);
			if (extraBottomShelves)placeBlock(world,rand,placeBookshelf,mpos.x+facing.rotateRight().getX(),mpos.y,mpos.z+facing.rotateRight().getZ());
		}
		
		// tall side shelves
		for(int side = 0; side < 2; side++){
			Facing4 sideFacing = connections.get(0).facing.perpendicular();
			if (side == 1)sideFacing = sideFacing.opposite();
			
			mpos.set(centerX,y+1,centerZ).move(sideFacing,5);
			placeBlock(world,rand,IBlockPicker.basic(Blocks.spruce_stairs,Meta.getStairs(sideFacing,true)),mpos.x,mpos.y+4,mpos.z);
			mpos.move(sideFacing);
			placeLine(world,rand,placeBookshelf,mpos.x,mpos.y,mpos.z,mpos.x+sideFacing.getX(),mpos.y+3,mpos.z+sideFacing.getZ());
			placeLine(world,rand,IBlockPicker.basic(Blocks.planks,Meta.planksSpruce),mpos.x,mpos.y+4,mpos.z,mpos.x+sideFacing.getX(),mpos.y+4,mpos.z+sideFacing.getZ());
			mpos.move(sideFacing,2);
			placeBlock(world,rand,IBlockPicker.basic(Blocks.spruce_stairs,Meta.getStairs(sideFacing.opposite(),true)),mpos.x,mpos.y+4,mpos.z);
		}
	}
	
	private void generateSecondFloor(StructureWorld world, Random rand, int x, int y, int z){
		final int centerX = x+maxX/2, centerZ = z+maxZ/2;
		PosMutable mpos = new PosMutable();
		
		// center (symmetrical part)
		boolean fillHoles = rand.nextBoolean();
		
		for(Facing4 facing:Facing4.list){
			mpos.set(centerX,y+1,centerZ).move(facing);
			
			placeLine(world,rand,placeBookshelf,mpos.x,mpos.y,mpos.z,mpos.x+facing.getX(),mpos.y,mpos.z+facing.getZ());
			placeBlock(world,rand,placeBookshelf,mpos.x+facing.getX(),mpos.y+1,mpos.z+facing.getZ());
			placeBlock(world,rand,placeBookshelf,mpos.x,mpos.y+2,mpos.z);
			placeBlock(world,rand,IBlockPicker.basic(Blocks.spruce_stairs,Meta.getStairs(facing.opposite(),false)),mpos.x+facing.getX(),mpos.y+2,mpos.z+facing.getZ());
			placeBlock(world,rand,IBlockPicker.basic(Blocks.spruce_stairs,Meta.getStairs(facing.opposite(),true)),mpos.x+facing.getX(),mpos.y+3,mpos.z+facing.getZ());
			placeBlock(world,rand,IBlockPicker.basic(Blocks.planks,Meta.planksSpruce),mpos.x,mpos.y+3,mpos.z);
			
			if (fillHoles)placeBlock(world,rand,placeBookshelf,mpos.x,mpos.y+1,mpos.z);
		}
		
		// center (branching off sides)
		for(int side = 0; side < 2; side++){
			Facing4 sideFacing = connections.get(2).facing.perpendicular();
			if (side == 1)sideFacing = sideFacing.opposite();

			mpos.set(centerX,y+1,centerZ).move(sideFacing,5);
			placeLine(world,rand,placeBookshelf,mpos.x,mpos.y,mpos.z,mpos.x,mpos.y+2,mpos.z);
			placeBlock(world,rand,IBlockPicker.basic(Blocks.spruce_stairs,Meta.getStairs(sideFacing,false)),mpos.x,mpos.y+3,mpos.z);
			
			mpos.move(sideFacing);
			placeLine(world,rand,placeBookshelf,mpos.x-sideFacing.rotateLeft().getX(),mpos.y,mpos.z-sideFacing.rotateRight().getZ(),mpos.x+sideFacing.rotateLeft().getX(),mpos.y+2,mpos.z+sideFacing.rotateRight().getZ());
			placeBlock(world,rand,IBlockPicker.basic(Blocks.spruce_stairs,Meta.getStairs(sideFacing.rotateRight(),false)),mpos.x+sideFacing.rotateLeft().getX(),mpos.y+3,mpos.z+sideFacing.rotateLeft().getZ());
			placeBlock(world,rand,IBlockPicker.basic(Blocks.spruce_stairs,Meta.getStairs(sideFacing.rotateLeft(),false)),mpos.x+sideFacing.rotateRight().getX(),mpos.y+3,mpos.z+sideFacing.rotateRight().getZ());
			placeBlock(world,rand,IBlockPicker.basic(Blocks.planks,Meta.planksSpruce),mpos.x,mpos.y+3,mpos.z);
			
			mpos.move(sideFacing);
			placeLine(world,rand,placeBookshelf,mpos.x,mpos.y,mpos.z,mpos.x,mpos.y+2,mpos.z);
			placeBlock(world,rand,IBlockPicker.basic(Blocks.planks,Meta.planksSpruce),mpos.x,mpos.y+3,mpos.z);
			
			mpos.move(sideFacing);
			placeBlock(world,rand,IBlockPicker.basic(Blocks.planks,Meta.planksSpruce),mpos.x,mpos.y,mpos.z);
			placeBlock(world,rand,IBlockPicker.basic(Blocks.spruce_stairs,Meta.getStairs(sideFacing.opposite(),false)),mpos.x,mpos.y+1,mpos.z);
			placeBlock(world,rand,IBlockPicker.basic(Blocks.spruce_stairs,Meta.getStairs(sideFacing.opposite(),true)),mpos.x,mpos.y+2,mpos.z);
			placeBlock(world,rand,IBlockPicker.basic(Blocks.planks,Meta.planksSpruce),mpos.x,mpos.y+3,mpos.z);
		}
		
		// entrance shelves
		for(int entrance = 2; entrance <= 3; entrance++){
			final Connection connection = connections.get(entrance);
			final Facing4 facing = connection.facing.opposite();
			
			switch(rand.nextInt(2)){
				case 0: // expanding towards center
					for(int side = 0; side < 2; side++){
						final Facing4 sideFacing = side == 0 ? facing.rotateLeft() : facing.rotateRight();
						
						mpos.set(x+connection.offsetX,y+1,z+connection.offsetZ).move(facing,4).move(sideFacing,2);
						placeBlock(world,rand,placeBookshelf,mpos.x,mpos.y,mpos.z);
						mpos.move(facing);
						placeLine(world,rand,placeBookshelf,mpos.x,mpos.y,mpos.z,mpos.x,mpos.y+1,mpos.z);
						mpos.move(facing).move(sideFacing);
						placeLine(world,rand,placeBookshelf,mpos.x,mpos.y,mpos.z,mpos.x,mpos.y+1,mpos.z);
						mpos.move(sideFacing);
						placeBlock(world,rand,placeBookshelf,mpos.x,mpos.y,mpos.z);
					}
					
					break;
					
				case 1: // right next to entrance
					for(int side = 0; side < 2; side++){
						final Facing4 sideFacing = side == 0 ? facing.rotateLeft() : facing.rotateRight();
						
						mpos.set(x+connection.offsetX,y+1,z+connection.offsetZ).move(facing,2).move(sideFacing,2);
						placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(sideFacing.opposite(),false)),mpos.x,mpos.y,mpos.z);
						mpos.move(facing);
						placeLine(world,rand,placeBookshelf,mpos.x,mpos.y,mpos.z,mpos.x+facing.getX(),mpos.y+1,mpos.z+facing.getZ());
						mpos.move(facing,2);
						placeBlock(world,rand,placeBookshelf,mpos.x,mpos.y,mpos.z);
					}
					
					break;
			}
		}
	}
	
	private void generateThirdFloor(StructureWorld world, Random rand, int x, int y, int z){
		final int centerX = x+maxX/2, centerZ = z+maxZ/2;
		PosMutable mpos = new PosMutable();
		
		// side designs
		for(int floorSide = 0; floorSide < 2; floorSide++){
			Facing4 facing = connections.get(4).facing.perpendicular();
			if (floorSide == 1)facing = facing.opposite();
			
			Facing4 perpendicular = facing.perpendicular();
			
			switch(rand.nextInt(1)){
				case 0: // large bookshelf wall
					for(int side = 0; side < 2; side++){
						final Facing4 sideFacing = side == 0 ? facing.rotateLeft() : facing.rotateRight();
						
						mpos.set(centerX,y+1,centerZ).move(facing,5).move(sideFacing,4);
						placeBlock(world,rand,IBlockPicker.basic(Blocks.planks,Meta.planksSpruce),mpos.x,mpos.y,mpos.z);
						mpos.move(facing);
						placeBlock(world,rand,placeBookshelf,mpos.x,mpos.y,mpos.z);
						placeBlock(world,rand,IBlockPicker.basic(Blocks.spruce_stairs,Meta.getStairs(facing,false)),mpos.x,mpos.y+1,mpos.z);
						mpos.move(facing);
						placeLine(world,rand,placeBookshelf,mpos.x,mpos.y,mpos.z,mpos.x,mpos.y+1,mpos.z);
						placeBlock(world,rand,IBlockPicker.basic(Blocks.spruce_stairs,Meta.getStairs(facing,false)),mpos.x,mpos.y+2,mpos.z);
						mpos.move(facing);
						placeLine(world,rand,IBlockPicker.basic(Blocks.planks,Meta.planksSpruce),mpos.x,mpos.y,mpos.z,mpos.x,mpos.y+2,mpos.z);
						placeBlock(world,rand,IBlockPicker.basic(Blocks.wooden_slab,Meta.slabWoodSpruceBottom),mpos.x,mpos.y+3,mpos.z);
						mpos.move(sideFacing.opposite());
						placeLine(world,rand,IBlockPicker.basic(Blocks.planks,Meta.planksSpruce),mpos.x,mpos.y,mpos.z,mpos.x,mpos.y+2,mpos.z);
						placeBlock(world,rand,IBlockPicker.basic(Blocks.wooden_slab,Meta.slabWoodSpruceBottom),mpos.x,mpos.y+3,mpos.z);

						mpos.set(centerX,y+1,centerZ).move(facing,7).move(sideFacing,2);
						placeBlock(world,rand,placeStoneBrickPlain,mpos.x,mpos.y-1,mpos.z);
					}
					
					mpos.set(centerX,y+1,centerZ).move(facing,8);
					placeLine(world,rand,placeBookshelf,mpos.x-2*perpendicular.getX(),mpos.y,mpos.z-2*perpendicular.getZ(),mpos.x+2*perpendicular.getX(),mpos.y+2,mpos.z+2*perpendicular.getZ());
					placeLine(world,rand,IBlockPicker.basic(Blocks.wooden_slab,Meta.slabWoodSpruceBottom),mpos.x-2*perpendicular.getX(),mpos.y+3,mpos.z-2*perpendicular.getZ(),mpos.x+2*perpendicular.getX(),mpos.y+3,mpos.z+2*perpendicular.getZ());
					break;
			}
		}
	}
}
