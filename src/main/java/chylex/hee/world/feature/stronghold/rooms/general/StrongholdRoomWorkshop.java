package chylex.hee.world.feature.stronghold.rooms.general;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.init.BlockList;
import chylex.hee.system.abstractions.Meta;
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
			mpos.move(offFacing.rotateLeft(),2);
			placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneBrickTop),mpos.x,y+5,mpos.z);
			mpos.move(offFacing.rotateRight());
			placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneBrickTop),mpos.x,y+5,mpos.z);
			mpos.move(offFacing.opposite());
			placeBlock(world,rand,IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneBrickTop),mpos.x,y+5,mpos.z);
		}
	}
}
