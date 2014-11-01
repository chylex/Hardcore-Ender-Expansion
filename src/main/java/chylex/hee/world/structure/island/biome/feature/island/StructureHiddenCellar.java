package chylex.hee.world.structure.island.biome.feature.island;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.Direction;
import chylex.hee.block.BlockList;
import chylex.hee.block.BlockPersegrit;
import chylex.hee.system.weight.IWeightProvider;
import chylex.hee.system.weight.WeightedList;
import chylex.hee.world.structure.island.biome.feature.AbstractIslandStructure;
import chylex.hee.world.util.BlockLocation;

public class StructureHiddenCellar extends AbstractIslandStructure{
	private static final byte[] horCheckX = new byte[]{ -1, 0, 1, -1, 1, -1, 0, 1 },
								horCheckZ = new byte[]{ -1, -1, -1, 0, 0, 1, 1, 1 };
	
	public enum EnchantedIslandVariation{
		HOMELAND, LABORATORY
	}
	
	private EnchantedIslandVariation variation;
	private final List<BlockLocation> patternBlocks = new ArrayList<>();
	
	public void setVariation(EnchantedIslandVariation variation){
		this.variation = variation;
	}
	
	@Override
	protected boolean generate(Random rand){
		patternBlocks.clear();
		
		int height = 4+rand.nextInt(4);
		
		for(int attempt = 0, x, y, z; attempt < 50; attempt++){
			x = getRandomXZ(rand,32);
			y = 10+rand.nextInt(30);
			z = getRandomXZ(rand,32);
			
			RoomInfo info = genRoom(x,z,3+rand.nextInt(6),y,height,rand);
			if (info == null)continue;
			
			List<RoomInfo> rooms = new ArrayList<RoomInfo>();
			rooms.add(info);
			
			for(int roomAttemptsLeft = 70+rand.nextInt(110), roomsLeft = 5+rand.nextInt(16), side, dist, hWidth, offX, offZ; roomAttemptsLeft >= 0 && roomsLeft > 0; roomAttemptsLeft--){
				RoomInfo room = rooms.get(rand.nextInt(rooms.size()));
				if ((side = room.getUseableSide(rand)) == -1)continue;
				
				offX = Direction.offsetX[side];
				offZ = Direction.offsetZ[side];
				
				hWidth = 3+rand.nextInt(6);
				if (hWidth == 3)hWidth = 2;
				
				dist = room.halfWidth+hWidth+2+rand.nextInt(7);
				
				RoomInfo newRoom = genRoom(room.x+offX*dist,room.z+offZ*dist,hWidth,y,height,rand);
				if (newRoom == null)continue;
				
				room.canUseSide[side] = false;
				newRoom.canUseSide[Direction.rotateOpposite[side]] = false;
				rooms.add(newRoom);
				
				genHall(room.x+(room.halfWidth-1)*offX-offZ*2,room.z+(room.halfWidth-1)*offZ-offX*2,newRoom.x-(hWidth-1)*offX+offZ*2,newRoom.z-(hWidth-1)*offZ+offX*2,y,height,rand);
			}
			
			genPatterns(rand);
			return true;
		}
		
		return false;
	}

	private RoomInfo genRoom(int x, int z, int halfWidth, int bottomY, int height, Random rand){
		if (halfWidth == 3)halfWidth = 2; // too small, turn it into a "hall"
		
		int halfHeight = height>>1;

		if (world.getBlock(x,bottomY+halfHeight*2+2,z) != Blocks.end_stone)return null;
		
		for(int horCheck = 0; horCheck < 8; horCheck++){
			for(int yCheck = -1; yCheck <= 1; yCheck++){
				if (world.getBlock(x+horCheckX[horCheck]*halfWidth,bottomY+halfHeight+yCheck*(halfHeight+1),z+horCheckZ[horCheck]*halfWidth) != Blocks.end_stone)return null;
			}
		}
		
		int x1 = x-halfWidth, z1 = z-halfWidth, x2 = x+halfWidth, z2 = z+halfWidth;
		
		for(int xx = x1; xx <= x2; xx++){
			for(int zz = z1; zz <= z2; zz++){
				for(int yy = bottomY; yy <= bottomY+height; yy++){
					boolean edge = xx == x1 || xx == x2 || zz == z1 || zz == z2 || yy == bottomY || yy == bottomY+height;
					world.setBlock(xx,yy,zz,edge ? BlockList.persegrit : Blocks.air);
					if (edge && rand.nextInt(60) == 0)patternBlocks.add(new BlockLocation(xx,yy,zz));
				}
			}
		}
		
		if (halfWidth != 2)genRoomContent(x,z,halfWidth,bottomY,height,rand);
		return new RoomInfo(x,z,halfWidth);
	}
	
	private enum RoomContent implements IWeightProvider{
		NONE(50);
		
		static final WeightedList<RoomContent> weights = new WeightedList<>(values());
		private final int weight;
		
		private RoomContent(int weight){
			this.weight = weight;
		}
		
		@Override
		public int getWeight(){
			return weight;
		}
	}
	
