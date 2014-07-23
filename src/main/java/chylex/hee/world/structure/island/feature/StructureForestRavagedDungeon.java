package chylex.hee.world.structure.island.feature;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.block.BlockList;
import chylex.hee.block.BlockRavagedBrick;
import chylex.hee.system.util.TimeMeasurement;
import chylex.hee.world.structure.island.ComponentScatteredFeatureIsland;
import chylex.hee.world.structure.island.feature.ravageddungeon.DungeonDir;
import chylex.hee.world.structure.island.feature.ravageddungeon.DungeonElement;
import chylex.hee.world.structure.island.feature.ravageddungeon.DungeonElementList;
import chylex.hee.world.structure.island.feature.ravageddungeon.DungeonElementType;
import chylex.hee.world.structure.island.feature.ravageddungeon.RavagedDungeonGenerator;

public class StructureForestRavagedDungeon extends AbstractIslandStructure{
	private static final byte dungW = 26, dungH = 26,
							  dungHalfW = dungW>>1, dungHalfH = dungH>>1,
							  scale = 5, scaleHalf = 2,
							  hallHeight = 4, maxEntranceHeight = 10;

	private int islandCenterX, islandCenterZ;
	
	@Override
	protected boolean generate(Random rand){
		islandCenterX = ComponentScatteredFeatureIsland.halfSize;
		islandCenterZ = ComponentScatteredFeatureIsland.halfSize;
		int y = 25;
		
		while(++y < 80){
			if (world.getBlock(islandCenterX,y,islandCenterZ) == surface())break;
		}
		
		if (y >= 80)return false;
		
		TimeMeasurement.start("RavagedDungeonGen");
		RavagedDungeonGenerator gen = new RavagedDungeonGenerator(dungW,dungH,3);

		int xx, zz, yy, worldX, worldZ, th = hallHeight+1, layer, visibility;
		for(xx = 0; xx < dungW; xx++){
			for(zz = 0; zz < dungH; zz++){
				worldX = islandCenterX-dungHalfW*scale+xx*scale+scaleHalf;
				worldZ = islandCenterZ-dungHalfH*scale+zz*scale+scaleHalf;
				yy = y-maxEntranceHeight;
				
				for(layer = 0; layer < 3; layer++){
					visibility = 0;
					
					if (world.getBlock(worldX-scaleHalf-1,yy-1,worldZ) != Blocks.end_stone)++visibility;
					if (world.getBlock(worldX+scaleHalf+1,yy-1,worldZ) != Blocks.end_stone)++visibility;
					if (world.getBlock(worldX-scaleHalf-1,yy+th+1,worldZ) != Blocks.end_stone)++visibility;
					if (world.getBlock(worldX+scaleHalf+1,yy+th+1,worldZ) != Blocks.end_stone)++visibility;
					if (world.getBlock(worldX,yy-1,worldZ-scaleHalf-1) != Blocks.end_stone)++visibility;
					if (world.getBlock(worldX,yy-1,worldZ+scaleHalf+1) != Blocks.end_stone)++visibility;
					if (world.getBlock(worldX,yy+th+1,worldZ-scaleHalf-1) != Blocks.end_stone)++visibility;
					if (world.getBlock(worldX,yy+th+1,worldZ+scaleHalf+1) != Blocks.end_stone)++visibility;
					if (world.getBlock(worldX,yy+(hallHeight>>1),worldZ) != Blocks.end_stone)++visibility;
					
					if (visibility > 3)gen.blockLocation(layer,xx,zz);
					
					yy -= hallHeight+2;
				}
			}
		}
		
		if (!gen.generate()){
			System.out.println("Could not generate dungeon :(");
			return false;
		}
		TimeMeasurement.finish("RavagedDungeonGen");
		TimeMeasurement.start("RavagedDungeonPlace");
		
		for(int a = 0; a < gen.layers.length; a++){
			DungeonElementList elements = gen.layers[a].getElements();
			
			if (a == 0){
				y = generateEntrance(rand,elements,elements.getAll(DungeonElementType.ENTRANCE).get(0),y);
			}
			
			for(DungeonElement hallway:elements.getAll(DungeonElementType.HALLWAY))generateHallway(rand,elements,hallway,y);
			for(DungeonElement room:elements.getAll(DungeonElementType.ROOM))generateRoom(rand,elements,room,y);
			
			y -= hallHeight+2;
		}
		
		TimeMeasurement.finish("RavagedDungeonPlace");
		
		return true;
	}
	
