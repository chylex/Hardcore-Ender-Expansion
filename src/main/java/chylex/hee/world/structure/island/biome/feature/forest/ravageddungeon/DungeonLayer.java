package chylex.hee.world.structure.island.biome.feature.forest.ravageddungeon;
import gnu.trove.set.hash.TIntHashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import chylex.hee.world.structure.island.biome.feature.forest.ravageddungeon.DungeonElementType.RoomCombo;

public final class DungeonLayer{
	private final DungeonElementType[][] elementArray;
	private final DungeonElementList elementList;
	private final TIntHashSet blockedLocs;
	private final byte width, height;
	private byte x = Byte.MIN_VALUE, y = Byte.MIN_VALUE;
	private byte entranceX = -1, entranceY = -1;
	
	public DungeonLayer(int width, int height, TIntHashSet blockedLocs){
		elementArray = new DungeonElementType[width][height];
		elementList = new DungeonElementList(width,height);
		
		for(int xx = 0; xx < width; xx++){
			for(int yy = 0; yy < height; yy++)elementArray[xx][yy] = DungeonElementType.EMPTY;
		}
		
		this.width = (byte)width;
		this.height = (byte)height;
		this.blockedLocs = blockedLocs;
	}
	
	public void createEntrance(int x, int y){
		if (this.entranceX != -1 || this.entranceY != -1)throw new IllegalStateException("Created entrance twice!");
		this.entranceX = this.x = (byte)x;
		this.entranceY = this.y = (byte)y;
		elementArray[x][y] = DungeonElementType.ENTRANCE;
		elementList.add(new DungeonElement(x,y,DungeonElementType.ENTRANCE));
	}
	
	public void createDescend(int x, int y){
		if (elementList.getTypeAt(x,y) != DungeonElementType.ROOM)throw new IllegalStateException("Cannot create descend, invalid dungeon element!");
		
		DungeonElement descend;
		elementArray[x][y] = DungeonElementType.DESCEND;
		elementList.add(descend = new DungeonElement(x,y,DungeonElementType.DESCEND));
		
		for(DungeonDir dir:DungeonDir.values){
			DungeonElement element = elementList.getAt(x+dir.addX*2,y+dir.addY*2);
			
			if (element != null){
				descend.connect(dir);
				element.connect(dir.reversed());
			}
		}
	}
	
	public void createDescendBottom(int x, int y){
		this.entranceX = this.x = (byte)x;
		this.entranceY = this.y = (byte)y;
		elementArray[x][y] = DungeonElementType.DESCENDBOTTOM;
		elementList.add(new DungeonElement(x,y,DungeonElementType.DESCENDBOTTOM));
	}
	
	public void createEnd(int x, int y){
		if (elementList.getTypeAt(x,y) != DungeonElementType.ROOM)throw new IllegalStateException("Cannot create end, invalid dungeon element!");
		
		Set<DungeonElement> connected = elementList.getGrouped(elementList.getAt(x,y));
		if (connected.size() != 4)throw new IllegalStateException("Cannot create end, invalid dungeon element size!");
		
		List<DungeonElement> endElements = new ArrayList<>();
		
		for(DungeonElement element:connected){
			elementList.remove(element);
			
			DungeonElement newElement = new DungeonElement(element.x,element.y,DungeonElementType.END);
			elementArray[element.x][element.y] = DungeonElementType.END;
			endElements.add(newElement);
			elementList.add(newElement);
			
			for(DungeonDir dir:DungeonDir.values){
				if (element.checkConnection(dir))newElement.connect(dir);
			}
		}
		
		for(DungeonElement endElement:endElements){
			for(DungeonDir dir:DungeonDir.values){
				if (elementList.getTypeAt(endElement.x+dir.addX*2,endElement.y+dir.addY*2) == DungeonElementType.END){
					endElement.connect(dir);
				}
			}
		}
	}
	
	public boolean addHallway(DungeonDir dir){
		int amount = elementArray[x][y] == DungeonElementType.ROOM ? 2 : 1;
		int prevX = x, prevY = y;
		
		if (move(dir,amount)){
			if (elementArray[x][y] == DungeonElementType.EMPTY){
				elementArray[x][y] = DungeonElementType.HALLWAY;
				elementList.add(new DungeonElement(x,y,DungeonElementType.HALLWAY).connect(dir.reversed()));
				if (elementList.getAt(prevX,prevY) != null)elementList.getAt(prevX,prevY).connect(dir);
				return true;
			}
			else move(dir.reversed(),amount);
		}
		
		return false;
	}
	
