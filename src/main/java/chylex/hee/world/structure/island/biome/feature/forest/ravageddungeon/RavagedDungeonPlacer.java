package chylex.hee.world.structure.island.biome.feature.forest.ravageddungeon;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import chylex.hee.block.BlockList;
import chylex.hee.block.BlockRavagedBrick;
import chylex.hee.system.weight.ObjectWeightPair;
import chylex.hee.system.weight.WeightedList;
import chylex.hee.world.structure.island.biome.IslandBiomeBase;
import chylex.hee.world.structure.util.Facing;
import chylex.hee.world.structure.util.pregen.ITileEntityGenerator;
import chylex.hee.world.structure.util.pregen.LargeStructureWorld;

public final class RavagedDungeonPlacer implements ITileEntityGenerator{
	private static final byte radEntrance = 2, radHallway = 2, radRoom = 7;
	
	private final byte hallHeight;
	
	public RavagedDungeonPlacer(int hallHeight){
		this.hallHeight = (byte)hallHeight;
	}
	
	/*
	 * ENTRANCE
	 */
	
	public void generateEntrance(LargeStructureWorld world, Random rand, int x, int y, int z, int maxEntranceHeight, DungeonElement entrance){
		int surfaceY = y-maxEntranceHeight-1;
		while(++surfaceY <= y){
			if (world.getBlock(x,surfaceY,z) == IslandBiomeBase.getTopBlock())break;
		}
		
		for(int yy = surfaceY+5; yy >= y-maxEntranceHeight; yy--){
			for(int xx = x-radEntrance; xx <= x+radEntrance; xx++){
				for(int zz = z-radEntrance; zz <= z+radEntrance; zz++){
					if (yy > surfaceY || (Math.abs(xx-x) <= radEntrance-1 && Math.abs(zz-z) <= radEntrance-1 && yy != y-maxEntranceHeight))world.setBlock(xx,yy,zz,Blocks.air);
					else world.setBlock(xx,yy,zz,BlockList.ravaged_brick,getBrickMeta(rand));
				}
			}
		}
	}
	
	/*
	 * DESCEND
	 */
	
	public void generateDescend(LargeStructureWorld world, Random rand, int x, int y, int z, DungeonElement descend){
		for(int yy = y; yy >= y-hallHeight-2; yy--){
			for(int xx = x-radEntrance; xx <= x+radEntrance; xx++){
				for(int zz = z-radEntrance; zz <= z+radEntrance; zz++){
					if (Math.abs(xx-x) <= 1 && Math.abs(zz-z) <= radEntrance-1 && yy != y-hallHeight-2)world.setBlock(xx,yy,zz,Blocks.air);
					else world.setBlock(xx,yy,zz,BlockList.ravaged_brick,getBrickMeta(rand));
				}
			}
		}
	}
	
	/*
	 * HALLWAY
	 */
	
	private enum EnumHallwayDesign{
		NONE, DESTROYED_WALLS, STAIR_PATTERN, EMBEDDED_CHEST, COBWEBS, FLOOR_CEILING_SLABS
	}
	
	private static final WeightedList<ObjectWeightPair<EnumHallwayDesign>> hallwayDesignList = new WeightedList<>(
		ObjectWeightPair.make(EnumHallwayDesign.NONE, 100),
		ObjectWeightPair.make(EnumHallwayDesign.EMBEDDED_CHEST, 48),
		ObjectWeightPair.make(EnumHallwayDesign.DESTROYED_WALLS, 42),
		ObjectWeightPair.make(EnumHallwayDesign.STAIR_PATTERN, 30),
		ObjectWeightPair.make(EnumHallwayDesign.COBWEBS, 24),
		ObjectWeightPair.make(EnumHallwayDesign.FLOOR_CEILING_SLABS, 24)
	);
	
