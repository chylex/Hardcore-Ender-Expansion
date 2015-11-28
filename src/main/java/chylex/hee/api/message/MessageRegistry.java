package chylex.hee.api.message;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.NBTTagCompound;
import chylex.hee.api.message.handlers.ImcHandler;
import chylex.hee.api.message.utils.MessageLogger;
import chylex.hee.api.message.utils.RunEvent;
import com.google.common.collect.ImmutableMap;

public final class MessageRegistry{
	private static Map<String,MessagePattern> registry;
	private static Map<String,RunEvent> events;
	
	public static MessagePattern register(String key, IMessageHandler handler, RunEvent event){
		MessagePattern pattern = new MessagePattern(handler);
		if (registry.put(key,pattern) != null)throw new IllegalArgumentException("Cannot register duplicate IMC message key: "+key);
		events.put(key,event);
		return pattern;
	}
	
	public static boolean runMessage(String key, NBTTagCompound root){
		MessagePattern pattern = registry.get(key);
		
		if (pattern == null){
			MessageLogger.logError("Message key not found: $0",key);
			return false;
		}
		
		return pattern.tryRun(root);
	}
	
	public static boolean canRunInEvent(String key, RunEvent event){
		return events.get(key) == event || event == null;
	}
	
	static{
		registry = new HashMap<>();
		events = new HashMap<>();
		ImcHandler.loadHandlers();
		registry = ImmutableMap.copyOf(registry);
		events = ImmutableMap.copyOf(events);
	}
	
	private MessageRegistry(){}
}
