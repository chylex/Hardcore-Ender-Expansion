package chylex.hee.world.feature.stronghold.corridors;
import java.util.Arrays;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.abstractions.Meta.FlowerPotPlant;
import chylex.hee.system.abstractions.Meta.Skull;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.system.util.RandUtil;
import chylex.hee.world.feature.stronghold.StrongholdPieceGeneric;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.structure.util.IBlockPicker;
import chylex.hee.world.util.Size;

public class StrongholdEndWallDecorations extends StrongholdPieceGeneric{
	public static StrongholdEndWallDecorations[] generateDeadEnds(){
		return Arrays.stream(Facing4.list).map(facing -> new StrongholdEndWallDecorations(facing)).toArray(StrongholdEndWallDecorations[]::new);
	}
	
	private final Facing4 entranceFrom;
	
	public StrongholdEndWallDecorations(Facing4 entranceFrom){
		super(Type.DEADEND,new Size(7,7,7),new Facing4[]{ entranceFrom.opposite() },fromDeadEnd);
		this.entranceFrom = entranceFrom;
	}
	
	@Override
	public void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		super.generate(inst,world,rand,x,y,z);
		
		final Connection connection = connections.get(0);
		PosMutable mpos = new PosMutable();
		
		// layout
		mpos.set(x+connection.offsetX,0,z+connection.offsetZ).move(entranceFrom,4);
		placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(entranceFrom,true)),mpos.x,y+5,mpos.z);
		mpos.move(entranceFrom);
		placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(entranceFrom,true)),mpos.x,y+4,mpos.z);
		placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(entranceFrom,false)),mpos.x,y+3,mpos.z);
		placeLine(world,rand,placeStoneBrickPlain,mpos.x,y+1,mpos.z,mpos.x,y+2,mpos.z);
		
		for(int side = 0; side < 2; side++){
			final Facing4 sideFacing = side == 0 ? entranceFrom.rotateLeft() : entranceFrom.rotateRight();
			
			mpos.set(x+connection.offsetX,0,z+connection.offsetZ).move(entranceFrom).move(sideFacing);
			placeLine(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(sideFacing,true)),mpos.x,y+5,mpos.z,mpos.x+3*entranceFrom.getX(),y+5,mpos.z+3*entranceFrom.getZ());
			
			mpos.move(sideFacing);
			placeLine(world,rand,placeStoneBrickPlain,mpos.x,y+1,mpos.z,mpos.x+4*entranceFrom.getX(),y+1,mpos.z+4*entranceFrom.getZ());
			placeBlock(world,rand,placeStoneBrickPlain,mpos.x+2*entranceFrom.getX(),y+2,mpos.z+2*entranceFrom.getZ());
			placeLine(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(sideFacing,false)),mpos.x,y+3,mpos.z,mpos.x+4*entranceFrom.getX(),y+3,mpos.z+4*entranceFrom.getZ());
			placeLine(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(sideFacing,true)),mpos.x,y+4,mpos.z,mpos.x+4*entranceFrom.getX(),y+4,mpos.z+4*entranceFrom.getZ());
			
			mpos.move(sideFacing.opposite()).move(entranceFrom,4);
			placeBlock(world,rand,placeStoneBrickPlain,mpos.x,y+1,mpos.z);
			placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(entranceFrom,false)),mpos.x,y+3,mpos.z);
			placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(entranceFrom,true)),mpos.x,y+4,mpos.z);
		}
		
		// chest
		Facing4 chestOffFacing = rand.nextBoolean() ? entranceFrom.rotateLeft() : entranceFrom.rotateRight();
		int chestPos = rand.nextInt(3);
		
		mpos.set(x+connection.offsetX,0,z+connection.offsetZ).move(chestOffFacing,2);
		
		switch(chestPos){
			case 0: mpos.move(entranceFrom); break;
			case 1: mpos.move(entranceFrom,2); break;
			case 2: mpos.move(entranceFrom,4); break;
		}
		
		placeBlock(world,rand,placeChest,mpos.x,y+2,mpos.z);
		world.setTileEntity(mpos.x,y+2,mpos.z,Meta.generateChest(chestOffFacing.opposite(),generateLootGeneral));
		
		// skull
		Facing4 skullOffFacing = chestPos == 2 ? chestOffFacing.opposite() : (rand.nextBoolean() ? chestOffFacing : chestOffFacing.opposite());
		mpos.set(x+connection.offsetX,0,z+connection.offsetZ).move(entranceFrom,5).move(skullOffFacing,2);
		
		placeBlock(world,rand,IBlockPicker.basic(Blocks.skull,Meta.skullGround),mpos.x,y+2,mpos.z);
		world.setTileEntity(mpos.x,y+2,mpos.z,Meta.generateSkullGround(rand.nextBoolean() ? Skull.SKELETON : Skull.ZOMBIE,mpos,mpos.offset(entranceFrom.opposite()).offset(skullOffFacing.opposite())));
		
		// flower pots
		FlowerPotPlant[] plants = new FlowerPotPlant[]{
			FlowerPotPlant.TULIP_PINK, FlowerPotPlant.TULIP_ORANGE, FlowerPotPlant.POPPY, FlowerPotPlant.DANDELION
		};
		
		for(int pot = 0; pot < rand.nextInt(3); pot++){
			for(int attempt = 0; attempt < 2; attempt++){
				Facing4 potOffFacing = rand.nextBoolean() ? entranceFrom.rotateLeft() : entranceFrom.rotateRight();
				mpos.set(x+connection.offsetX,0,z+connection.offsetZ).move(entranceFrom);
				
				switch(rand.nextInt(3)){
					case 0: mpos.move(potOffFacing,2); break;
					case 1: mpos.move(potOffFacing,2).move(entranceFrom); break;
					case 2: mpos.move(potOffFacing).move(entranceFrom,4); break;
				}
				
				if (world.isAir(mpos.x,y+2,mpos.z)){
					placeBlock(world,rand,placeFlowerPot,mpos.x,y+2,mpos.z);
					world.setTileEntity(mpos.x,y+2,mpos.z,Meta.generateFlowerPot(RandUtil.anyOf(rand,plants)));
					break;
				}
			}
		}
	}
}
