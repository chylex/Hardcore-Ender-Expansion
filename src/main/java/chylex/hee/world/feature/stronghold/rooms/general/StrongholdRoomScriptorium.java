package chylex.hee.world.feature.stronghold.rooms.general;
import java.util.Arrays;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import chylex.hee.init.BlockList;
import chylex.hee.system.abstractions.BlockInfo;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.abstractions.Meta.FlowerPotPlant;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.world.feature.stronghold.rooms.StrongholdRoom;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.structure.util.IBlockPicker;
import chylex.hee.world.util.Size;

public class StrongholdRoomScriptorium extends StrongholdRoom{
	public static StrongholdRoomScriptorium[] generateScriptoriums(){
		return Arrays.stream(Facing4.list).map(facing -> new StrongholdRoomScriptorium(facing)).toArray(StrongholdRoomScriptorium[]::new);
	}
	
	private final Facing4 entranceFrom;
	
	public StrongholdRoomScriptorium(Facing4 entranceFrom){
		super(new Size(15,7,15),new Facing4[]{ entranceFrom.opposite(), entranceFrom.rotateLeft(), entranceFrom.rotateRight() });
		this.entranceFrom = entranceFrom;
	}
	
	@Override
	public void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){ // TODO maybe add more torches
		super.generate(inst,world,rand,x,y,z);
		
		final Connection connection = connections.stream().filter(c -> c.facing == entranceFrom.opposite()).findFirst().get();
		PosMutable mpos = new PosMutable();
		
		IBlockPicker placeBookshelf = IBlockPicker.basic(Blocks.bookshelf);
		
		// entrance area
		mpos.set(x+connection.offsetX,0,z+connection.offsetZ).move(entranceFrom,4);
		placeLine(world,rand,placeBookshelf,mpos.x,y+1,mpos.z,mpos.x,y+2,mpos.z);
		mpos.move(entranceFrom);
		placeBlock(world,rand,placeStoneBrickPlain,mpos.x,y+1,mpos.z);
		
		for(int side = 0; side < 2; side++){
			final Facing4 sideFacing = side == 0 ? entranceFrom.rotateLeft() : entranceFrom.rotateRight();
			
			// side bookshelves
			mpos.set(x+connection.offsetX,0,z+connection.offsetZ).move(entranceFrom).move(sideFacing,5);
			placeLine(world,rand,placeBookshelf,mpos.x,y+1,mpos.z,mpos.x,y+3,mpos.z);
			placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneBrickBottom),mpos.x,y+4,mpos.z);
			mpos.move(sideFacing);
			placeLine(world,rand,IBlockPicker.basic(Blocks.planks,Meta.planksSpruce),mpos.x,y+1,mpos.z,mpos.x,y+3,mpos.z);
			placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneBrickBottom),mpos.x,y+4,mpos.z);
			mpos.move(entranceFrom);
			placeLine(world,rand,placeBookshelf,mpos.x,y+1,mpos.z,mpos.x,y+3,mpos.z);
			placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneBrickBottom),mpos.x,y+4,mpos.z);
			
			// table
			mpos.set(x+connection.offsetX,0,z+connection.offsetZ).move(entranceFrom,5).move(sideFacing);
			placeBlock(world,rand,IBlockPicker.basic(Blocks.dark_oak_stairs,Meta.getStairs(sideFacing.opposite(),true)),mpos.x,y+1,mpos.z);
			mpos.move(sideFacing);
			placeBlock(world,rand,IBlockPicker.basic(Blocks.dark_oak_stairs,Meta.getStairs(sideFacing,false)),mpos.x,y+1,mpos.z);
		}
		
		// middle wall
		mpos.set(x+connection.offsetX,0,z+connection.offsetZ).move(entranceFrom,9);
		placeLine(world,rand,IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneBrickTop),mpos.x+2*entranceFrom.rotateLeft().getX(),y+maxY-1,mpos.z+2*entranceFrom.rotateLeft().getZ(),mpos.x+2*entranceFrom.rotateRight().getX(),y+maxY-1,mpos.z+2*entranceFrom.rotateRight().getZ());
		
		for(int side = 0; side < 2; side++){
			final Facing4 sideFacing = side == 0 ? entranceFrom.rotateLeft() : entranceFrom.rotateRight();
			
			mpos.set(x+connection.offsetX,0,z+connection.offsetZ).move(entranceFrom,9).move(sideFacing,6);
			placeCube(world,rand,placeStoneBrick,mpos.x,y+1,mpos.z,mpos.x-3*sideFacing.getX(),y+2,mpos.z-3*sideFacing.getZ());
			placeLine(world,rand,IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneBrickBottom),mpos.x,y+3,mpos.z,mpos.x-2*sideFacing.getX(),y+3,mpos.z-2*sideFacing.getZ());
			placeBlock(world,rand,placeStoneBrick,mpos.x-3*sideFacing.getX(),y+3,mpos.z-3*sideFacing.getZ());
			placeCube(world,rand,placeStoneBrick,mpos.x,y+4,mpos.z,mpos.x-3*sideFacing.getX(),y+5,mpos.z-3*sideFacing.getZ());
			world.setAttentionWhore(mpos.x-4*sideFacing.getX(),y+4,mpos.z-4*sideFacing.getZ(),new BlockInfo(Blocks.torch,Meta.getTorch(sideFacing)));
			
			mpos.move(entranceFrom);
			placeLine(world,rand,IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneBrickTop),mpos.x,y+maxY-1,mpos.z,mpos.x-4*sideFacing.getX(),y+maxY-1,mpos.z-4*sideFacing.getZ());
			mpos.move(entranceFrom.opposite(),2);
			placeLine(world,rand,IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneBrickTop),mpos.x,y+maxY-1,mpos.z,mpos.x-4*sideFacing.getX(),y+maxY-1,mpos.z-4*sideFacing.getZ());
		}
		
		// back table
		mpos.set(x+connection.offsetX,0,z+connection.offsetZ).move(entranceFrom,12);
		placeBlock(world,rand,IBlockPicker.basic(Blocks.dark_oak_stairs,Meta.getStairs(entranceFrom,true)),mpos.x,y+1,mpos.z);
		mpos.move(entranceFrom);
		placeBlock(world,rand,IBlockPicker.basic(Blocks.planks,Meta.planksDarkOak),mpos.x,y+1,mpos.z);
		
		placeBlock(world,rand,IBlockPicker.basic(Blocks.flower_pot),mpos.x,y+2,mpos.z);
		world.setTileEntity(mpos.x,y+2,mpos.z,Meta.generateFlowerPot(FlowerPotPlant.DEAD_BUSH));
		
		for(int side = 0; side < 2; side++){
			final Facing4 sideFacing = side == 0 ? entranceFrom.rotateLeft() : entranceFrom.rotateRight();
			
			mpos.set(x+connection.offsetX,0,z+connection.offsetZ).move(entranceFrom,13).move(sideFacing);
			placeLine(world,rand,IBlockPicker.basic(Blocks.dark_oak_stairs,Meta.getStairs(sideFacing.opposite(),true)),mpos.x,y+1,mpos.z,mpos.x-entranceFrom.getX(),y+1,mpos.z-entranceFrom.getZ());
			mpos.move(sideFacing);
			placeBlock(world,rand,IBlockPicker.basic(Blocks.dark_oak_stairs,Meta.getStairs(sideFacing,false)),mpos.x,y+1,mpos.z);
		}
		
		// back bookshelves
		for(int side = 0; side < 2; side++){
			final Facing4 sideFacing = side == 0 ? entranceFrom.rotateLeft() : entranceFrom.rotateRight();
			
			mpos.set(x+connection.offsetX,0,z+connection.offsetZ).move(entranceFrom,8).move(sideFacing,3);
			placeLine(world,rand,placeBookshelf,mpos.x,y+1,mpos.z,mpos.x+sideFacing.getX(),y+2,mpos.z+sideFacing.getZ());
			mpos.move(sideFacing.opposite()).move(entranceFrom);
			placeLine(world,rand,placeBookshelf,mpos.x,y+1,mpos.z,mpos.x,y+2,mpos.z);
			mpos.move(sideFacing).move(entranceFrom);
			
			for(int off = 0; off < 2; off++){
				placeLine(world,rand,placeBookshelf,mpos.x,y+1,mpos.z,mpos.x+2*sideFacing.getX(),y+1,mpos.z+2*sideFacing.getZ());
				placeBlock(world,rand,placeBookshelf,mpos.x,y+2,mpos.z);
				placeBlock(world,rand,placeBookshelf,mpos.x+2*sideFacing.getX(),y+2,mpos.z+2*sideFacing.getZ());
				mpos.move(entranceFrom,3);
			}
			
			mpos.set(x+connection.offsetX,0,z+connection.offsetZ).move(entranceFrom,10).move(sideFacing,6);
			
			placeCube(world,rand,random -> random.nextInt(9) == 0 ? new BlockInfo(Blocks.planks,Meta.planksOak) : new BlockInfo(Blocks.bookshelf),mpos.x,y+1,mpos.z,mpos.x+3*entranceFrom.getX(),y+4,mpos.z+3*entranceFrom.getZ());
		}
				
		// cobwebs
		for(int cobwebs = 7+rand.nextInt(6); cobwebs > 0; cobwebs--){
			for(int attempt = 0; attempt < 50; attempt++){
				mpos.set(x+1+rand.nextInt(maxX-1),y+1,z+1+rand.nextInt(maxZ-1));
				while(!world.isAir(mpos.x,mpos.y,mpos.z) && ++mpos.y < maxY);
				
				if (mpos.y == y+1 && rand.nextInt(4) != 0)continue;
				
				if (world.isAir(mpos.x,mpos.y,mpos.z) && (mpos.y > y+1 || Arrays.stream(Facing4.list).anyMatch(off -> !world.isAir(mpos.x+off.getX(),mpos.y,mpos.z+off.getZ())))){
					Block below = world.getBlock(mpos.x,mpos.y-1,mpos.z);
					
					if (below == Blocks.bookshelf || below == Blocks.stonebrick || below == Blocks.monster_egg || below == Blocks.stone_slab){
						if (mpos.y > y+1 && rand.nextInt(4) == 0 && world.isAir(mpos.x,mpos.y+1,mpos.z))++mpos.y;
						
						placeBlock(world,rand,IBlockPicker.basic(BlockList.ancient_web),mpos.x,mpos.y,mpos.z);
						break;
					}
				}
			}
		}
	}
}
