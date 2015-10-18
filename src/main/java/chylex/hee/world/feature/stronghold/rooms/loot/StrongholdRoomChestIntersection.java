package chylex.hee.world.feature.stronghold.rooms.loot;
import java.util.Random;
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

public class StrongholdRoomChestIntersection extends StrongholdRoom{
	public StrongholdRoomChestIntersection(){
		super(new Size(11,7,11));
	}
	
	@Override
	public void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		super.generate(inst,world,rand,x,y,z);
		
		final int centerX = x+maxX/2, centerZ = z+maxZ/2;
		PosMutable mpos = new PosMutable();
		
		// floor pattern
		if (rand.nextInt(3) != 0){
			placeBlock(world,rand,IBlockPicker.basic(Blocks.stonebrick,Meta.stoneBrickChiseled),centerX,y,centerZ);
			
			for(Facing4 facing:Facing4.list){
				placeLine(world,rand,IBlockPicker.basic(Blocks.stonebrick,Meta.stoneBrickChiseled),centerX+facing.getX(),y,centerZ+facing.getZ(),centerX+3*facing.getX(),y,centerZ+3*facing.getZ());
			}
		}
		
		// ceiling stairs
		placeStairOutline(world,rand,Blocks.stone_brick_stairs,centerX,y+maxY-1,centerZ,4,false,true);
		
		// corner and ceiling layout
		for(int cornerX = 0; cornerX < 2; cornerX++){
			for(int cornerZ = 0; cornerZ < 2; cornerZ++){
				placeCube(world,rand,IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneBrickTop),x+2+5*cornerX,y+maxY-1,z+2+5*cornerZ,x+3+5*cornerX,y+maxY-1,z+3+5*cornerZ);
				
				for(int level = 0; level < 3; level++){
					int slabMeta = level == 2 ? Meta.slabStoneSmoothTop : Meta.slabStoneBrickTop;
					int slabY = level == 0 ? y+1 : y+2+level;
					
					placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_slab,slabMeta),x+1+8*cornerX,slabY,z+1+8*cornerZ);
					placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_slab,slabMeta),x+2+6*cornerX,slabY,z+1+8*cornerZ);
					placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_slab,slabMeta),x+1+8*cornerX,slabY,z+2+6*cornerZ);
				}
			}
		}
		
		// side wall layout
		for(Facing4 facing:Facing4.list){
			mpos.set(centerX,0,centerZ).move(facing,4);
			
			placeBlock(world,rand,IBlockPicker.basic(Blocks.double_stone_slab,Meta.slabStoneSmoothDouble),mpos.x,y+4,mpos.z);
			world.setAttentionWhore(mpos.x-facing.getX(),y+4,mpos.z-facing.getZ(),new BlockInfo(Blocks.torch,Meta.getTorch(facing)));
			
			placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneSmoothTop),mpos.x+facing.rotateLeft().getX(),y+4,mpos.z+facing.rotateLeft().getZ());
			placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneSmoothTop),mpos.x+facing.rotateRight().getX(),y+4,mpos.z+facing.rotateRight().getZ());
			
			int leftX = mpos.x+2*facing.rotateLeft().getX(), leftZ = mpos.z+2*facing.rotateLeft().getZ();
			int rightX = mpos.x+2*facing.rotateRight().getX(), rightZ = mpos.z+2*facing.rotateRight().getZ();
			
			placeLine(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(facing.rotateLeft(),false)),leftX,y+1,leftZ,leftX,y+3,leftZ);
			placeLine(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(facing.rotateRight(),false)),rightX,y+1,rightZ,rightX,y+3,rightZ);
			placeBlock(world,rand,IBlockPicker.basic(Blocks.double_stone_slab,Meta.slabStoneSmoothDouble),leftX,y+4,leftZ);
			placeBlock(world,rand,IBlockPicker.basic(Blocks.double_stone_slab,Meta.slabStoneSmoothDouble),rightX,y+4,rightZ);
		}
		
		// chests and decorations
		for(int chestAttempt = 0; chestAttempt < 1+rand.nextInt(2); chestAttempt++){
			Facing4 offFacing = Facing4.list[rand.nextInt(Facing4.list.length)];
			
			mpos.set(centerX,0,centerZ).move(offFacing,4).move(rand.nextBoolean() ? offFacing.rotateLeft() : offFacing.rotateRight(),3);
			if (!world.isAir(mpos.x,y+2,mpos.z))continue;
			
			placeBlock(world,rand,IBlockPicker.basic(Blocks.chest),mpos.x,y+2,mpos.z);
			world.setTileEntity(mpos.x,y+2,mpos.z,Meta.generateChest(offFacing.opposite(),generateLootGeneral));
		}
		
		FlowerPotPlant[] plants = new FlowerPotPlant[]{
			FlowerPotPlant.BLUE_ORCHID, FlowerPotPlant.TULIP_RED, FlowerPotPlant.DANDELION, FlowerPotPlant.OXEYE_DAISY, FlowerPotPlant.AZURE_BLUET
		};
		
		for(int decorationAttempt = 2+rand.nextInt(3); decorationAttempt > 0; decorationAttempt--){
			Facing4 offFacing = Facing4.list[rand.nextInt(Facing4.list.length)];
			
			mpos.set(centerX,0,centerZ).move(offFacing,4).move(rand.nextBoolean() ? offFacing.rotateLeft() : offFacing.rotateRight(),3);
			if (!world.isAir(mpos.x,y+2,mpos.z))continue;
			
			if (rand.nextInt(7) <= 1){
				placeBlock(world,rand,IBlockPicker.basic(BlockList.ancient_web),mpos.x,y+2,mpos.z);
			}
			else{
				placeBlock(world,rand,IBlockPicker.basic(Blocks.flower_pot),mpos.x,y+2,mpos.z);
				world.setTileEntity(mpos.x,y+2,mpos.z,Meta.generateFlowerPot(plants[rand.nextInt(plants.length)]));
			}
		}
	}
}
