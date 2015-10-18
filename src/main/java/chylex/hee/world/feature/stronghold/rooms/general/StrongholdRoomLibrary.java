package chylex.hee.world.feature.stronghold.rooms.general;
import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.init.BlockList;
import chylex.hee.system.abstractions.BlockInfo;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.abstractions.Meta.FlowerPotPlant;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.system.collections.CollectionUtil;
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
		
		// bottom corner decorations
		int bottomChest = rand.nextInt(2); // 0 = work table, 1 = wall shelves
		List<Facing4> offLeft = CollectionUtil.newList(Facing4.list);
		Facing4 offFacing;
		
		// bottom corner group table
		offFacing = offLeft.remove(rand.nextInt(offLeft.size()));
		mpos.set(centerX,0,centerZ).move(offFacing,7).move(offFacing = offFacing.rotateRight(),5);
		
		placeLine(world,rand,IBlockPicker.basic(Blocks.dark_oak_stairs,Meta.getStairs(offFacing.rotateLeft(),false)),mpos.x,y+1,mpos.z,mpos.x+2*offFacing.getX(),y+1,mpos.z+2*offFacing.getZ());
		mpos.move(offFacing,2).move(offFacing = offFacing.rotateRight());
		placeLine(world,rand,IBlockPicker.basic(Blocks.dark_oak_stairs,Meta.getStairs(offFacing.rotateLeft(),false)),mpos.x,y+1,mpos.z,mpos.x+offFacing.getX(),y+1,mpos.z+offFacing.getZ());
		
		mpos.move(offFacing).move(offFacing = offFacing.rotateRight(),2);
		
		for(int corner = 0; corner < 4; corner++){
			placeBlock(world,rand,IBlockPicker.basic(Blocks.spruce_stairs,Meta.getStairs(offFacing.rotateRight(),true)),mpos.x,y+1,mpos.z);
			if (rand.nextInt(4) == 0)placeBlock(world,rand,IBlockPicker.basic(Blocks.flower_pot),mpos.x,y+2,mpos.z);
			mpos.move(offFacing = offFacing.rotateRight());
		}
		
		// bottom corner work table
		offFacing = offLeft.remove(rand.nextInt(offLeft.size()));
		mpos.set(centerX,0,centerZ).move(offFacing,6).move(offFacing = offFacing.rotateRight(),7);
		
		placeBlock(world,rand,IBlockPicker.basic(Blocks.dark_oak_stairs,Meta.getStairs(offFacing.rotateRight(),false)),mpos.x,y+1,mpos.z);
		mpos.move(offFacing).move(offFacing = offFacing.rotateLeft());
		placeLine(world,rand,IBlockPicker.basic(Blocks.spruce_stairs,Meta.getStairs(offFacing,true)),mpos.x,y+1,mpos.z,mpos.x+2*offFacing.rotateLeft().getX(),y+1,mpos.z+2*offFacing.rotateLeft().getZ());
		mpos.move(offFacing.rotateLeft(),2).move(offFacing);
		placeBlock(world,rand,IBlockPicker.basic(Blocks.spruce_stairs,Meta.getStairs(offFacing.rotateRight(),true)),mpos.x,y+1,mpos.z);
		
		if (bottomChest == 0){
			placeBlock(world,rand,IBlockPicker.basic(Blocks.chest),mpos.x,y+2,mpos.z);
			world.setTileEntity(mpos.x,y+2,mpos.z,Meta.generateChest(offFacing.opposite(),generateLootLibrarySecondary));
		}
		
		mpos.move(offFacing = offFacing.rotateRight());
		placeLine(world,rand,IBlockPicker.basic(Blocks.planks,Meta.planksSpruce),mpos.x,y+1,mpos.z,mpos.x+offFacing.getX(),y+1,mpos.z+offFacing.getZ());
		mpos.move(offFacing);
		placeBlock(world,rand,IBlockPicker.basic(Blocks.flower_pot),mpos.x,y+2,mpos.z);
		world.setTileEntity(mpos.x,y+2,mpos.z,Meta.generateFlowerPot(rand.nextBoolean() ? FlowerPotPlant.TULIP_WHITE : FlowerPotPlant.DANDELION));
		
		Facing4 offFacingPrev = offFacing;
		Pos posPrev = mpos.immutable();
		
		for(int attempt = 0; attempt < 2; attempt++){
			if (rand.nextInt(4) <= 2){
				offFacing = attempt == 0 ? offFacing.opposite() : offFacing.rotateRight();
				mpos.move(offFacing,4);
				
				if (world.isAir(mpos.x+2*offFacing.getX(),y+1,mpos.z+2*offFacing.getZ())){
					placeLine(world,rand,placeBookshelf,mpos.x,y+1,mpos.z,mpos.x+offFacing.getX(),y+2,mpos.z+offFacing.getZ());
					placeLine(world,rand,IBlockPicker.basic(Blocks.wooden_slab,Meta.slabWoodSpruceBottom),mpos.x,y+3,mpos.z,mpos.x+offFacing.getX(),y+3,mpos.z+offFacing.getZ());
				}
				else{
					placeBlock(world,rand,placeBookshelf,mpos.x,y+1,mpos.z);
					placeBlock(world,rand,IBlockPicker.basic(Blocks.wooden_slab,Meta.slabWoodSpruceBottom),mpos.x,y+2,mpos.z);
				}
			}
			
			offFacing = offFacingPrev;
			mpos.set(posPrev);
		}
		
		// corner torches
		for(Facing4 facing:Facing4.list){
			for(int level = 0; level < 3; level++){
				mpos.set(centerX,y+4*(level+1),centerZ).move(facing,8).move(facing.rotateRight(),6);
				placeBlock(world,rand,IBlockPicker.basic(BlockList.stone_brick_wall),mpos.x,mpos.y,mpos.z);
				world.setAttentionWhore(mpos.x,mpos.y+1,mpos.z,new BlockInfo(Blocks.torch,Meta.torchGround));
				
				mpos.set(centerX,y+4*(level+1),centerZ).move(facing,8).move(facing.rotateLeft(),6);
				placeBlock(world,rand,IBlockPicker.basic(BlockList.stone_brick_wall),mpos.x,mpos.y,mpos.z);
				world.setAttentionWhore(mpos.x,mpos.y+1,mpos.z,new BlockInfo(Blocks.torch,Meta.torchGround));
			}
		}
		
		// bottom corner wall shelves
		offFacing = offLeft.remove(rand.nextInt(offLeft.size()));
		boolean dirMatches = offFacing == connections.get(0).facing || offFacing == connections.get(1).facing;
		Facing4 frontFacing = dirMatches ? offFacing : offFacing.rotateRight();
		
		mpos.set(centerX,0,centerZ).move(frontFacing,3).move(offFacing = (dirMatches ? offFacing.rotateRight() : offFacing),6);
		placeLine(world,rand,IBlockPicker.basic(Blocks.spruce_stairs,Meta.getStairs(offFacing,false)),mpos.x,y+1,mpos.z,mpos.x+frontFacing.getX(),y+1,mpos.z+frontFacing.getZ());
		mpos.move(offFacing);
		placeCube(world,rand,IBlockPicker.basic(Blocks.planks,Meta.planksSpruce),mpos.x,y+1,mpos.z,mpos.x+offFacing.getX()+frontFacing.getX(),y+1,mpos.z+offFacing.getZ()+frontFacing.getZ());
		
		mpos.move(frontFacing,2);
		placeLine(world,rand,IBlockPicker.basic(Blocks.spruce_stairs,Meta.getStairs(frontFacing,false)),mpos.x,y+2,mpos.z,mpos.x+offFacing.getX(),y+2,mpos.z+offFacing.getZ());
		mpos.move(frontFacing);
		placeCube(world,rand,IBlockPicker.basic(Blocks.planks,Meta.planksSpruce),mpos.x,y+2,mpos.z,mpos.x+offFacing.getX()+2*frontFacing.getX(),y+2,mpos.z+offFacing.getZ()+2*frontFacing.getZ());
		
		mpos.move(offFacing);
		placeBlock(world,rand,placeAir,mpos.x,y+4,mpos.z);
		world.setAttentionWhore(mpos.x,y+5,mpos.z,null);
		mpos.move(offFacing.opposite(),2).move(frontFacing,2);
		placeBlock(world,rand,placeAir,mpos.x,y+4,mpos.z);
		world.setAttentionWhore(mpos.x,y+5,mpos.z,null);
		
		placeCube(world,rand,placeBookshelf,mpos.x,y+1,mpos.z,mpos.x-offFacing.getX()-3*frontFacing.getX(),y+4,mpos.z-offFacing.getZ()-3*frontFacing.getZ());
		placeCube(world,rand,IBlockPicker.basic(Blocks.wooden_slab,Meta.slabWoodSpruceBottom),mpos.x,y+5,mpos.z,mpos.x-offFacing.getX()-3*frontFacing.getX(),y+5,mpos.z-offFacing.getZ()-3*frontFacing.getZ());
		
		if (bottomChest == 1){
			placeBlock(world,rand,IBlockPicker.basic(Blocks.chest),mpos.x,y+4,mpos.z);
			world.setTileEntity(mpos.x,y+4,mpos.z,Meta.generateChest(offFacing,generateLootLibrarySecondary));
		}
		
		// stairs
		offFacing = offLeft.get(0);
		boolean dirMatch;
		
		dirMatch = offFacing == connections.get(0).facing || offFacing == connections.get(1).facing;
		mpos.set(centerX,y,centerZ).move(offFacing,4).move(offFacing.rotateRight(),4);
		generateBottomStairs(world,rand,mpos.x,mpos.y,mpos.z,dirMatch ? offFacing.rotateRight() : offFacing,!dirMatch);
		
		offFacing = offFacing.opposite();
		
		dirMatch = offFacing == connections.get(2).facing || offFacing == connections.get(3).facing;
		mpos.set(centerX,y+levels[1],centerZ).move(offFacing,dirMatch ? 4 : 7).move(offFacing.rotateRight(),dirMatch ? 7 : 4);
		generateMiddleStairs(world,rand,mpos.x,mpos.y,mpos.z,dirMatch ? offFacing : offFacing.rotateRight(),!dirMatch);
		
		// middle floor decorations
		for(int corner = 0; corner < 4; corner++){
			Facing4 facing = connections.get(2).facing.rotateLeft();
			if (corner > 1)facing = facing.opposite();
			
			Facing4 sideFacing = corner%2 == 0 ? facing.rotateLeft() : facing.rotateRight();
			mpos.set(centerX,y+levels[1]+1,centerZ).move(facing,8).move(sideFacing);
			
			Pos stairCheck1 = mpos.offset(facing.opposite()).offset(sideFacing,3);
			Pos stairCheck2 = mpos.offset(facing.opposite(),2).offset(sideFacing,4).getDown();
			
			if (world.isAir(stairCheck1.getX(),stairCheck1.getY(),stairCheck1.getZ()) && world.isAir(stairCheck2.getX(),stairCheck2.getY(),stairCheck2.getZ())){
				switch(rand.nextInt(3)){
					case 0:
						mpos.move(sideFacing,2);
						placeBlock(world,rand,IBlockPicker.basic(Blocks.dark_oak_stairs,Meta.getStairs(facing,false)),mpos.x,mpos.y,mpos.z);
						mpos.move(sideFacing);
						placeLine(world,rand,IBlockPicker.basic(Blocks.dark_oak_stairs,Meta.getStairs(sideFacing,false)),mpos.x,mpos.y,mpos.z,mpos.x-2*facing.getX(),mpos.y,mpos.z-2*facing.getZ());
						break;
						
					case 1:
						mpos.move(sideFacing,2);
						placeLine(world,rand,placeBookshelf,mpos.x,mpos.y,mpos.z,mpos.x+sideFacing.getX(),mpos.y+1,mpos.z+sideFacing.getZ());
						placeLine(world,rand,IBlockPicker.basic(Blocks.wooden_slab,Meta.slabWoodSpruceBottom),mpos.x,mpos.y+2,mpos.z,mpos.x+sideFacing.getX(),mpos.y+2,mpos.z+sideFacing.getZ());
						mpos.move(sideFacing).move(facing.opposite());
						placeLine(world,rand,placeBookshelf,mpos.x,mpos.y,mpos.z,mpos.x-facing.getX(),mpos.y+2,mpos.z-facing.getZ());
						placeLine(world,rand,IBlockPicker.basic(Blocks.wooden_slab,Meta.slabWoodSpruceBottom),mpos.x,mpos.y+3,mpos.z,mpos.x-facing.getX(),mpos.y+3,mpos.z-facing.getZ());
						break;
						
					case 2:
						mpos.move(sideFacing,2);
						placeLine(world,rand,placeBookshelf,mpos.x,mpos.y,mpos.z,mpos.x+sideFacing.getX(),mpos.y,mpos.z+sideFacing.getZ());
						placeLine(world,rand,IBlockPicker.basic(Blocks.wooden_slab,Meta.slabWoodSpruceBottom),mpos.x,mpos.y+1,mpos.z,mpos.x+sideFacing.getX(),mpos.y+1,mpos.z+sideFacing.getZ());
						mpos.move(sideFacing).move(facing.opposite());
						placeBlock(world,rand,placeBookshelf,mpos.x,mpos.y,mpos.z);
						placeBlock(world,rand,IBlockPicker.basic(Blocks.wooden_slab,Meta.slabWoodSpruceBottom),mpos.x,mpos.y+1,mpos.z);
						break;
				}
			}
		}
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
			if (fillHoles && extraBottomShelves)placeBlock(world,rand,IBlockPicker.basic(Blocks.wooden_slab,Meta.slabWoodSpruceBottom),mpos.x+facing.rotateRight().getX(),mpos.y+1,mpos.z+facing.rotateRight().getZ());
		}
		
		// tall side shelves
		for(int side = 0; side < 2; side++){
			Facing4 sideFacing = connections.get(0).facing.perpendicular();
			if (side == 1)sideFacing = sideFacing.opposite();
			
			mpos.set(centerX,y+1,centerZ).move(sideFacing,4);
			placeBlock(world,rand,IBlockPicker.basic(Blocks.spruce_stairs,Meta.getStairs(sideFacing,true)),mpos.x,mpos.y+4,mpos.z);
			mpos.move(sideFacing);
			placeLine(world,rand,placeBookshelf,mpos.x,mpos.y,mpos.z,mpos.x+2*sideFacing.getX(),mpos.y+3,mpos.z+2*sideFacing.getZ());
			placeLine(world,rand,IBlockPicker.basic(Blocks.planks,Meta.planksSpruce),mpos.x,mpos.y+4,mpos.z,mpos.x+2*sideFacing.getX(),mpos.y+4,mpos.z+2*sideFacing.getZ());
			mpos.move(sideFacing,3);
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
					boolean slabsOnTop = rand.nextInt(5) <= 1;
					
					for(int side = 0; side < 2; side++){
						final Facing4 sideFacing = side == 0 ? facing.rotateLeft() : facing.rotateRight();
						
						mpos.set(x+connection.offsetX,y+1,z+connection.offsetZ).move(facing,4).move(sideFacing,2);
						placeBlock(world,rand,placeBookshelf,mpos.x,mpos.y,mpos.z);
						if (slabsOnTop)placeBlock(world,rand,IBlockPicker.basic(Blocks.wooden_slab,Meta.slabWoodSpruceBottom),mpos.x,mpos.y+1,mpos.z);
						
						mpos.move(facing);
						placeLine(world,rand,placeBookshelf,mpos.x,mpos.y,mpos.z,mpos.x,mpos.y+1,mpos.z);
						if (slabsOnTop)placeBlock(world,rand,IBlockPicker.basic(Blocks.wooden_slab,Meta.slabWoodSpruceBottom),mpos.x,mpos.y+2,mpos.z);
						
						mpos.move(facing).move(sideFacing);
						placeLine(world,rand,placeBookshelf,mpos.x,mpos.y,mpos.z,mpos.x,mpos.y+1,mpos.z);
						if (slabsOnTop)placeBlock(world,rand,IBlockPicker.basic(Blocks.wooden_slab,Meta.slabWoodSpruceBottom),mpos.x,mpos.y+2,mpos.z);
						
						mpos.move(sideFacing);
						placeBlock(world,rand,placeBookshelf,mpos.x,mpos.y,mpos.z);
						if (slabsOnTop)placeBlock(world,rand,IBlockPicker.basic(Blocks.wooden_slab,Meta.slabWoodSpruceBottom),mpos.x,mpos.y+1,mpos.z);
					}
					
					break;
					
				case 1: // right next to entrance
					boolean stairOnTop = rand.nextInt(3) != 0;
					
					for(int side = 0; side < 2; side++){
						final Facing4 sideFacing = side == 0 ? facing.rotateLeft() : facing.rotateRight();
						
						mpos.set(x+connection.offsetX,y+1,z+connection.offsetZ).move(facing,2).move(sideFacing,2);
						placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(sideFacing.opposite(),false)),mpos.x,mpos.y,mpos.z);
						mpos.move(facing);
						placeLine(world,rand,placeBookshelf,mpos.x,mpos.y,mpos.z,mpos.x+facing.getX(),mpos.y+1,mpos.z+facing.getZ());
						mpos.move(facing,2);
						placeBlock(world,rand,placeBookshelf,mpos.x,mpos.y,mpos.z);
						if (stairOnTop)placeBlock(world,rand,IBlockPicker.basic(Blocks.spruce_stairs,Meta.getStairs(facing.opposite(),false)),mpos.x,mpos.y+1,mpos.z);
					}
					
					break;
			}
		}
		
		// chest
		Facing4 offFacing = connections.get(2).facing.perpendicular();
		if (rand.nextBoolean())offFacing = offFacing.opposite();
		Facing4 chestFacing = rand.nextBoolean() ? offFacing.rotateLeft() : offFacing.rotateRight();
		
		mpos.set(centerX,y+2,centerZ).move(offFacing,8);
		placeBlock(world,rand,IBlockPicker.basic(Blocks.chest),mpos.x,mpos.y,mpos.z);
		world.setTileEntity(mpos.x,mpos.y,mpos.z,Meta.generateChest(chestFacing,generateLootLibraryMain));
		placeBlock(world,rand,placeAir,mpos.x,mpos.y+1,mpos.z);
		
		mpos.move(chestFacing.opposite());
		placeLine(world,rand,placeBookshelf,mpos.x,mpos.y-1,mpos.z,mpos.x,mpos.y+1,mpos.z);
		placeBlock(world,rand,IBlockPicker.basic(Blocks.spruce_stairs,Meta.getStairs(chestFacing,false)),mpos.x,mpos.y+2,mpos.z);
	}
	
	private void generateThirdFloor(StructureWorld world, Random rand, int x, int y, int z){
		final int centerX = x+maxX/2, centerZ = z+maxZ/2;
		PosMutable mpos = new PosMutable();
		
		// side designs
		for(int floorSide = 0; floorSide < 2; floorSide++){
			Facing4 facing = connections.get(4).facing.perpendicular();
			if (floorSide == 1)facing = facing.opposite();
			
			Facing4 perpendicular = facing.perpendicular();
			int perX = perpendicular.getX(), perZ = perpendicular.getZ();
			
			final int type = rand.nextInt(4);
			
			switch(type){
				case 0: case 1: // bookshelf wall with side extensions
					for(int side = 0; side < 2; side++){
						final Facing4 sideFacing = side == 0 ? facing.rotateLeft() : facing.rotateRight();
						
						mpos.set(centerX,y+1,centerZ).move(facing,5).move(sideFacing,4);
						placeBlock(world,rand,IBlockPicker.basic(Blocks.planks,Meta.planksSpruce),mpos.x,mpos.y,mpos.z);
						world.setAttentionWhore(mpos.x,mpos.y+1,mpos.z,new BlockInfo(Blocks.torch,Meta.torchGround));
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
						placeLine(world,rand,type == 0 ? IBlockPicker.basic(Blocks.planks,Meta.planksSpruce) : placeBookshelf,mpos.x,mpos.y,mpos.z,mpos.x,mpos.y+2,mpos.z);
						placeBlock(world,rand,IBlockPicker.basic(Blocks.wooden_slab,Meta.slabWoodSpruceBottom),mpos.x,mpos.y+3,mpos.z);
						
						mpos.set(centerX,y+1,centerZ).move(facing,7).move(sideFacing,2);
						placeBlock(world,rand,placeStoneBrickPlain,mpos.x,mpos.y-1,mpos.z);
					}
					
					mpos.set(centerX,y+1,centerZ).move(facing,8);
					placeLine(world,rand,placeBookshelf,mpos.x-2*perX,mpos.y,mpos.z-2*perZ,mpos.x+2*perX,mpos.y+2,mpos.z+2*perZ);
					if (type == 1)placeLine(world,rand,IBlockPicker.basic(Blocks.planks,Meta.planksSpruce),mpos.x,mpos.y,mpos.z,mpos.x,mpos.y+2,mpos.z);
					placeLine(world,rand,IBlockPicker.basic(Blocks.wooden_slab,Meta.slabWoodSpruceBottom),mpos.x-2*perX,mpos.y+3,mpos.z-2*perZ,mpos.x+2*perX,mpos.y+3,mpos.z+2*perZ);
					break;
					
				case 2: case 3: // large bookshelf wall
					for(int side = 0; side < 2; side++){
						final Facing4 sideFacing = side == 0 ? facing.rotateLeft() : facing.rotateRight();
						
						mpos.set(centerX,y+1,centerZ).move(facing,6).move(sideFacing,3);
						placeBlock(world,rand,IBlockPicker.basic(Blocks.spruce_stairs,Meta.getStairs(facing,false)),mpos.x,mpos.y,mpos.z);
						placeBlock(world,rand,IBlockPicker.basic(Blocks.spruce_stairs,Meta.getStairs(facing,true)),mpos.x,mpos.y+3,mpos.z);
						world.setAttentionWhore(mpos.x,mpos.y+1,mpos.z,new BlockInfo(Blocks.torch,Meta.getTorch(facing)));
						mpos.move(facing);
						placeLine(world,rand,IBlockPicker.basic(Blocks.planks,Meta.planksSpruce),mpos.x,mpos.y,mpos.z,mpos.x+facing.getX(),mpos.y+3,mpos.z+facing.getZ());
						mpos.move(sideFacing);
						placeLine(world,rand,IBlockPicker.basic(Blocks.spruce_stairs,Meta.getStairs(sideFacing.opposite(),false)),mpos.x,mpos.y,mpos.z,mpos.x+facing.getX(),mpos.y,mpos.z+facing.getZ());
						placeLine(world,rand,IBlockPicker.basic(Blocks.spruce_stairs,Meta.getStairs(sideFacing.opposite(),true)),mpos.x,mpos.y+3,mpos.z,mpos.x+facing.getX(),mpos.y+3,mpos.z+facing.getZ());
						
						mpos.set(centerX,y+1,centerZ).move(facing,7).move(sideFacing,2);
						placeBlock(world,rand,placeStoneBrickPlain,mpos.x,mpos.y-1,mpos.z);
					}
					
					mpos.set(centerX,y+1,centerZ).move(facing,7);
					placeLine(world,rand,IBlockPicker.basic(Blocks.spruce_stairs,Meta.getStairs(facing,true)),mpos.x-2*perX,mpos.y+3,mpos.z-2*perZ,mpos.x+2*perX,mpos.y+3,mpos.z+2*perZ);
					mpos.move(facing);
					placeLine(world,rand,placeBookshelf,mpos.x-2*perX,mpos.y,mpos.z-2*perZ,mpos.x+2*perX,mpos.y+2,mpos.z+2*perZ);
					placeLine(world,rand,IBlockPicker.basic(Blocks.planks,Meta.planksSpruce),mpos.x-2*perX,mpos.y+3,mpos.z-2*perZ,mpos.x+2*perX,mpos.y+3,mpos.z+2*perZ);
					break;
			}
		}
		
		// center
		Facing4 connectionFacing = connections.get(4).facing;
		Facing4 perpendicular = connectionFacing.perpendicular();
		
		Pos pos1 = mpos.set(centerX,0,centerZ).move(perpendicular,2).move(connectionFacing).immutable();
		Pos pos2 = mpos.set(centerX,0,centerZ).move(perpendicular.opposite(),2).move(connectionFacing.opposite()).immutable();
		placeOutline(world,rand,placeBookshelf,pos1.getX(),y+1,pos1.getZ(),pos2.getX(),y+3,pos2.getZ(),1);
		
		mpos.set(centerX,0,centerZ).move(connectionFacing);
		placeLine(world,rand,IBlockPicker.basic(Blocks.spruce_stairs,Meta.getStairs(connectionFacing.opposite(),false)),mpos.x-perpendicular.getX(),y+4,mpos.z-perpendicular.getZ(),mpos.x+perpendicular.getX(),y+4,mpos.z+perpendicular.getZ());
		mpos.set(centerX,0,centerZ).move(connectionFacing.opposite());
		placeLine(world,rand,IBlockPicker.basic(Blocks.spruce_stairs,Meta.getStairs(connectionFacing,false)),mpos.x-perpendicular.getX(),y+4,mpos.z-perpendicular.getZ(),mpos.x+perpendicular.getX(),y+4,mpos.z+perpendicular.getZ());
		
		for(int side = 0; side < 2; side++){
			Facing4 offFacing = side == 0 ? perpendicular : perpendicular.opposite();
			
			mpos.set(centerX,y+4,centerZ).move(offFacing,2).move(offFacing.rotateLeft());
			placeBlock(world,rand,IBlockPicker.basic(Blocks.wooden_slab,Meta.slabWoodSpruceBottom),mpos.x,mpos.y,mpos.z);
			mpos.set(centerX,y+4,centerZ).move(offFacing,2).move(offFacing.rotateRight());
			placeBlock(world,rand,IBlockPicker.basic(Blocks.wooden_slab,Meta.slabWoodSpruceBottom),mpos.x,mpos.y,mpos.z);

			mpos.set(centerX,0,centerZ).move(offFacing,4);
			placeLine(world,rand,placeBookshelf,mpos.x,y+1,mpos.z,mpos.x-offFacing.getX(),y+2,mpos.z-offFacing.getZ());
			placeBlock(world,rand,IBlockPicker.basic(Blocks.wooden_slab,Meta.slabWoodSpruceBottom),mpos.x,y+3,mpos.z);
			mpos.move(offFacing.opposite());
			placeBlock(world,rand,IBlockPicker.basic(Blocks.spruce_stairs,Meta.getStairs(offFacing.opposite(),false)),mpos.x,y+3,mpos.z);
			mpos.move(offFacing.opposite());
			placeBlock(world,rand,IBlockPicker.basic(Blocks.spruce_stairs,Meta.getStairs(offFacing.opposite(),false)),mpos.x,y+4,mpos.z);
		}
		
		// entrance shelves
		for(int entrance = 4; entrance <= 5; entrance++){
			final Connection connection = connections.get(entrance);
			final Facing4 facing = connection.facing.opposite();
			
			switch(rand.nextInt(2)){
				case 0: // simple bookshelves next to the entrance
					boolean slabsOnTop = rand.nextBoolean();
					
					for(int side = 0; side < 2; side++){
						mpos.set(x+connection.offsetX,y+1,z+connection.offsetZ).move(facing,4).move(side == 0 ? facing.rotateLeft() : facing.rotateRight(),2);
						placeLine(world,rand,placeBookshelf,mpos.x,mpos.y,mpos.z,mpos.x+facing.getX(),mpos.y,mpos.z+facing.getZ());
						if (slabsOnTop)placeLine(world,rand,IBlockPicker.basic(Blocks.wooden_slab,Meta.slabWoodSpruceBottom),mpos.x,mpos.y+1,mpos.z,mpos.x+facing.getX(),mpos.y+1,mpos.z+facing.getZ());
					}
					
					break;
					
				case 1: // bookshelves in entrance sides
					for(int side = 0; side < 2; side++){
						mpos.set(x+connection.offsetX,y+1,z+connection.offsetZ).move(facing,2).move(side == 0 ? facing.rotateLeft() : facing.rotateRight(),2);
						placeLine(world,rand,placeBookshelf,mpos.x,mpos.y,mpos.z,mpos.x+facing.getX(),mpos.y+1,mpos.z+facing.getZ());
						placeBlock(world,rand,placeStoneBrickPlain,mpos.x,mpos.y+2,mpos.z);
						placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(facing.opposite(),false)),mpos.x+facing.getX(),mpos.y+2,mpos.z+facing.getZ());
					}
					
					break;
			}
		}
	}
	
	private void generateBottomStairs(StructureWorld world, Random rand, int x, int y, int z, Facing4 facing, boolean rotateRight){
		PosMutable mpos = new PosMutable();
		
		mpos.set(x,y+4,z).move(facing,4).move(rotateRight ? facing.rotateRight() : facing.rotateLeft(),2);
		placeBlock(world,rand,placeAir,mpos.x,mpos.y,mpos.z);
		world.setAttentionWhore(mpos.x,mpos.y+1,mpos.z,BlockInfo.air);
		mpos.move(rotateRight ? facing.rotateRight() : facing.rotateLeft(),2).move(facing.opposite(),2);
		placeBlock(world,rand,placeAir,mpos.x,mpos.y,mpos.z);
		world.setAttentionWhore(mpos.x,mpos.y+1,mpos.z,BlockInfo.air);
		
		mpos.set(x,y+1,z);
		placeBlock(world,rand,IBlockPicker.basic(Blocks.dark_oak_stairs,Meta.getStairs(facing,false)),mpos.x,mpos.y,mpos.z);
		
		for(int cycle = 0; cycle < 2; cycle++){
			mpos.move(facing);
			placeBlock(world,rand,IBlockPicker.basic(Blocks.dark_oak_stairs,Meta.getStairs(facing.opposite(),true)),mpos.x,mpos.y+cycle,mpos.z);
			placeBlock(world,rand,IBlockPicker.basic(Blocks.dark_oak_stairs,Meta.getStairs(facing,false)),mpos.x,mpos.y+1+cycle,mpos.z);
		}
		
		mpos.move(facing);
		placeBlock(world,rand,IBlockPicker.basic(Blocks.dark_oak_stairs,Meta.getStairs(facing.opposite(),true)),mpos.x,mpos.y+2,mpos.z);
		mpos.move(facing = rotateRight ? facing.rotateRight() : facing.rotateLeft());
		placeBlock(world,rand,IBlockPicker.basic(Blocks.dark_oak_stairs,Meta.getStairs(facing.opposite(),true)),mpos.x,mpos.y+2,mpos.z);
		placeBlock(world,rand,IBlockPicker.basic(Blocks.dark_oak_stairs,Meta.getStairs(facing,false)),mpos.x,mpos.y+3,mpos.z);
		mpos.move(facing);
		placeBlock(world,rand,IBlockPicker.basic(Blocks.dark_oak_stairs,Meta.getStairs(facing.opposite(),true)),mpos.x,mpos.y+3,mpos.z);
		mpos.move(facing = rotateRight ? facing.rotateRight() : facing.rotateLeft());
		
		for(int cycle = 0; cycle < 2; cycle++){
			placeBlock(world,rand,IBlockPicker.basic(Blocks.dark_oak_stairs,Meta.getStairs(facing.opposite(),true)),mpos.x,mpos.y+3+cycle,mpos.z);
			placeBlock(world,rand,IBlockPicker.basic(Blocks.dark_oak_stairs,Meta.getStairs(facing,false)),mpos.x,mpos.y+4+cycle,mpos.z);
			mpos.move(facing);
		}
		
		placeBlock(world,rand,IBlockPicker.basic(Blocks.dark_oak_stairs,Meta.getStairs(facing.opposite(),true)),mpos.x,mpos.y+5,mpos.z);
		
		// bookshelves next to wall
		mpos.move(rotateRight ? facing.rotateLeft() : facing.rotateRight(),2);
		
		switch(rand.nextInt(3)){
			case 0:
				placeLine(world,rand,placeBookshelf,mpos.x,mpos.y+6,mpos.z,mpos.x+facing.getX(),mpos.y+7,mpos.z+facing.getZ());
				placeLine(world,rand,IBlockPicker.basic(Blocks.wooden_slab,Meta.slabWoodSpruceBottom),mpos.x,mpos.y+8,mpos.z,mpos.x+facing.getX(),mpos.y+8,mpos.z+facing.getZ());
				break;
				
			case 1:
				placeLine(world,rand,placeBookshelf,mpos.x,mpos.y+6,mpos.z,mpos.x+facing.getX(),mpos.y+6,mpos.z+facing.getZ());
				placeLine(world,rand,IBlockPicker.basic(Blocks.wooden_slab,Meta.slabWoodSpruceBottom),mpos.x,mpos.y+7,mpos.z,mpos.x+facing.getX(),mpos.y+7,mpos.z+facing.getZ());
				break;
				
			case 2:
				placeLine(world,rand,placeBookshelf,mpos.x,mpos.y+6,mpos.z,mpos.x+facing.getX(),mpos.y+6,mpos.z+facing.getZ());
				placeBlock(world,rand,IBlockPicker.basic(Blocks.wooden_slab,Meta.slabWoodSpruceBottom),mpos.x,mpos.y+7,mpos.z);
				placeBlock(world,rand,placeBookshelf,mpos.x+facing.getX(),mpos.y+7,mpos.z+facing.getZ());
				placeBlock(world,rand,IBlockPicker.basic(Blocks.wooden_slab,Meta.slabWoodSpruceBottom),mpos.x+facing.getX(),mpos.y+8,mpos.z+facing.getZ());
				break;
		}
	}
	
	private void generateMiddleStairs(StructureWorld world, Random rand, int x, int y, int z, Facing4 facing, boolean rotateRight){
		PosMutable mpos = new PosMutable(x,y+1,z);
		
		placeBlock(world,rand,IBlockPicker.basic(Blocks.dark_oak_stairs,Meta.getStairs(facing,false)),mpos.x,mpos.y,mpos.z);
		mpos.move(facing);
		placeBlock(world,rand,IBlockPicker.basic(Blocks.dark_oak_stairs,Meta.getStairs(facing.opposite(),true)),mpos.x,mpos.y,mpos.z);
		placeBlock(world,rand,IBlockPicker.basic(Blocks.dark_oak_stairs,Meta.getStairs(facing,false)),mpos.x,mpos.y+1,mpos.z);
		mpos.move(facing);
		placeBlock(world,rand,IBlockPicker.basic(Blocks.dark_oak_stairs,Meta.getStairs(facing.opposite(),true)),mpos.x,mpos.y+1,mpos.z);

		mpos.move(facing = rotateRight ? facing.rotateLeft() : facing.rotateRight());
		placeBlock(world,rand,placeAir,mpos.x,mpos.y+1,mpos.z);
		world.setAttentionWhore(mpos.x,mpos.y+2,mpos.z,BlockInfo.air);
		
		mpos.move(facing = facing.opposite(),2);
		placeBlock(world,rand,IBlockPicker.basic(Blocks.dark_oak_stairs,Meta.getStairs(facing.opposite(),true)),mpos.x,mpos.y+1,mpos.z);
		placeBlock(world,rand,IBlockPicker.basic(Blocks.dark_oak_stairs,Meta.getStairs(facing,false)),mpos.x,mpos.y+2,mpos.z);
		mpos.move(facing);
		placeBlock(world,rand,IBlockPicker.basic(Blocks.dark_oak_stairs,Meta.getStairs(facing.opposite(),true)),mpos.x,mpos.y+2,mpos.z);
		
		mpos.move(facing = rotateRight ? facing.rotateRight() : facing.rotateLeft());
		placeBlock(world,rand,IBlockPicker.basic(Blocks.dark_oak_stairs,Meta.getStairs(facing.opposite(),true)),mpos.x,mpos.y+2,mpos.z);
		placeBlock(world,rand,IBlockPicker.basic(Blocks.dark_oak_stairs,Meta.getStairs(facing,false)),mpos.x,mpos.y+3,mpos.z);
		mpos.move(facing);
		placeBlock(world,rand,IBlockPicker.basic(Blocks.dark_oak_stairs,Meta.getStairs(facing.opposite(),true)),mpos.x,mpos.y+3,mpos.z);
		placeBlock(world,rand,IBlockPicker.basic(Blocks.dark_oak_stairs,Meta.getStairs(facing,false)),mpos.x,mpos.y+4,mpos.z);
		mpos.move(facing);
		placeBlock(world,rand,IBlockPicker.basic(Blocks.dark_oak_stairs,Meta.getStairs(facing.opposite(),true)),mpos.x,mpos.y+4,mpos.z);
		
		mpos.move(facing = rotateRight ? facing.rotateLeft() : facing.rotateRight());
		placeBlock(world,rand,IBlockPicker.basic(Blocks.wooden_slab,Meta.slabWoodSpruceTop),mpos.x,mpos.y+4,mpos.z);
		mpos.move(facing = rotateRight ? facing.rotateLeft() : facing.rotateRight());
		placeBlock(world,rand,IBlockPicker.basic(Blocks.wooden_slab,Meta.slabWoodSpruceTop),mpos.x,mpos.y+4,mpos.z);
	}
}