	/*
	 * ELEMENT GENERATION
	 */
	
	private int generateEntrance(Random rand, DungeonElementList elements, DungeonElement entrance, int y){
		int x = getElementX(entrance), z = getElementZ(entrance);
		
		int surfaceY = y-maxEntranceHeight-1;
		while(++surfaceY <= y){
			if (world.getBlock(x,surfaceY,z) == surface())break;
		}
		
		for(int yy = surfaceY+5; yy >= y-maxEntranceHeight; yy--){
			for(int xx = x-2; xx <= x+2; xx++){
				for(int zz = z-2; zz <= z+2; zz++){
					if (yy > surfaceY || (Math.abs(xx-x) <= 1 && Math.abs(zz-z) <= 1 && yy != y-maxEntranceHeight))world.setBlock(xx,yy,zz,Blocks.air);
					else world.setBlock(xx,yy,zz,BlockList.ravaged_brick,getBrickMeta(rand));
				}
			}
		}
		
		generateConnections(elements,entrance,y-maxEntranceHeight);
		
		return y-maxEntranceHeight;
	}
	
	private void generateHallway(Random rand, DungeonElementList elements, DungeonElement hallway, int y){
		int x = getElementX(hallway), z = getElementZ(hallway);
		
		for(int yy = y; yy <= y+hallHeight+1; yy++){
			for(int xx = x-scaleHalf; xx <= x+scaleHalf; xx++){
				for(int zz = z-scaleHalf; zz <= z+scaleHalf; zz++){
					if (yy == y || yy == y+hallHeight+1 || xx == x-scaleHalf || xx == x+scaleHalf || zz == z-scaleHalf || zz == z+scaleHalf){
						world.setBlock(xx,yy,zz,BlockList.ravaged_brick,getBrickMeta(rand));
					}
					else world.setBlock(xx,yy,zz,Blocks.air);
				}
			}
		}
		
		generateConnections(elements,hallway,y);
	}
	
	private void generateRoom(Random rand, DungeonElementList elements, DungeonElement room, int y){
		int x = getElementX(room), z = getElementZ(room);
		int sz = scale+scaleHalf;
		
		for(int yy = y; yy <= y+hallHeight+1; yy++){
			for(int xx = x-sz; xx <= x+sz; xx++){
				for(int zz = z-sz; zz <= z+sz; zz++){
					if (yy == y || yy == y+hallHeight+1 || xx == x-sz || xx == x+sz || zz == z-sz || zz == z+sz){
						world.setBlock(xx,yy,zz,BlockList.ravaged_brick,getBrickMeta(rand));
					}
					else{
						world.setBlock(xx,yy,zz,Blocks.air);
					}
				}
			}
		}
		
		generateConnections(elements,room,y);
	}
	
	private void generateConnections(DungeonElementList elements, DungeonElement element, int y){
		for(DungeonDir dir:DungeonDir.values){
			if (element.checkConnection(dir)){
				int px = getElementX(element)+dir.addX*scaleHalf, pz = getElementZ(element)+dir.addY*scaleHalf;
				
				if (element.size == 3){
					px += dir.addX*scale;
					pz += dir.addY*scale;
				}
				
				for(int yy = y+1; yy <= y+hallHeight; yy++){
					for(int xx = px-Math.abs(dir.addY); xx <= px+Math.abs(dir.addY); xx++){
						for(int zz = pz-Math.abs(dir.addX); zz <= pz+Math.abs(dir.addX); zz++){
							world.setBlock(xx,yy,zz,Blocks.air);
						}
					}
				}
			}
		}
	}
	
	/*
	 * OTHER STUFF
	 */
	
	private int getBrickMeta(Random rand){
		return rand.nextInt(8) != 0 ? 0 : rand.nextInt(7) == 0 ? BlockRavagedBrick.metaCracked : BlockRavagedBrick.metaDamaged1+rand.nextInt(1+BlockRavagedBrick.metaDamaged3-BlockRavagedBrick.metaDamaged1);
	}
	
	private int getElementX(DungeonElement element){
		return islandCenterX-dungHalfW*scale+element.x*scale+scaleHalf;
	}
	
	private int getElementZ(DungeonElement element){
		return islandCenterZ-dungHalfH*scale+element.y*scale+scaleHalf;
	}
}
