package chylex.hee.api.message.handlers;
import chylex.hee.api.message.MessageHandler;
import chylex.hee.api.message.MessagePattern;
import chylex.hee.api.message.MessageRegistry;
import chylex.hee.api.message.utils.RunEvent;

public abstract class ImcHandler{
	public static final void loadHandlers(){
		ImcHandler[] handlers = new ImcHandler[]{
			new ImcEssenceHandlers(),
			new ImcMobHandlers(),
			new ImcTableHandlers(),
			new ImcWorldHandlers()
		};
		
		for(ImcHandler handler:handlers)handler.register();
	}
	
	protected final MessagePattern register(String key, MessageHandler handler, RunEvent event){
		return MessageRegistry.register(key,handler,event);
	}
	
	public abstract void register();
}