	public boolean canGenerateRoom(DungeonDir dir){
		byte tempX = x, tempY = y;
		boolean res = false;
		
		if (move(dir,2) && check(x-1,y-1) && check(x+1,y+1)){
			for(int xx = x-1; xx <= x+1; xx++){
				for(int yy = y-1; yy <= y+1; yy++){
					if (elementArray[xx][yy] != DungeonElementType.EMPTY || blockedLocs.contains(xx+yy*width)){
						x = tempX;
						y = tempY;
						return false;
					}
				}
			}
			
			res = true;
		}
		
		x = tempX;
		y = tempY;
		
		return res;
	}
	
	public int addRoom(DungeonDir dir, Random rand){
		if (!canGenerateRoom(dir))return 0;
		
		int prevX = x, prevY = y;
		
		if (move(dir,2)){
			addRoomAt(x,y);
			elementList.getAt(x,y).connect(dir.reversed());
			DungeonElement prevElement = elementList.getAt(prevX,prevY);
			if (prevElement != null)prevElement.connect(dir);
			
			RoomCombo shape = DungeonElementType.RoomCombo.random(rand);
			
			if (shape != RoomCombo.SINGLE){
				List<byte[]> rooms = new ArrayList<>(4);
				byte origX = x, origY = y;
				
				switch(shape){
					case LONG:
						switch(rand.nextInt(3)){
							case 0: dir = dir.rotatedLeft(); break;
							case 1: dir = dir.rotatedRight(); break;
							default:
						}

						move(dir,1);
						if (canGenerateRoom(dir)){
							move(dir,2);
							rooms.add(new byte[]{ x, y });
						}
						
						break;
						
					case TURN:
					case BLOCK:
						boolean turnWay = rand.nextBoolean();
						
						for(int a = 0; a < (shape == RoomCombo.TURN ? 2 : 3); a++){
							move(dir,1);
							if (!canGenerateRoom(dir)){
								rooms.clear();
								break;
							}
							
							move(dir,2);
							rooms.add(new byte[]{ x, y });
							dir = turnWay ? dir.rotatedLeft() : dir.rotatedRight();
						}
						
						break;
						
					default:
				}
				
				for(byte[] loc:rooms)addRoomAt(loc[0],loc[1]);
				
				rooms.add(new byte[]{ origX, origY });
				
				if (rooms.size() > 1){
					int xx, yy;
					boolean found;
					
					for(byte[] loc:rooms){
						for(DungeonDir testDir:DungeonDir.values){
							xx = loc[0]+testDir.addX*3;
							yy = loc[1]+testDir.addY*3;
							
							if (elementList.getTypeAt(xx,yy) == DungeonElementType.ROOM){
								found = false;
								
								for(byte[] testLoc:rooms){
									if (testLoc[0] == xx && testLoc[1] == yy){
										found = true;
										break;
									}
								}
								
								if (found)elementList.getAt(loc[0],loc[1]).connect(testDir);
							}
						}
					}
				}
				
				byte[] moveTo = rooms.get(rand.nextInt(rooms.size()));
				moveToElement(elementList.getAt(moveTo[0],moveTo[1]));
				
				return rooms.size();
			}
			
			return 1;
		}
		else return 0;
	}
	
	private void addRoomAt(int x, int y){
		for(int xx = x-1; xx <= x+1; xx++){
			for(int yy = y-1; yy <= y+1; yy++){
				elementArray[xx][yy] = DungeonElementType.ROOM;
			}
		}
		
		elementList.add(new DungeonElement(x,y,DungeonElementType.ROOM));
	}
	
	public DungeonElementList getElements(){
		return elementList;
	}
	
	public void moveToEntrance(){
		x = entranceX;
		y = entranceY;
	}
	
	public void moveToElement(DungeonElement element){
		x = element.x;
		y = element.y;
	}
	
	private boolean check(int x, int y){
		return x >= 0 && y >= 0 && x < width && y < height && !blockedLocs.contains(x+y*width);
	}
	
	private boolean move(DungeonDir dir, int amount){
		x += dir.addX*amount;
		y += dir.addY*amount;
		
		if (check(x,y))return true;
		x -= dir.addX*amount;
		y -= dir.addY*amount;
		return false;
	}
}
