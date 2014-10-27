package chylex.hee.world.structure.island.biome.feature.island;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.Direction;
import chylex.hee.block.BlockList;
import chylex.hee.world.structure.island.biome.feature.AbstractIslandStructure;
import chylex.hee.world.util.BlockLocation;

public class StructureHiddenCellar extends AbstractIslandStructure{
	private static final byte[] horCheckX = new byte[]{ -1, 0, 1, -1, 1, -1, 0, 1 },
								horCheckZ = new byte[]{ -1, -1, -1, 0, 0, 1, 1, 1 };
	
	public enum EnchantedIslandVariation{
		HOMELAND, LABORATORY
	}
	
	private final EnchantedIslandVariation variation;
	private final List<BlockLocation> patternBlocks = new ArrayList<>();
	
	public StructureHiddenCellar(EnchantedIslandVariation variation){
		this.variation = variation;
	}
	
	@Override
	protected boolean generate(Random rand){
		int height = 3+rand.nextInt(4);
		
		for(int attempt = 0, x, y, z; attempt < 50; attempt++){
			x = getRandomXZ(rand,32);
			y = 10+rand.nextInt(30);
			z = getRandomXZ(rand,32);
			
			RoomInfo info = genRoom(x,z,2+rand.nextInt(4),y,height,rand);
			if (info == null)continue;
			
			List<RoomInfo> rooms = new ArrayList<RoomInfo>();
			rooms.add(info);
			
			for(int roomAttemptsLeft = 70+rand.nextInt(110), roomsLeft = 5+rand.nextInt(16), side, dist, hWidth, offX, offZ; roomAttemptsLeft >= 0 && roomsLeft > 0; roomAttemptsLeft--){
				RoomInfo room = rooms.get(rand.nextInt(rooms.size()));
				if ((side = room.getUseableSide(rand)) == -1)continue;
				
				offX = Direction.offsetX[side];
				offZ = Direction.offsetZ[side];
				
				hWidth = 2+rand.nextInt(4);
				dist = room.halfWidth+hWidth+2+rand.nextInt(7);
				
				RoomInfo newRoom = genRoom(room.x+offX*dist,room.z+offZ*dist,hWidth,y,height,rand);
				if (newRoom == null)continue;
				
				room.canUseSide[side] = false;
				rooms.add(newRoom);
				
				genHall(room.x+room.halfWidth*offX-offZ,room.z+room.halfWidth*offZ-offX,newRoom.x-hWidth*offX+offZ,newRoom.z-hWidth*offZ+offX,y,height,rand);
			}
			
			// TODO pattern generation
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Walls count to the width of the room. Height has to be divisible by 2.
	 */
	private RoomInfo genRoom(int x, int z, int halfWidth, int bottomY, int height, Random rand){
		int halfHeight = height>>1;

		if (world.getBlock(x,bottomY+halfHeight,z) != Blocks.end_stone)return null;
		
		for(int horCheck = 0; horCheck < 8; horCheck++){
			for(int yCheck = -1; yCheck <= 1; yCheck++){
				if (world.getBlock(x+horCheckX[horCheck]*halfWidth,bottomY+halfHeight+yCheck*halfHeight,z+horCheckZ[horCheck]*halfWidth) != Blocks.end_stone)return null;
			}
		}
		
		int x1 = x-halfWidth, z1 = z-halfWidth, x2 = x+halfWidth, z2 = z+halfWidth;
		
		for(int xx = x1; xx <= x2; xx++){
			for(int zz = z1; zz <= z2; zz++){
				for(int yy = bottomY; yy <= bottomY+height; yy++){
					boolean edge = xx == x1 || xx == x2 || zz == z1 || zz == z2 || yy == bottomY || yy == bottomY+height;
					world.setBlock(xx,yy,zz,edge ? BlockList.purplething : Blocks.air);
					
					if (rand.nextInt(60) == 0)patternBlocks.add(new BlockLocation(xx,yy,zz));
				}
			}
		}
		
		// TODO room content
		
		return new RoomInfo(x,z,halfWidth);
	}
	
	private void genHall(int x1, int z1, int x2, int z2, int bottomY, int height, Random rand){
		for(int xx = x1; xx <= x2; xx++){
			for(int zz = z1; zz <= z2; zz++){
				for(int yy = bottomY; yy <= bottomY+height; yy++){
					boolean edge = xx == x1 || xx == x2 || zz == z1 || zz == z2 || yy == bottomY || yy == bottomY+height;
					if (!edge || world.getBlock(xx,yy,zz) == Blocks.end_stone)world.setBlock(xx,yy,zz,edge ? BlockList.purplething : Blocks.air);
					
					if (rand.nextInt(60) == 0)patternBlocks.add(new BlockLocation(xx,yy,zz));
				}
			}
		}
	}
	
	private final class RoomInfo{
		final int x, z;
		final byte halfWidth;
		final boolean[] canUseSide = new boolean[]{ true, true, true, true };
		
		RoomInfo(int x, int z, int halfWidth){
			this.x = x;
			this.z = z;
			this.halfWidth = (byte)halfWidth;
		}
		
		int getUseableSide(Random rand){
			if (!canUseSide[0] && !canUseSide[1] && !canUseSide[2] && !canUseSide[3])return -1;
			
			for(int attempt = 0, side; attempt < 8; attempt++){
				if (canUseSide[side = rand.nextInt(4)])return side;
			}
			
			for(int side = 0; side < 4; side++){
				if (canUseSide[side])return side;
			}
			
			return -1; // should never happen
		}
	}
}
