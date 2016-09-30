package chylex.hee.api.message.element.base;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;
import chylex.hee.api.message.MessageRunner;
import chylex.hee.api.message.utils.MessageLogger;

public abstract class PreconditionComposite<T> extends Precondition<T>{
	public static final PreconditionComposite<Object> noValue(){
		return new PreconditionComposite<Object>(){
			@Override
			public Object getValue(MessageRunner runner){
				return null;
			}
		};
	}
	
	private final Map<String, Precondition> conditions = new HashMap<>();
	
	public final void addCondition(String property, Precondition condition){
		conditions.put(property, condition);
	}
	
	public final <R> Precondition<R> getCondition(String property){
		return conditions.get(property);
	}
	
	@Override
	public final boolean checkType(NBTBase tag){
		return tag != null && tag.getId() == NBT.TAG_COMPOUND;
	}

	@Override
	public final boolean checkValue(NBTBase tag){
		NBTTagCompound compound = (NBTTagCompound)tag;
		
		for(Entry<String, Precondition> entry:conditions.entrySet()){
			NBTBase compTag = compound.getTag(entry.getKey());
			
			if (!entry.getValue().checkType(compTag)){
				MessageLogger.logError("Incorrect type of tag $0. || $1", entry.getKey(), compTag == null ? "<null>" : (compTag.toString()+"/"+compTag.getId()));
				return false;
			}
			else if (!entry.getValue().checkValue(compTag)){
				MessageLogger.logError("Incorrect value for tag $0. || $1", entry.getKey(), compTag == null ? "<null>" : compTag.toString());
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public final T getValue(NBTBase tag){
		return getValue(new MessageRunner(this, (NBTTagCompound)tag));
	}
	
	public abstract T getValue(MessageRunner runner);
}
