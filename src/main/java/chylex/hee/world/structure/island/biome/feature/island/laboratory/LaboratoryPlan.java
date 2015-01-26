package chylex.hee.world.structure.island.biome.feature.island.laboratory;
import gnu.trove.map.hash.TByteByteHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import chylex.hee.system.util.Direction;
import chylex.hee.world.structure.island.ComponentIsland;
import chylex.hee.world.structure.util.pregen.LargeStructureWorld;

public final class LaboratoryPlan{
	private static TByteByteHashMap tryMap = new TByteByteHashMap(4);
	final List<LaboratoryElement> elements = new ArrayList<>();
	final List<LaboratoryElement> roomElements = new ArrayList<>();
	private int score;
	
	public boolean generate(LargeStructureWorld world, Random rand){
		roomElements.clear();
		elements.clear();
		score = 0;
		
		int x, z;
		
		for(int locAttempt = 0; locAttempt < 420; locAttempt++){
			x = rand.nextInt(ComponentIsland.size)-ComponentIsland.halfSize;
			z = rand.nextInt(ComponentIsland.size)-ComponentIsland.halfSize;
			LaboratoryElement element = trySpawnElement(world,LaboratoryElementType.SMALL_ROOM,x,z,0);
			if (element == null)continue;
			
			elements.add(element);
			roomElements.add(element);
			break;
		}
		
		if (roomElements.isEmpty())return false;
		
		int attempts = 100, dir, roomAmount = roomElements.size();
		
		while(--attempts >= 0){
			if (roomAmount > 17 && roomAmount > 17+rand.nextInt(8))break;
			else if (roomAmount > 8 && roomAmount > 8+rand.nextInt(5))attempts -= 15;
			
			LaboratoryElement room = roomElements.get(rand.nextInt(roomAmount));
			if (room.connected[dir = rand.nextInt(4)])continue;
			
			int xx = room.x+room.type.halfSizeX*Direction.offsetX[dir], zz = room.z+room.type.halfSizeZ*Direction.offsetZ[dir], startY = room.y, prevY = room.y, dist = 0;
			
			while(++dist < 25){
				LaboratoryElement hall = trySpawnElement(world,dir == 0 || dir == 2 ? LaboratoryElementType.HALL_Z : LaboratoryElementType.HALL_X,xx+Direction.offsetX[dir]*dist,zz+Direction.offsetZ[dir]*dist,dir);
				if (hall == null || Math.abs(hall.y-prevY) > 1 || Math.abs(hall.y-startY) > 3 || !hasSpaceFor(hall,room))break;
			}
			
			if (dist <= 7)continue;
			
			boolean hasGenerated = false;
			
			for(int placeAttempt = 0, tmpDist; placeAttempt < dist+5; placeAttempt++){
				tmpDist = (dist-5)+(int)(rand.nextInt(dist-6)*(0.4F+rand.nextFloat()*0.6F));
				LaboratoryElementType type = rand.nextBoolean() ? LaboratoryElementType.LARGE_ROOM : LaboratoryElementType.SMALL_ROOM;
				LaboratoryElement newRoom = trySpawnElement(world,type,xx+tmpDist*Direction.offsetX[dir],zz+tmpDist*Direction.offsetZ[dir],dir);
				
				if (newRoom != null && Math.abs(newRoom.y-startY) <= 4 && hasSpaceFor(newRoom,null)){
					room.connected[dir] = true;
					newRoom.connected[Direction.rotateOpposite[dir]] = true;
					elements.add(newRoom);
					roomElements.add(newRoom);
					roomAmount = roomElements.size();
					
					hasGenerated = true;
					tmpDist -= Math.abs(Direction.offsetX[dir]*(newRoom.type.halfSizeX+1)+Direction.offsetZ[dir]*(newRoom.type.halfSizeZ+1));
					
					LaboratoryElementType hallType = dir == 0 || dir == 2 ? LaboratoryElementType.HALL_Z : LaboratoryElementType.HALL_X;
					
					for(int path = 0; path < tmpDist; path++){
						xx += Direction.offsetX[dir];
						zz += Direction.offsetZ[dir];
						LaboratoryElement ele = trySpawnElement(world,hallType,xx,zz,dir);
						if (ele != null)elements.add(ele);
					}
					
					break;
				}
			}
			
			if (hasGenerated)attempts = Math.min(100,attempts+10);
		}
		
		score = elements.size()+roomElements.size()*12;
		return true;
	}
	
	public LaboratoryPlan copy(){
		LaboratoryPlan copy = new LaboratoryPlan();
		copy.elements.addAll(elements);
		copy.roomElements.addAll(roomElements);
		copy.score = score;
		return copy;
	}
	
	public int getScore(){
		return score;
	}
	
	private boolean hasSpaceFor(LaboratoryElement testElement, LaboratoryElement exception){
		for(LaboratoryElement element:elements){
			if (element == exception)continue;
			if (Math.abs(element.x-testElement.x) <= element.type.sizeX && Math.abs(element.z-testElement.z) <= element.type.sizeZ)return false;
		}
		
		return true;
	}
	
	private static LaboratoryElement trySpawnElement(LargeStructureWorld world, LaboratoryElementType type, int x, int z, int dir){
		if (type == LaboratoryElementType.NONE)return null;
		
		tryMap.clear();
		
		int minY, maxY;
		minY = maxY = world.getHighestY(x,z);
		
		for(int xx = x-type.halfSizeX, zz, yy; xx <= x+type.halfSizeX && maxY-minY <= 5; xx++){
			for(zz = z-type.halfSizeZ; zz <= z+type.halfSizeZ; zz++){
				if ((yy = world.getHighestY(xx,zz)) == 0)return null;
				
				if (yy < minY)minY = yy;
				else if (yy > maxY)maxY = yy;
				
				tryMap.adjustOrPutValue((byte)(yy-128),(byte)1,(byte)1);
			}
		}
		
		if (maxY-minY > 5)return null;
		
		int mostFrequentY = 0;
		byte mostFrequentYAmount = 0;
		
		for(byte y:tryMap.keys()){
			byte amt = tryMap.get(y);
			
			if (amt > mostFrequentYAmount){
				mostFrequentYAmount = amt;
				mostFrequentY = y+128;
			}
		}
		
		if (maxY-mostFrequentY > 3 || mostFrequentY-minY > 2)return null;
		if (mostFrequentYAmount*type.oneOverArea < 0.25F)return null;
		
		return new LaboratoryElement(type,x,mostFrequentY,z);
	}
}
