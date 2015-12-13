package chylex.hee.world.feature.stronghold.rooms.loot;
import java.util.Arrays;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.init.BlockList;
import chylex.hee.system.abstractions.BlockInfo;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.abstractions.Meta.Skull;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.structure.util.IBlockPicker;
import chylex.hee.world.util.BoundingBox;
import chylex.hee.world.util.Size;

public class StrongholdRoomRelicDungeon extends StrongholdRoomRelic{
	public static StrongholdRoomRelicDungeon[] generateRelicRooms(){
		return Arrays.stream(Facing4.list).map(facing -> new StrongholdRoomRelicDungeon(facing)).toArray(StrongholdRoomRelicDungeon[]::new);
	}
	
	private final Facing4 entranceFrom;
	
	public StrongholdRoomRelicDungeon(Facing4 entranceFrom){
		super(new Size(entranceFrom.getX() != 0 ? 19 : 11,9,entranceFrom.getZ() != 0 ? 19 : 11),new Facing4[]{ entranceFrom.opposite() });
		this.entranceFrom = entranceFrom;
	}
	
	@Override
	public void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		super.generate(inst,world,rand,x,y,z);
		
		Facing4 left = entranceFrom.rotateLeft(), right = entranceFrom.rotateRight();
		Connection connection = connections.get(0);
		PosMutable mpos = new PosMutable();
		Pos point1, point2;
		
		// entrance corners
		for(int side = 0; side < 2; side++){
			Facing4 sideFacing = side == 0 ? left : right;
			
			// lower cube
			point1 = Pos.at(mpos.set(x+connection.offsetX,0,z+connection.offsetZ).move(entranceFrom).move(sideFacing,2));
			point2 = Pos.at(mpos.move(entranceFrom,2).move(sideFacing,2));
			placeCube(world,rand,placeStoneBrick,point1.getX(),y+1,point1.getZ(),point2.getX(),y+4,point2.getZ());
			
			// torch inside the cube
			mpos.set(point1).move(entranceFrom);
			placeBlock(world,rand,placeIronBars,mpos.x,y+2,mpos.z);
			world.setAttentionWhore(mpos.x+sideFacing.getX(),y+2,mpos.z+sideFacing.getZ(),new BlockInfo(Blocks.torch,Meta.torchGround));
			
			// upper cube
			point1 = Pos.at(mpos.set(x+connection.offsetX,0,z+connection.offsetZ).move(entranceFrom).move(sideFacing));
			point2 = Pos.at(mpos.move(entranceFrom,3).move(sideFacing,3));
			placeCube(world,rand,placeStoneBrick,point1.getX(),y+5,point1.getZ(),point2.getX(),y+maxY-1,point2.getZ());
			
			// stairs
			point1 = Pos.at(mpos.set(x+connection.offsetX,0,z+connection.offsetZ).move(entranceFrom).move(sideFacing));
			point2 = Pos.at(mpos.move(entranceFrom,3));
			placeLine(world,rand,placeStoneBrickStairs(sideFacing,true),point1.getX(),y+4,point1.getZ(),point2.getX(),y+4,point2.getZ());
			
			point1 = Pos.at(mpos.move(sideFacing));
			point2 = Pos.at(mpos.set(point1).move(sideFacing,2));
			placeLine(world,rand,placeStoneBrickStairs(entranceFrom.opposite(),true),point1.getX(),y+4,point1.getZ(),point2.getX(),y+4,point2.getZ());
		}
		
		// top stairs
		mpos.set(x+connection.offsetX,0,z+connection.offsetZ).move(entranceFrom,16);
		placeLine(world,rand,placeStoneBrickStairs(entranceFrom,true),mpos.x+4*left.getX(),y+maxY-1,mpos.z+4*left.getZ(),mpos.x+4*right.getX(),y+maxY-1,mpos.z+4*right.getZ());
		
		mpos.move(entranceFrom.opposite());
		
		for(int side = 0; side < 2; side++){
			Facing4 sideFacing = side == 0 ? left : right;
			Pos point = Pos.at(mpos).offset(sideFacing,4);
			
			placeLine(world,rand,placeStoneBrickStairs(sideFacing,true),point.getX(),y+maxY-1,point.getZ(),point.getX()-10*entranceFrom.getX(),y+maxY-1,point.getZ()-10*entranceFrom.getZ());
		}
		
