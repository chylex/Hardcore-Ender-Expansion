package chylex.hee.api.message;
import net.minecraft.nbt.NBTTagCompound;
import chylex.hee.api.message.element.base.Precondition;
import chylex.hee.api.message.element.base.PreconditionComposite;
import chylex.hee.api.message.utils.MessageLogger;

public final class MessagePattern{
	private final PreconditionComposite<?> conditions = PreconditionComposite.noValue();
	private final IMessageHandler handler;
	
	MessagePattern(IMessageHandler handler){
		this.handler = handler;
	}
	
	public MessagePattern addProp(String name, Precondition condition){
		conditions.addCondition(name,condition);
		return this;
	}
	
	boolean tryRun(NBTTagCompound root){
		if (!conditions.checkType(root)){
			MessageLogger.logError("Unexpected message format, expected NBTTagCompound, got $0 || $1",root.getClass().getSimpleName(),root);
			return false;
		}
		
		if (!conditions.checkValue(root))return false;
		
		handler.call(new MessageRunner(conditions,root));
		return true;
	}
}