	private void genRoomContent(int x, int z, int halfWidth, int bottomY, int height, Random rand){
		RoomContent type = RoomContent.weights.getRandomItem(rand);
		if (type == RoomContent.NONE)return;
		
		// TODO
	}
	
	private void genHall(int x1, int z1, int x2, int z2, int bottomY, int height, Random rand){
		for(int xx = Math.min(x1,x2), xMax = Math.max(x1,x2); xx <= xMax; xx++){
			for(int zz = Math.min(z1,z2), zMax = Math.max(z1,z2); zz <= zMax; zz++){
				for(int yy = bottomY; yy <= bottomY+height; yy++){
					boolean edge = xx == x1 || xx == x2 || zz == z1 || zz == z2 || yy == bottomY || yy == bottomY+height;
					
					if (!edge || world.getBlock(xx,yy,zz) != Blocks.air){
						world.setBlock(xx,yy,zz,edge ? BlockList.persegrit : Blocks.air);
						if (edge && rand.nextInt(60) == 0)patternBlocks.add(new BlockLocation(xx,yy,zz));
					}
				}
			}
		}
	}
	
	private void genPatterns(Random rand){
		List<BlockLocation> connections = new ArrayList<>();
		
		for(BlockLocation loc:patternBlocks){
			int x = loc.x, y = loc.y, z = loc.z, addX = 0, addY = 0, addZ = 0, iterations = 6+rand.nextInt(30+rand.nextInt(60));
			boolean wall = true;
			
			if (!isWall(x,y,z)){
				if (rand.nextBoolean())addX = rand.nextInt(2)*2-1;
				else addZ = rand.nextInt(2)*2-1;
				
				wall = false;
			}
			else if (world.getBlock(x-1,y,z) != BlockList.persegrit || world.getBlock(x+1,y,z) != BlockList.persegrit){
				if (rand.nextInt(3) != 0)addY = rand.nextInt(2)*2-1;
				else addZ = rand.nextInt(2)*2-1;
			}
			else if (world.getBlock(x,y,z-1) != BlockList.persegrit || world.getBlock(x,y,z+1) != BlockList.persegrit){
				if (rand.nextInt(3) != 0)addY = rand.nextInt(2)*2-1;
				else addX = rand.nextInt(2)*2-1;
			}
			
			for(int iteration = 0; iteration <= iterations; iteration++){
				x += addX;
				y += addY;
				z += addZ;
				
				if (isCorner(x,y,z)){
					if (addX != 0){
						addZ = world.getBlock(x,y,z-1) == BlockList.persegrit ? -1 : 1;
						addX = 0;
					}
					else if (addZ != 0){
						addX = world.getBlock(x-1,y,z) == BlockList.persegrit ? -1 : 1;
						addZ = 0;
					}
				}
				else if (rand.nextInt(12) == 0){
					int newAddX = 0, newAddY = 0, newAddZ = 0;
					
					if (!isWall(x,y,z)){
						if (rand.nextBoolean())newAddX = rand.nextBoolean() ? -1 : 1;
						else newAddZ = rand.nextBoolean() ? -1 : 1;
					}
					else if (world.getBlock(x-1,y,z) != BlockList.persegrit || world.getBlock(x+1,y,z) != BlockList.persegrit){
						if (rand.nextBoolean())newAddY = rand.nextInt(2)*2-1;
						else newAddZ = rand.nextInt(2)*2-1;
					}
					else if (world.getBlock(x,y,z-1) != BlockList.persegrit || world.getBlock(x,y,z+1) != BlockList.persegrit){
						if (rand.nextBoolean())newAddX = rand.nextInt(2)*2-1;
						else newAddY = rand.nextInt(2)*2-1;
					}
					
					if (!isCorner(x+newAddX,y+newAddY,z+newAddZ)){
						addX = newAddX;
						addY = newAddY;
						addZ = newAddZ;
					}
				}
				
				if (world.getBlock(x,y,z) == BlockList.persegrit){
					world.setBlock(x,y,z,BlockList.persegrit,15);
					connections.add(new BlockLocation(x,y,z));
				}
				else break;
			}
		}
		
		for(BlockLocation loc:connections){
			world.setBlock(loc.x,loc.y,loc.z,BlockList.persegrit,BlockPersegrit.getConnectionMeta(world,rand,loc.x,loc.y,loc.z));
		}
	}
	
	private boolean isCorner(int x, int y, int z){
		Block pg = BlockList.persegrit;
		
		Block l = world.getBlock(x-1,y,z), r = world.getBlock(x+1,y,z),
			  u = world.getBlock(x,y,z-1), d = world.getBlock(x,y,z+1),
			  b = world.getBlock(x,y-1,z), t = world.getBlock(x,y+1,z);
		
		return (((l == pg && r == pg) || (u == pg && d == pg)) && ((b == pg && t != pg) || (b != pg && t == pg))) ||
				(t == pg && b == pg && ((l == pg && r != pg) || (u == pg && d != pg) || (l != pg && r == pg) || (u != pg && d == pg)));
	}
	
	private boolean isWall(int x, int y, int z){
		return world.getBlock(x,y-1,z) == BlockList.persegrit || world.getBlock(x,y+1,z) == BlockList.persegrit;
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