		// redstone blood
		mpos.set(x+connection.offsetX,0,z+connection.offsetZ).move(entranceFrom,7);
		BoundingBox area = new BoundingBox(Pos.at(mpos).offset(left,4),Pos.at(mpos).offset(right,4).offset(entranceFrom,9));
		
		for(int redstoneAttempt = 9+rand.nextInt(6); redstoneAttempt > 0; redstoneAttempt--){
			placeBlock(world,rand,IBlockPicker.basic(Blocks.redstone_wire),area.x1+rand.nextInt(1+area.x2-area.x1),y+1,area.z1+rand.nextInt(1+area.z2-area.z1));
		}
		
		// side wall pillars
		IBlockPicker placeStoneWall = random -> new BlockInfo(BlockList.stone_brick_wall,random.nextInt(4) == 0 ? Meta.cobbleWallMossy : Meta.cobbleWallNormal);
		mpos.set(x+connection.offsetX,0,z+connection.offsetZ).move(entranceFrom,6);
		
		for(int part = 0; part < 3; part++){
			placeLine(world,rand,placeStoneWall,mpos.x+3*left.getX(),y+1,mpos.z+3*left.getZ(),mpos.x+3*left.getX(),y+maxY-1,mpos.z+3*left.getZ());
			placeLine(world,rand,placeStoneWall,mpos.x+3*right.getX(),y+1,mpos.z+3*right.getZ(),mpos.x+3*right.getX(),y+maxY-1,mpos.z+3*right.getZ());
			mpos.move(entranceFrom,3);
		}
		
		// wall behind chest area
		mpos.set(x+connection.offsetX,0,z+connection.offsetZ).move(entranceFrom,17);
		placeLine(world,rand,placeStoneBrick,mpos.x+4*left.getX(),y+1,mpos.z+4*left.getZ(),mpos.x+4*right.getX(),y+maxY-1,mpos.z+4*right.getZ());

		placeBlock(world,rand,IBlockPicker.basic(Blocks.torch,Meta.torchGround),mpos.x+2*left.getX(),y+4,mpos.z+2*left.getZ());
		placeBlock(world,rand,IBlockPicker.basic(Blocks.torch,Meta.torchGround),mpos.x+2*right.getX(),y+4,mpos.z+2*right.getZ());
		
		// chest area
		mpos.move(entranceFrom.opposite(),2);
		placeLine(world,rand,placeStoneBrickStairs(entranceFrom,false),mpos.x+2*left.getX(),y+1,mpos.z+2*left.getZ(),mpos.x+2*right.getX(),y+1,mpos.z+2*right.getZ());
		mpos.move(entranceFrom);
		placeLine(world,rand,placeStoneBrickPlain,mpos.x+2*left.getX(),y+1,mpos.z+2*left.getZ(),mpos.x+2*right.getX(),y+1,mpos.z+2*right.getZ());
		placeBlock(world,rand,placeStoneBrickStairs(right,false),mpos.x+2*left.getX(),y+1,mpos.z+2*left.getZ());
		placeBlock(world,rand,placeStoneBrickStairs(left,false),mpos.x+2*right.getX(),y+1,mpos.z+2*right.getZ());
		
		point1 = Pos.at(mpos).offset(rand.nextBoolean() ? left : right);
		point2 = Pos.at(mpos).offset(entranceFrom.opposite(),3);
		placeBlock(world,rand,IBlockPicker.basic(Blocks.skull,Meta.skullGround),point1.getX(),y+2,point1.getZ());
		world.setTileEntity(point1.getX(),y+2,point1.getZ(),Meta.generateSkullGround(Skull.SKELETON,point1,point2));
		
		// chest itself
		placeBlock(world,rand,IBlockPicker.basic(BlockList.loot_chest),mpos.x,y+2,mpos.z);
		world.setTileEntity(mpos.x,y+2,mpos.z,Meta.generateChest(entranceFrom.opposite(),getRelicGenerator()));
	}
}
