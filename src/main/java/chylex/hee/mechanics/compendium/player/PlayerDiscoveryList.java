package chylex.hee.mechanics.compendium.player;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.objects.IKnowledgeObjectInstance;
import chylex.hee.system.util.NBTUtil;

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
		return NBTUtil.writeList(discoveredObjects.stream().map(object -> new NBTTagString(serializer.serialize(object))));
	}
	
	public void loadFromNBTList(NBTTagList list){
		discoveredObjects.clear();
		
		NBTUtil.<NBTTagString>readList(list).map(tag -> serializer.deserialize(tag.func_150285_a_())).filter(object -> object != null).forEach(object -> {
			discoveredObjects.add((T)KnowledgeObject.getObject(object).getObject().getUnderlyingObject());
		});
	}
	
	public interface IObjectSerializer<T>{
		String serialize(T object);
		T deserialize(String data);
	}
}
