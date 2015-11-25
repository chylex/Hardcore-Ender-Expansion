package chylex.hee.world.feature.stronghold.rooms.loot;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.init.BlockList;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.abstractions.Meta.FlowerPotPlant;
import chylex.hee.system.abstractions.Meta.LogType;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.system.util.RandUtil;
import chylex.hee.world.feature.stronghold.rooms.StrongholdRoom;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.structure.util.IBlockPicker;
import chylex.hee.world.util.Size;

public class StrongholdRoomChestSupportSmall extends StrongholdRoom{
	public StrongholdRoomChestSupportSmall(){
		super(new Size(11,7,11));
	}
	
	@Override
	public void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		super.generate(inst,world,rand,x,y,z);
		
		int centerX = x+maxX/2, centerZ = z+maxZ/2;
		PosMutable mpos = new PosMutable();
		Facing4 facing;
		
		// wooden supports
		facing = rand.nextBoolean() ? Facing4.NORTH_NEGZ : Facing4.WEST_NEGX;
		placeLine(world,rand,Meta.getLog(LogType.DARK_OAK,facing),centerX+4*facing.getX(),y+maxY-1,centerZ+4*facing.getZ(),centerX-4*facing.getX(),y+maxY-1,centerZ-4*facing.getZ());
		facing = facing.perpendicular();
		placeLine(world,rand,Meta.getLog(LogType.DARK_OAK,facing),centerX+4*facing.getX(),y+maxY-2,centerZ+4*facing.getZ(),centerX-4*facing.getX(),y+maxY-2,centerZ-4*facing.getZ());
		
		// floor pattern
		placeStairOutline(world,rand,Blocks.stone_brick_stairs,centerX,y,centerZ,3,true,false);
		
		// ceiling cobwebs
		for(int attempts = 18, toPlace = 6+rand.nextInt(7); attempts > 0 && toPlace > 0; attempts--){
			mpos.set(centerX,y+maxY-1-rand.nextInt(2),centerZ);
			mpos.move(facing = Facing4.random(rand),1+rand.nextInt(2+rand.nextInt(3)));
			mpos.move(rand.nextBoolean() ? facing.rotateLeft() : facing.rotateRight(),rand.nextInt(2));
			
			if (world.isAir(mpos)){
				world.setBlock(mpos,BlockList.ancient_web);
				--toPlace;
			}
		}
		
		// corner content selection
		List<CornerContent> content = new ArrayList<>();
		
		if (rand.nextInt(8) == 0)content.add(CornerContent.DOUBLE_CHEST);
		else{
			content.add(rand.nextBoolean() ? CornerContent.CHEST : CornerContent.CHEST_FLOWER_POT);
			if (rand.nextInt(5) == 0)content.add(CornerContent.CHEST);
		}
		
		if (rand.nextInt(3) == 0)content.add(CornerContent.DOUBLE_FLOWER_POT);
		if (rand.nextBoolean())content.add(CornerContent.FLOWER_POT);
		content.add(CornerContent.FLOWER_POT);
		
		while(content.size() < 4)content.add(CornerContent.NOTHING);
		
		// corner content generation
		mpos.set(x+1,y+3,z+1);
		facing = Facing4.EAST_POSX;
		IBlockPicker placeTopSlab = IBlockPicker.basic(Blocks.stone_slab,Meta.slabStoneBrickTop);
		
		for(int corner = 0; corner < 4; corner++){
			placeBlock(world,rand,placeTopSlab,mpos.x,mpos.y-1,mpos.z);
			placeBlock(world,rand,placeTopSlab,mpos.x+facing.getX(),mpos.y-1,mpos.z+facing.getZ());
			placeBlock(world,rand,placeTopSlab,mpos.x+facing.rotateRight().getX(),mpos.y-1,mpos.z+facing.rotateRight().getZ());
			placeCornerContent(world,rand,mpos,facing,facing.rotateRight(),content.remove(rand.nextInt(content.size())));
			
			if (corner < 3){
				mpos.move(facing,8);
				facing = facing.rotateRight();
			}
		}
	}
	
	private enum CornerContent { NOTHING, CHEST, CHEST_FLOWER_POT, DOUBLE_CHEST, FLOWER_POT, DOUBLE_FLOWER_POT }
	
	private static final FlowerPotPlant[] plants = new FlowerPotPlant[]{
		FlowerPotPlant.POPPY, FlowerPotPlant.DANDELION, FlowerPotPlant.AZURE_BLUET, FlowerPotPlant.BLUE_ORCHID,
		FlowerPotPlant.ALLIUM, FlowerPotPlant.CACTUS, FlowerPotPlant.FERN
	};
	
	private void placeCornerContent(StructureWorld world, Random rand, Pos corner, Facing4 facing1, Facing4 facing2, CornerContent type){
		Pos[] posArray = new Pos[]{ corner, corner.offset(facing1), corner.offset(facing2) };
		
		if (type == CornerContent.DOUBLE_FLOWER_POT){
			placeFlowerPot(world,rand,posArray[1]);
			placeFlowerPot(world,rand,posArray[2]);
		}
		else if (type == CornerContent.FLOWER_POT){
			placeFlowerPot(world,rand,RandUtil.anyOf(rand,posArray));
		}
		else if (type == CornerContent.DOUBLE_CHEST){
			placeChest(world,rand,posArray[1],facing1.rotateRight());
			placeChest(world,rand,posArray[2],facing2.rotateLeft());
		}
		else if (type == CornerContent.CHEST || type == CornerContent.CHEST_FLOWER_POT){
			if (rand.nextBoolean())placeChest(world,rand,posArray[1],facing1.rotateRight());
			else placeChest(world,rand,posArray[2],facing2.rotateLeft());
			
			if (type == CornerContent.CHEST_FLOWER_POT){
				for(int attempt = 0; attempt < 3; attempt++){
					Pos pos = RandUtil.anyOf(rand,posArray);
					
					if (world.isAir(pos)){
						placeFlowerPot(world,rand,pos);
						break;
					}
				}
			}
		}
		
		for(int webAttempt = 0; webAttempt < 2; webAttempt++){
			if (rand.nextInt(5) == 0){
				Pos cobweb = RandUtil.anyOf(rand,posArray);
				
				if (world.isAir(cobweb.getX(),cobweb.getY(),cobweb.getZ())){
					world.setBlock(cobweb,BlockList.ancient_web);
				}
			}
		}
	}
	
	private void placeFlowerPot(StructureWorld world, Random rand, Pos pos){
		world.setBlock(pos,Blocks.flower_pot);
		world.setTileEntity(pos,Meta.generateFlowerPot(RandUtil.anyOf(rand,plants)));
	}
	
	private void placeChest(StructureWorld world, Random rand, Pos pos, Facing4 facing){
		world.setBlock(pos,Blocks.chest);
		world.setTileEntity(pos,Meta.generateChest(facing,generateLootGeneral));
	}
}
