package chylex.hee.mechanics.compendium.player;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.objects.IKnowledgeObjectInstance;

public class PlayerDiscoveryList<P extends IKnowledgeObjectInstance<T>,T>{
	private final IObjectSerializer<T> serializer;
	private final Set<T> discoveredObjects = new HashSet<>();
	
	public PlayerDiscoveryList(IObjectSerializer<T> serializer){
		this.serializer = serializer;
	}
	
	public boolean addObject(P object){
		return discoveredObjects.add(object.getUnderlyingObject());
	}
	
	public boolean hasDiscoveredObject(P object){
		return discoveredObjects.contains(object.getUnderlyingObject());
	}
	
	public NBTTagList saveToNBTList(){		
		NBTTagList list = new NBTTagList();
		for(T object:discoveredObjects)list.appendTag(new NBTTagString(serializer.serialize(object)));
		return list;
	}
	
	public void loadFromNBTList(NBTTagList list){
		discoveredObjects.clear();
		
		for(int a = 0, count = list.tagCount(); a < count; a++){
			T object = serializer.deserialize(list.getStringTagAt(a));
			if (object != null)discoveredObjects.add((T)KnowledgeObject.getObject(object).getObject().getUnderlyingObject());
		}
	}
	
	public interface IObjectSerializer<T>{
		String serialize(T object);
		T deserialize(String data);
	}
}
