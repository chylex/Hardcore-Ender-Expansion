package chylex.hee.api.handlers;
import chylex.hee.api.message.IMessageHandler;
import chylex.hee.api.message.MessagePattern;
import chylex.hee.api.message.MessageRegistry;
import chylex.hee.api.message.utils.RunEvent;

public abstract class ImcHandler{
	public static final void loadHandlers(){
		for(ImcHandler handler:new ImcHandler[]{
			new ImcEssenceHandlers(),
			new ImcMobHandlers(),
			new ImcOrbHandlers(),
			new ImcTableHandlers(),
			new ImcWorldHandlers(),
			new ImcSystemHandlers()
		}){
			handler.register();
		}
	}
	
	protected final MessagePattern register(String key, IMessageHandler handler, RunEvent event){
		return MessageRegistry.register(key, handler, event);
	}
	
	public abstract void register();
}
