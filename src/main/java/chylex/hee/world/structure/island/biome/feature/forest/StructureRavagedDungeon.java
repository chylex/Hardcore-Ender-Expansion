package chylex.hee.world.structure.island.biome.feature.forest;
import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.system.logging.Log;
import chylex.hee.system.logging.Stopwatch;
import chylex.hee.world.structure.island.ComponentIsland;
import chylex.hee.world.structure.island.biome.feature.AbstractIslandStructure;
import chylex.hee.world.structure.island.biome.feature.forest.ravageddungeon.DungeonDir;
import chylex.hee.world.structure.island.biome.feature.forest.ravageddungeon.DungeonElement;
import chylex.hee.world.structure.island.biome.feature.forest.ravageddungeon.DungeonElementList;
import chylex.hee.world.structure.island.biome.feature.forest.ravageddungeon.DungeonElementType;
import chylex.hee.world.structure.island.biome.feature.forest.ravageddungeon.RavagedDungeonGenerator;
import chylex.hee.world.structure.island.biome.feature.forest.ravageddungeon.RavagedDungeonPlacer;

public class StructureRavagedDungeon extends AbstractIslandStructure{
	private static final byte dungW = 26, dungH = 26,
							  dungHalfW = dungW>>1, dungHalfH = dungH>>1,
							  scale = 5, scaleHalf = 2,
							  hallHeight = 4, maxEntranceHeight = 10;

	private int islandCenterX, islandCenterZ;
	private final RavagedDungeonPlacer placer = new RavagedDungeonPlacer(hallHeight);
	
	@Override
	protected boolean generate(Random rand){
		islandCenterX = ComponentIsland.halfSize;
		islandCenterZ = ComponentIsland.halfSize;
		int y = 25;
		
		while(++y < 80){
			if (world.getBlock(islandCenterX,y,islandCenterZ) == surface())break;
		}
		
		if (y >= 80)return false;
		
		Stopwatch.time("RavagedDungeonGen");
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
					
					if (visibility > 3 && rand.nextInt(4) != 0)gen.blockLocation(layer,xx,zz);
					
					yy -= hallHeight+2;
				}
			}
		}
		
		if (!gen.generate()){
			Log.debug("Could not generate Ravaged Dungeon :(");
			return false;
		}
		Stopwatch.finish("RavagedDungeonGen");
		Stopwatch.time("RavagedDungeonPlace");
		
		for(int a = 0; a < gen.layers.length; a++){
			DungeonElementList elements = gen.layers[a].getElements();
			
			if (a == 0)y = generateEntrance(rand,elements,elements.getAll(DungeonElementType.ENTRANCE).get(0),y);
			if (a == gen.layers.length-1)generateEnd(rand,elements,elements.getAll(DungeonElementType.END),y);
			
			List<DungeonElement> hallways = elements.getAll(DungeonElementType.HALLWAY);
			for(DungeonElement hallway:hallways)generateHallwayWithoutConnections(rand,elements,hallway,y);
			for(DungeonElement hallway:hallways)generateConnections(elements,hallway,y);
			
			for(DungeonElement room:elements.getAll(DungeonElementType.ROOM))generateRoom(rand,elements,room,y);
			
			if (a < gen.layers.length-1)generateDescendRoom(rand,elements,elements.getAll(DungeonElementType.DESCEND).get(0),y);
			if (a > 0)generateConnections(elements,elements.getAll(DungeonElementType.DESCENDBOTTOM).get(0),y);
			
			y -= hallHeight+2;
		}
		
		Stopwatch.finish("RavagedDungeonPlace");
		
		return true;
	}
	
	/*
	 * ELEMENT GENERATION
	 */
	
	private int generateEntrance(Random rand, DungeonElementList elements, DungeonElement entrance, int y){
		placer.generateEntrance(world,rand,getElementX(entrance),y,getElementZ(entrance),maxEntranceHeight,entrance);
		generateConnections(elements,entrance,y-maxEntranceHeight);
		return y-maxEntranceHeight;
	}
	
	private void generateDescendRoom(Random rand, DungeonElementList elements, DungeonElement descend, int y){
		placer.generateDescend(world,rand,getElementX(descend),y,getElementZ(descend),descend);
		generateConnections(elements,descend,y);
	}
	
	private void generateHallwayWithoutConnections(Random rand, DungeonElementList elements, DungeonElement hallway, int y){
		placer.generateHallway(world,rand,getElementX(hallway),y,getElementZ(hallway),hallway);
	}
	
	private void generateRoom(Random rand, DungeonElementList elements, DungeonElement room, int y){
		placer.generateRoom(world,rand,getElementX(room),y,getElementZ(room),room);
		generateConnections(elements,room,y);
	}
	
	private void generateEnd(Random rand, DungeonElementList elements, List<DungeonElement> end, int y){
		if (end.size() != 4)throw new IllegalStateException("Ravaged Dungeon End does not consist of 4 rooms!");
		
		int[] x = new int[4], z = new int[4];
		
		for(int a = 0; a < 4; a++){
			x[a] = getElementX(end.get(a));
			z[a] = getElementZ(end.get(a));
		}

		placer.generateEndLayout(world,rand,x,y,z,end);
		for(DungeonElement endElement:end)generateConnections(elements,endElement,y); // generate connections first only in end room
		placer.generateEndContent(world,rand,x,y,z,end);
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
	
	private int getElementX(DungeonElement element){
		return islandCenterX-dungHalfW*scale+element.x*scale+scaleHalf;
	}
	
	private int getElementZ(DungeonElement element){
		return islandCenterZ-dungHalfH*scale+element.y*scale+scaleHalf;
	}
}
