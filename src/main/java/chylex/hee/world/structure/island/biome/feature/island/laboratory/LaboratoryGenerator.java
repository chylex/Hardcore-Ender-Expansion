package chylex.hee.world.structure.island.biome.feature.island.laboratory;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.List;
import java.util.Random;
import net.minecraft.util.Direction;
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
		
		for(LaboratoryElement room:roomElements){
			if (room.type == LaboratoryElementType.SMALL_ROOM)LaboratoryElementPlacer.generateSmallRoom(world,rand,room.x,room.y,room.z);
			else LaboratoryElementPlacer.generateLargeRoom(world,rand,room.x,room.y,room.z);
			
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
					
					while(true){
						xx += offX;
						zz += offZ;
						++dist;
						
						LaboratoryElement hall = getAt(xx,zz);
						
						if (hall == null || !hall.type.isHall()){
							LaboratoryElementPlacer.generateHall(world,rand,fromX,fromZ,xx,zz,yy);
							LaboratoryElementPlacer.generateRoomEntrance(world,rand,xx,yy,zz,offX != 0);
							break;
						}
						else if (hall.y != yy){
							LaboratoryElementPlacer.generateHall(world,rand,fromX,fromZ,xx,zz,yy);
							
							if (prevStairs){
								xx -= offX*LaboratoryElementPlacer.hallStairsLength;
								zz -= offZ*LaboratoryElementPlacer.hallStairsLength;
							}
							
							/*if (dist <= LaboratoryElementPlacer.hallStairsLength){
								xx -= offX*(LaboratoryElementPlacer.hallStairsLength>>1);
								zz -= offZ*(LaboratoryElementPlacer.hallStairsLength>>1);
							}*/
							
							LaboratoryElementPlacer.generateHallStairs(world,rand,xx,yy,zz,offX,hall.y-yy,offZ);
							fromX = xx += offX*LaboratoryElementPlacer.hallStairsLength;
							fromZ = zz += offZ*LaboratoryElementPlacer.hallStairsLength;
							yy = hall.y;
							prevStairs = true;
							continue;
						}
						
						prevStairs = false;
					}
					
					dist += room.type.halfSizeX*offX+room.type.halfSizeZ*offZ;
					
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
	}
	
	private LaboratoryElement getAt(int x, int z){
		return elements.get((x+ComponentIsland.halfSize)*(ComponentIsland.size+1)+z);
	}
}