	public void generateHallway(LargeStructureWorld world, Random rand, int x, int y, int z, DungeonElement hallway){
		for(int yy = y; yy <= y+hallHeight+1; yy++){
			for(int xx = x-radHallway; xx <= x+radHallway; xx++){
				for(int zz = z-radHallway; zz <= z+radHallway; zz++){
					if (yy == y || yy == y+hallHeight+1 || xx == x-radHallway || xx == x+radHallway || zz == z-radHallway || zz == z+radHallway){
						world.setBlock(xx,yy,zz,BlockList.ravaged_brick,getBrickMeta(rand));
					}
					else world.setBlock(xx,yy,zz,Blocks.air);
				}
			}
		}
		
		EnumHallwayDesign design = hallwayDesignList.getRandomItem(rand).getObject();
		if (design == EnumHallwayDesign.NONE)return;
		
		boolean isStraight = (hallway.checkConnection(DungeonDir.UP) && hallway.checkConnection(DungeonDir.DOWN) && !hallway.checkConnection(DungeonDir.LEFT) && !hallway.checkConnection(DungeonDir.RIGHT)) ||
							 (hallway.checkConnection(DungeonDir.LEFT) && hallway.checkConnection(DungeonDir.RIGHT) && !hallway.checkConnection(DungeonDir.UP) && !hallway.checkConnection(DungeonDir.DOWN));
		boolean isDeadEnd = ((hallway.checkConnection(DungeonDir.UP) ? 1 : 0) + (hallway.checkConnection(DungeonDir.DOWN) ? 1 : 0) + (hallway.checkConnection(DungeonDir.LEFT) ? 1 : 0) + (hallway.checkConnection(DungeonDir.RIGHT) ? 1 : 0)) == 1;
		boolean isFullyOpen = hallway.checkConnection(DungeonDir.UP) && hallway.checkConnection(DungeonDir.LEFT) && hallway.checkConnection(DungeonDir.DOWN) && hallway.checkConnection(DungeonDir.RIGHT);
		
		DungeonDir off;
		Facing offFacing;
		boolean isLR;
		
		switch(design){
			case DESTROYED_WALLS:				
				for(int attempt = 0, attempts = 80+rand.nextInt(40+rand.nextInt(30)), xx, yy, zz; attempt < attempts; attempt++){
					xx = x+rand.nextInt(radHallway+1)-rand.nextInt(radHallway+1);
					zz = z+rand.nextInt(radHallway+1)-rand.nextInt(radHallway+1);
					yy = y+1+rand.nextInt(hallHeight);
					
					if (world.getBlock(xx,yy,zz) == BlockList.ravaged_brick){
						world.setBlock(xx,yy,zz,BlockList.ravaged_brick,BlockRavagedBrick.metaDamaged1+rand.nextInt(1+BlockRavagedBrick.metaDamaged4-BlockRavagedBrick.metaDamaged1));
					}
				}
				
				break;
				
			case STAIR_PATTERN:
				if (isFullyOpen)break;
				
				off = DungeonDir.values[rand.nextInt(DungeonDir.values.length)];
				while(hallway.checkConnection(off))off = DungeonDir.values[rand.nextInt(DungeonDir.values.length)];
				offFacing = dirToFacing(off);
				
				isLR = off == DungeonDir.LEFT || off == DungeonDir.RIGHT;
				int metaStairsLeft = (isLR ? offFacing.getRotatedRight() : offFacing.getRotatedLeft()).getStairs()+(rand.nextBoolean() ? 0 : 4),
					metaStairsRight = (isLR ? offFacing.getRotatedLeft() : offFacing.getRotatedRight()).getStairs()+(rand.nextBoolean() ? 0 : 4);
				
				for(int yy = y+1; yy <= y+hallHeight; yy++){
					for(int ax1 = -1; ax1 <= 1; ax1++){
						for(int ax2 = 2; ax2 <= 3; ax2++){
							if (ax2 == 2 && (ax1 == -1 || ax1 == 1)){
								world.setBlock(x+rotX(off,ax1,2),yy,z+rotZ(off,ax1,2),BlockList.ravaged_brick_stairs,ax1 == -1 ? metaStairsLeft : metaStairsRight);
								continue;
							}
							
							if (world.getBlock(x+rotX(off,ax1,ax2),yy,z+rotZ(off,ax1,ax2)) != Blocks.end_stone)continue;
							world.setBlock(x+rotX(off,ax1,ax2),yy,z+rotZ(off,ax1,ax2),ax2 == 2 ? Blocks.air : BlockList.ravaged_brick,ax2 == 2 ? 0 : getBrickMeta(rand));
						}
					}
				}
				
				break;
				
			case EMBEDDED_CHEST:
				if (!isStraight && !isDeadEnd)break;
				
				off = hallway.checkConnection(DungeonDir.UP) || hallway.checkConnection(DungeonDir.DOWN) ? DungeonDir.LEFT : DungeonDir.UP;
				if (rand.nextBoolean())off = off.reversed();
				
				isLR = off == DungeonDir.LEFT || off == DungeonDir.RIGHT; // TODO metadata is a pile of ass, just die already
				
				offFacing = dirToFacing(off);
				
				int sz = isDeadEnd ? 1 : 2;
				
				for(int yy = y+1; yy <= y+hallHeight; yy++){
					for(int ax1 = -sz; ax1 <= sz; ax1++){
						for(int ax2 = 2; ax2 <= 3; ax2++){
							if (world.getBlock(x+rotX(off,ax1,ax2),yy,z+rotZ(off,ax1,ax2)) != Blocks.end_stone)continue;
							world.setBlock(x+rotX(off,ax1,ax2),yy,z+rotZ(off,ax1,ax2),ax2 == 2 ? Blocks.air : BlockList.ravaged_brick,ax2 == 2 ? 0 : getBrickMeta(rand));
						}
						
						world.setBlock(x+rotX(off,ax1,2),y+hallHeight,z+rotZ(off,ax1,2),BlockList.ravaged_brick_stairs,4+offFacing.getStairs());
					}
				}

				if (sz == 2){
					world.setBlock(x+rotX(off,-2,2),y+1,z+rotZ(off,-2,2),BlockList.ravaged_brick_stairs,4+(isLR ? offFacing.getRotatedRight() : offFacing.getRotatedLeft()).getStairs());
					world.setBlock(x+rotX(off,2,2),y+1,z+rotZ(off,2,2),BlockList.ravaged_brick_stairs,4+(isLR ? offFacing.getRotatedLeft() : offFacing.getRotatedRight()).getStairs());
				}
				
				for(int a = 0; a < 2; a++)world.setBlock(x+rotX(off,-1+2*a,2),y+1,z+rotZ(off,-1+2*a,2),BlockList.ravaged_brick_stairs,4+offFacing.getStairs());
				world.setBlock(x+rotX(off,0,2),y+1,z+rotZ(off,0,2),BlockList.ravaged_brick);
				
				world.setBlock(x+rotX(off,0,2),y+2,z+rotZ(off,0,2),Blocks.chest,offFacing.getReversed().get6Directional());
				world.setTileEntityGenerator(x+rotX(off,0,2),y+2,z+rotZ(off,0,2),"hallwayEmbeddedChest",this);
				break;
				
			case COBWEBS:
				for(int attempt = 0, attempts = 2+rand.nextInt(6*(1+rand.nextInt(2))), xx, yy, zz; attempt < attempts; attempt++){
					xx = x+rand.nextInt(radHallway+1)-rand.nextInt(radHallway+1);
					zz = z+rand.nextInt(radHallway+1)-rand.nextInt(radHallway+1);
					yy = y+1+rand.nextInt(hallHeight);
					
					if (world.isAir(xx,yy,zz))world.setBlock(xx,yy,zz,Blocks.web);
				}
				break;
				
			case FLOOR_CEILING_SLABS:
				for(int attempt = 0, attempts = 5+rand.nextInt(8), xx, yy, zz; attempt < attempts; attempt++){
					xx = x+rand.nextInt(radHallway+1)-rand.nextInt(radHallway+1);
					zz = z+rand.nextInt(radHallway+1)-rand.nextInt(radHallway+1);
					yy = rand.nextBoolean() ? y+1 : y+hallHeight;
					
					if (world.isAir(xx,yy,zz))world.setBlock(xx,yy,zz,BlockList.ravaged_brick_slab,yy == y+1 ? 0 : 8);
				}
				break;
				
			case NONE:
			default:
		}
	}
	
