package chylex.hee.mechanics.compendium.player;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

public class PlayerDiscoveryList<T>{
	private final IObjectSerializer<T> serializer;
	private final Set<T> discoveredObjects = new HashSet<>();
	
	public PlayerDiscoveryList(IObjectSerializer<T> serializer){
		this.serializer = serializer;
	}
	
	public boolean addObject(T object){
		return discoveredObjects.add(object);
	}
	
	public boolean hasDiscoveredObject(T object){
		return discoveredObjects.contains(object);
	}
	
	public NBTTagList saveToNBTList(){
		NBTTagList list = new NBTTagList();
		for(T object:discoveredObjects)list.appendTag(new NBTTagString(serializer.serialize(object)));
		return list;
	}
	
	public void loadFromNBTList(NBTTagList list){
		for(int a = 0, count = list.tagCount(); a < count; a++){
			T object = serializer.deserialize(list.getStringTagAt(a));
			if (object != null)discoveredObjects.add(object);
		}
	}
	
	public interface IObjectSerializer<T>{
		public String serialize(T object);
		public T deserialize(String data);
	}
}
