package chylex.hee.world.structure.island.biome.feature.forest.ravageddungeon;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import scala.actors.threadpool.Arrays;
import chylex.hee.block.BlockList;
import chylex.hee.block.BlockRavagedBrick;
import chylex.hee.system.weight.ObjectWeightPair;
import chylex.hee.system.weight.WeightedList;
import chylex.hee.tileentity.TileEntityCustomSpawner;
import chylex.hee.tileentity.spawner.LouseSpawnerLogic;
import chylex.hee.tileentity.spawner.LouseSpawnerLogic.LouseSpawnData;
import chylex.hee.world.structure.island.biome.IslandBiomeBase;
import chylex.hee.world.structure.util.Facing;
import chylex.hee.world.structure.util.pregen.ITileEntityGenerator;
import chylex.hee.world.structure.util.pregen.LargeStructureWorld;

public final class RavagedDungeonPlacer implements ITileEntityGenerator{
	private static final byte radEntrance = 2, radHallway = 2, radRoom = 7;
	
	private final byte hallHeight;
	private byte level;
	
	public RavagedDungeonPlacer(int hallHeight){
		this.hallHeight = (byte)hallHeight;
	}
	
	public void setDungeonLevel(int level){
		this.level = (byte)level;
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
		NONE, DESTROYED_WALLS, STAIR_PATTERN, EMBEDDED_CHEST, COBWEBS, FLOOR_CEILING_SLABS, LAPIS_BLOCK, WALL_MOUNTED_SPAWNERS, DEAD_END_CHEST, FLOWER_POT,
		SPAWNERS_IN_WALLS
	}
	
