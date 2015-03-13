package chylex.hee.api.message;
import net.minecraft.nbt.NBTTagCompound;
import chylex.hee.api.message.element.base.Precondition;
import chylex.hee.api.message.element.base.PreconditionComposite;

public final class MessageRunner{
	private final PreconditionComposite<?> pattern;
	private final NBTTagCompound root;
	
	public MessageRunner(PreconditionComposite<?> pattern, NBTTagCompound root){
		this.pattern = pattern;
		this.root = root;
	}
	
	public final <R> R getValue(String property){
		Precondition<R> condition = pattern.getCondition(property);
		return condition.getValue(root.getTag(property));
	}
	
	public final String getString(String property){
		return pattern.<String>getCondition(property).getValue(root.getTag(property));
	}
	
	public final int getInt(String property){
		return pattern.<Integer>getCondition(property).getValue(root.getTag(property)).intValue();
	}
	
	public final double getDouble(String property){
		return pattern.<Double>getCondition(property).getValue(root.getTag(property)).doubleValue();
	}
	
	public final int[] getIntArray(String property){
		return pattern.<int[]>getCondition(property).getValue(root.getTag(property));
	}
	
	public final boolean getBool(String property){
		return pattern.<Boolean>getCondition(property).getValue(root.getTag(property)).booleanValue();
	}
}
