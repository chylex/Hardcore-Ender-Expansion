package chylex.hee.world.structure.island.biome.feature.island.laboratory;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import chylex.hee.world.util.Direction;
import chylex.hee.system.logging.Log;
import chylex.hee.system.util.CycleProtection;
import chylex.hee.world.structure.island.ComponentIsland;
import chylex.hee.world.structure.util.pregen.LargeStructureWorld;

public final class LaboratoryGenerator{
	private final TIntObjectHashMap<LaboratoryElement> elements;
	private final List<LaboratoryElement> roomElements;
	
	public LaboratoryGenerator(LaboratoryPlan plan){
		this.elements = new TIntObjectHashMap<>(plan.elements.size());
		this.roomElements = plan.roomElements;
		
		for(LaboratoryElement element:plan.elements){
			elements.put((element.x+ComponentIsland.halfSize)*(ComponentIsland.size+1)+element.z,element);
		}
	}
	
	public void generateInWorld(LargeStructureWorld world, Random rand){
		int xx, yy, zz, offX, offZ, fromX, fromZ, dir, dist;
		boolean prevStairs = false;
		
		List<int[]> stairs = new ArrayList<>();
		
		for(LaboratoryElement room:roomElements){
			if (room.type == LaboratoryElementType.SMALL_ROOM)LaboratoryElementPlacer.generateSmallRoom(world,rand,room.x,room.y,room.z);
			else LaboratoryElementPlacer.generateLargeRoom(world,rand,room.x,room.y,room.z);
		}
		
		for(LaboratoryElement room:roomElements){
			for(dir = 0; dir < 4; dir++){
				if (room.connected[dir]){
					room.connected[dir] = false;
					
					offX = Direction.offsetX[dir];
					offZ = Direction.offsetZ[dir];
					fromX = xx = room.x+offX*(room.type.halfSizeX+1);
					fromZ = zz = room.z+offZ*(room.type.halfSizeZ+1);
					yy = room.y;
					dist = 1;
					
					LaboratoryElementPlacer.generateRoomEntrance(world,rand,fromX-offX,yy,fromZ-offZ,offX != 0);
					CycleProtection.setCounter(512);
					
					while(CycleProtection.proceed()){
						xx += offX;
						zz += offZ;
						++dist;
						
						LaboratoryElement hall = getAt(xx,zz);
						
						if (hall == null){
							LaboratoryElement nextRoom = null;
							
							for(int test = 0, tx = xx, tz = zz; test < 16; test++){
								LaboratoryElement element = getAt(tx += offX,tz += offZ);
								
								if (element != null){
									nextRoom = element;
									break;
								}
							}
							
							if (nextRoom == null){
								Log.error("Next room is null, this was never supposed to happen!");
								continue;
							}
							
							if (nextRoom.y != yy){
								xx -= offX;
								zz -= offZ;
								stairs.add(new int[]{ xx, yy, zz, nextRoom.y-yy, dir });
							}
							
							LaboratoryElementPlacer.generateHall(world,rand,fromX,fromZ,xx-offX,zz-offZ,yy,offX != 0);
							
							if (nextRoom.y != yy){
								xx += offX;
								zz += offZ;
								yy = nextRoom.y;
							}
							
							LaboratoryElementPlacer.generateRoomEntrance(world,rand,xx,yy,zz,offX != 0);
							break;
						}
						else if (hall.y != yy){
							LaboratoryElementPlacer.generateHall(world,rand,fromX,fromZ,xx-offX,zz-offZ,yy,offX != 0);
							stairs.add(new int[]{ xx, yy, zz, hall.y-yy, dir });
							fromX = xx += offX;
							fromZ = zz += offZ;
							yy = hall.y;
							continue;
						}

						prevStairs = false;
					}
					
					CycleProtection.reset();
					
					dist += room.type.halfSizeX*offX+room.type.halfSizeZ*offZ-1;
					
					while(--dist >= 0){
						LaboratoryElement ele = getAt(xx,zz);
						
						if (ele != null && ele.type.isRoom()){
							ele.connected[Direction.rotateOpposite[dir]] = false;
							break;
						}
						else{						
							xx += offX;
							zz += offZ;
						}
					}
				}
			}
		}
		
		for(LaboratoryElement room:roomElements){
			if (room.type == LaboratoryElementType.SMALL_ROOM)LaboratoryContent.populateSmallRoom(world,rand,room.x,room.y,room.z);
			else LaboratoryContent.populateLargeRoom(world,rand,room.x,room.y,room.z);
		}
		
		for(int[] array:stairs)LaboratoryElementPlacer.generateHallStairs(world,rand,array[0],array[1],array[2],Direction.offsetX[array[4]],array[3],Direction.offsetZ[array[4]]);
	}
	
	private LaboratoryElement getAt(int x, int z){
		return elements.get((x+ComponentIsland.halfSize)*(ComponentIsland.size+1)+z);
	}
}