	/*
	 * ROOM
	 */
	
	public void generateRoom(LargeStructureWorld world, Random rand, int x, int y, int z, DungeonElement room){
		for(int yy = y; yy <= y+hallHeight+1; yy++){
			for(int xx = x-radRoom; xx <= x+radRoom; xx++){
				for(int zz = z-radRoom; zz <= z+radRoom; zz++){
					if (yy == y || yy == y+hallHeight+1 || xx == x-radRoom || xx == x+radRoom || zz == z-radRoom || zz == z+radRoom){
						world.setBlock(xx,yy,zz,BlockList.ravaged_brick,getBrickMeta(rand));
					}
					else world.setBlock(xx,yy,zz,Blocks.air);
				}
			}
		}
	}
	
	/*
	 * TILE ENTITIES
	 */
	
	@Override
	public void onTileEntityRequested(String key, TileEntity tile, Random rand){
		if (key.equals("hallwayEmbeddedChest")){
			
		}
	}
	
	/*
	 * UTILITIES
	 */
	
	private static int getBrickMeta(Random rand){
		return rand.nextInt(8) != 0 ? 0 : rand.nextInt(12) == 0 ? BlockRavagedBrick.metaCracked : BlockRavagedBrick.metaDamaged1+rand.nextInt(1+BlockRavagedBrick.metaDamaged4-BlockRavagedBrick.metaDamaged1);
	}
	
	private static int rotX(DungeonDir dir, int x, int z){
		switch(dir){
			case UP: return x;
			case DOWN: return -x;
			case LEFT: return z;
			case RIGHT: return -z;
			default: return 0;
		}
	}
	
	private static int rotZ(DungeonDir dir, int x, int z){
		switch(dir){
			case UP: return z;
			case DOWN: return -z;
			case LEFT: return x;
			case RIGHT: return -x;
			default: return 0;
		}
	}
	
	private static Facing dirToFacing(DungeonDir dir){
		switch(dir){
			case UP: return Facing.SOUTH_POSZ;
			case DOWN: return Facing.NORTH_NEGZ;
			case LEFT: return Facing.EAST_POSX;
			case RIGHT: return Facing.WEST_NEGX;
			default: return null;
		}
	}
}
