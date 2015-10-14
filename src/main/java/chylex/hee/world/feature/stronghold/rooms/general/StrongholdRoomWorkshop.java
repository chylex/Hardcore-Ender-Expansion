package chylex.hee.world.feature.stronghold.rooms.general;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import net.minecraft.init.Blocks;
import chylex.hee.init.BlockList;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.abstractions.Meta.FlowerPotPlant;
import chylex.hee.system.abstractions.Meta.Skull;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.world.feature.stronghold.rooms.StrongholdRoom;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.structure.util.IBlockPicker;
import chylex.hee.world.util.Size;

public class StrongholdRoomWorkshop extends StrongholdRoom{
	public StrongholdRoomWorkshop(){
		super(new Size(15,7,15));
	}
	
	@Override
	public void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		super.generate(inst,world,rand,x,y,z);
		
		final int centerX = x+maxX/2, centerZ = z+maxZ/2;
		PosMutable mpos = new PosMutable();
		
		// fireplace
		placeOutline(world,rand,IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneSmoothBottom),centerX-2,y+1,centerZ-2,centerX+2,y+1,centerZ+2,1);
		placeStairOutline(world,rand,Blocks.stone_brick_stairs,centerX,y+1,centerZ,1,true,false);
		placeStairOutline(world,rand,Blocks.stone_brick_stairs,centerX,y+2,centerZ,1,true,true);

		placeOutline(world,rand,IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneSmoothTop),centerX-2,y+5,centerZ-2,centerX+2,y+5,centerZ+2,1);
		placeStairOutline(world,rand,Blocks.stone_brick_stairs,centerX,y+5,centerZ,1,true,true);
		
		placeLine(world,rand,IBlockPicker.basic(Blocks.flowing_lava),centerX,y+1,centerZ,centerX,y+2,centerZ);
		
		// corners
		for(int cornerX = 0; cornerX < 2; cornerX++){
			for(int cornerZ = 0; cornerZ < 2; cornerZ++){
				placeLine(world,rand,placeStoneBrick,x+1+12*cornerX,y+1,z+1+12*cornerZ,x+1+12*cornerX,y+5,z+1+12*cornerZ);
			}
		}
		
		// decoration setup
		byte[] decorations = new byte[16];
		int decorationIndex = 0;
		
		for(int cobwebs = 0, index; cobwebs < 3+rand.nextInt(8); cobwebs++){
			decorations[rand.nextInt(decorations.length)] = 1;
		}
		
		for(int skulls = 0; skulls < 1+rand.nextInt(2)*rand.nextInt(3); skulls++){
			decorations[rand.nextInt(decorations.length)] = 2;
		}
		
		// walls
		boolean hasInnerCeiling = rand.nextBoolean();
		
		for(Facing4 offFacing:Facing4.list){
			Pos wall = Pos.at(centerX,0,centerZ).offset(offFacing,6);
			
			// main wall layer
			placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneBrickTop),wall.getX(),y+4,wall.getZ());
			placeLine(world,rand,placeStoneBrick,wall.getX()+offFacing.rotateLeft().getX(),y+5,wall.getZ()+offFacing.rotateLeft().getZ(),wall.getX()+offFacing.rotateRight().getX(),y+5,wall.getZ()+offFacing.rotateRight().getZ());
			
			for(int side = 0; side < 2; side++){
				Facing4 sideFacing = side == 0 ? offFacing.rotateLeft() : offFacing.rotateRight();
				placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_brick_stairs,Meta.getStairs(sideFacing,true)),wall.getX()+sideFacing.getX(),y+4,wall.getZ()+sideFacing.getZ());
				placeLine(world,rand,placeStoneBrick,wall.getX()+2*sideFacing.getX(),y+1,wall.getZ()+2*sideFacing.getZ(),wall.getX()+5*sideFacing.getX(),y+5,wall.getZ()+5*sideFacing.getZ());
				
				for(int decoration = 0; decoration < 2; decoration++){
					placeBlock(world,rand,IBlockPicker.basic(Blocks.iron_bars),wall.getX()+(2+decoration*2)*sideFacing.getX()-offFacing.getX(),y+3,wall.getZ()+(2+decoration*2)*sideFacing.getZ()-offFacing.getZ());
					
					int holeX = wall.getX()+(3+decoration*2)*sideFacing.getX(), holeZ = wall.getZ()+(3+decoration*2)*sideFacing.getZ();
					
					switch(decorations[decorationIndex++]){
						case 0: placeBlock(world,rand,placeAir,holeX,y+3,holeZ); break;
						case 1: placeBlock(world,rand,IBlockPicker.basic(BlockList.ancient_web),holeX,y+3,holeZ); break;
						case 2:
							placeBlock(world,rand,IBlockPicker.basic(Blocks.skull,Meta.skullGround),holeX,y+3,holeZ);
							world.setTileEntity(holeX,y+3,holeZ,Meta.generateSkullGround(Skull.ZOMBIE,Pos.at(holeX,0,holeZ),Pos.at(centerX,0,centerZ).offset(sideFacing,3*decoration)));
							break;
					}
				}
			}
			
			// outer ceiling
			mpos.set(centerX,0,centerZ).move(offFacing,5).move(offFacing.rotateLeft());
			placeLine(world,rand,IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneBrickTop),mpos.x,y+5,mpos.z,mpos.x+2*offFacing.rotateRight().getX(),y+5,mpos.z+2*offFacing.rotateRight().getZ());
			mpos.move(offFacing.rotateRight(),3);
			placeLine(world,rand,placeStoneBrickPlain,mpos.x,y+5,mpos.z,mpos.x+3*offFacing.rotateRight().getX(),y+5,mpos.z+3*offFacing.rotateRight().getZ());
			mpos.move(offFacing.rotateRight(),3).move(offFacing.opposite());
			placeLine(world,rand,placeStoneBrickPlain,mpos.x,y+5,mpos.z,mpos.x+2*offFacing.opposite().getX(),y+5,mpos.z+2*offFacing.opposite().getZ());
			
			// inner ceiling
			if (hasInnerCeiling){
				mpos.move(offFacing.rotateLeft(),2);
				placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneBrickTop),mpos.x,y+5,mpos.z);
				mpos.move(offFacing.rotateRight());
				placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneBrickTop),mpos.x,y+5,mpos.z);
				mpos.move(offFacing.opposite());
				placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneBrickTop),mpos.x,y+5,mpos.z);
			}
		}
		
		// tables and utilities
		for(Facing4 facing:Facing4.list){
			mpos.set(centerX,0,centerZ).move(facing,5).move(facing.rotateRight(),2);
			placeUtilitySection(world,rand,mpos.x,y+1,mpos.z,facing.opposite());
			
			Facing4 right = facing.rotateRight();
			mpos.move(right);
			placeBlock(world,rand,IBlockPicker.basic(Blocks.spruce_stairs,Meta.getStairs(right,true)),mpos.x,y+1,mpos.z);
			mpos.move(right);
			placeBlock(world,rand,IBlockPicker.basic(Blocks.spruce_stairs,Meta.getStairs(facing,true)),mpos.x,y+1,mpos.z);
			mpos.move(right);
			placeBlock(world,rand,IBlockPicker.basic(Blocks.spruce_stairs,Meta.getStairs(facing,true)),mpos.x,y+1,mpos.z);
			
			Facing4 back = facing.opposite();
			mpos.move(back);
			placeBlock(world,rand,IBlockPicker.basic(Blocks.spruce_stairs,Meta.getStairs(right,true)),mpos.x,y+1,mpos.z);
			mpos.move(back);
			placeBlock(world,rand,IBlockPicker.basic(Blocks.spruce_stairs,Meta.getStairs(facing,true)),mpos.x,y+1,mpos.z);

			mpos.move(back);
			placeUtilitySection(world,rand,mpos.x,y+1,mpos.z,facing.rotateLeft());
		}
		
		// table decorations
		FlowerPotPlant[] plants = new FlowerPotPlant[]{
			FlowerPotPlant.BLUE_ORCHID, FlowerPotPlant.POPPY, FlowerPotPlant.ALLIUM, FlowerPotPlant.TULIP_WHITE
		};
		
		Set<Pos> usedPos = new HashSet<>();
		
		for(int attempts = 4+rand.nextInt(4); attempts > 0; attempts--){
			Facing4 offFacing = Facing4.list[rand.nextInt(Facing4.list.length)];
			int offExtra = rand.nextInt(3);
			
			mpos.set(centerX,0,centerZ).move(offFacing,5).move(rand.nextBoolean() ? offFacing.rotateLeft() : offFacing.rotateRight(),3+offExtra); // higher chance for corners
			if (usedPos.contains(mpos))continue;
			
			if (offExtra != 2 && rand.nextInt(5) <= 1){
				/* TODO FIX THIS FUCKING SHIT JESUS ITEM FRAMES SUCK
				EntityItemFrame frame = new EntityItemFrame(world.getParentWorld(),mpos.x+offFacing.getX(),y+2,mpos.z+offFacing.getZ(),offFacing.toEnumFacing().ordinal());
				world.addEntity(frame,e -> ((EntityItemFrame)e).setDisplayedItem(new ItemStack(Items.writable_book)));*/
			}
			else if (rand.nextInt(4+offExtra) == 0){ // less cobwebs near corners
				placeBlock(world,rand,IBlockPicker.basic(BlockList.ancient_web),mpos.x,y+2,mpos.z);
			}
			else{
				placeBlock(world,rand,IBlockPicker.basic(Blocks.flower_pot),mpos.x,y+2,mpos.z);
				world.setTileEntity(mpos.x,y+2,mpos.z,Meta.generateFlowerPot(plants[rand.nextInt(plants.length)]));
			}
			
			usedPos.add(mpos.immutable());
		}
	}
	
	private void placeUtilitySection(StructureWorld world, Random rand, int x, int y, int z, Facing4 facingTowards){
		int type = rand.nextInt(11);
		
		if (type < 4){
			placeLine(world,rand,IBlockPicker.basic(Blocks.bookshelf),x,y,z,x,y+1,z);
		}
		else if (type < 7){
			placeBlock(world,rand,IBlockPicker.basic(Blocks.furnace),x,y,z);
			world.setTileEntity(x,y,z,Meta.generateFurnace(facingTowards,null));
			placeBlock(world,rand,IBlockPicker.basic(Blocks.crafting_table),x,y+1,z);
		}
		else if (type < 9){
			placeBlock(world,rand,IBlockPicker.basic(Blocks.bookshelf),x,y,z);
			placeBlock(world,rand,IBlockPicker.basic(Blocks.crafting_table),x,y+1,z);
		}
		else if (type < 11){
			placeLine(world,rand,IBlockPicker.basic(Blocks.furnace),x,y,z,x,y+1,z);
			world.setTileEntity(x,y,z,Meta.generateFurnace(facingTowards,null));
			world.setTileEntity(x,y+1,z,Meta.generateFurnace(facingTowards,null));
		}
	}
}
