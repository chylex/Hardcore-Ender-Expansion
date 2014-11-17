package chylex.hee.world.structure.island.biome.feature.island.laboratory;
import gnu.trove.map.hash.TByteByteHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.util.Direction;
import chylex.hee.world.structure.island.ComponentIsland;
import chylex.hee.world.structure.util.pregen.LargeStructureWorld;

public final class LaboratoryPlan{
	private final List<LaboratoryElement> elements = new ArrayList<>();
	private final List<LaboratoryElement> roomElements = new ArrayList<>();
	private int score;
	
	public boolean generate(LargeStructureWorld world, Random rand){
		elements.clear();
		
		int x, z;
		
		for(int locAttempt = 0; locAttempt < 42; locAttempt++){
			x = (rand.nextInt(ComponentIsland.size)-ComponentIsland.halfSize)*3/4;
			z = (rand.nextInt(ComponentIsland.size)-ComponentIsland.halfSize)*3/4;
			LaboratoryElement element = trySpawnElement(world,LaboratoryElementType.SMALL_ROOM,x,z,0);
			if (element == null)continue;
			
			elements.add(element);
			roomElements.add(element);
			break;
		}
		
		if (elements.isEmpty())return false;
		
		int attempts = 100, dir;
		
		while(--attempts >= 0){
			LaboratoryElement room = roomElements.get(rand.nextInt(roomElements.size()));
			if (room.connected[dir = rand.nextInt(4)])continue;
			
			int xx = room.x+room.type.halfSizeX+1, startY = room.y, prevY = room.y, zz = room.z+room.type.halfSizeZ+1, dist = 0;
			boolean hasGenerated = false;
			
			while(++dist < 32){
				LaboratoryElement hall = trySpawnElement(world,dir == 0 || dir == 2 ? LaboratoryElementType.HALL_Z : LaboratoryElementType.HALL_X,xx,zz,dir);
				if (hall == null || Math.abs(hall.y-prevY) > 2 || Math.abs(hall.y-startY) > 4 || !isEmptyFor(hall))break;
			}
			
			if (dist > 3){
				
			}
			
			if (hasGenerated)attempts = Math.min(100,attempts+10);
			
			if (roomElements.size() > 17+rand.nextInt(8))break;
			else if (roomElements.size() > 8+rand.nextInt(5))attempts -= 15;
		}
		
		return true;
	}
	
	public int getScore(){
		return score;
	}
	
	private boolean isEmptyFor(LaboratoryElement testElement){
		int addDist = Math.max(testElement.type.halfSizeX,testElement.type.halfSizeZ)*2+3, max;
		
		for(LaboratoryElement element:elements){
			max = Math.max(element.type.halfSizeX,element.type.halfSizeZ)*2+addDist;
			if (Math.abs(element.x-testElement.x) <= max && Math.abs(element.z-testElement.z) <= max)return false;
		}
		
		return true;
	}
	
	private static LaboratoryElement trySpawnElement(LargeStructureWorld world, LaboratoryElementType type, int x, int z, int dir){
		if (type.halfSizeX == -1 || type.halfSizeZ == -1)return null;
		
		TByteByteHashMap map = new TByteByteHashMap();
		int minY = -1, maxY = -1;
		
		for(int xx = x-type.halfSizeX*Direction.offsetX[dir], zz, yy; xx <= x+type.halfSizeX*Direction.offsetX[dir]; xx++){
			for(zz = z-type.halfSizeZ*Direction.offsetZ[dir]; zz <= z+type.halfSizeZ*Direction.offsetZ[dir]; zz++){
				if ((yy = world.getHighestY(xx,zz)) == 0)return null;
				
				if (minY == -1)minY = maxY = yy;
				else map.adjustOrPutValue((byte)(yy-128),(byte)1,(byte)1);
			}
		}
		
		if (maxY-minY > 6)return null;
		
		int mostFrequentY = 0;
		byte mostFrequentYAmount = 0;
		
		for(byte y:map.keys()){
			byte amt = map.get(y);
			
			if (amt > mostFrequentYAmount){
				mostFrequentYAmount = amt;
				mostFrequentY = y+128;
			}
		}
		
		if (maxY-mostFrequentY > 3)return null;
		if (mostFrequentYAmount/((type.halfSizeX*2+1)*(type.halfSizeZ*2+1)) < 0.25F)return null;
		
		return new LaboratoryElement(type,x,mostFrequentY,z);
	}
}
