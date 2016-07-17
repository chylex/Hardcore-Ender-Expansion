package chylex.hee.world.structure.island.biome.feature.forest.ravageddungeon;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

public final class DungeonElementList{
	private final EnumMap<DungeonElementType,List<DungeonElement>> data = new EnumMap<>(DungeonElementType.class);
	private final DungeonElement[] locReference;
	private final byte width;
	
	public DungeonElementList(int width, int height){
		for(DungeonElementType type:DungeonElementType.values())data.put(type,new ArrayList<DungeonElement>());
		this.locReference = new DungeonElement[width*height];
		this.width = (byte)width;
	}
	
	public void add(DungeonElement element){
		if (locReference[element.x+element.y*width] != null)remove(locReference[element.x+element.y*width]);
		data.get(element.type).add(element);
		locReference[element.x+element.y*width] = element;
	}
	
	public void remove(DungeonElement element){
		for(Iterator<DungeonElement> iter = data.get(element.type).iterator(); iter.hasNext();){
			if (iter.next().equals(element)){
				iter.remove();
				break;
			}
		}
	}
	
	public List<DungeonElement> getAll(DungeonElementType type){
		return data.get(type);
	}
	
	public DungeonElement getRandom(DungeonElementType type, Random rand){
		List<DungeonElement> elements = data.get(type);
		if (elements.size() > 0)return elements.get(rand.nextInt(elements.size()));
		return null;
	}
	
	public DungeonElement getAt(int x, int y){
		if (x < 0 || y < 0 || x >= width || x+y*width >= locReference.length)return null;
		return locReference[x+y*width];
	}
	
	public DungeonElementType getTypeAt(int x, int y){
		DungeonElement element = getAt(x,y);
		
		if (element == null)return DungeonElementType.EMPTY;
		else return element.type;
	}
	
	public Set<DungeonElement> getGrouped(DungeonElement element){
		Set<DungeonElement> list = new HashSet<>();
		addElementAndGrouped(element,list);
		return list;
	}
	
	private Set<DungeonElement> addElementAndGrouped(DungeonElement element, Set<DungeonElement> set){
		set.add(element);
		
		for(DungeonDir dir:DungeonDir.values){
			if (element.checkConnection(dir)){
				DungeonElement connectedElement = getAt(element.x+dir.addX*element.size,element.y+dir.addY*element.size);
				
				if (connectedElement != null && connectedElement.type == element.type && !set.contains(connectedElement))addElementAndGrouped(connectedElement,set);
			}
		}
		
		return set;
	}
}
