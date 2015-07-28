package chylex.hee.world.structure.island.biome.feature.forest.ravageddungeon;
import java.util.List;
import java.util.Random;
import chylex.hee.block.BlockRavagedBrick;
import chylex.hee.init.BlockList;
import chylex.hee.system.collections.WeightedList;
import chylex.hee.system.collections.weight.ObjectWeightPair;
import chylex.hee.system.util.CollectionUtil;
import chylex.hee.world.structure.island.biome.IslandBiomeBase;
import chylex.hee.world.structure.util.Facing;
import chylex.hee.world.structure.util.pregen.ITileEntityGenerator;
import chylex.hee.world.structure.util.pregen.LargeStructureWorld;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;

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
	
	private enum EnumDescendDesign{
		PILLARS_WITH_SPAWNER, GOO_CORNERS, FENCE_LIGHTS, CORNER_LIGHTS
	}
	
	private static final WeightedList<ObjectWeightPair<EnumDescendDesign>> descendDesignList = new WeightedList<>(
		ObjectWeightPair.of(EnumDescendDesign.PILLARS_WITH_SPAWNER, 50),
		ObjectWeightPair.of(EnumDescendDesign.FENCE_LIGHTS, 42),
		ObjectWeightPair.of(EnumDescendDesign.GOO_CORNERS, 35),
		ObjectWeightPair.of(EnumDescendDesign.CORNER_LIGHTS, 25)
	);
	
	public void generateDescend(LargeStructureWorld world, Random rand, int x, int y, int z, DungeonElement descend){
		generateRoomLayout(world,rand,x,y,z);
		
		Block coverBlock = rand.nextInt(3) == 0 ? Blocks.glass : Blocks.air;
		
		for(int yy = y; yy >= y-hallHeight-2; yy--){
			for(int xx = x-radEntrance; xx <= x+radEntrance; xx++){
				for(int zz = z-radEntrance; zz <= z+radEntrance; zz++){
					if (Math.abs(xx-x) <= 1 && Math.abs(zz-z) <= radEntrance-1 && yy != y-hallHeight-2)world.setBlock(xx,yy,zz,yy == y ? coverBlock : Blocks.air);
					else world.setBlock(xx,yy,zz,BlockList.ravaged_brick,getBrickMeta(rand));
				}
			}
		}
		
		boolean hasGenerated = false;
		
		while(!hasGenerated){
			hasGenerated = true;
			
			EnumDescendDesign design = descendDesignList.getRandomItem(rand).getObject();
			
			switch(design){
				case PILLARS_WITH_SPAWNER:
					for(int a = 0; a < 2; a++){
						for(int b = 0; b < 2; b++){
							for(int py = 0; py < hallHeight-1; py++){
								world.setBlock(x-4+8*a,y+1+py,z-4+8*b,py == 2 ? BlockList.ravaged_brick_glow : BlockList.ravaged_brick);
							}
						}
					}
					
					world.setBlock(x,y+hallHeight,z,BlockList.ravaged_brick);
					world.setBlock(x,y+hallHeight-1,z,BlockList.custom_spawner,2);
					world.setTileEntityGenerator(x,y+hallHeight-1,z,"louseSpawner",this);
					
					break;
					
				case GOO_CORNERS:
					for(int a = 0; a < 2; a++){
						for(int b = 0; b < 2; b++){
							world.setBlock(x-6+12*a,y+1,z-6+12*b,BlockList.ender_goo);
							world.setBlock(x-5+10*a,y+1,z-6+12*b,BlockList.ender_goo);
							world.setBlock(x-4+8*a,y+1,z-6+12*b,BlockList.ender_goo);
							world.setBlock(x-6+12*a,y+1,z-5+10*b,BlockList.ender_goo);
							world.setBlock(x-6+12*a,y+1,z-4+8*b,BlockList.ender_goo);
							
							world.setBlock(x-6+12*a,y+1,z-3+6*b,BlockList.ravaged_brick);
							world.setBlock(x-6+12*a,y+2,z-3+6*b,BlockList.ravaged_brick);
							world.setBlock(x-6+12*a,y+3,z-3+6*b,BlockList.ravaged_brick_glow);
							world.setBlock(x-6+12*a,y+4,z-3+6*b,BlockList.ravaged_brick);
							world.setBlock(x-3+6*a,y+1,z-6+12*b,BlockList.ravaged_brick);
							world.setBlock(x-3+6*a,y+2,z-6+12*b,BlockList.ravaged_brick);
							world.setBlock(x-3+6*a,y+3,z-6+12*b,BlockList.ravaged_brick_glow);
							world.setBlock(x-3+6*a,y+4,z-6+12*b,BlockList.ravaged_brick);
							
							world.setBlock(x-5+10*a,y+1,z-5+10*b,BlockList.ravaged_brick);
							world.setBlock(x-4+8*a,y+1,z-5+10*b,BlockList.ravaged_brick);
							world.setBlock(x-3+6*a,y+1,z-5+10*b,BlockList.ravaged_brick);
							world.setBlock(x-5+10*a,y+1,z-4+8*b,BlockList.ravaged_brick);
							world.setBlock(x-5+10*a,y+1,z-3+6*b,BlockList.ravaged_brick);
							
							world.setBlock(x-4+8*a,y+1,z-4+8*b,BlockList.ravaged_brick_slab);
							world.setBlock(x-3+6*a,y+1,z-4+8*b,BlockList.ravaged_brick_slab);
							world.setBlock(x-2+4*a,y+1,z-4+8*b,BlockList.ravaged_brick_slab);
							world.setBlock(x-4+8*a,y+1,z-3+6*b,BlockList.ravaged_brick_slab);
							world.setBlock(x-4+8*a,y+1,z-2+4*b,BlockList.ravaged_brick_slab);
							world.setBlock(x-2+4*a,y+1,z-5+10*b,BlockList.ravaged_brick_slab);
							world.setBlock(x-2+4*a,y+1,z-6+12*b,BlockList.ravaged_brick_slab);
							world.setBlock(x-5+10*a,y+1,z-2+4*b,BlockList.ravaged_brick_slab);
							world.setBlock(x-6+12*a,y+1,z-2+4*b,BlockList.ravaged_brick_slab);
						}
					}
					
					break;
					
				case FENCE_LIGHTS:
					int xx, zz;
					
					xx = x-5+rand.nextInt(4);
					zz = z-5+rand.nextInt(4);
					world.setBlock(xx,y+hallHeight,zz,BlockList.ravaged_brick_fence);
					world.setBlock(xx,y+hallHeight-1,zz,BlockList.ravaged_brick_glow);
					
					xx = x+5-rand.nextInt(4);
					zz = z-5+rand.nextInt(4);
					world.setBlock(xx,y+hallHeight,zz,BlockList.ravaged_brick_fence);
					world.setBlock(xx,y+hallHeight-1,zz,BlockList.ravaged_brick_glow);
					
					xx = x-5+rand.nextInt(4);
					zz = z+5-rand.nextInt(4);
					world.setBlock(xx,y+hallHeight,zz,BlockList.ravaged_brick_fence);
					world.setBlock(xx,y+hallHeight-1,zz,BlockList.ravaged_brick_glow);
					
					xx = x+5-rand.nextInt(4);
					zz = z+5-rand.nextInt(4);
					world.setBlock(xx,y+hallHeight,zz,BlockList.ravaged_brick_fence);
					world.setBlock(xx,y+hallHeight-1,zz,BlockList.ravaged_brick_glow);
					
					xx = x-1+rand.nextInt(3);
					zz = z-1+rand.nextInt(3);
					world.setBlock(xx,y+hallHeight,zz,BlockList.ravaged_brick_fence);
					world.setBlock(xx,y+hallHeight-1,zz,BlockList.ravaged_brick_glow);
					
					break;
					
				case CORNER_LIGHTS:
					for(int a = 0; a < 2; a++){
						for(int b = 0; b < 2; b++){
							world.setBlock(x-5+10*a,y+1,z-5+10*b,BlockList.ravaged_brick_glow);
							world.setBlock(x-5+10*a,y+2,z-5+10*b,BlockList.ravaged_brick_slab);
						}
					}
					
					break;
					
				default:
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
		ObjectWeightPair.of(EnumHallwayDesign.NONE, 88),
		ObjectWeightPair.of(EnumHallwayDesign.DEAD_END_CHEST, 45),
		ObjectWeightPair.of(EnumHallwayDesign.DESTROYED_WALLS, 44),
		ObjectWeightPair.of(EnumHallwayDesign.EMBEDDED_CHEST, 33),
		ObjectWeightPair.of(EnumHallwayDesign.STAIR_PATTERN, 32),
		ObjectWeightPair.of(EnumHallwayDesign.COBWEBS, 24),
		ObjectWeightPair.of(EnumHallwayDesign.SPAWNERS_IN_WALLS, 24),
		ObjectWeightPair.of(EnumHallwayDesign.FLOOR_CEILING_SLABS, 21),
		ObjectWeightPair.of(EnumHallwayDesign.WALL_MOUNTED_SPAWNERS, 19),
		ObjectWeightPair.of(EnumHallwayDesign.FLOWER_POT, 16),
		ObjectWeightPair.of(EnumHallwayDesign.LAPIS_BLOCK, 4)
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
		
		int connections = ((hallway.checkConnection(DungeonDir.UP) ? 1 : 0) + (hallway.checkConnection(DungeonDir.DOWN) ? 1 : 0) + (hallway.checkConnection(DungeonDir.LEFT) ? 1 : 0) + (hallway.checkConnection(DungeonDir.RIGHT) ? 1 : 0));
		
		boolean isStraight = (hallway.checkConnection(DungeonDir.UP) && hallway.checkConnection(DungeonDir.DOWN) && !hallway.checkConnection(DungeonDir.LEFT) && !hallway.checkConnection(DungeonDir.RIGHT)) ||
							 (hallway.checkConnection(DungeonDir.LEFT) && hallway.checkConnection(DungeonDir.RIGHT) && !hallway.checkConnection(DungeonDir.UP) && !hallway.checkConnection(DungeonDir.DOWN));
		boolean isFullyOpen = hallway.checkConnection(DungeonDir.UP) && hallway.checkConnection(DungeonDir.LEFT) && hallway.checkConnection(DungeonDir.DOWN) && hallway.checkConnection(DungeonDir.RIGHT);
		boolean isDeadEnd = connections == 1;
		
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
				
				isLR = off == DungeonDir.LEFT || off == DungeonDir.RIGHT;
				
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
				List<DungeonDir> availableDirs = CollectionUtil.newList(DungeonDir.values);
				
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
				int spawnerMeta = rand.nextBoolean() ? 3 : 2;
				
				for(int attempt = 0, placed = 0, maxPlaced = 2+level+rand.nextInt(3+level), xx, yy, zz; attempt < 22 && placed < maxPlaced; attempt++){
					xx = x+rand.nextInt(radHallway+1)-rand.nextInt(radHallway+1);
					zz = z+rand.nextInt(radHallway+1)-rand.nextInt(radHallway+1);
					yy = rand.nextBoolean() ? y+1 : y+hallHeight;
					
					if (world.getBlock(xx,yy,zz) == BlockList.ravaged_brick){
						world.setBlock(xx,yy,zz,BlockList.custom_spawner,spawnerMeta);
						if (spawnerMeta == 2)world.setTileEntityGenerator(xx,yy,zz,"louseSpawner",this);
						++placed;
					}
				}
				
				for(DungeonDir dir:DungeonDir.values){
					if (!hallway.checkConnection(dir)){
						for(int a = -1; a <= 1; a++){
							for(int yy = y+1; yy <= y+hallHeight; yy++){
								if (canReplaceBlock(world.getBlock(x+rotX(dir,a,3),yy,z+rotZ(dir,a,3))))world.setBlock(x+rotX(dir,a,3),yy,z+rotZ(dir,a,3),BlockList.ravaged_brick,getBrickMeta(rand));
							}	
						}
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
					world.setTileEntityGenerator(x+rotX(off,0,-1),y+3,z+rotZ(off,0,-1),"flowerPot",this);
				}
				
				break;
				
			case NONE:
			default:
		}
		
		if (connections >= 3 && rand.nextInt(10) == 0)world.setBlock(x,y+hallHeight,z,BlockList.ravaged_brick_glow);
	}
	
	/*
	 * ROOM
	 */
	
	private enum EnumRoomDesign{
		GOO_FOUNTAINS, BOWLS, CARPET_TARGET, SCATTERED_SPAWNERS_WITH_COAL, GLOWING_ROOM, FOUR_SPAWNERS, ENCASED_CUBICLE, TERRARIUM, RUINS
	}
	
	private static final WeightedList<ObjectWeightPair<EnumRoomDesign>> roomDesignList = new WeightedList<>(
		ObjectWeightPair.of(EnumRoomDesign.GOO_FOUNTAINS, 56),
		ObjectWeightPair.of(EnumRoomDesign.RUINS, 56),
		ObjectWeightPair.of(EnumRoomDesign.BOWLS, 54),
		ObjectWeightPair.of(EnumRoomDesign.FOUR_SPAWNERS, 51),
		ObjectWeightPair.of(EnumRoomDesign.SCATTERED_SPAWNERS_WITH_COAL, 44),
		ObjectWeightPair.of(EnumRoomDesign.GLOWING_ROOM, 35),
		ObjectWeightPair.of(EnumRoomDesign.CARPET_TARGET, 26),
		ObjectWeightPair.of(EnumRoomDesign.ENCASED_CUBICLE, 20),
		ObjectWeightPair.of(EnumRoomDesign.TERRARIUM, 17)
	);
	
	private void generateRoomLayout(LargeStructureWorld world, Random rand, int x, int y, int z){
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
	
	public void generateRoom(LargeStructureWorld world, Random rand, int x, int y, int z, DungeonElement room){
		generateRoomLayout(world,rand,x,y,z);
		
		boolean isStraight = (room.checkConnection(DungeonDir.UP) && room.checkConnection(DungeonDir.DOWN) && !room.checkConnection(DungeonDir.LEFT) && !room.checkConnection(DungeonDir.RIGHT)) ||
				 			 (room.checkConnection(DungeonDir.LEFT) && room.checkConnection(DungeonDir.RIGHT) && !room.checkConnection(DungeonDir.UP) && !room.checkConnection(DungeonDir.DOWN));
		
		boolean hasGenerated = false;
		
		while(!hasGenerated){
			hasGenerated = true;
			
			EnumRoomDesign design = roomDesignList.getRandomItem(rand).getObject();
			
			switch(design){
				case GOO_FOUNTAINS:
					for(int a = 0; a < 2; a++){
						for(int b = 0; b < 2; b++){
							world.setBlock(x-6+12*a,y+hallHeight,z-6+12*b,BlockList.ender_goo);
							world.setBlock(x-6+12*a,y+1,z-2+4*b,BlockList.ravaged_brick_slab);
							world.setBlock(x-5+10*a,y+1,z-3+6*b,BlockList.ravaged_brick_slab);
							world.setBlock(x-4+8*a,y+1,z-4+8*b,BlockList.ravaged_brick_slab);
							world.setBlock(x-3+6*a,y+1,z-5+10*b,BlockList.ravaged_brick_slab);
							world.setBlock(x-2+4*a,y+1,z-6+12*b,BlockList.ravaged_brick_slab);
						}
					}
					
					world.setBlock(x,y+hallHeight,z,BlockList.ender_goo);
					world.setBlock(x,y+1,z,BlockList.custom_spawner,3);

					for(int a = 0; a < 2; a++){
						for(int b = 0; b < 2; b++){
							world.setBlock(x-2+4*a,y+1,z-2+4*b,BlockList.ravaged_brick_slab);
							world.setBlock(x-3+6*a,y+1,z-1+2*b,BlockList.ravaged_brick_slab);
							world.setBlock(x-1+2*a,y+1,z-3+6*b,BlockList.ravaged_brick_slab);
						}
						
						world.setBlock(x-3+6*a,y+1,z,BlockList.ravaged_brick_slab);
						world.setBlock(x,y+1,z-3+6*a,BlockList.ravaged_brick_slab);
					}
					
					break;
					
				case CARPET_TARGET:
					int color;
					
					switch(rand.nextInt(4)){
						case 0: color = 10; break; // purple
						case 1: color = 14; break; // red
						case 2: color = 15; break; // black
						default: color = 0; break; // white
					}
					
					for(int a = -6; a <= 6; a++){
						world.setBlock(x+a,y+1,z-6,Blocks.carpet,color);
						world.setBlock(x+a,y+1,z+6,Blocks.carpet,color);
					}
					
					for(int a = -4; a <= 4; a++){
						world.setBlock(x+a,y+1,z-4,Blocks.carpet,color);
						world.setBlock(x+a,y+1,z+4,Blocks.carpet,color);
					}
					
					for(int a = -2; a <= 2; a++){
						world.setBlock(x+a,y+1,z-2,Blocks.carpet,color);
						world.setBlock(x+a,y+1,z+2,Blocks.carpet,color);
					}
					
					for(int a = -5; a <= 5; a++){
						world.setBlock(x-6,y+1,z+a,Blocks.carpet,color);
						world.setBlock(x+6,y+1,z+a,Blocks.carpet,color);
					}
					
					for(int a = -3; a <= 3; a++){
						world.setBlock(x-4,y+1,z+a,Blocks.carpet,color);
						world.setBlock(x+4,y+1,z+a,Blocks.carpet,color);
					}
					
					for(int a = -1; a <= 1; a++){
						world.setBlock(x-2,y+1,z+a,Blocks.carpet,color);
						world.setBlock(x+2,y+1,z+a,Blocks.carpet,color);
					}
					
					for(DungeonDir dir:DungeonDir.values){
						if (room.checkConnection(dir)){
							for(int ax1 = -1; ax1 <= 1; ax1++){
								for(int ax2 = 2; ax2 <= 6; ax2++){
									world.setBlock(x+rotX(dir,ax1,ax2),y+1,z+rotZ(dir,ax1,ax2),Blocks.air);
								}
							}
						}
					}
					
					break;
					
				case SCATTERED_SPAWNERS_WITH_COAL:
					for(int attempt = 0, attemptAmount = rand.nextInt(3)+4+level*2, xx, zz, spawnerMeta = rand.nextBoolean() ? 2 : 3; attempt < attemptAmount; attempt++){
						xx = x+rand.nextInt(5)-rand.nextInt(5);
						zz = z+rand.nextInt(5)-rand.nextInt(5);
						
						world.setBlock(xx,y+1,zz,BlockList.custom_spawner,spawnerMeta);
						if (spawnerMeta == 2)world.setTileEntityGenerator(xx,y+1,zz,"louseSpawner",this);
						world.setBlock(xx,y+2,zz,Blocks.coal_block);
					}
					
					break;
					
				case ENCASED_CUBICLE:
					if (level == 0){
						hasGenerated = false;
						break;
					}
					
					for(int a = 0; a < 2; a++){
						for(int b = 0; b < 2; b++){
							world.setBlock(x-6+12*a,y+hallHeight,z-6+12*b,BlockList.ravaged_brick_glow);
							world.setBlock(x-6+12*a,y+hallHeight-1,z-6+12*b,BlockList.ravaged_brick_slab,8);
						}
					}
					
					for(int yy = y+1; yy <= y+hallHeight; yy++){
						for(int xx = x-2; xx <= x+2; xx++){
							world.setBlock(xx,yy,z+2,BlockList.ravaged_brick,getBrickMeta(rand));
							world.setBlock(xx,yy,z-2,BlockList.ravaged_brick,getBrickMeta(rand));
						}

						for(int zz = z-1; zz <= z+1; zz++){
							world.setBlock(x+2,yy,zz,BlockList.ravaged_brick,getBrickMeta(rand));
							world.setBlock(x-2,yy,zz,BlockList.ravaged_brick,getBrickMeta(rand));
						}
					}
					
					world.setBlock(x,y+1,z,Blocks.chest);
					world.setTileEntityGenerator(x,y+1,z,"encasedCubicleChest",this);
					
					int[] spawnerX = new int[]{ -1, -1, -1, 0, 0, 1, 1, 1 }, spawnerZ = new int[]{ -1, 0, 1, -1, 1, -1, 0, 1 };
					
					for(int a = 0, spawnerMeta; a < spawnerX.length; a++){
						spawnerMeta = rand.nextInt(3) == 0 ? 2 : 3;
						world.setBlock(x+spawnerX[a],y+1,z+spawnerZ[a],BlockList.custom_spawner,spawnerMeta);
						if (spawnerMeta == 2)world.setTileEntityGenerator(x+spawnerX[a],y+1,z+spawnerZ[a],"louseSpawner",this);
					}
					
					break;
					
				case TERRARIUM:
					for(int a = 0; a < 2; a++){
						for(int b = 0; b < 2; b++){
							for(int c = 6; c >= 2; c--){
								world.setBlock(x-c+2*c*a,y+1,z-6+12*b,Blocks.dirt,2);
								world.setBlock(x-c+2*c*a,y+1,z-5+10*b,Blocks.dirt,2);
								world.setBlock(x-c+2*c*a,y+1,z-4+8*b,Blocks.dirt,2);
							}
							
							world.setBlock(x-6+12*a,y+1,z-3+6*b,Blocks.dirt,2);
							world.setBlock(x-5+10*a,y+1,z-3+6*b,Blocks.dirt,2);
							world.setBlock(x-4+8*a,y+1,z-3+6*b,Blocks.dirt,2);
					
							world.setBlock(x-6+12*a,y+1,z-2+4*b,BlockList.ravaged_brick_glow);
							world.setBlock(x-5+10*a,y+1,z-2+4*b,BlockList.ravaged_brick);
							world.setBlock(x-4+8*a,y+1,z-2+4*b,BlockList.ravaged_brick_glow);
							world.setBlock(x-3+6*a,y+1,z-2+4*b,BlockList.ravaged_brick);
							world.setBlock(x-3+6*a,y+1,z-3+6*b,BlockList.ravaged_brick_glow);
							world.setBlock(x-2+4*a,y+1,z-3+6*b,BlockList.ravaged_brick);
							world.setBlock(x-2+4*a,y+1,z-4+8*b,BlockList.ravaged_brick_glow);
							world.setBlock(x-2+4*a,y+1,z-5+10*b,BlockList.ravaged_brick);
							world.setBlock(x-2+4*a,y+1,z-6+12*b,BlockList.ravaged_brick_glow);
							
							for(int yy = y+2; yy <= y+hallHeight; yy++){
								for(int c = 3; c <= 6; c++){
									world.setBlock(x-c+2*c*a,yy,z-2+4*b,Blocks.stained_glass,14);
									world.setBlock(x-2+4*a,yy,z-c+2*c*b,Blocks.stained_glass,14);
								}
							}
							
							int plants = 5+rand.nextInt(9);
							
							for(int plant = 0, xx, zz; plant < plants; plant++){
								xx = x-6+9*a+rand.nextInt(4);
								zz = z-6+9*b+rand.nextInt(4);
								
								if (world.getBlock(xx,y+1,zz) == Blocks.dirt){
									Block plantBlock = Blocks.air;
									int plantMeta = 0;
									
									switch(rand.nextInt(11)){
										case 0:
										case 1:
											plantBlock = Blocks.sapling;
											plantMeta = rand.nextBoolean() ? 2 : 0; // birch & oak sapling
											break;
											
										case 2:
											plantBlock = Blocks.yellow_flower; // dandelion
											break;
											
										case 3:
										case 4:
											plantBlock = Blocks.tallgrass;
											plantMeta = rand.nextBoolean() ? 1 : 2; // tall grass & fern
											break;
											
										case 5:
											plantBlock = rand.nextBoolean() ? Blocks.brown_mushroom : Blocks.red_mushroom; // mushrooms are a little more rare
											break;
											
										default: // 6,7,8,9,10
											plantBlock = Blocks.red_flower;
											plantMeta = rand.nextInt(5); // poppy, blue orchid, allium, azure bluet
											if (plantMeta == 4)plantMeta = 8; // oxeye daisy
											break;
									}
									
									world.setBlock(xx,y+2,zz,plantBlock,plantMeta);
								}
							}
						}
					}
					
					break;
					
				case RUINS:
					for(int attempt = 0, attemptAmount = 28+rand.nextInt(12), placedChests = 0, xx, zz; attempt < attemptAmount; attempt++){
						xx = x+rand.nextInt(radRoom)-rand.nextInt(radRoom);
						zz = z+rand.nextInt(radRoom)-rand.nextInt(radRoom);
						
						if (!world.isAir(xx,y+1,zz))continue;
						
						int height = rand.nextInt(3);
						for(int yy = y+1; yy <= y+1+height; yy++){
							if (rand.nextInt(8-level) == 0)world.setBlock(xx,yy,zz,BlockList.custom_spawner,3);
							else world.setBlock(xx,yy,zz,BlockList.ravaged_brick);
						}
						
						if (rand.nextInt(3) == 0)world.setBlock(xx,y+2+height,zz,BlockList.ravaged_brick_slab);
						else if (rand.nextInt(4) == 0)world.setBlock(xx,y+2+height,zz,BlockList.ravaged_brick_stairs,rand.nextInt(4));
						else if (placedChests < 2 && height < 2 && rand.nextInt(4+placedChests*2) == 0){
							world.setBlock(xx,y+2+height,zz,Blocks.chest);
							world.setTileEntityGenerator(xx,y+2+height,zz,"ruinChest",this);
							++placedChests;
						}
					}
					
					break;
					
				case FOUR_SPAWNERS:
					if (!isStraight){
						hasGenerated = false;
						break;
					}
					
					for(int a = 0; a < 2; a++){
						for(int b = 0; b < 2; b++){
							world.setBlock(x-2+4*a,y+1,z-2+4*b,BlockList.custom_spawner,2);
							world.setTileEntityGenerator(x-2+4*a,y+1,z-2+4*b,"louseSpawner",this);
							world.setBlock(x-2+4*a,y+2,z-2+4*b,BlockList.ravaged_brick_slab);
						}
					}
					
					DungeonDir dir = room.checkConnection(DungeonDir.UP) ? DungeonDir.UP : DungeonDir.LEFT;
					
					int[] zValues = new int[]{ -5, 0, 5 };
					
					for(int a = 0; a < zValues.length; a++){
						world.setBlock(x+rotX(dir,6,zValues[a]),y+2,z+rotZ(dir,6,zValues[a]),BlockList.ravaged_brick_fence);
						world.setBlock(x+rotX(dir,6,zValues[a]),y+3,z+rotZ(dir,6,zValues[a]),BlockList.ravaged_brick_glow);
						world.setBlock(x+rotX(dir,-6,zValues[a]),y+2,z+rotZ(dir,-6,zValues[a]),BlockList.ravaged_brick_fence);
						world.setBlock(x+rotX(dir,-6,zValues[a]),y+3,z+rotZ(dir,-6,zValues[a]),BlockList.ravaged_brick_glow);
					}
					
					break;
					
				case GLOWING_ROOM:
					int[] pillarX = new int[]{ -6, -2, 2, 6, -6, 6, -6, 6, -6, -2, 2, 6 }, pillarZ = new int[]{ -6, -6, -6, -6, -2, -2, 2, 2, 6, 6, 6, 6 };
					
					for(int a = 0; a < pillarX.length; a++){
						for(int yy = y+1; yy <= y+hallHeight; yy++){
							world.setBlock(x+pillarX[a],yy,z+pillarZ[a],BlockList.ravaged_brick_glow);
						}
					}
					
					if (rand.nextBoolean()){ // fences
						for(int yy = y+1; yy <= y+hallHeight; yy++){
							for(int xx = x-5; xx <= x+5; xx++){
								if (world.isAir(xx,yy,z-6))world.setBlock(xx,yy,z-6,BlockList.ravaged_brick_fence);
								if (world.isAir(xx,yy,z+6))world.setBlock(xx,yy,z+6,BlockList.ravaged_brick_fence);
							}
							
							for(int zz = z-5; zz <= z+5; zz++){
								if (world.isAir(x-6,yy,zz))world.setBlock(x-6,yy,zz,BlockList.ravaged_brick_fence);
								if (world.isAir(x+6,yy,zz))world.setBlock(x+6,yy,zz,BlockList.ravaged_brick_fence);
							}
							
							for(DungeonDir checkDir:DungeonDir.values){
								if (room.checkConnection(checkDir)){
									for(int ax1 = -1; ax1 <= 1; ax1++){
										world.setBlock(x+rotX(checkDir,ax1,-6),yy,z+rotZ(checkDir,ax1,-6),Blocks.air);
									}
								}
							}
						}
					}
					
					break;
					
				case BOWLS:
					int bowlFill = rand.nextInt(4);
					
					for(int a = 0; a < 2; a++){
						for(int b = 0; b < 2; b++){
							world.setBlock(x-5+7*a,y+1,z-5+7*b,BlockList.ravaged_brick_glow);
							world.setBlock(x-4+7*a,y+1,z-5+7*b,BlockList.ravaged_brick);
							world.setBlock(x-3+7*a,y+1,z-5+7*b,BlockList.ravaged_brick);
							world.setBlock(x-2+7*a,y+1,z-5+7*b,BlockList.ravaged_brick_glow);
							world.setBlock(x-5+7*a,y+1,z-4+7*b,BlockList.ravaged_brick);
							world.setBlock(x-5+7*a,y+1,z-3+7*b,BlockList.ravaged_brick);
							world.setBlock(x-5+7*a,y+1,z-2+7*b,BlockList.ravaged_brick_glow);
							world.setBlock(x-3+7*a,y+1,z-2+7*b,BlockList.ravaged_brick);
							world.setBlock(x-4+7*a,y+1,z-2+7*b,BlockList.ravaged_brick);
							world.setBlock(x-2+7*a,y+1,z-2+7*b,BlockList.ravaged_brick_glow);
							world.setBlock(x-2+7*a,y+1,z-3+7*b,BlockList.ravaged_brick);
							world.setBlock(x-2+7*a,y+1,z-4+7*b,BlockList.ravaged_brick);
							
							switch(bowlFill){
								case 0:
								case 1:
								case 2:
									world.setBlock(x-4+7*a,y+1,z-4+7*b,bowlFill == 0 ? BlockList.ender_goo : bowlFill == 1 ? BlockList.ravaged_brick_slab : Blocks.obsidian);
									world.setBlock(x-3+7*a,y+1,z-4+7*b,bowlFill == 0 ? BlockList.ender_goo : bowlFill == 1 ? BlockList.ravaged_brick_slab : Blocks.obsidian);
									world.setBlock(x-3+7*a,y+1,z-3+7*b,bowlFill == 0 ? BlockList.ender_goo : bowlFill == 1 ? BlockList.ravaged_brick_slab : Blocks.obsidian);
									world.setBlock(x-4+7*a,y+1,z-3+7*b,bowlFill == 0 ? BlockList.ender_goo : bowlFill == 1 ? BlockList.ravaged_brick_slab : Blocks.obsidian);
									break;
									
								case 3:
									world.setBlock(x-4+7*a,y+1,z-4+7*b,(a^b) == 1 ? BlockList.custom_spawner : Blocks.coal_block,(a^b) == 1 ? 3 : 0);
									world.setBlock(x-3+7*a,y+1,z-4+7*b,(a^b) == 0 ? BlockList.custom_spawner : Blocks.coal_block,(a^b) == 0 ? 3 : 0);
									world.setBlock(x-3+7*a,y+1,z-3+7*b,(a^b) == 1 ? BlockList.custom_spawner : Blocks.coal_block,(a^b) == 1 ? 3 : 0);
									world.setBlock(x-4+7*a,y+1,z-3+7*b,(a^b) == 0 ? BlockList.custom_spawner : Blocks.coal_block,(a^b) == 0 ? 3 : 0);
									break;
							}
						}
					}
					
					break;
					
				default:
			}
		}
	}
	
	/*
	 * END
	 */

	public void generateEndLayout(LargeStructureWorld world, Random rand, int[] x, int y, int[] z, List<DungeonElement> end){
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
	
	public void generateEndContent(LargeStructureWorld world, Random rand, int[] x, int y, int[] z, List<DungeonElement> end){
		int minX = x[0], minZ = z[0];
		
		for(int a = 1; a < x.length; a++){
			if (x[a] < minX)minX = x[a];
			if (z[a] < minZ)minZ = z[a];
		}
		
		int topLeftAirX = minX-radRoom+1, topLeftAirZ = minZ-radRoom+1;
		
		int[] gooX = new int[]{ 2, 12, 22, 2, 22, 2, 12, 22 }, gooZ = new int[]{ 2, 2, 2, 12, 12, 22, 22, 22 };
		
		for(int a = 0; a < gooX.length; a++){
			for(int px = 0; px < 2; px++){
				for(int pz = 0; pz < 2; pz++){
					world.setBlock(topLeftAirX+gooX[a]+1+px,y+hallHeight,topLeftAirZ+gooZ[a]+1+pz,BlockList.ender_goo);
					world.setBlock(topLeftAirX+gooX[a]+1+px,y+hallHeight+1,topLeftAirZ+gooZ[a]+1+pz,BlockList.ravaged_brick_glow);
				}
			}
			
			for(int py = 0; py < 2; py++){
				for(int px = 0; px < 4; px++){
					world.setBlock(topLeftAirX+gooX[a]+px,y+2+py,topLeftAirZ+gooZ[a],Blocks.air);
					world.setBlock(topLeftAirX+gooX[a]+px,y+1+py*(hallHeight-1),topLeftAirZ+gooZ[a],BlockList.ravaged_brick,getBrickMeta(rand));
					world.setBlock(topLeftAirX+gooX[a]+px,y+2+py,topLeftAirZ+gooZ[a]+3,Blocks.air);
					world.setBlock(topLeftAirX+gooX[a]+px,y+1+py*(hallHeight-1),topLeftAirZ+gooZ[a]+3,BlockList.ravaged_brick,getBrickMeta(rand));
				}
				
				for(int pz = 0; pz < 2; pz++){
					world.setBlock(topLeftAirX+gooX[a],y+2+py,topLeftAirZ+gooZ[a]+1+pz,Blocks.air);
					world.setBlock(topLeftAirX+gooX[a],y+1+py*(hallHeight-1),topLeftAirZ+gooZ[a]+1+pz,BlockList.ravaged_brick,getBrickMeta(rand));
					world.setBlock(topLeftAirX+gooX[a]+3,y+2+py,topLeftAirZ+gooZ[a]+1+pz,Blocks.air);
					world.setBlock(topLeftAirX+gooX[a]+3,y+1+py*(hallHeight-1),topLeftAirZ+gooZ[a]+1+pz,BlockList.ravaged_brick,getBrickMeta(rand));
				}
			}
			
			for(int attempt = 0, xx, yy, zz; attempt < 3+rand.nextInt(4); attempt++){
				xx = topLeftAirX+gooX[a]+rand.nextInt(4);
				zz = topLeftAirZ+gooZ[a]+rand.nextInt(4);
				yy = rand.nextBoolean() ? y+1 : y+hallHeight;
				
				if (world.getBlock(xx,yy,zz) == BlockList.ravaged_brick){
					world.setBlock(xx,yy,zz,BlockList.custom_spawner,2);
					world.setTileEntityGenerator(xx,yy,zz,"louseSpawner",this);
				}
			}
		}
		
		for(int a = 0; a < 2; a++){
			for(int b = 0; b < 2; b++){
				for(int py = 0; py < 4; py++){
					world.setBlock(topLeftAirX+12+3*a,y+1+py,topLeftAirZ+12+3*b,py == 0 || py == 3 ? BlockList.ravaged_brick_glow : BlockList.ravaged_brick);
					
					if (py > 0){
						world.setBlock(topLeftAirX+13+a,y+1+py,topLeftAirZ+13+b,BlockList.custom_spawner,2);
						world.setTileEntityGenerator(topLeftAirX+13+a,y+1+py,topLeftAirZ+13+b,"louseSpawner",this);
					}
					else world.setBlock(topLeftAirX+13+a,y+1,topLeftAirZ+13+b,BlockList.ravaged_brick);
				}
			}
		}
		
		int[] chestX = new int[]{ 13, 14, 13, 14, 12, 12, 15, 15 }, chestZ = new int[]{ 12, 12, 15, 15, 13, 14, 13, 14 };
		
		for(int a = 0; a < chestX.length; a++){
			world.setBlock(topLeftAirX+chestX[a],y+1,topLeftAirZ+chestZ[a],Blocks.chest);
			world.setTileEntityGenerator(topLeftAirX+chestX[a],y+1,topLeftAirZ+chestZ[a],"endRoomChest",this);
		}
	}
	
	/*
	 * TILE ENTITIES
	 */
	
	@Override
	public void onTileEntityRequested(String key, TileEntity tile, Random rand){
		// TODO
		/*
		if (key.equals("hallwayEmbeddedChest")){
			TileEntityChest chest = (TileEntityChest)tile;
			
			for(int attempt = 0, attemptAmount = 4+rand.nextInt(5)+rand.nextInt(4+level); attempt < attemptAmount; attempt++){
				chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()),(rand.nextInt(3) == 0 ? RavagedDungeonLoot.lootUncommon : RavagedDungeonLoot.lootGeneral).generateIS(rand));
			}
		}
		else if (key.equals("hallwayDeadEndChest")){
			TileEntityChest chest = (TileEntityChest)tile;
			
			for(int attempt = 0, attemptAmount = 7+rand.nextInt(8+level*2); attempt < attemptAmount; attempt++){
				chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()),(rand.nextInt(3) != 0 ? RavagedDungeonLoot.lootRare : RavagedDungeonLoot.lootGeneral).generateIS(rand));
			}
		}
		else if (key.equals("ruinChest")){
			TileEntityChest chest = (TileEntityChest)tile;
			
			for(int attempt = 0, attemptAmount = 5+rand.nextInt(5+level*2); attempt < attemptAmount; attempt++){
				chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()),RavagedDungeonLoot.lootGeneral.generateIS(rand));
			}
		}
		else if (key.equals("encasedCubicleChest")){
			TileEntityChest chest = (TileEntityChest)tile;
			
			for(int attempt = 0, attemptAmount = 10+level+rand.nextInt(10+level); attempt < attemptAmount; attempt++){
				chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()),RavagedDungeonLoot.lootRare.generateIS(rand));
			}
		}
		else if (key.equals("endRoomChest")){
			TileEntityChest chest = (TileEntityChest)tile;
			
			for(int attempt = 0, attemptAmount = 3+rand.nextInt(2+rand.nextInt(2)); attempt < attemptAmount; attempt++){
				chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()),RavagedDungeonLoot.lootEnd.generateIS(rand));
			}
		}
		else if (key.equals("louseSpawner")){
			LouseRavagedSpawnerLogic logic = (LouseRavagedSpawnerLogic)((TileEntityCustomSpawner)tile).getSpawnerLogic();
			logic.setLouseSpawnData(new LouseSpawnData(level,rand));
		}
		else if (key.equals("flowerPot")){
			ItemStack is = RavagedDungeonLoot.flowerPotItems[rand.nextInt(RavagedDungeonLoot.flowerPotItems.length)].copy();
			
			if (is.getItem() == Item.getItemFromBlock(BlockList.death_flower)){
				tile.getWorldObj().setBlock(tile.xCoord,tile.yCoord,tile.zCoord,BlockList.death_flower_pot,is.getItemDamage(),3);
			}
			else{
				TileEntityFlowerPot flowerPot = (TileEntityFlowerPot)tile;
				
				flowerPot.func_145964_a(is.getItem(),is.getItemDamage());
				flowerPot.markDirty();
	
				if (!flowerPot.getWorldObj().setBlockMetadataWithNotify(flowerPot.xCoord,flowerPot.yCoord,flowerPot.zCoord,is.getItemDamage(),2)){
					flowerPot.getWorldObj().markBlockForUpdate(flowerPot.xCoord,flowerPot.yCoord,flowerPot.zCoord);
				}
			}
		}*/
	}
	
	/*
	 * UTILITIES
	 */
	
	private static boolean canReplaceBlock(Block block){
		return block == Blocks.end_stone || block == BlockList.end_terrain || block == Blocks.air || block == BlockList.crossed_decoration || block == BlockList.stardust_ore || block == BlockList.end_powder_ore;
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