	private static final WeightedList<ObjectWeightPair<EnumHallwayDesign>> hallwayDesignList = new WeightedList<>(
		ObjectWeightPair.make(EnumHallwayDesign.NONE, 88),
		ObjectWeightPair.make(EnumHallwayDesign.DEAD_END_CHEST, 45),
		ObjectWeightPair.make(EnumHallwayDesign.DESTROYED_WALLS, 44),
		ObjectWeightPair.make(EnumHallwayDesign.STAIR_PATTERN, 32),
		ObjectWeightPair.make(EnumHallwayDesign.EMBEDDED_CHEST, 30),
		ObjectWeightPair.make(EnumHallwayDesign.COBWEBS, 24),
		ObjectWeightPair.make(EnumHallwayDesign.SPAWNERS_IN_WALLS, 24),
		ObjectWeightPair.make(EnumHallwayDesign.FLOOR_CEILING_SLABS, 21),
		ObjectWeightPair.make(EnumHallwayDesign.WALL_MOUNTED_SPAWNERS, 19),
		ObjectWeightPair.make(EnumHallwayDesign.FLOWER_POT, 15),
		ObjectWeightPair.make(EnumHallwayDesign.LAPIS_BLOCK, 4)
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
							
							if (ax2 == 3 && !canReplaceBlock(world.getBlock(x+rotX(off,ax1,ax2),yy,z+rotZ(off,ax1,ax2))))continue;
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
				boolean canGenerate = true;
				
				for(int yy = y+1; yy <= y+hallHeight && canGenerate; yy++){
					for(int ax1 = -sz; ax1 <= sz && canGenerate; ax1++){
						for(int ax2 = 2; ax2 <= 3 && canGenerate; ax2++){
							if (ax2 == 3 && !canReplaceBlock(world.getBlock(x+rotX(off,ax1,ax2),yy,z+rotZ(off,ax1,ax2)))){
								if (world.getBlock(x+rotX(off,ax1,ax2),yy,z+rotZ(off,ax1,ax2)) == Blocks.chest){
									canGenerate = false;
								}
								
								continue;
							}
							
							world.setBlock(x+rotX(off,ax1,ax2),yy,z+rotZ(off,ax1,ax2),ax2 == 2 ? Blocks.air : BlockList.ravaged_brick,ax2 == 2 ? 0 : getBrickMeta(rand));
						}
						
						world.setBlock(x+rotX(off,ax1,2),y+hallHeight,z+rotZ(off,ax1,2),BlockList.ravaged_brick_stairs,4+offFacing.getStairs());
					}
				}
				
				if (!canGenerate)break;

				if (sz == 2){
					world.setBlock(x+rotX(off,-2,2),y+1,z+rotZ(off,-2,2),BlockList.ravaged_brick_stairs,4+(isLR ? offFacing.getRotatedRight() : offFacing.getRotatedLeft()).getStairs());
					world.setBlock(x+rotX(off,2,2),y+1,z+rotZ(off,2,2),BlockList.ravaged_brick_stairs,4+(isLR ? offFacing.getRotatedLeft() : offFacing.getRotatedRight()).getStairs());
				}
				
				for(int a = 0; a < 2; a++)world.setBlock(x+rotX(off,-1+2*a,2),y+1,z+rotZ(off,-1+2*a,2),BlockList.ravaged_brick_stairs,4+offFacing.getStairs());
				world.setBlock(x+rotX(off,0,2),y+1,z+rotZ(off,0,2),BlockList.ravaged_brick);
				
				world.setBlock(x+rotX(off,0,2),y+2,z+rotZ(off,0,2),Blocks.chest,offFacing.getReversed().get6Directional());
				world.setTileEntityGenerator(x+rotX(off,0,2),y+2,z+rotZ(off,0,2),"hallwayEmbeddedChest",this);
				break;
				
			case DEAD_END_CHEST:
				if (!isDeadEnd)break;
				
				off = DungeonDir.DOWN;
				
				for(DungeonDir dir:DungeonDir.values){
					if (hallway.checkConnection(dir)){
						off = dir;
						break;
					}
				}
				
				offFacing = dirToFacing(off);
				
				for(int ax1 = -1; ax1 <= 1; ax1++){
					world.setBlock(x+rotX(off,ax1,1),y+hallHeight,z+rotZ(off,ax1,1),BlockList.ravaged_brick_stairs,4+offFacing.getStairs());
					if (ax1 != 0)world.setBlock(x+rotX(off,ax1,1),y+1,z+rotZ(off,ax1,1),BlockList.ravaged_brick_stairs,offFacing.getStairs());
				}
				
				world.setBlock(x+rotX(off,0,1),y+1,z+rotZ(off,0,1),BlockList.ravaged_brick,offFacing.getStairs());
				world.setBlock(x+rotX(off,0,1),y+2,z+rotZ(off,0,1),Blocks.chest,offFacing.getReversed().get6Directional());
				world.setTileEntityGenerator(x+rotX(off,0,1),y+2,z+rotZ(off,0,1),"hallwayDeadEndChest",this);
				
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
				for(int attempt = 0, attempts = 6+rand.nextInt(7), xx, yy, zz; attempt < attempts; attempt++){
					xx = x+rand.nextInt(radHallway+1)-rand.nextInt(radHallway+1);
					zz = z+rand.nextInt(radHallway+1)-rand.nextInt(radHallway+1);
					yy = rand.nextBoolean() ? y+1 : y+hallHeight;
					
					if (world.isAir(xx,yy,zz))world.setBlock(xx,yy,zz,BlockList.ravaged_brick_slab,yy == y+1 ? 0 : 8);
				}
				
				break;
				
			case LAPIS_BLOCK:
				world.setBlock(x,y,z,Blocks.lapis_block);
				world.setBlock(x,y-1,z,BlockList.ravaged_brick);
				break;
				
			case WALL_MOUNTED_SPAWNERS:
				List<DungeonDir> availableDirs = new ArrayList<DungeonDir>(Arrays.asList(DungeonDir.values));
				
				for(int attempt = 0, placed = 0; attempt < 1+rand.nextInt(3) && !availableDirs.isEmpty(); attempt++){
					off = availableDirs.remove(rand.nextInt(availableDirs.size()));
					
					if (hallway.checkConnection(off)){
						if (placed == 0)--attempt;
						continue;
					}
					
					world.setBlock(x+rotX(off,0,-1),y+2,z+rotZ(off,0,-1),BlockList.ravaged_brick_slab,8);
					world.setBlock(x+rotX(off,0,-1),y+3,z+rotZ(off,0,-1),BlockList.custom_spawner,2);
					world.setTileEntityGenerator(x+rotX(off,0,-1),y+3,z+rotZ(off,0,-1),"louseSpawner",this);
					world.setBlock(x+rotX(off,0,-1),y+4,z+rotZ(off,0,-1),BlockList.ravaged_brick_slab,0);
				}
				
				break;
				
			case SPAWNERS_IN_WALLS:
				int spawnerMeta = rand.nextBoolean() ? 1 : 2;
				
				for(int attempt = 0, placed = 0, maxPlaced = 4+rand.nextInt(6), xx, yy, zz; attempt < 25 && placed < maxPlaced; attempt++){
					xx = x+rand.nextInt(radHallway+1)-rand.nextInt(radHallway+1);
					zz = z+rand.nextInt(radHallway+1)-rand.nextInt(radHallway+1);
					yy = rand.nextBoolean() ? y+1 : y+hallHeight;
					
					if (world.getBlock(xx,yy,zz) == BlockList.ravaged_brick){
						world.setBlock(xx,yy,zz,BlockList.custom_spawner,spawnerMeta);
						if (spawnerMeta == 2)world.setTileEntityGenerator(xx,yy,zz,"louseSpawner",this);
						++placed;
					}
				}
				
				break;
				
			case FLOWER_POT:
				int type = rand.nextInt(6);
				
				if (type == 0){ // embedded in wall
					if (!isStraight)break;
					
					off = hallway.checkConnection(DungeonDir.UP) || hallway.checkConnection(DungeonDir.DOWN) ? DungeonDir.LEFT : DungeonDir.UP;
					if (rand.nextBoolean())off = off.reversed();
					
					for(int ax1 = -1; ax1 <= 1; ax1++){
						if (canReplaceBlock(world.getBlock(x+rotX(off,ax1,3),y+2,z+rotZ(off,ax1,3))))world.setBlock(x+rotX(off,ax1,3),y+2,z+rotZ(off,ax1,3),BlockList.ravaged_brick,getBrickMeta(rand));
						
						world.setBlock(x+rotX(off,ax1,2),y+2,z+rotZ(off,ax1,2),Blocks.flower_pot);
						world.setTileEntityGenerator(x+rotX(off,ax1,2),y+2,z+rotZ(off,ax1,2),"flowerPot",this);
					}
				}
				else if (type == 1 || type == 2){ // floor
					int xx = x+rand.nextInt(3)-1, zz = z+rand.nextInt(3)-1;
					world.setBlock(xx,y+1,zz,Blocks.flower_pot);
					world.setTileEntityGenerator(xx,y+1,zz,"flowerPot",this);
				}
				else if (type == 3 || type == 4){ // slab on wall
					if (isFullyOpen)break;
					
					off = DungeonDir.values[rand.nextInt(DungeonDir.values.length)];
					while(hallway.checkConnection(off))off = DungeonDir.values[rand.nextInt(DungeonDir.values.length)];
					
					world.setBlock(x+rotX(off,0,-1),y+2,z+rotZ(off,0,-1),BlockList.ravaged_brick_slab,8);
					world.setBlock(x+rotX(off,0,-1),y+3,z+rotZ(off,0,-1),Blocks.flower_pot);
					world.setTileEntityGenerator(x+rotX(off,0,-1),y+3,z+rotZ(off,0,-1),"flowerPot",this); // TODO add support for custom flowers
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
	 * END
	 */
	
	public void generateEnd(LargeStructureWorld world, Random rand, int[] x, int y, int[] z, List<DungeonElement> end){
		int minX = x[0], maxX = x[0], minZ = z[0], maxZ = z[0];
		
		for(int a = 1; a < x.length; a++){
			if (x[a] < minX)minX = x[a];
			if (x[a] > maxX)maxX = x[a];
			if (z[a] < minZ)minZ = z[a];
			if (z[a] > maxZ)maxZ = z[a];
		}
		
		for(int yy = y; yy <= y+hallHeight+1; yy++){
			for(int xx = minX-radRoom; xx <= maxX+radRoom; xx++){
				for(int zz = minZ-radRoom; zz <= maxZ+radRoom; zz++){
					if (yy == y || yy == y+hallHeight+1 || xx == minX-radRoom || xx == maxX+radRoom || zz == minZ-radRoom || zz == maxZ+radRoom){
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
			// TODO general loot
		}
		else if (key.equals("hallwayDeadEndChest")){
			// TODO rare
		}
		else if (key.equals("louseSpawner")){
			LouseSpawnerLogic logic = (LouseSpawnerLogic)((TileEntityCustomSpawner)tile).getSpawnerLogic();
			logic.setLouseSpawnData(new LouseSpawnData(level,rand));
		}
		else if (key.equals("flowerPot")){
			TileEntityFlowerPot flowerPot = (TileEntityFlowerPot)tile;
			ItemStack is = RavagedDungeonLoot.flowerPotItems[rand.nextInt(RavagedDungeonLoot.flowerPotItems.length)].copy();
			
			flowerPot.func_145964_a(is.getItem(),is.getItemDamage());
			flowerPot.markDirty();

			if (!flowerPot.getWorldObj().setBlockMetadataWithNotify(flowerPot.xCoord,flowerPot.yCoord,flowerPot.zCoord,is.getItemDamage(),2)){
				flowerPot.getWorldObj().markBlockForUpdate(flowerPot.xCoord,flowerPot.yCoord,flowerPot.zCoord);
			}
		}
	}
	
	/*
	 * UTILITIES
	 */
	
	private static boolean canReplaceBlock(Block block){
		return block == Blocks.end_stone || block == BlockList.end_terrain || block == Blocks.air;
	}
	
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
